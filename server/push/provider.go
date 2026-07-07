package push


type Message struct {

	Title string

	Body string

	Level string

}



type Provider interface {


	// 名称
	Name() string


	// 发送消息
	Send(
		token string,
		message Message,
	) error

}