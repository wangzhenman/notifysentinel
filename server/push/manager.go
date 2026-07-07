package push



type Manager struct {


	providers map[string]Provider

}



func NewManager() *Manager {


	return &Manager{

		providers: make(map[string]Provider),

	}

}




func (m *Manager) Register(
	p Provider,
){

	m.providers[p.Name()] = p

}




func (m *Manager) Send(
	platform string,
	token string,
	message Message,
) error {


	provider,
	ok := m.providers[platform]



	if !ok {

		return nil
	}



	return provider.Send(
		token,
		message,
	)

}