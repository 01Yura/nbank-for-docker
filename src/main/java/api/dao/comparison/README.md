# DAO Model Comparison

Этот пакет предоставляет инструменты для сравнения DAO моделей (данных из базы данных) с моделями из пакета `api.models`.

## Компоненты

### 1. DaoModelComparator

Основной класс для сравнения DAO моделей с моделями.

**Методы:**

- `compareFields(A daoModel, B model, Map<String, String> fieldMappings)` - простое сравнение полей
- `compareFieldsWithConditions(A daoModel, B model, List<FieldCondition> conditions)` - сравнение с условиями

### 2. DaoModelAssertions

AssertJ assertions для удобного тестирования.

**Методы:**

- `assertThatDaoModel(daoModel, model).match()` - сравнение с использованием конфигурации
- `assertThatDaoModel(daoModel, model).matchWithConditions()` - сравнение с условиями

### 3. DaoModelComparisonConfigLoader

Загрузчик конфигурации из файла `dao-model-comparison.properties`.

## Конфигурация

Файл `dao-model-comparison.properties` содержит правила сравнения в формате:

```
DaoModelClass=ModelClass:daoField=modelField,daoField2=modelField2
```

### Поддерживаемые условия сравнения:

- `=` - равенство (по умолчанию)
- `!=` - неравенство
- `~=` - содержит
- `!~=` - не содержит

## Примеры использования

### 1. Простое сравнение с конфигурацией

```java
UserDao userDao = UserDao.builder()
    .id(1L)
    .username("john_doe")
    .password("password123")
    .role("USER")
    .name("John Doe")
    .build();

Customer customer = Customer.builder()
    .id(1L)
    .username("john_doe")
    .password("password123")
    .role("USER")
    .name("John Doe")
    .build();

// Сравнение с использованием конфигурации
DaoModelAssertions.assertThatDaoModel(userDao, customer).match();
```

### 2. Ручное сравнение с маппингом

```java
Map<String, String> fieldMappings = new HashMap<>();
fieldMappings.put("id", "id");
fieldMappings.put("username", "username");
fieldMappings.put("password", "password");

DaoModelComparator.ComparisonResult result = DaoModelComparator.compareFields(
    userDao,
    customer,
    fieldMappings
);

assertThat(result.isSuccess()).isTrue();
```

### 3. Сравнение с условиями

```java
List<DaoModelComparator.FieldCondition> conditions = Arrays.asList(
    new DaoModelComparator.FieldCondition("id", "id", DaoModelComparator.ConditionType.EQUALS),
    new DaoModelComparator.FieldCondition("username", "username", DaoModelComparator.ConditionType.EQUALS)
);

DaoModelComparator.ComparisonResult result = DaoModelComparator.compareFieldsWithConditions(
    userDao,
    customer,
    conditions
);
```

## Структура результата сравнения

`ComparisonResult` содержит:

- `isSuccess()` - успешно ли прошло сравнение
- `getMismatches()` - список несоответствий
- `toString()` - текстовое представление результата

`Mismatch` содержит:

- `fieldName` - имя поля
- `expected` - ожидаемое значение (из DAO)
- `actual` - фактическое значение (из модели)
- `conditionType` - тип условия сравнения
