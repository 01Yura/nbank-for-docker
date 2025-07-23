package ui.senior.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class UserBage extends BaseElement{
    private String username;
    private String role;

    public UserBage(SelenideElement element) {
        super(element);
        username = Arrays.stream(element.getText().split("\n")).findFirst().get();
        role = Arrays.stream(element.getText().split("\n")).skip(1).findFirst().get();
    }

}
