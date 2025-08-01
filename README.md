# NBank API & UI Testing Framework

Автоматизированный фреймворк для тестирования API и UI банковской системы, построенный на Java 21, REST Assured, Selenide и JUnit 5. Поддерживает многопоточное тестирование и расширяемую архитектуру.

## 🏗️ Архитектура проекта

Проект поддерживает как API, так и UI тесты с модульной архитектурой и переиспользуемыми компонентами.

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
    │   ├── BaseTest.java
    │   ├── DepositMoneyTest.java
    │   ├── TransferMoneyTest.java
    │   └── UpdateCustomerNameTest.java
    └── ui/                    # UI тесты
        ├── BaseUiTest.java
        ├── DepositMoneyTest.java
        ├── TransferMoneyTest.java
        └── UpdateCustomerNameTest.java
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
mvn test -Dtest="CreateUserTest"

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
./restart_docker.sh

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

- **Java 21** — основной язык программирования
- **Maven** — система сборки
- **REST Assured 5.5.1** — API тестирование
- **JUnit 5** — фреймворк для тестирования
- **Selenide 7.9.3** — UI тестирование
- **Lombok** — уменьшение boilerplate кода
- **Jackson** — сериализация/десериализация JSON
- **AssertJ** — fluent assertions
- **rgxgen** — генерация данных по regex
- **SLF4J** — логирование

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

### API тест - создание пользователя

```java
@Test
void adminCanCreateUserWithValidCredentials() {
    CreateUserRequestModel request = RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);
    CreateUserResponseModel response = new ValidatedCrudRequester<CreateUserResponseModel>(
        RequestSpecs.adminSpec(),
        ResponseSpecs.responseReturns201Spec(),
        Endpoint.ADMIN_USERS
    ).post(request);
    ModelAssertions.assertThatModels(request, response).match();
}
```

### UI тест - перевод денег

```java
@Test
void userCanTransferMoney() {
    LoginPage loginPage = new LoginPage();
    UserDashboard dashboard = loginPage.loginAsUser("user", "password");

    TransferPage transferPage = dashboard.openTransferPage();
    transferPage.transferMoney("recipient", 100.0);

    assertThat(transferPage.getSuccessMessage()).isEqualTo("Transfer successful");
}
```

### Параметризованный тест

```java
@ParameterizedTest
@MethodSource("argsFor_userCanDepositMoney")
void userCanDepositMoney(Float depositBalance) {
    // Тестовая логика
}
```

## 🧪 Типы тестов

1. **Позитивные тесты** — проверка корректной работы API и UI
2. **Негативные тесты** — проверка обработки ошибок
3. **Параметризованные тесты** — тестирование с различными данными
4. **Интеграционные тесты** — тестирование полных сценариев

## 📈 Метрики качества

- Покрытие тестами: все основные API и UI endpoints
- Поддержка различных уровней авторизации
- Валидация бизнес-правил
- Проверка граничных значений

## 📋 Доступные скрипты

### Основные скрипты запуска

| Скрипт                                   | Описание                            | Использование                                    |
| ---------------------------------------- | ----------------------------------- | ------------------------------------------------ |
| `run-tests.sh`                           | Запуск тестов в Docker-контейнере   | `./run-tests.sh [api\|ui]`                       |
| `infra/run-tests-with-docker-compose.sh` | Полный цикл (окружение + тесты)     | `cd infra && ./run-tests-with-docker-compose.sh` |
| `infra/restart_docker.sh`                | Перезапуск Docker Compose окружения | `cd infra && ./restart_docker.sh`                |
| `push-tests.sh`                          | Сборка и публикация Docker-образа   | `./push-tests.sh`                                |

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
