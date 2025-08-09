package ui;

import api.configs.Config;
import api.models.CreateUserRequestModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import api.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith(BrowserMatchExtension.class)

// проверка 2

public class BaseUiTest extends BaseTest {
    @BeforeAll
    static void globalSelenideSetup() {
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.headless = true;
    }

//    Использую BeforeEach и AfterEach чтобы после каждого теста закрывать сессию и чтобы на каждый тест открывалась
//    новая сессия (открывается новый контейнер и соответственно новый браузер) для того, чтобы каждый тест имел своё
//    уникальное имя (это нужно для записи видео с уникальными именами по названию класса тестов)
    @BeforeEach
    void setupVideoName(TestInfo testInfo) {
        // Форматируем текущий timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));

        // Формируем имя видео с датой
        String videoName = testInfo.getTestClass().get().getSimpleName()
                + "_" + testInfo.getDisplayName().replace("()", "")
                + "_" + timestamp
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


    //      используем для того, чтобы закрыть сессию и новая видеозапись имела новое имя по имени класса теста
    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }

    void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthToken = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthToken);
    }

    //        это перегруженный метод authAsUser, используем чтобы сократить код, так как в нем вызываем первый authAsUser
    void authAsUser(CreateUserRequestModel createUserRequestModel) {
        authAsUser(createUserRequestModel.getUsername(), createUserRequestModel.getPassword());
    }
}
