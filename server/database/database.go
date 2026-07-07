package database

import (
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"

	"github.com/wangzhenman/notifysentinel/server/models"
)

var DB *gorm.DB


func Init() {

	db, err := gorm.Open(
		sqlite.Open("notifysentinel.db"),
		&gorm.Config{},
	)

	if err != nil {
		panic(err)
	}

	DB = db


	err = DB.AutoMigrate(
		&models.Event{},
	)

	if err != nil {
		panic(err)
	}

}