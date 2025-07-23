package ui.iteration1_senior_level;

import api.iteration2_senior_level.BaseTest;
import api.senior.configs.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

//  добавляем ExtendWith и название аннотации, чтобы аннотации работали в тестах
@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith(BrowserMatchExtension.class)
public class BaseUiTest extends BaseTest {
    @BeforeAll
    static void globalSelenideSetup() {
//        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
    }

    //    Использую BeforeEach и AfterEach чтобы после каждого теста закрывать сессию и чтобы на каждый тест открывалась
//    новая сессия (открывается новый контейнер и соответственно новый браузер) для того, чтобы каждый тест имел своё
//    уникальное имя (это нужно для записи видео с уникальными именами по названию класса тестов)
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
