package ui.middle.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    FAILD_TO_CREATE_USER("Failed to create user"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: ");
    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}
