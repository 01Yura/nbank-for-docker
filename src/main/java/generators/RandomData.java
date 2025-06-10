package generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {
    private RandomData() {
    }


    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(4, 15);
    }


    public static String getPassword() {
        return RandomStringUtils.randomAlphanumeric(6) +
                RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomNumeric(3) + "!";
    }
}
