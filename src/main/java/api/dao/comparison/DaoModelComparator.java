package api.dao.comparison;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
DaoModelComparator - Основной класс для сравнения DAO моделей с моделями
Этот класс предоставляет функциональность для сравнения полей DAO объектов с моделями:
compareFields(A daoModel, B model, Map<String, String> fieldMappings) - основной метод для сравнения
compareFieldsWithConditions(A daoModel, B model, List<FieldCondition> conditions) - метод с условиями сравнения
ComparisonResult - внутренний класс, содержащий результат сравнения
Mismatch - внутренний класс для хранения информации о несоответствиях

ПРИМЕР ИСПОЛЬЗОВАНИЯ:
// 1. Простое сравнение с конфигурацией
DaoModelAssertions.assertThatDaoModel(userDao, customer).match();

// 2. Ручное сравнение
Map<String, String> mappings = Map.of("id", "id", "username", "username");
DaoModelComparator.ComparisonResult result = DaoModelComparator.compareFields(userDao, customer, mappings);

// 3. Сравнение с условиями
List<FieldCondition> conditions = Arrays.asList(
    new FieldCondition("id", "id", ConditionType.EQUALS)
);
DaoModelComparator.ComparisonResult result = DaoModelComparator.compareFieldsWithConditions(userDao, customer, conditions);
*/

public class DaoModelComparator {

    public static <A, B> ComparisonResult compareFields(A daoModel, B model, Map<String, String> fieldMappings) {
        List<Mismatch> mismatches = new ArrayList<>();

        for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
            String daoField = entry.getKey();
            String modelField = entry.getValue();

            Object value1 = getFieldValue(daoModel, daoField);
            Object value2 = getFieldValue(model, modelField);

            if (!Objects.equals(String.valueOf(value1), String.valueOf(value2))) {
                mismatches.add(new Mismatch(daoField + " -> " + modelField, value1, value2));
            }
        }

        return new ComparisonResult(mismatches);
    }

    public static <A, B> ComparisonResult compareFieldsWithConditions(A daoModel, B model, List<FieldCondition> conditions) {
        List<Mismatch> mismatches = new ArrayList<>();

        for (FieldCondition condition : conditions) {
            Object value1 = getFieldValue(daoModel, condition.getDaoField());
            Object value2 = getFieldValue(model, condition.getModelField());

            boolean conditionMet = condition.evaluate(value1, value2);
            if (!conditionMet) {
                mismatches.add(new Mismatch(
                    condition.getDaoField() + " -> " + condition.getModelField(), 
                    value1, 
                    value2,
                    condition.getConditionType().name()
                ));
            }
        }

        return new ComparisonResult(mismatches);
    }

    private static Object getFieldValue(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access field: " + fieldName, e);
            }
        }
        throw new RuntimeException("Field not found: " + fieldName + " in class " + obj.getClass().getName());
    }

    public static class ComparisonResult {
        private final List<Mismatch> mismatches;

        public ComparisonResult(List<Mismatch> mismatches) {
            this.mismatches = mismatches;
        }

        public boolean isSuccess() {
            return mismatches.isEmpty();
        }

        public List<Mismatch> getMismatches() {
            return mismatches;
        }

        @Override
        public String toString() {
            if (isSuccess()) {
                return "All fields match.";
            }
            StringBuilder sb = new StringBuilder("Mismatched fields:\n");
            for (Mismatch m : mismatches) {
                sb.append("- ").append(m.fieldName)
                        .append(" (").append(m.conditionType).append(")")
                        .append(": dao=").append(m.expected)
                        .append(", model=").append(m.actual).append("\n");
            }
            return sb.toString();
        }
    }

    public static class Mismatch {
        public final String fieldName;
        public final Object expected;
        public final Object actual;
        public final String conditionType;

        public Mismatch(String fieldName, Object expected, Object actual) {
            this.fieldName = fieldName;
            this.expected = expected;
            this.actual = actual;
            this.conditionType = "EQUALS";
        }

        public Mismatch(String fieldName, Object expected, Object actual, String conditionType) {
            this.fieldName = fieldName;
            this.expected = expected;
            this.actual = actual;
            this.conditionType = conditionType;
        }
    }

    public static class FieldCondition {
        private final String daoField;
        private final String modelField;
        private final ConditionType conditionType;

        public FieldCondition(String daoField, String modelField, ConditionType conditionType) {
            this.daoField = daoField;
            this.modelField = modelField;
            this.conditionType = conditionType;
        }

        public String getDaoField() {
            return daoField;
        }

        public String getModelField() {
            return modelField;
        }

        public ConditionType getConditionType() {
            return conditionType;
        }

        public boolean evaluate(Object value1, Object value2) {
            return conditionType.evaluate(value1, value2);
        }
    }

    public enum ConditionType {
        EQUALS {
            @Override
            public boolean evaluate(Object value1, Object value2) {
                return Objects.equals(String.valueOf(value1), String.valueOf(value2));
            }
        },
        NOT_EQUALS {
            @Override
            public boolean evaluate(Object value1, Object value2) {
                return !Objects.equals(String.valueOf(value1), String.valueOf(value2));
            }
        },
        CONTAINS {
            @Override
            public boolean evaluate(Object value1, Object value2) {
                String str1 = String.valueOf(value1);
                String str2 = String.valueOf(value2);
                return str1.contains(str2) || str2.contains(str1);
            }
        },
        NOT_CONTAINS {
            @Override
            public boolean evaluate(Object value1, Object value2) {
                String str1 = String.valueOf(value1);
                String str2 = String.valueOf(value2);
                return !str1.contains(str2) && !str2.contains(str1);
            }
        };

        public abstract boolean evaluate(Object value1, Object value2);
    }
}
