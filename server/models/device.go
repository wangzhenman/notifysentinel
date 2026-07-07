package models

import "time"


type Device struct {

	ID uint `gorm:"primaryKey" json:"id"`

	// 设备名称，例如：小米14
	Name string `json:"name"`

	// 平台，例如：
	// mipush
	// fcm
	// apns
	Platform string `json:"platform"`


	// 推送Token
	Token string `json:"token"`


	// 是否启用
	Enabled bool `json:"enabled"`


	CreatedAt time.Time `json:"created_at"`

	UpdatedAt time.Time `json:"updated_at"`
}