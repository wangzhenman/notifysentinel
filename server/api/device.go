package api

import (
	"errors"
	"net/http"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"

	"github.com/wangzhenman/notifysentinel/server/database"
	"github.com/wangzhenman/notifysentinel/server/models"
)

type DeviceRegisterRequest struct {
	Name     string `json:"name" binding:"required"`
	Platform string `json:"platform" binding:"required"`
	Token    string `json:"token" binding:"required"`
}

type DeviceStatusQuery struct {
	Token string `form:"token" binding:"required"`
}

// 注册设备
func RegisterDevice(c *gin.Context) {
	var req DeviceRegisterRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(
			http.StatusBadRequest,
			gin.H{
				"error": err.Error(),
			},
		)

		return
	}

	var device models.Device

	result := database.DB.
		Where(
			"token = ?",
			req.Token,
		).
		First(&device)

	switch {
	case result.Error == nil:
		device.Name = req.Name
		device.Platform = req.Platform
		device.Enabled = true
		result = database.DB.Save(&device)

	case errors.Is(result.Error, gorm.ErrRecordNotFound):
		device = models.Device{
			Name:     req.Name,
			Platform: req.Platform,
			Token:    req.Token,
			Enabled:  true,
		}
		result = database.DB.Create(&device)
	}

	if result.Error != nil {
		c.JSON(
			500,
			gin.H{
				"error": result.Error.Error(),
			},
		)

		return
	}

	c.JSON(
		200,
		device,
	)
}

// 获取当前设备状态
func GetCurrentDevice(c *gin.Context) {
	var query DeviceStatusQuery

	if err := c.ShouldBindQuery(&query); err != nil {
		c.JSON(
			http.StatusBadRequest,
			gin.H{
				"error": err.Error(),
			},
		)

		return
	}

	var device models.Device

	result := database.DB.
		Where(
			"token = ?",
			query.Token,
		).
		First(&device)

	switch {
	case result.Error == nil:
		c.JSON(
			http.StatusOK,
			device,
		)

	case errors.Is(result.Error, gorm.ErrRecordNotFound):
		c.JSON(
			http.StatusNotFound,
			gin.H{
				"error": "device not found",
			},
		)

	default:
		c.JSON(
			http.StatusInternalServerError,
			gin.H{
				"error": result.Error.Error(),
			},
		)
	}
}

// 获取设备列表
func ListDevices(c *gin.Context) {
	var devices []models.Device
	result := database.DB.Find(&devices)

	if result.Error != nil {
		c.JSON(
			500,
			gin.H{
				"error": result.Error.Error(),
			},
		)

		return
	}

	c.JSON(
		200,
		devices,
	)
}

// 删除设备
func DeleteDevice(c *gin.Context) {
	id := c.Param("id")

	result := database.DB.Delete(
		&models.Device{},
		id,
	)

	if result.Error != nil {
		c.JSON(
			500,
			gin.H{
				"error": result.Error.Error(),
			},
		)

		return
	}

	c.JSON(
		200,
		gin.H{
			"status": "deleted",
		},
	)
}