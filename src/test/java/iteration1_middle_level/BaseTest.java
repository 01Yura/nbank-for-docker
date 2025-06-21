package iteration1_middle_level;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected SoftAssertions softly;


    @BeforeEach
    public void initSoftAssertions() {
        this.softly = new SoftAssertions();
    }


    @AfterEach
    public void verifySoftAssertions() {
        this.softly.assertAll();
    }
}
