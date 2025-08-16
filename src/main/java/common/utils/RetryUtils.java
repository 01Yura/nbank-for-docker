package common.utils;

import java.util.function.Predicate;
import java.util.function.Supplier;

// я не использую это класс и метод, так как он усложняет понимание кода

public final class RetryUtils {
    private RetryUtils() {}

    /**
     * Повторяет вызов action, пока condition(result) не станет true
     * или не исчерпаются попытки. Возвращает result, на котором условие
     * выполнилось. Если не выполнилось — кидает IllegalStateException.
     */
    public static <T> T retry(
            Supplier<T> action,
            Predicate<T> condition,
            int maxAttempts,
            long delayMillis
    ) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be > 0");
        }
        if (delayMillis < 0) {
            throw new IllegalArgumentException("delayMillis must be >= 0");
        }

        T result = null;
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;
            result = action.get();

            if (condition.test(result)) {
                return result;
            }

            if (attempts < maxAttempts) {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }

        throw new IllegalStateException(
                "Condition not met after " + attempts + " attempts. Last result: " + result);
    }
}

