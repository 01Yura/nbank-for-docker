package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage> {
    private final SelenideElement enterNewNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private final SelenideElement saveChangesButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));


    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage changeName(String name) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        enterNewNameInput.sendKeys(name);
        saveChangesButton.click();
        return this;
    }
}
