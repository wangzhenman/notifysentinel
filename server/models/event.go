package models

import "time"

type Event struct {
	ID uint `gorm:"primaryKey" json:"id"`

	Source  string `json:"source"`
	Level   string `json:"level"`
	Title   string `json:"title"`
	Message string `json:"message"`

	CreatedAt time.Time `json:"created_at"`
}