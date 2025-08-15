#!/bin/bash

###############################################################################
# Скрипт собирает и пушит Docker-образ в Docker Hub, используя токен из .env-for-push-test
# Работает на Linux, macOS и Windows (Git Bash)
#
# 1. Создайте файл .env-for-push-test в той же папке:
#    DOCKERHUB_USERNAME=ваш_логин
#    DOCKERHUB_TOKEN=ваш_токен
#    IMAGE_NAME=nbank-tests
#    TAG=latest
#
# 2. Сделайте скрипт исполняемым: chmod +x push-tests.sh
# 3. Запустите: ./push-tests.sh
###############################################################################

# Загружаем переменные окружения из .env-for-push-test
if [ -f ".env-for-push-test" ]; then
    echo ">>> Загружаю переменные из .env-for-push-test"
    export $(grep -v '^#' .env-for-push-test | sed 's/\r$//' | xargs)
else
    echo "❌ Файл .env-for-push-test не найден! Создайте его рядом со скриптом."
    sleep 10
    exit 1
fi

# Проверка переменных
if [ -z "$DOCKERHUB_USERNAME" ] || [ -z "$DOCKERHUB_TOKEN" ] || [ -z "$IMAGE_NAME" ] || [ -z "$TAG" ]; then
    echo "❌ В .env-for-push-test должны быть заданы DOCKERHUB_USERNAME, DOCKERHUB_TOKEN, IMAGE_NAME, TAG!"
    sleep 10
    exit 1
fi

# Сборка Docker-образа
echo ">>> Сборка Docker-образа $IMAGE_NAME (host network + BuildKit)..."
DOCKER_BUILDKIT=1 docker build --network=host -t "$IMAGE_NAME" . \
  || { echo "❌ Ошибка при сборке образа!"; sleep 10; exit 1; }

# Логин в Docker Hub
echo ">>> Логин в Docker Hub..."
echo "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USERNAME" --password-stdin \
    || { echo "❌ Не удалось залогиниться!"; sleep 10; exit 1; }

# Тегирование образа
echo ">>> Тегирование образа..."
docker tag "$IMAGE_NAME" "$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG" \
    || { echo "❌ Ошибка при тегировании!"; sleep 10; exit 1; }

# Отправка образа
echo ">>> Отправка образа в Docker Hub..."
docker push "$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"
if [ $? -eq 0 ]; then
    echo "✅ Готово! Образ доступен как:"
    echo "docker pull $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"
else
    echo "❌ Ошибка при отправке образа!"
    sleep 10
    exit 1
fi

# Пауза, чтобы увидеть результат
echo ">>> Скрипт завершён. Окно закроется через 10 секунд..."
sleep 10