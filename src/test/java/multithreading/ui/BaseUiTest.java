package multithreading.ui;

import api.senior.configs.Config;
import api.senior.models.CreateUserRequestModel;
import api.senior.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import multithreading.api.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

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
        Configuration.headless = true;
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
