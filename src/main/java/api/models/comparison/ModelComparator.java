package api.models.comparison;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
ModelComparator - Основной класс для сравнения моделей
Этот класс предоставляет функциональность для сравнения полей двух объектов (моделей):
compareFields(A request, B response, Map<String, String> fieldMappings) - основной метод для сравнения
compareFieldsWithConditions(A request, B response, List<FieldCondition> conditions) - метод с условиями сравнения
ComparisonResult - внутренний класс, содержащий результат сравнения
Mismatch - внутренний класс для хранения информации о несоответствиях
*/

public class ModelComparator {

    public static <A, B> ComparisonResult compareFields(A request, B response, Map<String, String> fieldMappings) {
        List<Mismatch> mismatches = new ArrayList<>();

        for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
            String requestField = entry.getKey();
            String responseField = entry.getValue();

            Object value1 = getFieldValue(request, requestField);
            Object value2 = getFieldValue(response, responseField);

            if (!Objects.equals(String.valueOf(value1), String.valueOf(value2))) {
                mismatches.add(new Mismatch(requestField + " -> " + responseField, value1, value2));
            }
        }

        return new ComparisonResult(mismatches);
    }

    public static <A, B> ComparisonResult compareFieldsWithConditions(A request, B response, List<FieldCondition> conditions) {
        List<Mismatch> mismatches = new ArrayList<>();

        for (FieldCondition condition : conditions) {
            Object value1 = getFieldValue(request, condition.getRequestField());
            Object value2 = getFieldValue(response, condition.getResponseField());

            boolean conditionMet = condition.evaluate(value1, value2);
            if (!conditionMet) {
                mismatches.add(new Mismatch(
                    condition.getRequestField() + " -> " + condition.getResponseField(), 
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
                        .append(": expected=").append(m.expected)
                        .append(", actual=").append(m.actual).append("\n");
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
        private final String requestField;
        private final String responseField;
        private final ConditionType conditionType;

        public FieldCondition(String requestField, String responseField, ConditionType conditionType) {
            this.requestField = requestField;
            this.responseField = responseField;
            this.conditionType = conditionType;
        }

        public String getRequestField() {
            return requestField;
        }

        public String getResponseField() {
            return responseField;
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
