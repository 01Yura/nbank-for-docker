package api.dao.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/*
DaoModelComparisonConfigLoader - Загрузчик конфигурации сравнения DAO моделей с моделями
Этот класс загружает правила сравнения из файла конфигурации:
Загружает настройки из файла dao-model-comparison.properties
Создает объекты ComparisonRule для каждого правила
Позволяет получить правило сравнения для конкретного DAO класса
Поддерживает различные условия сравнения: равенство, неравенство, содержит, не содержит
*/

public class DaoModelComparisonConfigLoader {

    private final Map<String, ComparisonRule> rules = new HashMap<>();

    public DaoModelComparisonConfigLoader(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new IllegalArgumentException("Config file not found: " + configFile);
            }
            Properties props = new Properties();
            props.load(input);
            for (String key : props.stringPropertyNames()) {
                String[] target = props.getProperty(key).split(":");
                if (target.length != 2) continue;

                String modelClassName = target[0].trim();
                List<String> fields = Arrays.asList(target[1].split(","));

                rules.put(key.trim(), new ComparisonRule(modelClassName, fields));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DAO model comparison config", e);
        }
    }

    public ComparisonRule getRuleFor(Class<?> daoClass) {
        return rules.get(daoClass.getSimpleName());
    }

    public static class ComparisonRule {
        private final String modelClassSimpleName;
        private final Map<String, String> fieldMappings;
        private final List<DaoModelComparator.FieldCondition> fieldConditions;

        public ComparisonRule(String modelClassSimpleName, List<String> fieldPairs) {
            this.modelClassSimpleName = modelClassSimpleName;
            this.fieldMappings = new HashMap<>();
            this.fieldConditions = new ArrayList<>();

            for (String pair : fieldPairs) {
                String trimmedPair = pair.trim();
                
                // Парсим условия сравнения
                if (trimmedPair.contains("!=")) {
                    // Неравенство: daoField!=modelField
                    String[] parts = trimmedPair.split("!=");
                    if (parts.length == 2) {
                        fieldConditions.add(new DaoModelComparator.FieldCondition(
                            parts[0].trim(), 
                            parts[1].trim(), 
                            DaoModelComparator.ConditionType.NOT_EQUALS
                        ));
                    }
                } else if (trimmedPair.contains("~=")) {
                    // Содержит: daoField~=modelField
                    String[] parts = trimmedPair.split("~=");
                    if (parts.length == 2) {
                        fieldConditions.add(new DaoModelComparator.FieldCondition(
                            parts[0].trim(), 
                            parts[1].trim(), 
                            DaoModelComparator.ConditionType.CONTAINS
                        ));
                    }
                } else if (trimmedPair.contains("!~=")) {
                    // Не содержит: daoField!~=modelField
                    String[] parts = trimmedPair.split("!~=");
                    if (parts.length == 2) {
                        fieldConditions.add(new DaoModelComparator.FieldCondition(
                            parts[0].trim(), 
                            parts[1].trim(), 
                            DaoModelComparator.ConditionType.NOT_CONTAINS
                        ));
                    }
                } else if (trimmedPair.contains("=")) {
                    // Равенство: daoField=modelField или daoField (если имена одинаковые)
                    String[] parts = trimmedPair.split("=");
                    if (parts.length == 2) {
                        fieldMappings.put(parts[0].trim(), parts[1].trim());
                        fieldConditions.add(new DaoModelComparator.FieldCondition(
                            parts[0].trim(), 
                            parts[1].trim(), 
                            DaoModelComparator.ConditionType.EQUALS
                        ));
                    } else {
                        // fallback: same field name if mapping not explicitly given
                        fieldMappings.put(trimmedPair, trimmedPair);
                        fieldConditions.add(new DaoModelComparator.FieldCondition(
                            trimmedPair, 
                            trimmedPair, 
                            DaoModelComparator.ConditionType.EQUALS
                        ));
                    }
                } else {
                    // fallback: same field name if mapping not explicitly given
                    fieldMappings.put(trimmedPair, trimmedPair);
                    fieldConditions.add(new DaoModelComparator.FieldCondition(
                        trimmedPair, 
                        trimmedPair, 
                        DaoModelComparator.ConditionType.EQUALS
                    ));
                }
            }
        }

        public String getModelClassSimpleName() {
            return modelClassSimpleName;
        }

        public Map<String, String> getFieldMappings() {
            return fieldMappings;
        }

        public List<DaoModelComparator.FieldCondition> getFieldConditions() {
            return fieldConditions;
        }
    }
}
