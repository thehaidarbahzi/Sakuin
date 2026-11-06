package main

import (
	"context"
	"golang.org/x/time/rate"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"sakuin-backend/auth"
	"sakuin-backend/database"
	"sakuin-backend/handlers"
)

func main() {
	db, err := database.Connect()
	if err != nil {
		log.Fatalf("db: %v", err)
	}
	defer db.Close()

	limiter := rate.NewLimiter(rate.Every(100*time.Millisecond), 100)

	mux := http.NewServeMux()

	secureMux := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("X-Content-Type-Options", "nosniff")
		w.Header().Set("X-Frame-Options", "DENY")
		w.Header().Set("Referrer-Policy", "strict-origin-when-cross-origin")
		w.Header().Set("X-XSS-Protection", "1; mode=block")
		w.Header().Set("Content-Security-Policy", "default-src 'self'")
		w.Header().Set("Strict-Transport-Security", "max-age=31536000; includeSubDomains")

		if !limiter.Allow() {
			http.Error(w, `{"error":"too many requests"}`, http.StatusTooManyRequests)
			return
		}

		// Cap request body size to prevent memory-exhaustion attacks.
		r.Body = http.MaxBytesReader(w, r.Body, 1<<20) // 1 MiB

		mux.ServeHTTP(w, r)
	})

	mux.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/" {
			http.NotFound(w, r)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte(`{"status":"ok","message":"Sakuin API"}`))
	})

	mux.HandleFunc("/auth/login", auth.Login(db))
	mux.HandleFunc("/auth/register", auth.Register(db))

	handlers.RegisterProtectedRoutes(db, mux)

	port := ":8080"
	log.Printf("Sakuin API running on http://localhost%s", port)

	srv := &http.Server{
		Addr:    port,
		Handler: secureMux,
	}

	go func() {
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("server error: %v", err)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt, syscall.SIGTERM)
	<-quit

	log.Println("Shutting down server...")
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	if err := srv.Shutdown(ctx); err != nil {
		log.Fatalf("Server shutdown failed: %v", err)
	}
	log.Println("Server stopped")
}
