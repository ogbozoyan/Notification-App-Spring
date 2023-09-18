# Environment

## Telegram Bot Svc

### Mail

| KEY        | VALUE                    |
|------------|--------------------------|
| MAIL_LOGIN | your_email@email.ru      |
| MAIL_PASS  | your_email_smtp_app_pass |

### Bot

| KEY   | VALUE    |
|-------|----------|
| NAME  | BotName  |
| TOKEN | botToken |

## Centrobank Svc

### Feign client

| KEY       | VALUE                                                |
|-----------|------------------------------------------------------|
| API_TOKEN | need to get from https://api.freecurrencyapi.com/v1/ |


## Build
1. docker compose up
2. mvn clean package
