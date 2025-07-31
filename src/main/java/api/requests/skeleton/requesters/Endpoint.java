package api.requests.skeleton.requesters;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USERS("/admin/users", CreateUserRequestModel.class, CreateUserResponseModel.class),
    ACCOUNTS("/accounts", BaseModel.class, CreateAccountResponseModel.class),
    CUSTOMER_ACCOUNTS("/customer/accounts", BaseModel.class, GetCustomerAccountsResponseModel.class),
    AUTH_LOGIN("/auth/login", LoginUserRequestModel.class, LoginUserResponseModel.class),
    GET_CUSTOMER_PROFILE("/customer/profile", BaseModel.class, GetCustomerProfileResponseModel.class),
    UPDATE_CUSTOMER_PROFILE("/customer/profile", BaseModel.class, UpdateCustomerNameResponseModel.class),
    ACCOUNTS_DEPOSIT("/accounts/deposit", UserDepositMoneyRequestModel.class, UserDepositMoneyResponseModel.class),
    ACCOUNTS_TRANSFER("/accounts/transfer", TransferMoneyRequestModel.class, TransferMoneyResponseModel.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}

