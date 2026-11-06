package auth

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestGenerateAndValidateToken(t *testing.T) {
	token, err := GenerateToken(42, "alice")
	if err != nil {
		t.Fatalf("GenerateToken: %v", err)
	}

	claims, err := ValidateToken(token)
	if err != nil {
		t.Fatalf("ValidateToken: %v", err)
	}
	if claims.UserID != 42 || claims.Username != "alice" {
		t.Fatalf("unexpected claims: %+v", claims)
	}
}

func TestAuthMiddlewareSetsContext(t *testing.T) {
	token, err := GenerateToken(7, "bob")
	if err != nil {
		t.Fatalf("GenerateToken: %v", err)
	}

	handler := AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		id, ok := UserIDFromContext(r.Context())
		if !ok || id != 7 {
			t.Errorf("expected user id 7 in context, got %d ok=%v", id, ok)
		}
		w.WriteHeader(http.StatusOK)
	})

	req := httptest.NewRequest(http.MethodGet, "/", nil)
	req.Header.Set("Authorization", "Bearer "+token)
	rec := httptest.NewRecorder()
	handler(rec, req)

	if rec.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d", rec.Code)
	}
}

func TestAuthMiddlewareRejectsMissingToken(t *testing.T) {
	handler := AuthMiddleware(func(w http.ResponseWriter, r *http.Request) {
		t.Error("handler should not be called")
	})
	req := httptest.NewRequest(http.MethodGet, "/", nil)
	rec := httptest.NewRecorder()
	handler(rec, req)
	if rec.Code != http.StatusUnauthorized {
		t.Fatalf("expected 401, got %d", rec.Code)
	}
}

func TestValidateRejectsTamperedToken(t *testing.T) {
	token, _ := GenerateToken(1, "x")
	if _, err := ValidateToken(token + "tampered"); err == nil {
		t.Fatal("expected tampered token to be rejected")
	}
}
