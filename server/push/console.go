package push

import "fmt"



type ConsoleProvider struct {}



func (c ConsoleProvider) Name() string {

	return "console"

}



func (c ConsoleProvider) Send(
	token string,
	message Message,
) error {


	fmt.Println(
		"====== PUSH ======",
	)

	fmt.Println(
		"Token:",
		token,
	)

	fmt.Println(
		"Title:",
		message.Title,
	)

	fmt.Println(
		"Body:",
		message.Body,
	)

	fmt.Println(
		"Level:",
		message.Level,
	)


	fmt.Println(
		"==================",
	)


	return nil

}