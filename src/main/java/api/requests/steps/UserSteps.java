package api.requests.steps;


import api.models.*;
import api.requests.skeleton.requesters.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helper.StepLogger;

import java.util.List;


public class UserSteps {
    private CreateAccountResponseModel account;
    private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDepositMoneyResponseModel depositMoney(Float depositPerCycle, Float depositThreshold) {
        UserDepositMoneyRequestModel userDepositMoneyRequestModel = UserDepositMoneyRequestModel.builder()
                .id(account.getId())
                .balance(depositPerCycle)
                .build();

        UserDepositMoneyResponseModel userDepositMoneyResponseModel = null;
        Float currentBalance = account.getBalance();
        while (currentBalance < depositThreshold) {
            userDepositMoneyResponseModel = new ValidatedCrudRequester<UserDepositMoneyResponseModel>(
                    RequestSpecs.userSpec(this.username, this.password),
                    ResponseSpecs.responseReturns200Spec(),
                    Endpoint.ACCOUNTS_DEPOSIT)
                    .post(userDepositMoneyRequestModel);

            currentBalance = userDepositMoneyResponseModel.getBalance();
        }
        return userDepositMoneyResponseModel;
    }

    public List<GetCustomerAccountsResponseModel> getAllAccounts() {
        return StepLogger.log("User " + this.username + " get all accounts", () -> {
            Thread.sleep(1000);
            return new ValidatedCrudRequester<GetCustomerAccountsResponseModel>(
                    RequestSpecs.userSpec(this.username, this.password),
                    ResponseSpecs.responseReturns200Spec(),
                    Endpoint.CUSTOMER_ACCOUNTS)
                    .getAll(GetCustomerAccountsResponseModel[].class);
        });
    }

    public GetCustomerProfileResponseModel getUserInfo() {
        return new ValidatedCrudRequester<GetCustomerProfileResponseModel>(
                RequestSpecs.userSpec(this.username, this.password),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.GET_CUSTOMER_PROFILE)
                .get();
    }

    public CreateAccountResponseModel createAccount() {
        account = new ValidatedCrudRequester<CreateAccountResponseModel>(
                RequestSpecs.userSpec(this.username, this.password),
                ResponseSpecs.responseReturns201Spec(),
                Endpoint.ACCOUNTS)
                .post(null);

        return account;
    }

    public GetCustomerAccountsResponseModel getAccountWIthSpecificNumber(String accountNumber) {
        return this.getAllAccounts().stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst().orElseThrow(() -> new RuntimeException("Аккаунт с номером " + accountNumber + " не найден"));
    }

    public UserDepositMoneyResponseModel depositMoney(CreateAccountResponseModel account, Float deposit) {
        UserDepositMoneyRequestModel userDepositMoneyRequestModel = UserDepositMoneyRequestModel.builder()
                .id(account.getId())
                .balance(deposit)
                .build();

        return new ValidatedCrudRequester<UserDepositMoneyResponseModel>(
                RequestSpecs.userSpec(this.username, this.password),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.ACCOUNTS_DEPOSIT)
                .post(userDepositMoneyRequestModel);
    }
}
