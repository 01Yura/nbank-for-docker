package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCustomerProfileResponseModel extends BaseModel {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<CreateAccountResponseModel> accounts;
}
