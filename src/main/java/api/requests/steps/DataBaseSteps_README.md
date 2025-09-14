# DataBaseSteps - Шаги для работы с базой данных

## Описание

`DataBaseSteps` - это класс в пакете `steps`, который предоставляет удобные методы для работы с базой данных. Класс интегрирован с системой логирования `StepLogger` и использует собственные DAO модели для маппинга данных.

## Основные возможности

### 1. Работа с пользователями (CUSTOMERS)

```java
// Получение пользователя по имени
UserDao user = DataBaseSteps.getUserByUsername("john_doe");

// Получение пользователя по ID
UserDao user = DataBaseSteps.getUserById(1L);

// Получение пользователя по роли
UserDao user = DataBaseSteps.getUserByRole("USER");

// Получение всех пользователей
List<UserDao> users = DataBaseSteps.getAllUsers();

// Проверка существования пользователя
boolean exists = DataBaseSteps.userExists("john_doe");
```

### 2. Работа с аккаунтами (ACCOUNTS)

```java
// Получение аккаунта по номеру
AccountDao account = DataBaseSteps.getAccountByAccountNumber("1234567890");

// Получение аккаунта по ID
AccountDao account = DataBaseSteps.getAccountById(1L);

// Получение аккаунта по ID пользователя
AccountDao account = DataBaseSteps.getAccountByCustomerId(1L);

// Получение всех аккаунтов
List<AccountDao> accounts = DataBaseSteps.getAllAccounts();

// Получение всех аккаунтов пользователя
List<AccountDao> userAccounts = DataBaseSteps.getAccountsByCustomerId(1L);

// Проверка существования аккаунта
boolean exists = DataBaseSteps.accountExists("1234567890");
```

### 3. Работа с транзакциями (TRANSACTIONS)

```java
// Получение всех транзакций
List<TransactionDao> transactions = DataBaseSteps.getAllTransactions();

// Получение транзакций по ID аккаунта
List<TransactionDao> accountTransactions = DataBaseSteps.getTransactionsByAccountId(1L);
```

### 4. Обновление данных

```java
// Обновление баланса аккаунта
int updatedRows = DataBaseSteps.updateAccountBalance(1L, 1500.0);

// Обновление имени пользователя
int updatedRows = DataBaseSteps.updateUserName(1L, "New Name");
```

## Структура таблиц

### CUSTOMERS

- `id` - ID пользователя
- `username` - имя пользователя
- `password` - пароль
- `name` - полное имя
- `role` - роль пользователя
- `created_at` - дата создания
- `updated_at` - дата обновления

### ACCOUNTS

- `id` - ID аккаунта
- `account_number` - номер аккаунта
- `balance` - баланс
- `customer_id` - ID пользователя
- `created_at` - дата создания
- `updated_at` - дата обновления

### TRANSACTIONS

- `id` - ID транзакции
- `amount` - сумма
- `type` - тип транзакции
- `timestamp` - время транзакции
- `account_id` - ID аккаунта
- `related_account_id` - ID связанного аккаунта
- `created_at` - дата создания

## DAO модели

### UserDao

```java
@Data
@Builder
public class UserDao {
    private Long id;
    private String username;
    private String password;
    private String role;
    private String name;
}
```

### AccountDao

```java
@Data
@Builder
public class AccountDao {
    private Long id;
    private String accountNumber;
    private Double balance;
    private Long customerId;
}
```

### TransactionDao

```java
@Data
@Builder
public class TransactionDao {
    private Long id;
    private Long amount;
    private String type;
    private String timestamp;
    private Long accountId;
    private Long relatedAccountId;
}
```

## Конфигурация

Класс использует настройки из `config.properties`:

- `db.url` - URL базы данных
- `db.username` - имя пользователя БД
- `db.password` - пароль БД

## Примеры использования в тестах

```java
@Test
public void testUserCreation() {
    // Создаем пользователя через API
    CreateUserResponseModel response = AdminSteps.createUser();

    // Получаем данные из базы
    UserDao userDao = DataBaseSteps.getUserByUsername(response.getUsername());

    // Проверяем, что пользователь создался
    assertThat(userDao).isNotNull();
    assertThat(userDao.getUsername()).isEqualTo(response.getUsername());
}

@Test
public void testAccountCreation() {
    // Создаем аккаунт через API
    CreateAccountResponseModel response = UserSteps.createAccount();

    // Получаем данные из базы
    AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(response.getAccountNumber());

    // Проверяем, что аккаунт создался
    assertThat(accountDao).isNotNull();
    assertThat(accountDao.getAccountNumber()).isEqualTo(response.getAccountNumber());
}

@Test
public void testBalanceUpdate() {
    // Обновляем баланс
    int updatedRows = DataBaseSteps.updateAccountBalance(1L, 2000.0);

    // Проверяем, что обновление прошло успешно
    assertThat(updatedRows).isEqualTo(1);

    // Проверяем новый баланс
    AccountDao account = DataBaseSteps.getAccountById(1L);
    assertThat(account.getBalance()).isEqualTo(2000.0);
}
```

## Интеграция с StepLogger

Все методы автоматически логируют свои действия через `StepLogger`, что позволяет:

- Отслеживать выполнение операций с БД в отчетах
- Видеть детали операций в логах
- Интегрироваться с существующей системой логирования

## Обработка ошибок

- Все методы выбрасывают `RuntimeException` при ошибках БД
- SQL исключения оборачиваются в `RuntimeException` с понятным сообщением
- Автоматическое закрытие ресурсов через try-with-resources

## Зависимости

- `api.dao.*` (DAO модели)
- `api.configs.Config`
- `api.database.DBRequest`
- `api.database.Condition`
- `common.helper.StepLogger`
- `java.sql.*` (JDBC)
