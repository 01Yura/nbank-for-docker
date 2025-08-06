package api.models.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/*
ModelComparisonConfigLoader - Загрузчик конфигурации сравнения
Этот класс загружает правила сравнения из файла конфигурации:
Загружает настройки из файла model-comparison.properties
Создает объекты ComparisonRule для каждого правила
Позволяет получить правило сравнения для конкретного класса
Поддерживает различные условия сравнения: равенство, неравенство, содержит, не содержит
*/

public class ModelComparisonConfigLoader {

    private final Map<String, ComparisonRule> rules = new HashMap<>();

    public ModelComparisonConfigLoader(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new IllegalArgumentException("Config file not found: " + configFile);
            }
            Properties props = new Properties();
            props.load(input);
            for (String key : props.stringPropertyNames()) {
                String[] target = props.getProperty(key).split(":");
                if (target.length != 2) continue;

                String responseClassName = target[0].trim();
                List<String> fields = Arrays.asList(target[1].split(","));

                rules.put(key.trim(), new ComparisonRule(responseClassName, fields));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DTO comparison config", e);
        }
    }

    public ComparisonRule getRuleFor(Class<?> requestClass) {
        return rules.get(requestClass.getSimpleName());
    }

    public static class ComparisonRule {
        private final String responseClassSimpleName;
        private final Map<String, String> fieldMappings;
        private final List<ModelComparator.FieldCondition> fieldConditions;

        public ComparisonRule(String responseClassSimpleName, List<String> fieldPairs) {
            this.responseClassSimpleName = responseClassSimpleName;
            this.fieldMappings = new HashMap<>();
            this.fieldConditions = new ArrayList<>();

            for (String pair : fieldPairs) {
                String trimmedPair = pair.trim();
                
                // Парсим условия сравнения
                if (trimmedPair.contains("!=")) {
                    // Неравенство: field1!=field2
                    String[] parts = trimmedPair.split("!=");
                    if (parts.length == 2) {
                        fieldConditions.add(new ModelComparator.FieldCondition(
                            parts[0].trim(), 
                            parts[1].trim(), 
                            ModelComparator.ConditionType.NOT_EQUALS
                        ));
                    }
                } else if (trimmedPair.contains("~=")) {
                    // Содержит: field1~=field2
                    String[] parts = trimmedPair.split("~=");
                    if (parts.length == 2) {
                        fieldConditions.add(new ModelComparator.FieldCondition(
                            parts[0].trim(), 
                            parts[1].trim(), 
                            ModelComparator.ConditionType.CONTAINS
                        ));
                    }
                } else if (trimmedPair.contains("!~=")) {
                    // Не содержит: field1!~=field2
                    String[] parts = trimmedPair.split("!~=");
                    if (parts.length == 2) {
                        fieldConditions.add(new ModelComparator.FieldCondition(
                            parts[0].trim(), 
                            parts[1].trim(), 
                            ModelComparator.ConditionType.NOT_CONTAINS
                        ));
                    }
                } else if (trimmedPair.contains("=")) {
                    // Равенство: field1=field2 или field1 (если имена одинаковые)
                    String[] parts = trimmedPair.split("=");
                    if (parts.length == 2) {
                        fieldMappings.put(parts[0].trim(), parts[1].trim());
                        fieldConditions.add(new ModelComparator.FieldCondition(
                            parts[0].trim(), 
                            parts[1].trim(), 
                            ModelComparator.ConditionType.EQUALS
                        ));
                    } else {
                        // fallback: same field name if mapping not explicitly given
                        fieldMappings.put(trimmedPair, trimmedPair);
                        fieldConditions.add(new ModelComparator.FieldCondition(
                            trimmedPair, 
                            trimmedPair, 
                            ModelComparator.ConditionType.EQUALS
                        ));
                    }
                } else {
                    // fallback: same field name if mapping not explicitly given
                    fieldMappings.put(trimmedPair, trimmedPair);
                    fieldConditions.add(new ModelComparator.FieldCondition(
                        trimmedPair, 
                        trimmedPair, 
                        ModelComparator.ConditionType.EQUALS
                    ));
                }
            }
        }

        public String getResponseClassSimpleName() {
            return responseClassSimpleName;
        }

        public Map<String, String> getFieldMappings() {
            return fieldMappings;
        }

        public List<ModelComparator.FieldCondition> getFieldConditions() {
            return fieldConditions;
        }
    }

}
