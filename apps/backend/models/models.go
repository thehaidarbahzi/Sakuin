package models

import "time"

type User struct {
	ID        int64     `json:"id"`
	Username  string    `json:"username"`
	Email     string    `json:"email"`
	Password  string    `json:"-"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

type Asset struct {
	ID        int64     `json:"id"`
	Name      string    `json:"name"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

type Category struct {
	ID        int64     `json:"id"`
	Name      string    `json:"name"`
	Type      string    `json:"type"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

type Transaction struct {
	ID              int64     `json:"id"`
	UserID          int64     `json:"user_id"`
	CategoryID      int64     `json:"category_id"`
	AssetID         int64     `json:"asset_id"`
	Type            string    `json:"type"`
	Amount          float64   `json:"amount"`
	Title           string    `json:"title"`
	TransactionDate time.Time `json:"transaction_date"`
	Description     string    `json:"description"`
	CreatedAt       time.Time `json:"created_at"`
	UpdatedAt       time.Time `json:"updated_at"`
}
