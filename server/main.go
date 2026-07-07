package main

import (
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
)

type Event struct {
	Source  string `json:"source" binding:"required"`
	Level   string `json:"level" binding:"required"`
	Title   string `json:"title" binding:"required"`
	Message string `json:"message"`
}

type Response struct {
	Status string `json:"status"`
	Time   string `json:"time"`
}

func main() {

	r := gin.Default()

	// 健康检查
	r.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"status": "ok",
			"name":   "NotifySentinel",
		})
	})

	// 接收告警事件
	r.POST("/api/events", func(c *gin.Context) {

		var event Event

		if err := c.ShouldBindJSON(&event); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": err.Error(),
			})
			return
		}

		// 当前先打印
		// 后续这里接：
		// 1. 保存数据库
		// 2. 判断规则
		// 3. 调用 Push Provider

		println("====== NotifySentinel Event ======")
		println("Source:", event.Source)
		println("Level :", event.Level)
		println("Title :", event.Title)
		println("Message:", event.Message)
		println("==================================")

		c.JSON(http.StatusOK, Response{
			Status: "received",
			Time:   time.Now().Format(time.RFC3339),
		})
	})


	// 启动服务
	r.Run(":8080")
}