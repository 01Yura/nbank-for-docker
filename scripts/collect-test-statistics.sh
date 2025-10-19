#!/bin/bash

# Скрипт для сбора статистики тестов из Allure отчета и Swagger coverage

# Инициализация переменных
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
SUCCESS_RATE=0
API_COVERAGE=0

# Путь к результатам Allure
ALLURE_RESULTS_DIR="target/allure-results"

# Путь к Swagger coverage отчету
SWAGGER_COVERAGE_FILE="swagger-coverage-report.html"

echo "=== Collecting test statistics ==="

# 1. Парсинг статистики из Allure результатов
if [ -d "$ALLURE_RESULTS_DIR" ]; then
    echo "Processing Allure results from $ALLURE_RESULTS_DIR"

    # Подсчет тестов из *-result.json файлов
    for file in "$ALLURE_RESULTS_DIR"/*-result.json; do
        if [ -f "$file" ]; then
            TOTAL_TESTS=$((TOTAL_TESTS + 1))

            # Проверяем статус теста (passed, failed, broken, skipped)
            status=$(grep -o '"status":"[^"]*"' "$file" | head -1 | cut -d'"' -f4)

            if [ "$status" = "passed" ]; then
                PASSED_TESTS=$((PASSED_TESTS + 1))
            elif [ "$status" = "failed" ] || [ "$status" = "broken" ]; then
                FAILED_TESTS=$((FAILED_TESTS + 1))
            fi
        fi
    done

    # Вычисление процента успеха
    if [ $TOTAL_TESTS -gt 0 ]; then
        SUCCESS_RATE=$(awk "BEGIN {printf \"%.1f\", ($PASSED_TESTS / $TOTAL_TESTS) * 100}")
    fi

    echo "Total tests found: $TOTAL_TESTS"
    echo "Passed: $PASSED_TESTS"
    echo "Failed: $FAILED_TESTS"
    echo "Success rate: $SUCCESS_RATE%"
else
    echo "Warning: Allure results directory not found at $ALLURE_RESULTS_DIR"
fi

# 2. Парсинг API coverage из Swagger coverage отчета
if [ -f "$SWAGGER_COVERAGE_FILE" ]; then
    echo "Processing Swagger coverage from $SWAGGER_COVERAGE_FILE"

    # Метод 1: Ищем процент покрытия в формате "XX% covered"
    coverage_line=$(grep -o '[0-9]\+% covered' "$SWAGGER_COVERAGE_FILE" | head -1 | grep -o '[0-9]\+')

    if [ -n "$coverage_line" ]; then
        API_COVERAGE=$coverage_line
        echo "API Coverage: $API_COVERAGE%"
    else
        # Метод 2: Ищем в тексте "Full coverage"
        coverage_line=$(grep -i "full coverage" "$SWAGGER_COVERAGE_FILE" | grep -o '[0-9]\+%' | head -1 | grep -o '[0-9]\+')

        if [ -n "$coverage_line" ]; then
            API_COVERAGE=$coverage_line
            echo "API Coverage: $API_COVERAGE%"
        else
            # Метод 3: Ищем любое упоминание процента покрытия
            coverage_line=$(grep -o 'coverage.*[0-9]\+%' "$SWAGGER_COVERAGE_FILE" | grep -o '[0-9]\+%' | head -1 | grep -o '[0-9]\+')

            if [ -n "$coverage_line" ]; then
                API_COVERAGE=$coverage_line
                echo "API Coverage: $API_COVERAGE%"
            else
                # Метод 4: Подсчет из swagger-coverage-output (если доступен)
                if [ -d "target/swagger-coverage-output" ]; then
                    # Подсчитываем количество вызванных эндпоинтов
                    called_endpoints=$(find target/swagger-coverage-output -name "*.json" -type f | wc -l)
                    if [ $called_endpoints -gt 0 ]; then
                        API_COVERAGE="~$called_endpoints endpoints"
                    else
                        API_COVERAGE="0"
                    fi
                else
                    echo "Warning: Could not extract API coverage from report"
                    API_COVERAGE="N/A"
                fi
            fi
        fi
    fi
else
    echo "Warning: Swagger coverage file not found at $SWAGGER_COVERAGE_FILE"
    API_COVERAGE="N/A"
fi

# 3. Сохранение статистики в файл для использования в GitHub Actions
STATS_FILE="test-statistics.txt"

cat > "$STATS_FILE" << EOF
📊 Test Statistics:
━━━━━━━━━━━━━━━━━━
📝 Total tests: $TOTAL_TESTS
✅ Passed: $PASSED_TESTS
❌ Failed: $FAILED_TESTS
📈 Success rate: $SUCCESS_RATE%
🔌 API coverage: not configured yet :(
EOF

echo ""
echo "=== Statistics saved to $STATS_FILE ==="
cat "$STATS_FILE"

# 4. Экспорт переменных для GitHub Actions (если запущено в CI)
if [ -n "$GITHUB_OUTPUT" ]; then
    echo "total_tests=$TOTAL_TESTS" >> $GITHUB_OUTPUT
    echo "passed_tests=$PASSED_TESTS" >> $GITHUB_OUTPUT
    echo "failed_tests=$FAILED_TESTS" >> $GITHUB_OUTPUT
    echo "success_rate=$SUCCESS_RATE" >> $GITHUB_OUTPUT
    echo "api_coverage=$API_COVERAGE" >> $GITHUB_OUTPUT
    echo "Statistics exported to GITHUB_OUTPUT"
fi

exit 0