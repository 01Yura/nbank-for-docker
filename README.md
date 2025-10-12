# NBank API & UI Testing Framework

Автоматизированный фреймворк для тестирования API и UI банковской системы NBank, построенный на Java 21, REST Assured, Selenide и JUnit 5. Фреймворк обеспечивает комплексное тестирование банковских операций, включая создание пользователей, переводы денег, депозиты и управление счетами.

## 🎯 Назначение проекта

Фреймворк предназначен для автоматизированного тестирования банковского приложения NBank, которое включает:

- **Backend API** - REST API для банковских операций
- **Frontend UI** - веб-интерфейс для пользователей и администраторов
- **База данных** - PostgreSQL для хранения данных

## 🏗️ Архитектура проекта

Проект построен по модульному принципу с разделением на API и UI тестирование, что обеспечивает:

- Переиспользование компонентов между тестами
- Независимое выполнение API и UI тестов
- Легкое расширение функциональности
- Поддержку различных браузеров и окружений

### 📁 Структура проекта

```
src/
├── main/java/
│   ├── api/                    # API тестирование
│   │   ├── configs/           # Конфигурации и фильтры
│   │   │   ├── Config.java
│   │   │   └── CustomLoggingFilter.java
│   │   ├── generators/        # Генераторы тестовых данных
│   │   │   └── RandomModelGenerator.java
│   │   ├── models/            # Модели данных
│   │   │   ├── comparison/    # Сравнение моделей
│   │   │   │   ├── ModelAssertions.java
│   │   │   │   ├── ModelComparator.java
│   │   │   │   └── ModelComparisonConfigLoader.java
│   │   │   ├── BaseModel.java
│   │   │   ├── Customer.java
│   │   │   ├── Transaction.java
│   │   │   ├── TransactionType.java
│   │   │   ├── UserRole.java
│   │   │   ├── CreateUserRequestModel.java
│   │   │   ├── CreateUserResponseModel.java
│   │   │   ├── LoginUserRequestModel.java
│   │   │   ├── LoginUserResponseModel.java
│   │   │   ├── TransferMoneyRequestModel.java
│   │   │   ├── TransferMoneyResponseModel.java
│   │   │   ├── UserDepositMoneyRequestModel.java
│   │   │   ├── UserDepositMoneyResponseModel.java
│   │   │   ├── UpdateCustomerNameRequestModel.java
│   │   │   ├── UpdateCustomerNameResponseModel.java
│   │   │   ├── GetCustomerProfileResponseModel.java
│   │   │   ├── GetCustomerAccountsResponseModel.java
│   │   │   └── CreateAccountResponseModel.java
│   │   ├── requests/          # HTTP запросы и шаги
│   │   │   ├── steps/         # Шаги для тестов
│   │   │   │   ├── AdminSteps.java
│   │   │   │   └── UserSteps.java
│   │   │   └── skeleton/      # Базовые классы для запросов
│   │   │       ├── interfaces/
│   │   │       │   ├── CrudEndpointInterface.java
│   │   │       │   ├── GeneratingRule.java
│   │   │       │   └── GetAllEndpointsInterface.java
│   │   │       └── requesters/
│   │   │           ├── CrudRequester.java
│   │   │           ├── ValidatedCrudRequester.java
│   │   │           ├── HttpRequest.java
│   │   │           └── Endpoint.java
│   │   └── specs/             # Спецификации запросов и ответов
│   │       ├── RequestSpecs.java
│   │       └── ResponseSpecs.java
│   ├── common/                # Общие компоненты
│   │   ├── annotations/       # Аннотации для тестов
│   │   │   ├── AdminSession.java
│   │   │   ├── UserSession.java
│   │   │   └── Browsers.java
│   │   ├── extensions/        # JUnit5 расширения
│   │   │   ├── AdminSessionExtension.java
│   │   │   ├── UserSessionExtension.java
│   │   │   ├── BrowserMatchExtension.java
│   │   │   └── TimingExtension.java
│   │   ├── helper/            # Вспомогательные классы
│   │   │   └── StepLogger.java
│   │   └── storage/           # Хранилище данных
│   │       └── SessionStorage.java
│   └── ui/                    # UI тестирование
│       ├── elements/          # Базовые элементы UI
│       │   ├── BaseElement.java
│       │   └── UserBage.java
│       └── pages/             # Страницы приложения
│           ├── BasePage.java
│           ├── LoginPage.java
│           ├── UserDashboard.java
│           ├── AdminPanel.java
│           ├── TransferPage.java
│           ├── DepositPage.java
│           ├── EditProfilePage.java
│           └── BankAlert.java
└── test/java/
    ├── api/                   # API тесты
    │   ├── BaseApiTest.java
    │   ├── CreateAccountApiTest.java
    │   ├── CreateUserApiTest.java
    │   ├── DepositMoneyApiTest.java
    │   ├── LoginUserApiTest.java
    │   ├── TransferMoneyApiTest.java
    │   └── UpdateCustomerNameApiTest.java
    └── ui/                    # UI тесты
        ├── BaseUiTest.java
        ├── CreateAccountUiTest.java
        ├── CreateUserUiTest.java
        ├── DepositMoneyUiTest.java
        ├── LoginUserUiTest.java
        ├── TransferMoneyUiTest.java
        └── UpdateCustomerNameUiTest.java
```

## 🚀 Способы запуска тестов

### 1. Локальный запуск через Maven

#### Предварительные требования

- Java 21+
- Maven 3.6+
- Запущенный API сервер на `http://localhost:4111`

#### Установка и запуск

1. **Клонирование репозитория**

```bash
git clone <repository-url>
cd nbank-for-docker
```

2. **Запуск тестов через Maven**

```bash
# Запуск всех тестов
mvn test

# Запуск конкретного теста
mvn test -Dtest="CreateUserApiTest"

# Запуск с профилем тестирования
mvn test -P api    # Только API тесты
mvn test -P ui     # Только UI тесты
```

### 2. Запуск через Docker-контейнер

#### Использование готового образа

```bash
# Запуск всех тестов
./run-tests.sh

# Запуск только API тестов
./run-tests.sh api

# Запуск только UI тестов
./run-tests.sh ui
```

#### Настройка удаленного окружения если бэк и фронт подняты не локально. Если локально то этот пункт нужно пропустить.

Создайте файл `.env-for-run-tests` в корне проекта для указания адресов удаленного сервера:

```bash
# .env-for-run-tests
APIBASEURL=http://192.168.1.50:4111
UIBASEURL=http://192.168.1.50
```

### 3. Полный цикл с Docker Compose

#### Автоматический запуск тестового окружения + тесты

```bash
# Запуск полного цикла (окружение + тесты + остановка)
cd infra
./run-tests-with-docker-compose.sh
```

Этот скрипт:

1. Поднимает тестовое окружение (backend, frontend, nginx, selenoid)
2. Запускает все тесты
3. Останавливает окружение

#### Ручное управление окружением

```bash
# Поднятие тестового окружения
cd infra
./start-docker-compose.sh

# Запуск тестов (в отдельном терминале)
cd ..
./run-tests.sh

# Остановка окружения
cd infra
docker-compose down
```

### 4. Сборка и публикация Docker-образа

#### Подготовка к публикации

Создайте файл `.env-for-push-test` в корне проекта:

```bash
# .env-for-push-test
DOCKERHUB_USERNAME=ваш_логин
DOCKERHUB_TOKEN=ваш_токен
IMAGE_NAME=nbank-tests
TAG=latest
```

#### Публикация образа

```bash
# Сборка и публикация в Docker Hub
./push-tests.sh
```

## 🛠️ Технологический стек

### Основные технологии

- **Java 21** — основной язык программирования для написания тестов
- **Maven** — система сборки и управления зависимостями
- **JUnit 5** — фреймворк для тестирования с поддержкой параметризации

### API тестирование

- **REST Assured 5.5.1** — библиотека для тестирования REST API
- **Jackson** — сериализация/десериализация JSON данных
- **AssertJ** — fluent assertions для удобной проверки результатов
- **rgxgen** — генерация тестовых данных по регулярным выражениям

### UI тестирование

- **Selenide 7.9.3** — обертка над Selenium для упрощения UI тестирования
- **Selenoid** — Selenium Grid для запуска тестов в Docker контейнерах
- **Chrome/Firefox** — браузеры для тестирования веб-интерфейса

### Инфраструктура и отчетность

- **Docker** — контейнеризация тестов и тестового окружения
- **Docker Compose** — оркестрация сервисов (БД, API, UI, Selenoid)
- **Allure** — генерация красивых HTML отчетов о тестах
- **Swagger Coverage** — анализ покрытия API эндпоинтов
- **GitHub Actions** — CI/CD пайплайн для автоматического запуска тестов

### Вспомогательные библиотеки

- **Lombok** — уменьшение boilerplate кода через аннотации
- **SLF4J** — логирование выполнения тестов
- **Checkstyle** — проверка стиля кода

## 📊 Возможности фреймворка

### API Тестирование

- ✅ Автоматическая генерация тестовых данных по regex
- ✅ Валидация моделей через аннотации
- ✅ Сравнение request/response моделей
- ✅ CRUD операции через универсальный интерфейс
- ✅ Кастомное логирование запросов
- ✅ Конфигурация через properties файлы
- ✅ Базовые HTTP операции
- ✅ Генерация случайных данных
- ✅ Авторизация пользователей

### UI Тестирование

- ✅ Page Object Model архитектура
- ✅ Базовые элементы UI
- ✅ Страницы приложения (Login, Dashboard, Transfer, Deposit, Admin)
- ✅ UI тесты пользовательских сценариев
- ✅ Интеграция с Selenoid для параллельного выполнения

### Общие возможности

- ✅ Аннотации для управления сессиями
- ✅ JUnit5 расширения для кастомной логики
- ✅ Хранилище сессий для переиспользования данных
- ✅ Поддержка различных браузеров
- ✅ Тайминг и логирование тестов
- ✅ Checkstyle для проверки качества кода
- ✅ Allure для генерации красивых отчетов

## 🔧 Конфигурация

### Основные настройки

Файл `src/main/resources/config.properties`:

```properties
url=http://localhost:4111
apiVersion=/api/v1
```

### Docker Compose конфигурация

Файл `infra/.env` (создайте перед запуском):

```bash
# Укажите абсолютный путь к проекту
HOST_PATH=/home/yura/test/nbank-for-docker
# Для Windows (Git Bash/WSL):
# HOST_PATH=C:/Users/Yura/nbank-for-docker
```

### Настройка браузеров для UI тестов

Файл `infra/config/browsers.json` содержит конфигурацию браузеров для Selenoid.

## 📝 Примеры использования

### API тест - создание пользователя администратором

```java
@Test
void adminCanCreateUserWithValidCredentials() throws InterruptedException {
    // Генерируем случайные валидные данные пользователя
    CreateUserRequestModel createUserRequestModel = RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

    // Выполняем POST запрос с валидацией ответа
    CreateUserResponseModel createUserResponseModel = new ValidatedCrudRequester<CreateUserResponseModel>(
        RequestSpecs.adminSpec(),
        ResponseSpecs.responseReturns201Spec(),
        Endpoint.ADMIN_USERS
    ).post(createUserRequestModel);

    // Сравниваем request и response модели
    ModelAssertions.assertThatModels(createUserRequestModel, createUserResponseModel).match();
}
```

### API тест - негативные сценарии с параметризацией

```java
@ParameterizedTest
@MethodSource("argsFor_AdminCannotCreateUserWithInvalidCredentials")
void adminCannotCreateUserWithInvalidCredentials(String username, String password, String role, String errorKey, String errorValue) {
    CreateUserRequestModel createUserRequestModel = CreateUserRequestModel.builder()
            .username(username)
            .password(password)
            .role(role)
            .build();

    new CrudRequester(RequestSpecs.adminSpec(),
            ResponseSpecs.responseReturns400Spec(errorKey, errorValue),
            Endpoint.ADMIN_USERS)
            .post(createUserRequestModel);
}
```

### UI тест - авторизация пользователя

```java
@Test
void userCanLoginWithValidCredentials() {
    // Создаем пользователя через API
    CreateUserRequestModel user = AdminSteps.createUser();

    // Тестируем UI авторизацию
    new LoginPage().open().login(user.getUsername(), user.getPassword())
            .getPage(UserDashboard.class).getWelcomeText()
            .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, " + "noname!"));
}
```

### UI тест - авторизация администратора

```java
@Test
@Browsers({"chrome"})
void adminCanLoginWithValidCredentials() {
    CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();

    new LoginPage().open().login(admin.getUsername(), admin.getPassword())
            .getPage(AdminPanel.class).getAdminPanelText()
            .shouldBe(Condition.visible);
}
```

## 🧪 Тестируемая функциональность

### API тесты

- **Управление пользователями**: создание, авторизация, обновление профиля
- **Банковские операции**: переводы денег, депозиты, создание счетов
- **Валидация данных**: проверка корректности входных параметров
- **Авторизация**: тестирование ролей ADMIN и USER
- **Обработка ошибок**: негативные сценарии с различными типами ошибок

### UI тесты

- **Авторизация**: вход в систему под разными ролями
- **Пользовательский интерфейс**: навигация по страницам приложения
- **Банковские операции**: UI для переводов, депозитов, управления счетами
- **Административная панель**: управление пользователями через веб-интерфейс
- **Кроссбраузерность**: тестирование в различных браузерах

### Типы тестов

1. **Позитивные тесты** — проверка корректной работы API и UI
2. **Негативные тесты** — проверка обработки ошибок и валидации
3. **Параметризованные тесты** — тестирование с различными наборами данных
4. **Интеграционные тесты** — тестирование полных пользовательских сценариев

## 📈 Метрики качества

- Покрытие тестами: все основные API и UI endpoints
- Поддержка различных уровней авторизации
- Валидация бизнес-правил
- Проверка граничных значений
- Автоматическая проверка стиля кода через Checkstyle

## 📋 Bash скрипты проекта

### Основные скрипты запуска

| Скрипт                                   | Описание                          | Использование                                    |
| ---------------------------------------- | --------------------------------- | ------------------------------------------------ |
| `run-tests.sh`                           | Запуск тестов в Docker-контейнере | `./run-tests.sh [api\|ui]`                       |
| `infra/run-tests-with-docker-compose.sh` | Полный цикл (окружение + тесты)   | `cd infra && ./run-tests-with-docker-compose.sh` |
| `infra/start-docker-compose.sh`          | Запуск Docker Compose окружения   | `cd infra && ./start-docker-compose.sh`          |
| `push-tests.sh`                          | Сборка и публикация Docker-образа | `./push-tests.sh`                                |

### Вспомогательные скрипты

| Скрипт                               | Описание                  | Функции                                                                                     |
| ------------------------------------ | ------------------------- | ------------------------------------------------------------------------------------------- |
| `scripts/collect-test-statistics.sh` | Сбор статистики тестов    | Парсит Allure результаты и Swagger coverage, генерирует статистику для Telegram уведомлений |
| `scripts/extract-docker-images.sh`   | Извлечение Docker образов | Парсит docker-compose.yml, извлекает список образов с ссылками на Docker Hub                |

### Детальное описание скриптов

#### `run-tests.sh`

- **Назначение**: Запуск тестов в изолированном Docker-контейнере
- **Функции**:
  - Сборка Docker-образа с тестами
  - Запуск тестов с переданными параметрами
  - Поддержка профилей (api/ui)
- **Использование**: `./run-tests.sh api` или `./run-tests.sh ui`

#### `infra/start-docker-compose.sh`

- **Назначение**: Запуск тестового окружения (backend, frontend, база данных, Selenoid)
- **Функции**:
  - Остановка предыдущих контейнеров
  - Pull всех образов браузеров из browsers.json
  - Запуск docker-compose в фоновом режиме
- **Использование**: `cd infra && ./start-docker-compose.sh`

#### `infra/run-tests-with-docker-compose.sh`

- **Назначение**: Полный цикл тестирования (окружение + тесты + очистка)
- **Функции**:
  - Запуск тестового окружения
  - Выполнение тестов
  - Остановка и очистка контейнеров
- **Использование**: `cd infra && ./run-tests-with-docker-compose.sh`

#### `scripts/collect-test-statistics.sh`

- **Назначение**: Сбор и анализ статистики тестов для уведомлений
- **Функции**:
  - Парсинг Allure JSON результатов
  - Извлечение процента покрытия из Swagger coverage
  - Генерация файла статистики для Telegram
  - Валидация данных (0-100% для покрытия)
- **Использование**: Автоматически вызывается в GitHub Actions

#### `scripts/extract-docker-images.sh`

- **Назначение**: Извлечение списка Docker образов для уведомлений
- **Функции**:
  - Парсинг docker-compose.yml с помощью yq
  - Извлечение имен образов
  - Генерация HTML-ссылок на Docker Hub
- **Использование**: Автоматически вызывается в GitHub Actions

### Файлы конфигурации

| Файл                 | Назначение                        | Обязательность |
| -------------------- | --------------------------------- | -------------- |
| `.env-for-run-tests` | Настройка удаленных адресов       | Опционально    |
| `.env-for-push-test` | Настройка Docker Hub              | Для публикации |
| `infra/.env`         | Путь к проекту для Docker Compose | Обязательно    |

## 🐳 Docker-образы

### Используемые образы

- **01yura/nbank-tests** — образ с автотестами
- **nobugsme/nbank:with_username_error_fix** — backend API
- **nobugsme/nbank-ui:latest** — frontend UI
- **aerokube/selenoid** — Selenium Grid для UI тестов
- **aerokube/selenoid-ui** — Web UI для Selenoid

### Создание собственного образа

```bash
# Сборка образа
docker build -t my-nbank-tests .

# Запуск с кастомными параметрами
docker run --rm \
  -e TEST_PROFILE=api \
  -e APIBASEURL=http://my-server:4111 \
  -e UIBASEURL=http://my-server \
  my-nbank-tests
```

## 📊 Результаты тестов

После выполнения тестов результаты сохраняются в:

```
test-output/
└── YYYY-MM-DD_HH-MM-SS/
    ├── logs/              # Логи выполнения
    ├── reports/           # Отчёты о тестах
    └── surefire-reports/  # Surefire отчёты
```

## 🔄 CI/CD Pipeline

Проект настроен с автоматическим CI/CD пайплайном через GitHub Actions, который обеспечивает:

### Автоматические процессы

- **Запуск тестов** при каждом push в репозиторий
- **Проверка стиля кода** через Checkstyle
- **Генерация отчетов** Allure и Swagger coverage
- **Сборка Docker-образа** с тестами
- **Публикация отчетов** на GitHub Pages
- **Уведомления в Telegram** с детальной статистикой

### Настройка CI/CD

1. **Добавьте секреты в GitHub** (Settings → Secrets and variables → Actions):

   - `DOCKERHUB_USERNAME` - имя пользователя Docker Hub
   - `DOCKERHUB_TOKEN` - токен доступа Docker Hub
   - `TELEGRAM_BOT_TOKEN` - токен Telegram бота
   - `TELEGRAM_CHAT_ID` - ID чата для уведомлений

2. **При push в репозиторий автоматически**:
   - Запускаются все тесты (API + UI)
   - Генерируются отчеты Allure и Swagger coverage
   - Собирается статистика выполнения тестов
   - Отчеты публикуются на GitHub Pages
   - Собирается и публикуется Docker-образ
   - Отправляется уведомление в Telegram

### Telegram уведомления со статистикой

Каждое уведомление содержит автоматически собранную статистику:

```
✅ Tests PASSED in GitHub Actions: 01yura/nbank-for-docker on main

📦 Repository: 01yura/nbank-for-docker
🌿 Branch: main
🔗 Commit: abc123def456
👤 Author: 01yura
📋 GitHub Actions: View Run

📊 Test Statistics:
━━━━━━━━━━━━━━━━━━━━
📝 Total tests: 51
✅ Passed: 51
❌ Failed: 0
📈 Success rate: 100.0%
🔌 API coverage: 85%

📊 Allure Report: View Report
📈 Swagger Coverage: View Coverage

🐳 Docker Images:
• postgres:15-alpine
• nobugsme/nbank:with_validation_fix
• nobugsme/nbank-ui:with_nginx
• aerokube/selenoid:latest-release
```

### Источники статистики

- **Allure результаты** (`target/allure-results/*-result.json`) - подсчет тестов и их статусов
- **Swagger coverage** (`swagger-coverage-report.html`) - процент покрытия API эндпоинтов
- **Docker образы** (`infra/docker-compose.yml`) - список используемых контейнеров

## 🤝 Контрибуция

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект предназначен для образовательных целей.

## 📞 Поддержка

При возникновении вопросов создайте Issue в репозитории.
