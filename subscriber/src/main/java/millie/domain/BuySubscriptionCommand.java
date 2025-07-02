package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class BuySubscriptionCommand {

    private Boolean isPurchase;
    private Long userId;
}
