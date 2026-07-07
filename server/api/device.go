package api


import (
	"net/http"

	"github.com/gin-gonic/gin"

	"github.com/wangzhenman/notifysentinel/server/database"
	"github.com/wangzhenman/notifysentinel/server/models"
)



type DeviceRegisterRequest struct {


	Name string `json:"name" binding:"required"`


	Platform string `json:"platform" binding:"required"`


	Token string `json:"token" binding:"required"`

}



// 注册设备

func RegisterDevice(c *gin.Context){


	var req DeviceRegisterRequest



	if err:=c.ShouldBindJSON(&req);err!=nil{


		c.JSON(
			http.StatusBadRequest,
			gin.H{
				"error":err.Error(),
			},
		)

		return
	}




	device:=models.Device{

		Name:req.Name,

		Platform:req.Platform,

		Token:req.Token,

		Enabled:true,
	}



	result:=database.DB.Create(&device)



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
		200,
		device,
	)

}





// 获取设备列表

func ListDevices(c *gin.Context){


	var devices []models.Device



	result:=database.DB.Find(&devices)



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
		200,
		devices,
	)

}





// 删除设备

func DeleteDevice(c *gin.Context){



	id:=c.Param("id")



	result:=database.DB.Delete(
		&models.Device{},
		id,
	)



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
		200,
		gin.H{
			"status":"deleted",
		},
	)

}