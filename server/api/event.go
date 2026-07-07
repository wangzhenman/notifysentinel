package api

import (
	"log"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"

	"github.com/wangzhenman/notifysentinel/server/database"
	"github.com/wangzhenman/notifysentinel/server/models"
	"github.com/wangzhenman/notifysentinel/server/push"
)

type EventRequest struct {
	Source string `json:"source" binding:"required"`
	Level string `json:"level" binding:"required"`
	Title string `json:"title" binding:"required"`
	Message string `json:"message"`
}

func SubmitEvent(pushManager *push.Manager) gin.HandlerFunc {
	return func(c *gin.Context) {
		var req EventRequest

		if err := c.ShouldBindJSON(&req); err != nil {
			c.JSON(
				http.StatusBadRequest,
				gin.H{
					"error": err.Error(),
				},
			)

			return
		}

		event := models.Event{
			Source: req.Source,
			Level: req.Level,
			Title: req.Title,
			Message: req.Message,
		}

		result := database.DB.Create(&event)

		if result.Error != nil {
			c.JSON(
				500,
				gin.H{
					"error": result.Error.Error(),
				},
			)

			return
		}

		var devices []models.Device

		database.DB.
			Where(
				"enabled = ?",
				true,
			).
			Find(&devices)

		for _, device := range devices {
			if err := pushManager.Send(
				device.Platform,
				device.Token,
				push.Message{
					Title: event.Title,
					Body: event.Message,
					Level: event.Level,
				},
			); err != nil {
				log.Printf(
					"push send failed for device=%d platform=%s: %v",
					device.ID,
					device.Platform,
					err,
				)
			}
		}

		c.JSON(
			http.StatusOK,
			gin.H{
				"status": "received",
				"id": event.ID,
				"time": time.Now(),
			},
		)
	}
}

func ListEvents(c *gin.Context) {
	var events []models.Event

	result := database.DB.
		Order("created_at desc").
		Find(&events)

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
		http.StatusOK,
		events,
	)
}