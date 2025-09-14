package api.database;

import api.configs.Config;
import api.dao.AccountDao;
import api.dao.UserDao;
import lombok.Builder;
import lombok.Data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DBRequest - Универсальный класс для выполнения запросов к базе данных
 * 
 * Этот класс предоставляет fluent API для построения и выполнения SQL запросов:
 * - Поддерживает различные типы запросов (SELECT, INSERT, UPDATE, DELETE)
 * - Автоматически маппит результаты в DAO объекты
 * - Управляет соединением с базой данных
 * - Обрабатывает параметры запросов через PreparedStatement
 * 
 * Использование:
 * <pre>
 * UserDao user = DBRequest.builder()
 *     .requestType(DBRequest.RequestType.SELECT)
 *     .table("customers")
 *     .where(Condition.equalTo("username", "john_doe"))
 *     .extractAs(UserDao.class);
 * </pre>
 * 
 * @author Generated
 * @version 1.0
 */
@Data
@Builder
public class DBRequest {
    
    /** Тип SQL запроса (SELECT, INSERT, UPDATE, DELETE) */
    private RequestType requestType;
    
    /** Имя таблицы для выполнения запроса */
    private String table;
    
    /** Список условий WHERE для запроса */
    private List<Condition> conditions;
    
    /** Класс для маппинга результата запроса */
    private Class<?> extractAsClass;

    /**
     * Создает новый экземпляр билдера для построения запроса
     * 
     * @return новый экземпляр DBRequestBuilder
     */
    public static DBRequestBuilder builder() {
        return new DBRequestBuilder();
    }

    /**
     * Выполняет запрос и извлекает результат в указанный класс
     * 
     * Этот метод является основным для выполнения запросов. Он:
     * - Устанавливает класс для маппинга результата
     * - Выполняет SQL запрос
     * - Маппит результат в объект указанного типа
     * 
     * @param <T> тип возвращаемого объекта
     * @param clazz класс для маппинга результата
     * @return объект типа T с данными из базы
     * @throws RuntimeException если произошла ошибка при выполнении запроса
     */
    public <T> T extractAs(Class<T> clazz) {
        this.extractAsClass = clazz;
        return executeQuery(clazz);
    }

    /**
     * Выполняет SQL запрос и возвращает результат
     * 
     * Этот приватный метод выполняет основную работу:
     * - Строит SQL запрос
     * - Устанавливает соединение с базой данных
     * - Устанавливает параметры для PreparedStatement
     * - Выполняет запрос
     * - Маппит результат в соответствующий DAO объект
     * 
     * @param <T> тип возвращаемого объекта
     * @param clazz класс для маппинга результата
     * @return объект типа T с данными из базы
     * @throws RuntimeException если произошла ошибка при выполнении запроса
     */
    private <T> T executeQuery(Class<T> clazz) {
        String sql = buildSQL();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Устанавливаем параметры для условий WHERE
            if (conditions != null) {
                for (int i = 0; i < conditions.size(); i++) {
                    statement.setObject(i + 1, conditions.get(i).getValue());
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                // Маппим результат в соответствующий DAO объект
                if (clazz == UserDao.class) {
                    return (T) mapToUserDao(resultSet);
                }
                if (clazz == AccountDao.class) {
                    return (T) mapToAccountDao(resultSet);
                }
                // Добавляем новые маппинги по мере необходимости
                throw new UnsupportedOperationException("Mapping for " + clazz.getSimpleName() + " not implemented");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    /**
     * Маппит ResultSet в объект UserDao
     * 
     * Этот метод извлекает данные из ResultSet и создает объект UserDao.
     * Ожидает, что ResultSet содержит следующие колонки:
     * - id (Long) - ID пользователя
     * - username (String) - имя пользователя
     * - password (String) - пароль
     * - role (String) - роль пользователя
     * - name (String) - полное имя
     * 
     * @param resultSet результат SQL запроса
     * @return объект UserDao или null, если данных нет
     * @throws SQLException если произошла ошибка при чтении данных
     */
    private UserDao mapToUserDao(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return UserDao.builder()
                    .id(resultSet.getLong("id"))
                    .username(resultSet.getString("username"))
                    .password(resultSet.getString("password"))
                    .role(resultSet.getString("role"))
                    .name(resultSet.getString("name"))
                    .build();
        }
        return null;
    }

    /**
     * Маппит ResultSet в объект AccountDao
     * 
     * Этот метод извлекает данные из ResultSet и создает объект AccountDao.
     * Ожидает, что ResultSet содержит следующие колонки:
     * - id (Long) - ID аккаунта
     * - account_number (String) - номер аккаунта
     * - balance (Double) - баланс
     * - customer_id (Long) - ID пользователя-владельца
     * 
     * @param resultSet результат SQL запроса
     * @return объект AccountDao или null, если данных нет
     * @throws SQLException если произошла ошибка при чтении данных
     */
    private AccountDao mapToAccountDao(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return AccountDao.builder()
                    .id(resultSet.getLong("id"))
                    .accountNumber(resultSet.getString("account_number"))
                    .balance(resultSet.getDouble("balance"))
                    .customerId(resultSet.getLong("customer_id"))
                    .build();
        }
        return null;
    }

    /**
     * Строит SQL запрос на основе параметров запроса
     * 
     * Этот метод создает SQL запрос в зависимости от типа запроса:
     * - SELECT: создает SELECT запрос с условиями WHERE
     * - INSERT, UPDATE, DELETE: пока не реализованы
     * 
     * Для SELECT запросов:
     * - Добавляет "SELECT * FROM table_name"
     * - Добавляет условия WHERE, если они есть
     * - Объединяет несколько условий через AND
     * 
     * @return готовый SQL запрос
     * @throws UnsupportedOperationException если тип запроса не поддерживается
     */
    private String buildSQL() {
        StringBuilder sql = new StringBuilder();

        switch (requestType) {
            case SELECT:
                sql.append("SELECT * FROM ").append(table);
                if (conditions != null && !conditions.isEmpty()) {
                    sql.append(" WHERE ");
                    for (int i = 0; i < conditions.size(); i++) {
                        if (i > 0) sql.append(" AND ");
                        sql.append(conditions.get(i).getColumn()).append(" ").append(conditions.get(i).getOperator()).append(" ?");
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Request type " + requestType + " not implemented");
        }

        return sql.toString();
    }

    /**
     * Создает соединение с базой данных
     * 
     * Этот метод использует настройки из конфигурации для подключения к БД:
     * - db.url - URL базы данных
     * - db.username - имя пользователя
     * - db.password - пароль
     * 
     * @return Connection объект для работы с базой данных
     * @throws SQLException если не удается подключиться к базе данных
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.getProperty("db.url"),
                Config.getProperty("db.username"),
                Config.getProperty("db.password")
        );
    }

    /**
     * Перечисление типов SQL запросов
     * 
     * Поддерживаемые типы:
     * - SELECT - выборка данных
     * - INSERT - вставка данных (пока не реализовано)
     * - UPDATE - обновление данных (пока не реализовано)
     * - DELETE - удаление данных (пока не реализовано)
     */
    public enum RequestType {
        /** Выборка данных из таблицы */
        SELECT, 
        /** Вставка данных в таблицу (пока не реализовано) */
        INSERT, 
        /** Обновление данных в таблице (пока не реализовано) */
        UPDATE, 
        /** Удаление данных из таблицы (пока не реализовано) */
        DELETE
    }

    /**
     * Внутренний класс-билдер для построения DBRequest
     * 
     * Этот класс реализует паттерн Builder для удобного создания запросов.
     * Позволяет цепочкой вызовов настроить все параметры запроса.
     * 
     * Пример использования:
     * <pre>
     * UserDao user = DBRequest.builder()
     *     .requestType(DBRequest.RequestType.SELECT)
     *     .table("customers")
     *     .where(Condition.equalTo("username", "john_doe"))
     *     .extractAs(UserDao.class);
     * </pre>
     */
    public static class DBRequestBuilder {
        /** Тип SQL запроса */
        private RequestType requestType;
        
        /** Имя таблицы для запроса */
        private String table;
        
        /** Список условий WHERE */
        private List<Condition> conditions = new ArrayList<>();
        
        /** Класс для маппинга результата */
        private Class<?> extractAsClass;

        /**
         * Устанавливает тип SQL запроса
         * 
         * @param requestType тип запроса (SELECT, INSERT, UPDATE, DELETE)
         * @return this для поддержки цепочки вызовов
         */
        public DBRequestBuilder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        /**
         * Добавляет условие WHERE к запросу
         * 
         * Можно вызывать несколько раз для добавления нескольких условий.
         * Условия объединяются через AND.
         * 
         * @param condition условие для WHERE
         * @return this для поддержки цепочки вызовов
         */
        public DBRequestBuilder where(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        /**
         * Устанавливает имя таблицы для запроса
         * 
         * @param table имя таблицы
         * @return this для поддержки цепочки вызовов
         */
        public DBRequestBuilder table(String table) {
            this.table = table;
            return this;
        }

        /**
         * Выполняет запрос и извлекает результат в указанный класс
         * 
         * Этот метод завершает построение запроса и выполняет его.
         * Создает новый экземпляр DBRequest с текущими параметрами и выполняет запрос.
         * 
         * @param <T> тип возвращаемого объекта
         * @param clazz класс для маппинга результата
         * @return объект типа T с данными из базы
         * @throws RuntimeException если произошла ошибка при выполнении запроса
         */
        public <T> T extractAs(Class<T> clazz) {
            this.extractAsClass = clazz;
            DBRequest request = DBRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .conditions(conditions)
                    .extractAsClass(extractAsClass)
                    .build();
            return request.extractAs(clazz);
        }
    }
}
