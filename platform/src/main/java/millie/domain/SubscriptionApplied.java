package millie.domain;

import java.util.*;
import lombok.*;
import millie.domain.*;
import millie.infra.AbstractEvent;

@Data
@ToString
public class SubscriptionApplied extends AbstractEvent {

    private Long id;
    private Long bookId;
    private Long userId;
    private Boolean isSubscription;
    private Date startSubscription;
    private Date endSubscription;
    private String pdfPath;
}
