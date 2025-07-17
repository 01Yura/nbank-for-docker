package api.senior.models;

import api.senior.configs.Config;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.senior.requests.skeleton.interfaces.GeneratingRule;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestModel extends BaseModel {
    @GeneratingRule(regex = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRule(regex = "^[A-Z]{3,5}[a-z]{3,5}[0-9]{3,5}[!@#$%^&*]{3,5}$")
    private String password;
    @GeneratingRule(regex = "^USER$")
    private String role;

    public static CreateUserRequestModel getAdmin(){
        return CreateUserRequestModel.builder()
                .username(Config.getProperty("admin.username"))
                .password(Config.getProperty("admin.password"))
                .build();
    }
}
