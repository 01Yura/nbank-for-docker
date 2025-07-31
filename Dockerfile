# Используем официальный образ Maven с Java 21
FROM maven:3.9.11-eclipse-temurin-21

# Аргументы по умолчанию
ARG TEST_PROFILE=api
ARG APIBASEURL=http://localhost:4111
ARG UIBASEURL=http://localhost:3000

# Переменные окружения (будут доступны в runtime)
ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}

# Рабочая директория
WORKDIR /app

# Сначала копируем только pom.xml и загружаем зависимости (для кеширования)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Потом копируем всё остальное (исходный код, тесты и т.д.)
COPY . .

# Убедимся, что у нас есть права
USER root

# Команда по умолчанию: запуск тестов + логирование + генерация отчёта
CMD bash -c '\
  mkdir -p /app/logs && \
  echo ">>> Running tests with profile: $TEST_PROFILE" | tee /app/logs/run.log && \
  mvn test -q -P $TEST_PROFILE 2>&1 | tee -a /app/logs/run.log && \
  echo ">>> Generating surefire report" | tee -a /app/logs/run.log && \
  mvn -DskipTests=true surefire-report:report 2>&1 | tee -a /app/logs/run.log \
'
