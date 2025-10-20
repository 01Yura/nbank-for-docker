package ui;

import api.BaseApiTest;
import api.configs.Config;
import api.models.CreateUserRequestModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith(BrowserMatchExtension.class)

// проверка CI/CD pipeline 2

public class BaseUiTest extends BaseApiTest {
    @BeforeAll
    static void globalSelenideSetup() {
        // Настройки Selenide
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.headless = false;
        Configuration.timeout = 10_000;

        // Добавление слушателя для интеграции с Allure отчетностью
        // Позволяет записывать действия Selenide в Allure отчеты для лучшей диагностики
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        // Создание карты дополнительных опций для Selenoid (это уже настройка для Selenoid, а не для Selenide)
        Map<String, Object> options = new HashMap<>();
        options.put("enableVNC", true);
        options.put("enableLog", true);
        options.put("enableVideo", true);

        // Применение настроек Selenoid к capabilities браузера
        // Эти опции будут переданы в Selenoid при создании сессии браузера
        Configuration.browserCapabilities.setCapability("selenoid:options", options);
    }

    //    Использую BeforeEach и AfterEach чтобы после каждого теста закрывать сессию и чтобы на каждый тест открывалась
//    новая сессия (открывается новый контейнер и соответственно новый браузер) для того, чтобы каждый тест имел своё
//    уникальное имя (это нужно для записи видео с уникальными именами по названию класса тестов)
//    
//    Если нужны отдельные видео для каждого теста, раскомментируйте этот код:
//    @BeforeEach
//    void setupVideoName(TestInfo testInfo) {
//        String ts = java.time.LocalDateTime.now()
//                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
//
//        String cls = testInfo.getTestClass().map(Class::getSimpleName).orElse("Test");
//        String name = testInfo.getDisplayName().replaceAll("[^A-Za-z0-9._-]+", "_");
//
//        String videoName = cls + "_" + name + "_" + ts + ".mp4";
//
//        // достаём существующую map
//        Map<String, Object> options = (Map<String, Object>)
//                Configuration.browserCapabilities.getCapability("selenoid:options");
//
//        if (options == null) {
//            options = new HashMap<>();
//        }
//
//        // добавляем имя видео
//        options.put("videoName", videoName);
//
//        // кладём обратно в capabilities
//        Configuration.browserCapabilities.setCapability("selenoid:options", options);
//    }

    //      используем для того, чтобы закрыть сессию и новая видеозапись имела новое имя
    @AfterEach
    void tearDown(){
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
//    Пришлось заккоментировать метод удаления всех пользователей при использовании многопоточности, иначе тесты падали
/*    @AfterAll
    public static void deleteAllUsers() {
        List<CreateUserResponseModel> users = new CrudRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.ADMIN_USERS)
                .get()
                .extract().as(new TypeRef<List<CreateUserResponseModel>>() {
                });

        amountOfAllUsers += users.size();

        for (CreateUserResponseModel user : users) {
            new CrudRequester(RequestSpecs.adminSpec(),
                    ResponseSpecs.responseReturns200Spec(),
                    Endpoint.ADMIN_USERS)
                    .delete(user.getId());
        }

        users = new CrudRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.ADMIN_USERS)
                .get()
                .extract().as(new TypeRef<List<CreateUserResponseModel>>() {
                });

        if (users.isEmpty()) {
            System.out.println("All users after this test have been deleted successfully");
            System.out.println("Altogether " + amountOfAllUsers + " users have been deleted during this test run");
        }
    }*/
}
