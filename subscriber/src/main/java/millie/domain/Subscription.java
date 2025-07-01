package millie.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import millie.SubscriberApplication;
import millie.domain.SubscriptionApplied;
import millie.domain.SubscriptionFailed;

@Entity
@Table(name = "Subscription_table")
@Data
// <<< DDD / Aggregate Root
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean isSubscription;
    private Date rentalstart;
    private Date rentalend;
    private String webUrl;
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "book_id_id"))
    private BookId bookId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id_id"))
    private UserId userId;

    // @PostPersist
    // public void onPostPersist() {
    // SubscriptionApplied applied = new SubscriptionApplied(this);
    // applied.publishAfterCommit();
    // }

    public static SubscriptionRepository repository() {
        SubscriptionRepository subscriptionRepository = SubscriberApplication.applicationContext.getBean(
                SubscriptionRepository.class);
        return subscriptionRepository;
    }

    // <<< Clean Arch / Port Method
    public static void failSubscription(OutOfPoint outOfPoint) {
        if (outOfPoint.getUserId() == null || outOfPoint.getBookId() == null)
            return;

        SubscriptionFailed subscriptionFailed = new SubscriptionFailed();
        subscriptionFailed.setUserId(outOfPoint.getUserId());
        subscriptionFailed.setBookId(outOfPoint.getBookId());
        subscriptionFailed.setIsSubscription(false);
        subscriptionFailed.setMessage("포인트 부족으로 대여 실패");

        subscriptionFailed.publishAfterCommit();

        System.out.println(">>> [이벤트 발행] SubscriptionFailed: userId = "
                + outOfPoint.getUserId() + ", bookId = " + outOfPoint.getBookId());
    }
    // >>> Clean Arch / Port Method

}
// >>> DDD / Aggregate Root
