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
    private BookId bookId;

    @Embedded
    private UserId userId;

    @PostPersist
    public void onPostPersist() {
        System.out.println("✅ Subscription persisted: ID = " + this.getId());

        SubscriptionApplied applied = new SubscriptionApplied(this);
        applied.publishAfterCommit();
    }

    public static SubscriptionRepository repository() {
        SubscriptionRepository subscriptionRepository = SubscriberApplication.applicationContext.getBean(
                SubscriptionRepository.class);
        return subscriptionRepository;
    }

    // <<< Clean Arch / Port Method
    public static void failSubscription(OutOfPoint outOfPoint) {
        if (outOfPoint.getUserId() == null)
            return;
        String userId = outOfPoint.getUserId().toString();
        repository().findByUserId(userId).ifPresent(subscription -> {
            subscription.setIsSubscription(false); // 구독 취소 처리
            repository().save(subscription);

            SubscriptionFailed subscriptionFailed = new SubscriptionFailed(subscription);
            subscriptionFailed.publishAfterCommit();

            System.out.println("❌ Subscription failed due to out of point. userId = " + userId);
        });
    }
    // >>> Clean Arch / Port Method

}
// >>> DDD / Aggregate Root
