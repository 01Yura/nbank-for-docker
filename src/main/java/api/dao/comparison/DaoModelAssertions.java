package api.dao.comparison;

import org.assertj.core.api.AbstractAssert;

/*
DaoModelAssertions - Assertions для тестирования DAO моделей с моделями
Этот класс предоставляет удобный API для тестирования с использованием AssertJ:
Интегрируется с AssertJ framework
Предоставляет fluent API для сравнения DAO моделей с моделями
Автоматически загружает конфигурацию и выполняет сравнение
*/
public class DaoModelAssertions extends AbstractAssert<DaoModelAssertions, Object> {

    private final Object daoModel;
    private final Object model;

    private DaoModelAssertions(Object daoModel, Object model) {
        super(daoModel, DaoModelAssertions.class);
        this.daoModel = daoModel;
        this.model = model;
    }

    public static DaoModelAssertions assertThat(Object daoModel, Object model) {
        return new DaoModelAssertions(daoModel, model);
    }

    public DaoModelAssertions match() {
        DaoModelComparisonConfigLoader configLoader = new DaoModelComparisonConfigLoader("dao-model-comparison.properties");
        DaoModelComparisonConfigLoader.ComparisonRule rule = configLoader.getRuleFor(daoModel.getClass());

        if (rule != null) {
            DaoModelComparator.ComparisonResult result = DaoModelComparator.compareFields(
                    daoModel,
                    model,
                    rule.getFieldMappings()
            );

            if (!result.isSuccess()) {
                failWithMessage("DAO model comparison failed with mismatched fields:\n%s", result);
            }
        } else {
            failWithMessage("No comparison rule found for DAO class %s", daoModel.getClass().getSimpleName());
        }

        return this;
    }

    public DaoModelAssertions matchWithConditions() {
        DaoModelComparisonConfigLoader configLoader = new DaoModelComparisonConfigLoader("dao-model-comparison.properties");
        DaoModelComparisonConfigLoader.ComparisonRule rule = configLoader.getRuleFor(daoModel.getClass());

        if (rule != null) {
            DaoModelComparator.ComparisonResult result = DaoModelComparator.compareFieldsWithConditions(
                    daoModel,
                    model,
                    rule.getFieldConditions()
            );

            if (!result.isSuccess()) {
                failWithMessage("DAO model comparison with conditions failed:\n%s", result);
            }
        } else {
            failWithMessage("No comparison rule found for DAO class %s", daoModel.getClass().getSimpleName());
        }

        return this;
    }
}
