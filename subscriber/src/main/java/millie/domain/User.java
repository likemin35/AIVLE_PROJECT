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
import millie.domain.UserRegistered;

@Entity
@Table(name = "User_table")
@Data
//<<< DDD / Aggregate Root
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private String userName;

    private Boolean isPurchase;

    private String mseeage;

    @PostPersist
    public void onPostPersist() {
        UserRegistered userRegistered = new UserRegistered(this);
        userRegistered.publishAfterCommit();
    }

    public static UserRepository repository() {
        UserRepository userRepository = SubscriberApplication.applicationContext.getBean(
            UserRepository.class
        );
        return userRepository;
    }

    //<<< Clean Arch / Port Method
    public void buySubscription(BuySubscriptionCommand buySubscriptionCommand) {
        //implement business logic here:

        SubscriptionBought subscriptionBought = new SubscriptionBought(this);
        subscriptionBought.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void cancelSubscription(
        CancelSubscriptionCommand cancelSubscriptionCommand
    ) {
        //implement business logic here:

        SubscriptionCanceled subscriptionCanceled = new SubscriptionCanceled(
            this
        );
        subscriptionCanceled.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void guideFeeConversionSuggestion(
        SubscriptionFailed subscriptionFailed
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        User user = new User();
        repository().save(user);

        */

        /** Example 2:  finding and process
        
        // if subscriptionFailed.bookIduserId exists, use it
        
        // ObjectMapper mapper = new ObjectMapper();
        // Map<Long, Object> subscriptionMap = mapper.convertValue(subscriptionFailed.getBookId(), Map.class);
        // Map<Long, Object> subscriptionMap = mapper.convertValue(subscriptionFailed.getUserId(), Map.class);

        repository().findById(subscriptionFailed.get???()).ifPresent(user->{
            
            user // do something
            repository().save(user);


         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
