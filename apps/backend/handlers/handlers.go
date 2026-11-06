package handlers

import (
	"database/sql"
	"encoding/json"
	"net/http"
	"strconv"
	"strings"
	"time"

	"golang.org/x/crypto/bcrypt"

	"sakuin-backend/auth"
	"sakuin-backend/models"
)

func respondJSON(w http.ResponseWriter, status int, v interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(v)
}

func respondErr(w http.ResponseWriter, status int, msg string) {
	respondJSON(w, status, map[string]string{"error": msg})
}

func getUserID(r *http.Request) (int64, bool) {
	return auth.UserIDFromContext(r.Context())
}

func parseID(r *http.Request, prefix string) (int64, bool) {
	idStr := strings.TrimPrefix(r.URL.Path, prefix)
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil || id <= 0 {
		return 0, false
	}
	return id, true
}

// ---- Users ----

func GetUsers(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		authID, ok := getUserID(r)
		if !ok {
			respondErr(w, http.StatusUnauthorized, "authentication required")
			return
		}
		rows, err := db.Query(`SELECT id, username, email FROM users WHERE id=$1 ORDER BY id DESC`, authID)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		defer rows.Close()
		var users []models.User
		for rows.Next() {
			var u models.User
			if err := rows.Scan(&u.ID, &u.Username, &u.Email); err != nil {
				continue
			}
			users = append(users, u)
		}
		respondJSON(w, http.StatusOK, users)
	}
}

func GetUser(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/users/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		authID, ok := getUserID(r)
		if !ok || authID != id {
			respondErr(w, http.StatusForbidden, "you can only view your own account")
			return
		}
		var u models.User
		err := db.QueryRow(`SELECT id, username, email FROM users WHERE id=$1`, id).Scan(&u.ID, &u.Username, &u.Email)
		if err == sql.ErrNoRows {
			respondErr(w, http.StatusNotFound, "user not found")
			return
		} else if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		respondJSON(w, http.StatusOK, u)
	}
}

func CreateUser(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var payload struct{ Username, Email, Password string }
		if err := json.NewDecoder(r.Body).Decode(&payload); err != nil || payload.Username == "" || payload.Email == "" || payload.Password == "" {
			respondErr(w, http.StatusBadRequest, "username, email and password required")
			return
		}
		hashed, err := bcrypt.GenerateFromPassword([]byte(payload.Password), bcrypt.DefaultCost)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "failed to hash password")
			return
		}
		var id int64
		err = db.QueryRow(`INSERT INTO users (username, email, password, created_at, updated_at) VALUES ($1,$2,$3,$4,$5) RETURNING id`,
			payload.Username, payload.Email, string(hashed), time.Now(), time.Now()).Scan(&id)
		if err != nil {
			respondErr(w, http.StatusConflict, "user already exists")
			return
		}
		respondJSON(w, http.StatusCreated, map[string]interface{}{"id": id, "username": payload.Username, "email": payload.Email})
	}
}

func UpdateUser(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/users/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		authID, ok := getUserID(r)
		if !ok || authID != id {
			respondErr(w, http.StatusForbidden, "you can only update your own account")
			return
		}
		var payload struct{ Username, Email, Password string }
		json.NewDecoder(r.Body).Decode(&payload)

		var res sql.Result
		var err error
		if payload.Password != "" {
			hashed, herr := bcrypt.GenerateFromPassword([]byte(payload.Password), bcrypt.DefaultCost)
			if herr != nil {
				respondErr(w, http.StatusInternalServerError, "failed to hash password")
				return
			}
			res, err = db.Exec(`UPDATE users SET username=$1, email=$2, password=$3, updated_at=$4 WHERE id=$5`,
				payload.Username, payload.Email, string(hashed), time.Now(), id)
		} else {
			res, err = db.Exec(`UPDATE users SET username=$1, email=$2, updated_at=$3 WHERE id=$4`,
				payload.Username, payload.Email, time.Now(), id)
		}
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		n, _ := res.RowsAffected()
		if n == 0 {
			respondErr(w, http.StatusNotFound, "user not found")
			return
		}
		respondJSON(w, http.StatusOK, map[string]string{"message": "updated"})
	}
}

func DeleteUser(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/users/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		authID, ok := getUserID(r)
		if !ok || authID != id {
			respondErr(w, http.StatusForbidden, "you can only delete your own account")
			return
		}
		res, err := db.Exec(`DELETE FROM users WHERE id=$1`, id)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		n, _ := res.RowsAffected()
		if n == 0 {
			respondErr(w, http.StatusNotFound, "user not found")
			return
		}
		respondJSON(w, http.StatusOK, map[string]string{"message": "deleted"})
	}
}

// ---- Assets ----

func GetAssets(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		rows, err := db.Query(`SELECT id, name FROM assets ORDER BY id DESC`)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		defer rows.Close()
		var assets []models.Asset
		for rows.Next() {
			var a models.Asset
			if err := rows.Scan(&a.ID, &a.Name); err != nil {
				continue
			}
			assets = append(assets, a)
		}
		respondJSON(w, http.StatusOK, assets)
	}
}

func GetAsset(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/assets/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		var a models.Asset
		err := db.QueryRow(`SELECT id, name FROM assets WHERE id=$1`, id).Scan(&a.ID, &a.Name)
		if err == sql.ErrNoRows {
			respondErr(w, http.StatusNotFound, "asset not found")
			return
		} else if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		respondJSON(w, http.StatusOK, a)
	}
}

func CreateAsset(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var payload struct{ Name string }
		if err := json.NewDecoder(r.Body).Decode(&payload); err != nil || payload.Name == "" {
			respondErr(w, http.StatusBadRequest, "name required")
			return
		}
		var id int64
		err := db.QueryRow(`INSERT INTO assets (name, created_at, updated_at) VALUES ($1,$2,$3) RETURNING id`,
			payload.Name, time.Now(), time.Now()).Scan(&id)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		respondJSON(w, http.StatusCreated, map[string]interface{}{"id": id, "name": payload.Name})
	}
}

func UpdateAsset(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/assets/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		var payload struct{ Name string }
		json.NewDecoder(r.Body).Decode(&payload)
		res, err := db.Exec(`UPDATE assets SET name=$1 WHERE id=$2`, payload.Name, id)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		n, _ := res.RowsAffected()
		if n == 0 {
			respondErr(w, http.StatusNotFound, "asset not found")
			return
		}
		respondJSON(w, http.StatusOK, map[string]string{"message": "updated"})
	}
}

func DeleteAsset(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/assets/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		res, err := db.Exec(`DELETE FROM assets WHERE id=$1`, id)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		n, _ := res.RowsAffected()
		if n == 0 {
			respondErr(w, http.StatusNotFound, "asset not found")
			return
		}
		respondJSON(w, http.StatusOK, map[string]string{"message": "deleted"})
	}
}

// ---- Categories ----

func GetCategories(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		rows, err := db.Query(`SELECT id, name, type FROM categories ORDER BY id DESC`)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		defer rows.Close()
		var categories []models.Category
		for rows.Next() {
			var c models.Category
			if err := rows.Scan(&c.ID, &c.Name, &c.Type); err != nil {
				continue
			}
			categories = append(categories, c)
		}
		respondJSON(w, http.StatusOK, categories)
	}
}

func GetCategory(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/categories/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		var c models.Category
		err := db.QueryRow(`SELECT id, name, type FROM categories WHERE id=$1`, id).Scan(&c.ID, &c.Name, &c.Type)
		if err == sql.ErrNoRows {
			respondErr(w, http.StatusNotFound, "category not found")
			return
		} else if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		respondJSON(w, http.StatusOK, c)
	}
}

func CreateCategory(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var payload struct{ Name, Type string }
		if err := json.NewDecoder(r.Body).Decode(&payload); err != nil || payload.Name == "" || payload.Type == "" {
			respondErr(w, http.StatusBadRequest, "name and type required")
			return
		}
		var id int64
		err := db.QueryRow(`INSERT INTO categories (name, type, created_at, updated_at) VALUES ($1,$2,$3,$4) RETURNING id`,
			payload.Name, payload.Type, time.Now(), time.Now()).Scan(&id)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		respondJSON(w, http.StatusCreated, map[string]interface{}{"id": id, "name": payload.Name, "type": payload.Type})
	}
}

func UpdateCategory(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/categories/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		var payload struct{ Name, Type string }
		json.NewDecoder(r.Body).Decode(&payload)
		res, err := db.Exec(`UPDATE categories SET name=$1, type=$2 WHERE id=$3`, payload.Name, payload.Type, id)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		n, _ := res.RowsAffected()
		if n == 0 {
			respondErr(w, http.StatusNotFound, "category not found")
			return
		}
		respondJSON(w, http.StatusOK, map[string]string{"message": "updated"})
	}
}

func DeleteCategory(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/categories/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		res, err := db.Exec(`DELETE FROM categories WHERE id=$1`, id)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		n, _ := res.RowsAffected()
		if n == 0 {
			respondErr(w, http.StatusNotFound, "category not found")
			return
		}
		respondJSON(w, http.StatusOK, map[string]string{"message": "deleted"})
	}
}

// ---- Transactions ----

func GetTransactions(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		userID, ok := getUserID(r)
		if !ok {
			respondErr(w, http.StatusUnauthorized, "authentication required")
			return
		}
		rows, err := db.Query(`SELECT id, user_id, category_id, asset_id, type, amount, title, transaction_date, description FROM transactions WHERE user_id=$1 ORDER BY id DESC`, userID)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		defer rows.Close()
		var transactions []models.Transaction
		for rows.Next() {
			var t models.Transaction
			var desc sql.NullString
			if err := rows.Scan(&t.ID, &t.UserID, &t.CategoryID, &t.AssetID, &t.Type, &t.Amount, &t.Title, &t.TransactionDate, &desc); err != nil {
				continue
			}
			t.Description = desc.String
			transactions = append(transactions, t)
		}
		respondJSON(w, http.StatusOK, transactions)
	}
}

func GetTransaction(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/transactions/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		userID, ok := getUserID(r)
		if !ok {
			respondErr(w, http.StatusUnauthorized, "authentication required")
			return
		}
		var t models.Transaction
		var desc sql.NullString
		err := db.QueryRow(`SELECT id, user_id, category_id, asset_id, type, amount, title, transaction_date, description FROM transactions WHERE id=$1 AND user_id=$2`, id, userID).
			Scan(&t.ID, &t.UserID, &t.CategoryID, &t.AssetID, &t.Type, &t.Amount, &t.Title, &t.TransactionDate, &desc)
		if err == sql.ErrNoRows {
			respondErr(w, http.StatusNotFound, "transaction not found")
			return
		} else if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		t.Description = desc.String
		respondJSON(w, http.StatusOK, t)
	}
}

func CreateTransaction(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		userID, ok := getUserID(r)
		if !ok {
			respondErr(w, http.StatusUnauthorized, "authentication required")
			return
		}
		var payload struct {
			UserID          int64   `json:"user_id"`
			CategoryID      int64   `json:"category_id"`
			AssetID         int64   `json:"asset_id"`
			Type            string  `json:"type"`
			Amount          float64 `json:"amount"`
			Title           string  `json:"title"`
			TransactionDate string  `json:"transaction_date"`
			Description     string  `json:"description"`
		}
		if err := json.NewDecoder(r.Body).Decode(&payload); err != nil {
			respondErr(w, http.StatusBadRequest, "invalid payload")
			return
		}
		if payload.UserID != 0 && payload.UserID != userID {
			respondErr(w, http.StatusForbidden, "cannot create transactions for other users")
			return
		}
		if payload.CategoryID == 0 || payload.AssetID == 0 || payload.Type == "" || payload.Title == "" || payload.TransactionDate == "" {
			respondErr(w, http.StatusBadRequest, "required fields must be filled")
			return
		}
		if payload.Amount < 0 {
			respondErr(w, http.StatusBadRequest, "amount cannot be negative")
			return
		}
		tDate, err := time.Parse(time.RFC3339, payload.TransactionDate)
		if err != nil {
			tDate, err = time.Parse("2006-01-02", payload.TransactionDate)
			if err != nil {
				respondErr(w, http.StatusBadRequest, "invalid transaction_date (use RFC3339 or YYYY-MM-DD)")
				return
			}
		}
		var id int64
		err = db.QueryRow(`INSERT INTO transactions (user_id, category_id, asset_id, type, amount, title, transaction_date, description, created_at, updated_at) VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10) RETURNING id`,
			userID, payload.CategoryID, payload.AssetID, payload.Type, payload.Amount, payload.Title, tDate, payload.Description, time.Now(), time.Now()).Scan(&id)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		respondJSON(w, http.StatusCreated, map[string]interface{}{
			"id":               id,
			"user_id":          userID,
			"category_id":      payload.CategoryID,
			"asset_id":         payload.AssetID,
			"type":             payload.Type,
			"amount":           payload.Amount,
			"title":            payload.Title,
			"transaction_date": payload.TransactionDate,
		})
	}
}

func UpdateTransaction(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/transactions/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		var payload struct {
			UserID          int64   `json:"user_id"`
			CategoryID      int64   `json:"category_id"`
			AssetID         int64   `json:"asset_id"`
			Type            string  `json:"type"`
			Amount          float64 `json:"amount"`
			Title           string  `json:"title"`
			TransactionDate string  `json:"transaction_date"`
			Description     string  `json:"description"`
		}
		authID, ok := getUserID(r)
		if !ok {
			respondErr(w, http.StatusUnauthorized, "authentication required")
			return
		}
		json.NewDecoder(r.Body).Decode(&payload)
		var txDate time.Time
		if payload.TransactionDate != "" {
			parsed, err := time.Parse(time.RFC3339, payload.TransactionDate)
			if err == nil {
				txDate = parsed
			} else {
				parsed, err = time.Parse("2006-01-02", payload.TransactionDate)
				if err == nil {
					txDate = parsed
				}
			}
		}
		res, err := db.Exec(`UPDATE transactions SET category_id=$1, asset_id=$2, type=$3, amount=$4, title=$5, transaction_date=$6, description=$7, updated_at=$8 WHERE id=$9 AND user_id=$10`,
			payload.CategoryID, payload.AssetID, payload.Type, payload.Amount, payload.Title, txDate, payload.Description, time.Now(), id, authID)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		n, _ := res.RowsAffected()
		if n == 0 {
			respondErr(w, http.StatusNotFound, "transaction not found")
			return
		}
		respondJSON(w, http.StatusOK, map[string]string{"message": "updated"})
	}
}

func DeleteTransaction(db *sql.DB) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id, ok := parseID(r, "/transactions/")
		if !ok {
			respondErr(w, http.StatusBadRequest, "invalid id")
			return
		}
		authID, ok := getUserID(r)
		if !ok {
			respondErr(w, http.StatusUnauthorized, "authentication required")
			return
		}
		res, err := db.Exec(`DELETE FROM transactions WHERE id=$1 AND user_id=$2`, id, authID)
		if err != nil {
			respondErr(w, http.StatusInternalServerError, "internal server error")
			return
		}
		n, _ := res.RowsAffected()
		if n == 0 {
			respondErr(w, http.StatusNotFound, "transaction not found")
			return
		}
		respondJSON(w, http.StatusOK, map[string]string{"message": "deleted"})
	}
}

// ---------- protected route registration ----------

func RegisterProtectedRoutes(db *sql.DB, mux *http.ServeMux) {
	mux.HandleFunc("/users", auth.AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			GetUsers(db)(w, r)
		case http.MethodPost:
			CreateUser(db)(w, r)
		default:
			respondErr(w, http.StatusMethodNotAllowed, "method not allowed")
		}
	}))
	mux.HandleFunc("/users/", auth.AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			GetUser(db)(w, r)
		case http.MethodPut:
			UpdateUser(db)(w, r)
		case http.MethodDelete:
			DeleteUser(db)(w, r)
		default:
			respondErr(w, http.StatusMethodNotAllowed, "method not allowed")
		}
	}))
	mux.HandleFunc("/assets", auth.AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			GetAssets(db)(w, r)
		case http.MethodPost:
			CreateAsset(db)(w, r)
		default:
			respondErr(w, http.StatusMethodNotAllowed, "method not allowed")
		}
	}))
	mux.HandleFunc("/assets/", auth.AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			GetAsset(db)(w, r)
		case http.MethodPut:
			UpdateAsset(db)(w, r)
		case http.MethodDelete:
			DeleteAsset(db)(w, r)
		default:
			respondErr(w, http.StatusMethodNotAllowed, "method not allowed")
		}
	}))
	mux.HandleFunc("/categories", auth.AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			GetCategories(db)(w, r)
		case http.MethodPost:
			CreateCategory(db)(w, r)
		default:
			respondErr(w, http.StatusMethodNotAllowed, "method not allowed")
		}
	}))
	mux.HandleFunc("/categories/", auth.AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			GetCategory(db)(w, r)
		case http.MethodPut:
			UpdateCategory(db)(w, r)
		case http.MethodDelete:
			DeleteCategory(db)(w, r)
		default:
			respondErr(w, http.StatusMethodNotAllowed, "method not allowed")
		}
	}))
	mux.HandleFunc("/transactions", auth.AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			GetTransactions(db)(w, r)
		case http.MethodPost:
			CreateTransaction(db)(w, r)
		default:
			respondErr(w, http.StatusMethodNotAllowed, "method not allowed")
		}
	}))
	mux.HandleFunc("/transactions/", auth.AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			GetTransaction(db)(w, r)
		case http.MethodPut:
			UpdateTransaction(db)(w, r)
		case http.MethodDelete:
			DeleteTransaction(db)(w, r)
		default:
			respondErr(w, http.StatusMethodNotAllowed, "method not allowed")
		}
	}))
}
