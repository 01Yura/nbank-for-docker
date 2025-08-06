# 🚀 Быстрый старт CI/CD

## Что настроено

✅ **Maven Checkstyle Plugin** - добавлен в `pom.xml`  
✅ **Checkstyle Configuration** - создан `checkstyle.xml`  
✅ **GitHub Actions Workflow** - создан `.github/workflows/ci-cd.yml`  
✅ **Документация** - создан `CI_CD_SETUP.md`

## Настройка

### 1. Добавьте секреты в GitHub

Перейдите в **Settings → Secrets and variables → Actions** и добавьте:

- `DOCKERHUB_USERNAME` - ваше имя пользователя Docker Hub
- `DOCKERHUB_TOKEN` - токен доступа Docker Hub

### 2. Получите Docker Hub Token

1. Войдите в [Docker Hub](https://hub.docker.com)
2. Перейдите в **Account Settings → Security**
3. Создайте новый **Access Token**
4. Скопируйте токен в секреты GitHub

## Тестирование

### Локальная проверка

```bash
# Проверка Checkstyle
./mvnw checkstyle:check

# Сборка Docker-образа
docker build -t your-username/nbank-for-docker:latest .
```

### Запуск пайплайна

1. Сделайте изменения в папке `src/`
2. Запушьте в main/master ветку
3. Проверьте выполнение в **GitHub Actions**

## Особенности

🔍 **Запускается только** при изменениях в `src/`  
🏗️ **Собирает проект** с помощью Maven  
✅ **Проверяет стиль кода** с помощью Checkstyle  
🐳 **Собирает Docker-образ** с тегом commit hash  
📤 **Отправляет в Docker Hub** (только main/master)

## Полезные команды

```bash
# Проверить статус
git status

# Посмотреть логи
git log --oneline -5

# Локальная проверка
./mvnw clean compile test checkstyle:check
```

## Поддержка

📚 **Подробная документация**: `CI_CD_SETUP.md`  
🐛 **Проблемы**: проверьте логи в GitHub Actions

---

**Готово! CI/CD пайплайн настроен и готов к использованию.** 🎉
