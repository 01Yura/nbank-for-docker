#!/bin/bash
# run-tests-with-docker-compose.sh
# Поднимает окружение, запускает тесты, затем гарантированно гасит docker-compose.
set -euo pipefail

# Пути: скрипт лежит в ./infra
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"   # .../infra
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"                   # корень проекта

# Выбор docker compose CLI (новый/старый)
compose_down() {
  echo "Останавливаем тестовое окружение..."
  (
    cd "${SCRIPT_DIR}"
    if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
      docker compose down
    else
      docker-compose down
    fi
  )
}

# Всегда гасим окружение при выходе (включая ошибки)
trap compose_down EXIT

echo "Поднимаем тестовое окружение..."
(
  cd "${SCRIPT_DIR}"
  ./start-docker-compose.sh
)

echo "Запускаем тесты..."
(
  cd "${ROOT_DIR}"
  ./run-tests.sh "$@"
)

# Успешное завершение: снимаем trap и гасим окружение вручную (чтобы увидеть сообщение "Готово.")
trap - EXIT
compose_down

echo "Готово."