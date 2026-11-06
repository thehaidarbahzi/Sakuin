package auth

import (
	"context"
	"database/sql"
	"encoding/json"
	"errors"
	"net/http"
	"os"
	"strings"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"

	"sakuin-backend/models"
)

var jwtSecret = []byte(getenv("JWT_SECRET", "sakuin-secret-change-me"))

func getenv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

type Claims struct {
	UserID   int64  `json:"user_id"`
	Username string `json:"username"`
	jwt.RegisteredClaims
}

type contextKey string

// UserIDKey is the context key used to store the authenticated user ID.
const UserIDKey contextKey = "user_id"

// UserIDFromContext extracts the authenticated user ID from a request context.
func UserIDFromContext(ctx context.Context) (int64, bool) {
	id, ok := ctx.Value(UserIDKey).(int64)
	return id, ok
}

func GenerateToken(userID int64, username string) (string, error) {
	claims := Claims{
		UserID:   userID,
		Username: username,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(7 * 24 * time.Hour)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(jwtSecret)
}

func ValidateToken(tokenStr string) (*Claims, error) {
	claims := &Claims{}
	token, err := jwt.ParseWithClaims(tokenStr, claims, func(t *jwt.Token) (interface{}, error) {
		if t.Method != jwt.SigningMethodHS256 {
			return nil, errors.New("unexpected signing algorithm")
		}
		return jwtSecret, nil
	})
	if err != nil {
		return nil, err
	}
	if !token.Valid || claims == nil {
		return nil, errors.New("invalid token")
	}
	return claims, nil
}

func AuthMiddleware(next http.HandlerFunc) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		h := r.Header.Get("Authorization")
		if h == "" {
			writeJSONError(w, http.StatusUnauthorized, "authorization header required")
			return
		}
		parts := strings.SplitN(h, " ", 2)
		if len(parts) != 2 || !strings.EqualFold(parts[0], "Bearer") {
			writeJSONError(w, http.StatusUnauthorized, "expected 'Bearer <token>'")
			return
		}
		claims, err := ValidateToken(parts[1])
		if err != nil {
			writeJSONError(w, http.StatusUnauthorized, "invalid or expired token")
			return
		}
		ctx := context.WithValue(r.Context(), UserIDKey, claims.UserID)
		next(w, r.WithContext(ctx))
	}
}

func writeJSONError(w http.ResponseWriter, status int, msg string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(map[string]string{"error": msg})
}

func respondJSON(w http.ResponseWriter, status int, payload interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(payload)
}

func Login(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var payload struct {
			Email    string `json:"email"`
			Password string `json:"password"`
		}
		if err := json.NewDecoder(r.Body).Decode(&payload); err != nil {
			writeJSONError(w, http.StatusBadRequest, "invalid request body")
			return
		}
		if payload.Email == "" || payload.Password == "" {
			writeJSONError(w, http.StatusBadRequest, "email and password required")
			return
		}
		var u models.User
		err := db.QueryRow(`SELECT id, username, email, password FROM users WHERE email=$1`, payload.Email).
			Scan(&u.ID, &u.Username, &u.Email, &u.Password)
		if err == sql.ErrNoRows {
			writeJSONError(w, http.StatusUnauthorized, "invalid credentials")
			return
		} else if err != nil {
			writeJSONError(w, http.StatusInternalServerError, "internal server error")
			return
		}
		if bcrypt.CompareHashAndPassword([]byte(u.Password), []byte(payload.Password)) != nil {
			writeJSONError(w, http.StatusUnauthorized, "invalid credentials")
			return
		}
		token, err := GenerateToken(u.ID, u.Username)
		if err != nil {
			writeJSONError(w, http.StatusInternalServerError, "failed to generate token")
			return
		}
		respondJSON(w, http.StatusOK, map[string]interface{}{
			"token": token,
			"user":  map[string]interface{}{"id": u.ID, "username": u.Username, "email": u.Email},
		})
	}
}

func Register(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var payload struct {
			Username string `json:"username"`
			Email    string `json:"email"`
			Password string `json:"password"`
		}
		if err := json.NewDecoder(r.Body).Decode(&payload); err != nil {
			writeJSONError(w, http.StatusBadRequest, "invalid request body")
			return
		}
		if payload.Username == "" || payload.Email == "" || payload.Password == "" {
			writeJSONError(w, http.StatusBadRequest, "username, email and password required")
			return
		}
		if len(payload.Password) < 8 {
			writeJSONError(w, http.StatusBadRequest, "password must be at least 8 characters")
			return
		}
		hashed, err := bcrypt.GenerateFromPassword([]byte(payload.Password), bcrypt.DefaultCost)
		if err != nil {
			writeJSONError(w, http.StatusInternalServerError, "failed to hash password")
			return
		}
		var id int64
		err = db.QueryRow(`INSERT INTO users (username, email, password, created_at, updated_at) VALUES ($1,$2,$3,$4,$5) RETURNING id`,
			payload.Username, payload.Email, string(hashed), time.Now(), time.Now()).Scan(&id)
		if err != nil {
			writeJSONError(w, http.StatusConflict, "user already exists")
			return
		}
		token, err := GenerateToken(id, payload.Username)
		if err != nil {
			writeJSONError(w, http.StatusInternalServerError, "failed to generate token")
			return
		}
		respondJSON(w, http.StatusCreated, map[string]interface{}{
			"id":    id,
			"token": token,
			"user":  map[string]interface{}{"id": id, "username": payload.Username, "email": payload.Email},
		})
	}
}
