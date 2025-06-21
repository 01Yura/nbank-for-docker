package senior.requests.steps;


import senior.models.CreateAccountResponseModel;
import senior.models.CreateUserRequestModel;
import senior.models.UserDepositMoneyRequestModel;
import senior.models.UserDepositMoneyResponseModel;
import senior.requests.skeleton.requesters.Endpoint;
import senior.requests.skeleton.requesters.ValidatedCrudRequester;
import senior.specs.RequestSpecs;
import senior.specs.ResponseSpecs;


public class UserSteps {

    private static CreateUserRequestModel createUserRequestModel;
    private static CreateAccountResponseModel createAccountResponseModel;
    private static UserDepositMoneyResponseModel userDepositMoneyResponseModel;

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
}
