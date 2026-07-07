package main


import (
	"log"
	"net/http"

	"github.com/gin-gonic/gin"

	"github.com/wangzhenman/notifysentinel/server/api"
	"github.com/wangzhenman/notifysentinel/server/database"
	"github.com/wangzhenman/notifysentinel/server/push"
)


func main(){


	// 初始化数据库
	database.Init()

	// 初始化 Push Manager
	pushManager := push.NewManager()

	// 注册推送 Provider
	pushManager.Register(
		push.ConsoleProvider{},
	)

	miPushProvider,
	err := push.NewMiPushProviderFromEnv()

	if err != nil {
		log.Printf(
			"mipush provider disabled: %v",
			err,
		)
	} else if miPushProvider != nil {
		pushManager.Register(miPushProvider)
		log.Printf("mipush provider enabled")
	}


	r := gin.Default()

	// Device API

	r.POST(
		"/api/devices/register",
		api.RegisterDevice,
	)


	r.GET(
		"/api/devices",
		api.ListDevices,
	)

	r.GET(
		"/api/devices/me",
		api.GetCurrentDevice,
	)


	r.DELETE(
		"/api/devices/:id",
		api.DeleteDevice,
	)

	/*
		健康检查
	*/
	r.GET("/health",func(c *gin.Context){

		c.JSON(http.StatusOK,gin.H{

			"name":"NotifySentinel",

			"status":"ok",

		})

	})





	/*
		提交事件
	*/
	r.POST("/api/events", api.SubmitEvent(pushManager))





	/*
		查询历史事件
	*/
	r.GET("/api/events", api.ListEvents)





	/*
		启动服务
	*/

	r.Run(":8080")

}