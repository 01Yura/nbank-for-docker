package ui.pages;

import api.models.CreateUserRequestModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.*;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Getter
public abstract class BasePage<T extends BasePage> {
    protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));
    protected SelenideElement userNameInfoIcon = $(Selectors.byClassName("user-name"));
    protected SelenideElement logoutButton = $(Selectors.byText("\uD83D\uDEAA Logout"));

    public abstract String url();

    //    метод open в данном случае это фабрика, которой мы передаем url и тип класса того обьекта, который мы ходим
    //    создать
    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

//    Метод getPage(Class<T> pageClass) обычно используется в паттерне Page Object для перехода между страницами.
//    После какого-то действия (например, логина) ты оказываешься на новой странице, и тебе нужно получить объект,
//    который будет представлять эту новую страницу.
//    Он создаёт новый экземпляр нужного класса страницы.
//    Возвращает его, чтобы ты мог сразу работать с элементами этой страницы.
    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlertMessageAndAccept(String... bankAlerts) {
        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        for (String expected : bankAlerts) {
            if (alertText.contains(expected)) {
                alert.accept();
                return (T) this;
            }
        }
        // Если ни один вариант не подошёл — выбрасываем ошибку
        throw new AssertionError("Alert text does not contain any of the expected variants. Actual: " + alertText);
    }
//      перегрузил метод, чтобы проверять алерт после депозита
    public T checkAlertMessageAndAccept(String bankAlerts, Float moneyAmount, String accountNumber) {
        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText).contains(bankAlerts).contains(moneyAmount.toString()).contains(accountNumber);
        alert.accept();
        return (T) this;
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthToken = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthToken);
    }

    //        это перегруженный метод authAsUser, используем чтобы сократить код, так как в нем вызываем первый authAsUser
    public static void authAsUser(CreateUserRequestModel createUserRequestModel) {
        authAsUser(createUserRequestModel.getUsername(), createUserRequestModel.getPassword());
    }

//    метод преобразующий ElementCollection -> List<BaseElement>
    protected <T extends BaseElement> List<T> generatePageElements(ElementsCollection elementsCollection, Function<SelenideElement, T> constructor){
        return elementsCollection.stream().map(constructor).toList();
    }
}