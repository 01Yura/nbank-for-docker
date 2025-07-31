#!/bin/bash

# Скрипт запускает Docker контейнер с нужными переменными и монтированием папок
# Все артефакты сохраняются в ./test-output/<timestamp>
set -e

# Настройки
IMAGE_NAME="nbank-tests"                  # Имя образа
APIBASEURL="http://192.168.0.22:4111"     # API URL
UIBASEURL="http://192.168.0.22:3000"      # UI URL

# Проверяем аргумент
TEST_PROFILE="$1"  # первый аргумент скрипта
if [[ "$TEST_PROFILE" != "ui" && "$TEST_PROFILE" != "api" ]]; then
    echo "❗ Вы не указали профиль тестов (ui или api)."
    echo "Пример запуска:"
    echo "  ./run-tests.sh ui"
    echo "  ./run-tests.sh api"
    echo
    read -n 1 -s -r -p "Нажмите любую клавишу для выхода..."
    echo
    exit 1
fi

# Общая папка для всех прогонов
BASE_REPORT_DIR="test-output"

# Генерируем уникальную папку с таймстемпом
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
RUN_DIR="$BASE_REPORT_DIR/$TIMESTAMP"

# Создаём структуру папок для этого прогона
mkdir -p "$RUN_DIR/logs"
mkdir -p "$RUN_DIR/reports"
mkdir -p "$RUN_DIR/surefire-reports"

echo "=== Запуск Docker контейнера для профиля: $TEST_PROFILE ==="

# Определяем корректный путь проекта (для Windows и Linux)
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" || "$OSTYPE" == "cygwin" ]]; then
    PROJECT_DIR=$(pwd -W 2>/dev/null || pwd)
    PROJECT_DIR=$(echo "$PROJECT_DIR" | sed 's#\\#/#g')
else
    PROJECT_DIR=$(pwd)
fi

docker run --rm \
  -v "$PROJECT_DIR/$RUN_DIR/logs:/app/logs" \
  -v "$PROJECT_DIR/$RUN_DIR/reports:/app/target/reports" \
  -v "$PROJECT_DIR/$RUN_DIR/surefire-reports:/app/target/surefire-reports" \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL="$APIBASEURL" \
  -e UIBASEURL="$UIBASEURL" \
  "$IMAGE_NAME"

echo "=== Контейнер завершил работу ==="

# Вывод итоговой информации
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

# Ожидание нажатия клавиши
read -n 1 -s -r -p "Нажмите любую клавишу для выхода..."
echo
