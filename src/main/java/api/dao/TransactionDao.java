package api.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDao {
    private Long id;
    private Long amount;
    private String type;
    private String timestamp;
    private Long accountId;
    private Long relatedAccountId;
}
