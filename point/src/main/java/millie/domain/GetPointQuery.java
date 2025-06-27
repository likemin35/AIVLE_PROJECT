package millie.domain;

import java.util.Date;
import lombok.Data;

@Data
public class GetPointQuery {

    private Long id;
    private Integer point;
    private Boolean isSubscribe;
    private Long userId;
    private Long subscriptionId;
}
