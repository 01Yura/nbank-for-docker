#!/bin/bash

## ============================================================================
# Скрипт запускает Docker-контейнер с автотестами (образ 01yura/nbank-tests)
# с передачей профиля тестов и IP хоста на котором подняты фронт и бэк.
#
# Возможности скрипта:
# 1. Определяет IP текущего хоста (Linux, macOS, Windows через Git Bash/WSL)
#
# 2. Может загрузить переменные APIBASEURL и UIBASEURL из файла .env-for-run-tests
#    ────────────────────────────────────────────────────────────────────────
#    Если фронтенд и бэкенд запущены на ДРУГОЙ машине (например, на DEV/TEST
#    сервере в сети или на облачном хостинге), вы можете создать файл
#    `.env-for-run-tests` рядом со скриптом и указать в нём свои адреса:
#
#        APIBASEURL=http://192.168.1.50:4111
#        UIBASEURL=http://192.168.1.50
#
#    Скрипт автоматически подхватит эти значения и будет использовать их
#    вместо вычисленного локального IP. Это удобно для запуска тестов на
#    удалённые окружения.
#
# 3. Если файл .env-for-run-tests отсутствует или в нём не заданы переменные, скрипт сам формирует
#    APIBASEURL и UIBASEURL по локальному IP текущего компьютера:
#
#        APIBASEURL=http://<локальный_IP>:4111
#        UIBASEURL=http://<локальный_IP>
#
# 4. Монтирует директории для логов и отчётов в ./test-output/<timestamp>
#
# 5. После завершения контейнера выводит пути к логам и отчётам
#
# Использование:
#    ./run-tests.sh [profile]
#
# Аргумент [profile] может быть:
#   - ui   — запуск только UI тестов
#   - api  — запуск только API тестов
#   - не указан — будут запущены все тесты
#
# Примеры:
#    ./run-tests.sh          # Запуск всех тестов
#    ./run-tests.sh api      # Запуск только API тестов
#    ./run-tests.sh ui       # Запуск только UI тестов
# ============================================================================

set -e

IMAGE_NAME="01yura/nbank-tests"

# Загружаем переменные из .env-for-run-tests, если файл существует
if [ -f ".env-for-run-tests" ]; then
    echo ">>> Загружаю переменные из .env-for-run-tests"
    export $(grep -v '^#' .env-for-run-tests | sed 's/\r$//' | xargs)
fi

# Определяем IP хоста
if [[ "$OSTYPE" == "linux-gnu"* || "$OSTYPE" == "darwin"* ]]; then
    HOST_IP=$(hostname -I 2>/dev/null | awk '{print $1}')
elif [[ "$OSTYPE" == "msys"* || "$OSTYPE" == "cygwin"* || "$OSTYPE" == "win32" ]]; then
    HOST_IP=$(ipconfig | grep "IPv4" | head -n 1 | awk '{print $NF}')
else
    echo "Не удалось определить IP хоста, используем 127.0.0.1"
    HOST_IP="127.0.0.1"
fi

# Если APIBASEURL и UIBASEURL не заданы через .env, формируем их автоматически
APIBASEURL="${APIBASEURL:-http://$HOST_IP:4111}"
UIBASEURL="${UIBASEURL:-http://$HOST_IP}"

# Проверяем аргумент профиля
TEST_PROFILE="$1"
if [[ -z "$TEST_PROFILE" ]]; then
    echo "❗ Профиль не указан, будут запущены ВСЕ тесты"
    TEST_PROFILE="all"
elif [[ "$TEST_PROFILE" != "ui" && "$TEST_PROFILE" != "api" ]]; then
    echo "❗ Некорректный профиль: $TEST_PROFILE"
    echo "Доступные варианты: ui, api"
    read -n 1 -s -r -p "Нажмите любую клавишу для выхода..."
    echo
    exit 1
fi

BASE_REPORT_DIR="test-output"
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
RUN_DIR="$BASE_REPORT_DIR/$TIMESTAMP"

mkdir -p "$RUN_DIR/logs" "$RUN_DIR/reports" "$RUN_DIR/surefire-reports"

echo "=== Запуск Docker контейнера для профиля: $TEST_PROFILE ==="
echo "Используем:"
echo "  APIBASEURL = $APIBASEURL"
echo "  UIBASEURL  = $UIBASEURL"

# Определяем корректный путь проекта
if [[ "$OSTYPE" == "msys"* || "$OSTYPE" == "win32"* || "$OSTYPE" == "cygwin"* ]]; then
    PROJECT_DIR=$(pwd -W 2>/dev/null || pwd)
    PROJECT_DIR=$(echo "$PROJECT_DIR" | sed 's#\\#/#g')
else
    PROJECT_DIR=$(pwd)
fi

docker run --rm \
  --network=host \
  -v "$HOME/.m2:/root/.m2" \
  -v "$PROJECT_DIR/$RUN_DIR/logs:/app/logs" \
  -v "$PROJECT_DIR/$RUN_DIR/reports:/app/target/reports" \
  -v "$PROJECT_DIR/$RUN_DIR/surefire-reports:/app/target/surefire-reports" \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL="$APIBASEURL" \
  -e UIBASEURL="$UIBASEURL" \
  "$IMAGE_NAME"


echo "=== Контейнер завершил работу ==="

echo
echo "================ РЕЗУЛЬТАТЫ ТЕСТОВ ================"
echo "Лог-файлы:       $RUN_DIR/logs"
echo "Отчёты:          $RUN_DIR/reports"
echo "Surefire отчёты: $RUN_DIR/surefire-reports"
if [ -f "$RUN_DIR/logs/run.log" ]; then
    echo "Последний лог:   $RUN_DIR/logs/run.log"
fi
echo "==================================================="
echo

read -n 1 -s -r -p "Нажмите любую клавишу для выхода..."
echo 