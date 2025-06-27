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
//<<< DDD / Aggregate Root
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
        SubscriptionApplied subscriptionApplied = new SubscriptionApplied(this);
        subscriptionApplied.publishAfterCommit();

        SubscriptionFailed subscriptionFailed = new SubscriptionFailed(this);
        subscriptionFailed.publishAfterCommit();
    }

    public static SubscriptionRepository repository() {
        SubscriptionRepository subscriptionRepository = SubscriberApplication.applicationContext.getBean(
            SubscriptionRepository.class
        );
        return subscriptionRepository;
    }

    //<<< Clean Arch / Port Method
    public static void failSubscription(OutOfPoint outOfPoint) {
        //implement business logic here:

        /** Example 1:  new item 
        Subscription subscription = new Subscription();
        repository().save(subscription);

        SubscriptionFailed subscriptionFailed = new SubscriptionFailed(subscription);
        subscriptionFailed.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        // if outOfPoint.userIdsubscriptionId exists, use it
        
        // ObjectMapper mapper = new ObjectMapper();
        // Map<Long, Object> pointMap = mapper.convertValue(outOfPoint.getUserId(), Map.class);
        // Map<Long, Object> pointMap = mapper.convertValue(outOfPoint.getSubscriptionId(), Map.class);

        repository().findById(outOfPoint.get???()).ifPresent(subscription->{
            
            subscription // do something
            repository().save(subscription);

            SubscriptionFailed subscriptionFailed = new SubscriptionFailed(subscription);
            subscriptionFailed.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
