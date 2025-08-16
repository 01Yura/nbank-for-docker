# 🚀 Быстрый старт CI/CD

## Что настроено

✅ **Maven Checkstyle Plugin** - добавлен в `pom.xml`  
✅ **Checkstyle Configuration** - создан `checkstyle.xml`  
✅ **GitHub Actions Workflows** - созданы `.github/workflows/`  
✅ **Docker Image Push Pipeline** - автоматическая сборка и публикация  
✅ **Telegram Notifications** - уведомления о результатах CI/CD

## Настройка

### 1. Добавьте секреты в GitHub

Перейдите в **Settings → Secrets and variables → Actions** и добавьте:

- `DOCKERHUB_USERNAME` - ваше имя пользователя Docker Hub
- `DOCKERHUB_TOKEN` - токен доступа Docker Hub
- `TELEGRAM_BOT_TOKEN` - токен вашего Telegram бота
- `TELEGRAM_CHAT_ID` - ID чата для уведомлений

### 2. Получите Docker Hub Token

1. Войдите в [Docker Hub](https://hub.docker.com)
2. Перейдите в **Account Settings → Security**
3. Создайте новый **Access Token**
4. Скопируйте токен в секреты GitHub

### 3. Настройте Telegram Bot

1. Создайте бота через [@BotFather](https://t.me/botfather)
2. Получите токен бота
3. Добавьте бота в нужный чат
4. Получите ID чата (можно использовать [@userinfobot](https://t.me/userinfobot))

## Тестирование

### Локальная проверка

```bash
# Проверка Checkstyle
./mvnw checkstyle:check

# Сборка Docker-образа
docker build -t 01yura/nbank-tests:latest .

# Запуск тестов локально
./mvnw test

# Запуск тестов через Docker
./run-tests.sh api
```

### Запуск пайплайна

1. Сделайте изменения в папке `src/`
2. Запушьте в main/master ветку
3. Проверьте выполнение в **GitHub Actions**

## Доступные Workflows

### 🐳 Docker Image Push Pipeline

- **Файл**: `.github/workflows/docker-image-push.yml`
- **Запуск**: при изменениях в `src/` в main/master ветке
- **Действия**: сборка, тестирование, публикация Docker-образа
- **Уведомления**: Telegram о результатах

### 🧪 Run Tests Pipeline

- **Файл**: `.github/workflows/run-tests.yml`
- **Запуск**: при любых push или вручную
- **Действия**: поднятие окружения, запуск тестов
- **Уведомления**: Telegram о результатах тестов

## Особенности

🔍 **Запускается только** при изменениях в `src/`  
🏗️ **Собирает проект** с помощью Maven  
✅ **Проверяет стиль кода** с помощью Checkstyle  
🐳 **Собирает Docker-образ** с тегом commit hash  
📤 **Отправляет в Docker Hub** (только main/master)  
📱 **Уведомляет в Telegram** о результатах  
🚀 **Поднимает тестовое окружение** автоматически

## Полезные команды

```bash
# Проверить статус
git status

# Посмотреть логи
git log --oneline -5

# Локальная проверка
./mvnw clean compile test checkstyle:check

# Запуск тестов в Docker
./run-tests.sh api
./run-tests.sh ui

# Полный цикл с окружением
cd infra && ./run-tests-with-docker-compose.sh
```

## Структура CI/CD

```
.github/workflows/
├── docker-image-push.yml    # Основной пайплайн (сборка + публикация)
└── run-tests.yml           # Пайплайн для тестирования
```

## Мониторинг

- **GitHub Actions**: детальные логи выполнения
- **Docker Hub**: опубликованные образы
- **Telegram**: уведомления о результатах
- **Артефакты**: результаты тестов и Checkstyle

## Поддержка

📚 **Подробная документация**: `DOCKER_IMAGE_PUSH.md`  
🐛 **Проблемы**: проверьте логи в GitHub Actions  
💬 **Telegram**: настройте уведомления для быстрого мониторинга

---

**Готово! CI/CD пайплайн настроен и готов к использованию.** 🎉

**Дополнительные возможности:**

- Автоматическое поднятие тестового окружения
- Параллельное выполнение тестов
- Кеширование зависимостей
- Уведомления в Telegram
- Публикация в Docker Hub
