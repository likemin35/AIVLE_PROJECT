package millie.domain;

import java.util.Date;
import lombok.Data;

@Data
public class GetSubscriptionQuery {

    private Long id;
    private Boolean isSubscription;
    private Date rentalstart;
    private Date endSubscription;
    private String webUrl;
    private String subscribe;
    private BookId bookId;
    private UserId userId;
}
