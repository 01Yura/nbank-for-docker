package api.requests.steps;

import api.dao.AccountDao;
import api.dao.TransactionDao;
import api.dao.UserDao;
import api.configs.Config;
import api.database.Condition;
import api.database.DBRequest;
import common.helper.StepLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseSteps {

    // Чтобы не писать "магические строки" с именами таблиц
    public enum Table {
        CUSTOMERS("customers"),
        ACCOUNTS("accounts"),
        TRANSACTIONS("transactions");

        private final String table;

        Table(String table) { this.table = table; }
        public String getTable() { return table; }
    }

    /* ======================= USERS ======================= */

    public static UserDao getUserByUsername(String username) {
        return StepLogger.log(
            "Get user from database by username: " + username,
            () -> DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(Table.CUSTOMERS.getTable())
                    .where(Condition.equalTo("username", username))
                    .extractAs(UserDao.class)
        );
    }

    public static UserDao getUserById(Long id) {
        return StepLogger.log(
            "Get user from database by ID: " + id,
            () -> DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(Table.CUSTOMERS.getTable())
                    .where(Condition.equalTo("id", id))
                    .extractAs(UserDao.class)
        );
    }

    public static UserDao getUserByRole(String role) {
        return StepLogger.log(
            "Get user from database by role: " + role,
            () -> DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(Table.CUSTOMERS.getTable())
                    .where(Condition.equalTo("role", role))
                    .extractAs(UserDao.class)
        );
    }

    /**
     * Получает всех пользователей из базы данных
     * @return список всех пользователей
     */
    public static java.util.List<UserDao> getAllUsers() {
        return StepLogger.log(
            "Get all users from database",
            () -> {
                // Для получения списка нужно использовать прямой SQL запрос
                String sql = "SELECT * FROM " + Table.CUSTOMERS.getTable();
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     java.sql.ResultSet resultSet = statement.executeQuery()) {
                    
                    java.util.List<UserDao> users = new java.util.ArrayList<>();
                    while (resultSet.next()) {
                        users.add(UserDao.builder()
                                .id(resultSet.getLong("id"))
                                .username(resultSet.getString("username"))
                                .password(resultSet.getString("password"))
                                .role(resultSet.getString("role"))
                                .name(resultSet.getString("name"))
                                .build());
                    }
                    return users;
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to get all users", e);
                }
            }
        );
    }

    /* ===================== ACCOUNTS ====================== */

    public static AccountDao getAccountByAccountNumber(String accountNumber) {
        return StepLogger.log(
            "Get account from database by account number: " + accountNumber,
            () -> DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(Table.ACCOUNTS.getTable())
                    .where(Condition.equalTo("account_number", accountNumber))
                    .extractAs(AccountDao.class)
        );
    }

    public static AccountDao getAccountById(Long id) {
        return StepLogger.log(
            "Get account from database by ID: " + id,
            () -> DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(Table.ACCOUNTS.getTable())
                    .where(Condition.equalTo("id", id))
                    .extractAs(AccountDao.class)
        );
    }

    public static AccountDao getAccountByCustomerId(Long customerId) {
        return StepLogger.log(
            "Get account from database by customer ID: " + customerId,
            () -> DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(Table.ACCOUNTS.getTable())
                    .where(Condition.equalTo("customer_id", customerId))
                    .extractAs(AccountDao.class)
        );
    }

    /**
     * Получает все аккаунты из базы данных
     * @return список всех аккаунтов
     */
    public static java.util.List<AccountDao> getAllAccounts() {
        return StepLogger.log(
            "Get all accounts from database",
            () -> {
                String sql = "SELECT * FROM " + Table.ACCOUNTS.getTable();
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     java.sql.ResultSet resultSet = statement.executeQuery()) {
                    
                    java.util.List<AccountDao> accounts = new java.util.ArrayList<>();
                    while (resultSet.next()) {
                        accounts.add(AccountDao.builder()
                                .id(resultSet.getLong("id"))
                                .accountNumber(resultSet.getString("account_number"))
                                .balance(resultSet.getDouble("balance"))
                                .customerId(resultSet.getLong("customer_id"))
                                .build());
                    }
                    return accounts;
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to get all accounts", e);
                }
            }
        );
    }

    /**
     * Получает все аккаунты конкретного пользователя
     * @param customerId ID пользователя
     * @return список аккаунтов пользователя
     */
    public static java.util.List<AccountDao> getAccountsByCustomerId(Long customerId) {
        return StepLogger.log(
            "Get all accounts for customer ID: " + customerId,
            () -> {
                String sql = "SELECT * FROM " + Table.ACCOUNTS.getTable() + " WHERE customer_id = ?";
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    
                    statement.setLong(1, customerId);
                    java.util.List<AccountDao> accounts = new java.util.ArrayList<>();
                    
                    try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            accounts.add(AccountDao.builder()
                                    .id(resultSet.getLong("id"))
                                    .accountNumber(resultSet.getString("account_number"))
                                    .balance(resultSet.getDouble("balance"))
                                    .customerId(resultSet.getLong("customer_id"))
                                    .build());
                        }
                    }
                    return accounts;
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to get accounts for customer ID: " + customerId, e);
                }
            }
        );
    }

    /* =================== TRANSACTIONS =================== */

    /**
     * Получает все транзакции из базы данных
     * @return список всех транзакций
     */
    public static java.util.List<TransactionDao> getAllTransactions() {
        return StepLogger.log(
            "Get all transactions from database",
            () -> {
                String sql = "SELECT * FROM " + Table.TRANSACTIONS.getTable();
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     java.sql.ResultSet resultSet = statement.executeQuery()) {
                    
                    java.util.List<TransactionDao> transactions = new java.util.ArrayList<>();
                    while (resultSet.next()) {
                        transactions.add(TransactionDao.builder()
                                .id(resultSet.getLong("id"))
                                .amount(resultSet.getLong("amount"))
                                .type(resultSet.getString("type"))
                                .timestamp(resultSet.getString("timestamp"))
                                .accountId(resultSet.getLong("account_id"))
                                .relatedAccountId(resultSet.getLong("related_account_id"))
                                .build());
                    }
                    return transactions;
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to get all transactions", e);
                }
            }
        );
    }

    /**
     * Получает транзакции по ID аккаунта
     * @param accountId ID аккаунта
     * @return список транзакций аккаунта
     */
    public static java.util.List<TransactionDao> getTransactionsByAccountId(Long accountId) {
        return StepLogger.log(
            "Get transactions for account ID: " + accountId,
            () -> {
                String sql = "SELECT * FROM " + Table.TRANSACTIONS.getTable() + " WHERE account_id = ? OR related_account_id = ?";
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    
                    statement.setLong(1, accountId);
                    statement.setLong(2, accountId);
                    java.util.List<TransactionDao> transactions = new java.util.ArrayList<>();
                    
                    try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            transactions.add(TransactionDao.builder()
                                    .id(resultSet.getLong("id"))
                                    .amount(resultSet.getLong("amount"))
                                    .type(resultSet.getString("type"))
                                    .timestamp(resultSet.getString("timestamp"))
                                    .accountId(resultSet.getLong("account_id"))
                                    .relatedAccountId(resultSet.getLong("related_account_id"))
                                    .build());
                        }
                    }
                    return transactions;
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to get transactions for account ID: " + accountId, e);
                }
            }
        );
    }

    /* =================== UPDATE METHODS =================== */

    /**
     * UPDATE через "чистый" JDBC — как на твоём примере.
     * Возвращает количество обновлённых строк.
     */
    public static int updateAccountBalance(Long accountId, double newBalance) {
        return StepLogger.log(
            "Update account balance in database for account ID: " + accountId + " to: " + newBalance,
            () -> {
                String sql = "UPDATE " + Table.ACCOUNTS.getTable() + " SET balance = ? WHERE id = ?";

                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setDouble(1, newBalance);
                    statement.setLong(2, accountId);
                    return statement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to update balance for accountId=" + accountId, e);
                }
            }
        );
    }

    /**
     * Обновляет имя пользователя в базе данных
     * @param userId ID пользователя
     * @param newName новое имя
     * @return количество обновлённых строк
     */
    public static int updateUserName(Long userId, String newName) {
        return StepLogger.log(
            "Update user name in database for user ID: " + userId + " to: " + newName,
            () -> {
                String sql = "UPDATE " + Table.CUSTOMERS.getTable() + " SET name = ? WHERE id = ?";

                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setString(1, newName);
                    statement.setLong(2, userId);
                    return statement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to update name for userId=" + userId, e);
                }
            }
        );
    }

    /* =================== UTILITY METHODS =================== */

    /**
     * Получает соединение с базой данных
     * @return Connection объект
     * @throws SQLException если не удается подключиться
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.getProperty("db.url"),
                Config.getProperty("db.username"),
                Config.getProperty("db.password")
        );
    }

    /**
     * Проверяет существование пользователя в базе данных
     * @param username имя пользователя
     * @return true если пользователь существует, false в противном случае
     */
    public static boolean userExists(String username) {
        return StepLogger.log(
            "Check if user exists: " + username,
            () -> {
                String sql = "SELECT COUNT(*) FROM " + Table.CUSTOMERS.getTable() + " WHERE username = ?";
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    
                    statement.setString(1, username);
                    try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt(1) > 0;
                        }
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to check if user exists: " + username, e);
                }
            }
        );
    }

    /**
     * Проверяет существование аккаунта в базе данных
     * @param accountNumber номер аккаунта
     * @return true если аккаунт существует, false в противном случае
     */
    public static boolean accountExists(String accountNumber) {
        return StepLogger.log(
            "Check if account exists: " + accountNumber,
            () -> {
                String sql = "SELECT COUNT(*) FROM " + Table.ACCOUNTS.getTable() + " WHERE account_number = ?";
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    
                    statement.setString(1, accountNumber);
                    try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt(1) > 0;
                        }
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to check if account exists: " + accountNumber, e);
                }
            }
        );
    }
}
