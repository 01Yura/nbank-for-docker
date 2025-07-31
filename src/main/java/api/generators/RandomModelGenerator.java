package api.generators;

import com.github.curiousoddman.rgxgen.RgxGen;
import api.models.BaseModel;
import api.requests.skeleton.interfaces.GeneratingRule;

import java.lang.reflect.Field;

public class RandomModelGenerator {

    public static <T extends BaseModel> T generateRandomModel(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
                if (rule != null && field.getType().equals(String.class)) {
                    String regex = rule.regex();
                    RgxGen rgxGen = new RgxGen(regex);
                    String generatedValue = rgxGen.generate();
                    field.set(instance, generatedValue);
                }
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate model: " + clazz.getName(), e);
        }
    }
}
