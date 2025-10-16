#!/bin/bash

## ============================================================================
# Скрипт скачивает и запускает Docker-контейнер с автотестами (образ 01yura/nbank-tests) для приложения NBank
# с передачей профиля тестов (api, ui или оба) и IP хоста на котором подняты фронт и бэк.
#
# Возможности скрипта:
# 1. Определяет IP текущего хоста (Linux, macOS, Windows через Git Bash/WSL)
#
# 2. Может загрузить переменные APIBASEURL и UIBASEURL из файла .env-for-run-tests, если приложение Nbank поднято не локально
#    ────────────────────────────────────────────────────────────────────────
#    Если фронтенд и бэкенд запущены на ДРУГОЙ машине (например, на DEV/TEST
#    сервере в сети или на облачном хостинге), вы можете создать файл
#    `.env-for-run-tests` рядом со скриптом и указать в нём свои адреса:
#
#        APIBASEURL=http://194.87.199.75:4111
#        UIBASEURL=http://194.87.199.75
#
#    Скрипт автоматически подхватит эти значения и будет использовать их
#    вместо вычисленного локального IP. Это удобно для запуска тестов на
#    удалённом окружении, а не локально.
#
# 3. Можно ПЕРЕДАТЬ переменные прямо при запуске скрипта:
#
#        ./run-tests.sh api APIBASEURL=http://x:4111 UIBASEURL=http://x
#        ./run-tests.sh --apibaseurl=http://x:4111 --uibaseurl=http://x ui
#
#    В первом примере используется форма KEY=VALUE, во втором — флаги.
#    Профиль (ui|api) можно указывать в любом месте среди аргументов.
#
# 4. Если файл .env-for-run-tests отсутствует и переменные не переданы, скрипт
#    сам формирует APIBASEURL и UIBASEURL по локальному IP текущего компьютера:
#
#        APIBASEURL=http://<локальный_IP>:4111
#        UIBASEURL=http://<локальный_IP>
#
# 5. Монтирует директории для логов и отчётов в ./test-output/<timestamp>
#
# 6. После завершения контейнера выводит пути к логам и отчётам
#
# Использование:
#    ./run-tests.sh [profile] [APIBASEURL=...] [UIBASEURL=...] [--apibaseurl=...] [--uibaseurl=...]
#
# Профиль [profile]:
#   - ui   — запуск только UI тестов
#   - api  — запуск только API тестов
#   - не указан — будут запущены все тесты
#
# Примеры запуска:
#    ./run-tests.sh                                                       # Запуск всех тестов
#    ./run-tests.sh api                                                   # Запуск только API тестов
#    ./run-tests.sh ui                                                    # Запуск только UI тестов
#    ./run-tests.sh api APIBASEURL=http://x:4111                          # Запуск api тестов на определенном окружении переданном прямо в команде запуска
#    ./run-tests.sh --apibaseurl=http://x:4111 --uibaseurl=http://x ui    # Запуск ui тестов на определенном окружении переданном прямо в команде запуска
# ============================================================================

set -e

IMAGE_NAME="01yura/nbank-tests"

# Проверяем наличие Docker CLI
if ! command -v docker >/dev/null 2>&1; then
    echo "❗ Docker не найден. Установите Docker Desktop / Docker и повторите попытку."
    sleep 3
    exit 1
fi

# Проверяем, что Docker daemon запущен
if ! docker info >/dev/null 2>&1; then
    echo "❗ Docker daemon не запущен. Пожалуйста, запустите Docker Desktop и повторите."
    sleep 3
    exit 1
fi

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

# Разбираем аргументы командной строки: профиль и переопределения базовых URL
TEST_PROFILE=""
for arg in "$@"; do
    case "$arg" in
        ui|api)
            TEST_PROFILE="$arg"
            ;;
        --profile=*)
            TEST_PROFILE="${arg#*=}"
            ;;
        --apibaseurl=*)
            APIBASEURL="${arg#*=}"
            ;;
        --uibaseurl=*)
            UIBASEURL="${arg#*=}"
            ;;
        APIBASEURL=*)
            APIBASEURL="${arg#*=}"
            ;;
        UIBASEURL=*)
            UIBASEURL="${arg#*=}"
            ;;
        *)
            ;;
    esac
done

# Если APIBASEURL и UIBASEURL не заданы явно, формируем их автоматически по локальному IP
APIBASEURL="${APIBASEURL:-http://$HOST_IP:4111}"
UIBASEURL="${UIBASEURL:-http://$HOST_IP}"

# Проверяем профиль
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
  "$IMAGE_NAME" || { echo "❗ Не удалось запустить контейнер Docker. Проверьте, что Docker запущен и доступен."; sleep 3; exit 1; }


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