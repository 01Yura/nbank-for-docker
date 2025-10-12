#!/bin/bash

# Скрипт для извлечения Docker образов из docker-compose.yml
# Использует yq для парсинга YAML файла

# Проверяем наличие yq
if ! command -v yq &> /dev/null; then
    echo "yq не найден. Устанавливаем..."
    # Устанавливаем yq для парсинга YAML
    wget -qO /usr/local/bin/yq https://github.com/mikefarah/yq/releases/latest/download/yq_linux_amd64
    chmod +x /usr/local/bin/yq
fi

# Путь к docker-compose.yml
COMPOSE_FILE="infra/docker-compose.yml"

# Проверяем существование файла
if [ ! -f "$COMPOSE_FILE" ]; then
    echo "Файл $COMPOSE_FILE не найден!"
    exit 1
fi

# Извлекаем все образы из docker-compose.yml
echo "🐳 Docker Images:"
yq eval '.services[].image' "$COMPOSE_FILE" | while read -r image; do
    if [ -n "$image" ] && [ "$image" != "null" ]; then
        # Извлекаем имя образа (убираем тег)
        image_name=$(echo "$image" | cut -d':' -f1)
        # Создаем ссылку на Docker Hub
        echo "• <a href='https://hub.docker.com/r/$image_name'>$image</a>"
    fi
done