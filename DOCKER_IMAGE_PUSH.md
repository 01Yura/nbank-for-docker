# DOCKER-IMAGE-PUSH Setup

## Обзор

Настроен автоматический CI/CD пайплайн для проекта nbank-for-docker, который:

- Запускается только при изменениях в папке `src/`
- Выполняет сборку проекта с помощью Maven
- Проверяет стиль кода с помощью Checkstyle
- Собирает Docker-образ с тегом, равным commit hash
- Отправляет образ в Docker Hub (только для main/master ветки)

## Компоненты

### 1. Maven Checkstyle Plugin

Добавлен в `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.5.0</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <encoding>UTF-8</encoding>
        <consoleOutput>true</consoleOutput>
        <failsOnError>true</failsOnError>
        <linkXRef>false</linkXRef>
    </configuration>
    <executions>
        <execution>
            <id>validate</id>
            <phase>validate</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 2. Checkstyle Configuration

Файл `checkstyle.xml` содержит правила для проверки стиля кода:

- Максимальная длина строки: 120 символов
- Максимальная длина файла: 2000 строк
- Максимальная длина метода: 150 строк
- Проверка импортов, именования, отступов и других стандартов

### 3. GitHub Actions Workflow

Файл `.github/workflows/ci-cd.yml` содержит полный пайплайн:

#### Переменные окружения:

- `APIBASEURL: http://localhost:4111` - URL API сервиса
- `UIBASEURL: http://${{ steps.runner_ip.outputs.ip }}:80` - URL UI сервиса
- `UIREMOTE: http://localhost:4444/wd/hub` - URL Selenium Hub
- `REGISTRY: docker.io` - Docker Hub реестр
- `IMAGE_NAME: 01yura/nbank-tests` - Имя образа в Docker Hub

#### Триггеры:

- Push в main/master ветку с изменениями в `src/`
- Pull Request в main/master ветку с изменениями в `src/`
- Ручной запуск (workflow_dispatch)

#### Этапы:

1. **Checkout** - получение кода
2. **Setup JDK 21** - настройка Java
3. **Cache Maven** - кеширование зависимостей
4. **Get commit hash** - получение хеша коммита
5. **Detect runner IP** - определение IP адреса runner
6. **Setup HOST_PATH** - настройка переменных для docker-compose
7. **Setup services** - поднятие инфраструктуры (API, UI, Selenium)
8. **Build project** - сборка проекта с правильными переменными окружения
9. **Run Checkstyle** - проверка стиля кода с правильными переменными окружения
10. **Run tests** - запуск тестов с правильными переменными окружения
11. **Setup Docker Buildx** - настройка Docker
12. **Login to Docker Hub** - вход в Docker Hub (только для push)
13. **Build and push** - сборка и отправка образа

## Настройка

### 1. Docker Hub Secrets

Добавьте следующие секреты в настройках репозитория (Settings → Secrets and variables → Actions):

- `DOCKERHUB_USERNAME` - ваше имя пользователя Docker Hub
- `DOCKERHUB_TOKEN` - токен доступа Docker Hub

#### Как получить Docker Hub Token:

1. Войдите в Docker Hub
2. Перейдите в Account Settings → Security
3. Создайте новый Access Token
4. Скопируйте токен и добавьте его в секреты GitHub

### 2. Проверка настройки

После настройки секретов:

1. Сделайте изменения в папке `src/`
2. Запушьте изменения в main/master ветку
3. Проверьте выполнение workflow в GitHub Actions

### 3. Локальная проверка

Для локальной проверки Checkstyle:

```bash
./mvnw checkstyle:check
```

Для локальной сборки Docker-образа:

```bash
docker build -t 01yura/nbank-tests:latest .
```

## Особенности

### Условная отправка в Docker Hub

- **Push в main/master**: образ собирается и отправляется в Docker Hub
- **Pull Request**: образ только собирается (без отправки)
- **Другие ветки**: пайплайн не запускается

### Теги образов

- Основной тег: commit hash (например: `abc123def456`)
- Дополнительный тег: `latest` (только для main/master ветки)

### Кеширование

- Maven зависимости кешируются между запусками
- Docker слои кешируются для ускорения сборки

### Артефакты

После каждого запуска сохраняются:

- Результаты тестов (`target/surefire-reports/`)
- Результаты Checkstyle (`target/checkstyle-result.xml`)

## Устранение проблем

### Checkstyle ошибки

Если Checkstyle находит ошибки:

1. Исправьте стиль кода согласно правилам в `checkstyle.xml`
2. Или настройте правила в `checkstyle.xml` под ваши требования

### Docker Hub ошибки

1. Проверьте правильность секретов `DOCKERHUB_USERNAME` и `DOCKERHUB_TOKEN`
2. Убедитесь, что у токена есть права на push в репозиторий
3. Проверьте, что репозиторий в Docker Hub существует

### Maven ошибки

1. Проверьте, что все зависимости указаны в `pom.xml`
2. Убедитесь, что Java 21 установлена и доступна
3. Проверьте логи сборки в GitHub Actions

## Мониторинг

- Все результаты доступны в GitHub Actions
- Артефакты сохраняются на 30 дней
- Логи содержат подробную информацию о каждом этапе
