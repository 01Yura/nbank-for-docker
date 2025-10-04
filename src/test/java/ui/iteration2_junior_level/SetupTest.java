package ui.iteration2_junior_level;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.util.Map;

public class SetupTest {
    @BeforeAll
    static void globalSelenideSetup() {
//        Configuration.remote = "http://192.168.0.127:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.0.127";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
    }

//    Использую BeforeEach и AfterEach чтобы после каждого теста закрывать сессию и чтобы на каждый тест открывалась
//    новая сессия (открывается новый контейнер и соответственно новый браузер) для того, чтобы каждый тест имел своё
//    уникальное имя (это для записи видео с уникальными именами тестов)
    @BeforeEach
    void setupVideoName(TestInfo testInfo) {
        String videoName = testInfo.getTestClass().get().getSimpleName()
                + "_" + testInfo.getDisplayName().replace("()", "")
                + ".mp4";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of(
                        "enableVNC", true,
                        "enableLog", true,
                        "enableVideo", true,
                        "videoName", videoName
                )
        );
    }

    @AfterEach
    void tearDown() {
        WebDriverRunner.closeWindow();
    }
}
