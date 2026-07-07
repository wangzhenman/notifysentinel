package main


import (
	"net/http"
	"time"

	"github.com/gin-gonic/gin"

	"github.com/wangzhenman/notifysentinel/server/database"
	"github.com/wangzhenman/notifysentinel/server/models"
)



type EventRequest struct {

	Source string `json:"source" binding:"required"`

	Level string `json:"level" binding:"required"`

	Title string `json:"title" binding:"required"`

	Message string `json:"message"`
}



func main(){


	// 初始化数据库
	database.Init()



	r := gin.Default()



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
	r.POST("/api/events",func(c *gin.Context){


		var req EventRequest



		if err:=c.ShouldBindJSON(&req);err!=nil{


			c.JSON(
				http.StatusBadRequest,
				gin.H{
					"error":err.Error(),
				},
			)

			return
		}




		event:=models.Event{

			Source:req.Source,

			Level:req.Level,

			Title:req.Title,

			Message:req.Message,
		}



		// 保存数据库

		result:=database.DB.Create(&event)



		if result.Error!=nil{

			c.JSON(
				500,
				gin.H{
					"error":result.Error.Error(),
				},
			)

			return
		}




		c.JSON(
			http.StatusOK,
			gin.H{

				"status":"received",

				"id":event.ID,

				"time":time.Now(),
			},
		)

	})





	/*
		查询历史事件
	*/
	r.GET("/api/events",func(c *gin.Context){



		var events []models.Event



		result:=database.DB.
			Order("created_at desc").
			Find(&events)




		if result.Error!=nil{

			c.JSON(
				500,
				gin.H{
					"error":result.Error.Error(),
				},
			)

			return

		}




		c.JSON(
			http.StatusOK,
			events,
		)

	})





	/*
		启动服务
	*/

	r.Run(":8080")

}