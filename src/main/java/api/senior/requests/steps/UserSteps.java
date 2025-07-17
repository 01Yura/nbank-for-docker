package api.senior.requests.steps;


import api.senior.models.*;
import api.senior.requests.skeleton.requesters.Endpoint;
import api.senior.requests.skeleton.requesters.ValidatedCrudRequester;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;

import java.util.List;


public class UserSteps {
    private static CreateUserRequestModel createUserRequestModel;
    private static CreateAccountResponseModel createAccountResponseModel;
    private static UserDepositMoneyResponseModel userDepositMoneyResponseModel;
    private String username;
    private String password;
    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static CreateUserRequestModel getCreateUserRequestModel() {
        return UserSteps.createUserRequestModel;
    }

    public static CreateAccountResponseModel getCreateAccountResponseModel() {
        return UserSteps.createAccountResponseModel;
    }

    public static UserDepositMoneyResponseModel getUserDepositMoneyResponseModel() {
        return UserSteps.userDepositMoneyResponseModel;
    }

    public static CreateAccountResponseModel createAccount() {
        createUserRequestModel = AdminSteps.createUser();

        createAccountResponseModel =
                new ValidatedCrudRequester<CreateAccountResponseModel>(RequestSpecs.userSpec(createUserRequestModel.getUsername(),
                        createUserRequestModel.getPassword()),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ACCOUNTS)
                        .post(null);

        return createAccountResponseModel;
    }

    public static UserDepositMoneyResponseModel depositMoney(Float depositPerCycle, Float depositThreshold) {
        createAccountResponseModel = UserSteps.createAccount();

        UserDepositMoneyRequestModel userDepositMoneyRequestModel = UserDepositMoneyRequestModel.builder()
                .id(createAccountResponseModel.getId())
                .balance(depositPerCycle)
                .build();

        Float currentBalance = createAccountResponseModel.getBalance();
        while (currentBalance < depositThreshold) {
            userDepositMoneyResponseModel = new ValidatedCrudRequester<UserDepositMoneyResponseModel>(
                    RequestSpecs.userSpec(createUserRequestModel.getUsername(), createUserRequestModel.getPassword()),
                    ResponseSpecs.responseReturns200Spec(),
                    Endpoint.ACCOUNTS_DEPOSIT)
                    .post(userDepositMoneyRequestModel);

            currentBalance = userDepositMoneyResponseModel.getBalance();
        }

        return userDepositMoneyResponseModel;
    }

    public List<CreateAccountResponseModel> getAllAccounts() {
        return new ValidatedCrudRequester<CreateAccountResponseModel>(
                RequestSpecs.userSpec(this.username, this.password),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.CUSTOMER_ACCOUNTS)
                .getAll(CreateAccountResponseModel[].class);
    }
}
