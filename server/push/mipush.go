package push

import (
	"fmt"
	"io"
	"net/http"
	"net/url"
	"os"
	"strings"
	"time"
)

const defaultMiPushEndpoint = "https://api.xmpush.xiaomi.com/v3/message/regid"

type MiPushProvider struct {
	appSecret   string
	packageName string
	channelID   string
	endpoint    string
	httpClient  *http.Client
}

func NewMiPushProviderFromEnv() (Provider, error) {
	appSecret := strings.TrimSpace(os.Getenv("MIPUSH_APP_SECRET"))
	packageName := strings.TrimSpace(os.Getenv("MIPUSH_PACKAGE_NAME"))

	if appSecret == "" && packageName == "" {
		return nil, nil
	}

	if appSecret == "" {
		return nil, fmt.Errorf("MIPUSH_APP_SECRET is required")
	}

	if packageName == "" {
		return nil, fmt.Errorf("MIPUSH_PACKAGE_NAME is required")
	}

	endpoint := strings.TrimSpace(os.Getenv("MIPUSH_ENDPOINT"))
	if endpoint == "" {
		endpoint = defaultMiPushEndpoint
	}

	channelID := strings.TrimSpace(os.Getenv("MIPUSH_CHANNEL_ID"))

	return &MiPushProvider{
		appSecret:   appSecret,
		packageName: packageName,
		channelID:   channelID,
		endpoint:    endpoint,
		httpClient: &http.Client{
			Timeout: 15 * time.Second,
		},
	}, nil
}

func (p *MiPushProvider) Name() string {
	return "mipush"
}

func (p *MiPushProvider) Send(
	token string,
	message Message,
) error {
	form := url.Values{}
	form.Set("registration_id", token)
	form.Set("restricted_package_name", p.packageName)
	form.Set("title", message.Title)
	form.Set("description", firstNonEmpty(message.Body, message.Title))
	form.Set("payload", buildPayload(message))
	form.Set("pass_through", "0")
	form.Set("notify_type", "-1")
	form.Set("notify_foreground", "1")

	if p.channelID != "" {
		form.Set("channel_id", p.channelID)
	}

	req,
	err := http.NewRequest(
		http.MethodPost,
		p.endpoint,
		strings.NewReader(form.Encode()),
	)

	if err != nil {
		return err
	}

	req.Header.Set(
		"Authorization",
		"key="+p.appSecret,
	)
	req.Header.Set(
		"Content-Type",
		"application/x-www-form-urlencoded",
	)

	resp,
	err := p.httpClient.Do(req)

	if err != nil {
		return err
	}

	defer resp.Body.Close()

	body,
	err := io.ReadAll(io.LimitReader(resp.Body, 4096))

	if err != nil {
		return err
	}

	if resp.StatusCode < http.StatusOK || resp.StatusCode >= http.StatusMultipleChoices {
		return fmt.Errorf(
			"mipush send failed: status=%d body=%s",
			resp.StatusCode,
			strings.TrimSpace(string(body)),
		)
	}

	return nil
}

func buildPayload(message Message) string {
	body := strings.TrimSpace(message.Body)
	level := strings.TrimSpace(message.Level)

	if body == "" && level == "" {
		return message.Title
	}

	if body == "" {
		return fmt.Sprintf("[%s] %s", level, message.Title)
	}

	if level == "" {
		return body
	}

	return fmt.Sprintf("[%s] %s", level, body)
}

func firstNonEmpty(values ...string) string {
	for _, value := range values {
		trimmed := strings.TrimSpace(value)
		if trimmed != "" {
			return trimmed
		}
	}

	return "NotifySentinel"
}