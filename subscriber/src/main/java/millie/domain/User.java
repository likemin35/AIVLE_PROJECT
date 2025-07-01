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
        System.out.println("Processing buy subscription for user: " + this.id);
        
        try {
            // 구독 구매 로직
            if (buySubscriptionCommand != null && buySubscriptionCommand.getIsPurchase() != null) {
                this.setIsPurchase(buySubscriptionCommand.getIsPurchase());
                repository().save(this);
                
                System.out.println("User subscription purchase status updated to: " + this.isPurchase);
            }

            SubscriptionBought subscriptionBought = new SubscriptionBought(this);
            subscriptionBought.publishAfterCommit();
            
            System.out.println("SubscriptionBought event published for user: " + this.id);
            
        } catch (Exception e) {
            System.err.println("Error in buySubscription: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method
    
    //<<< Clean Arch / Port Method
    public void cancelSubscription(CancelSubscriptionCommand cancelSubscriptionCommand) {
        System.out.println("Processing cancel subscription for user: " + this.id);
        
        try {
            // 구독 취소 로직
            if (cancelSubscriptionCommand != null && cancelSubscriptionCommand.getIsPurchase() != null) {
                this.setIsPurchase(cancelSubscriptionCommand.getIsPurchase());
                repository().save(this);
                
                System.out.println("User subscription cancelled, status: " + this.isPurchase);
            }

            SubscriptionCanceled subscriptionCanceled = new SubscriptionCanceled(this);
            subscriptionCanceled.publishAfterCommit();
            
            System.out.println("SubscriptionCanceled event published for user: " + this.id);
            
        } catch (Exception e) {
            System.err.println("Error in cancelSubscription: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void guideFeeConversionSuggestion(SubscriptionFailed subscriptionFailed) {
        System.out.println("Processing fee conversion suggestion...");
        
        try {
            if (subscriptionFailed != null && subscriptionFailed.getUserId() != null) {
                
                // userId로 사용자를 찾아서 유료 전환 제안 메시지 발송
                Long userId = subscriptionFailed.getUserId().getId();
                
                if (userId != null) {
                    repository().findById(userId).ifPresent(user -> {
                        
                        // 사용자에게 유료 전환 제안 메시지 설정
                        String suggestionMessage = String.format(
                            "구독에 실패했습니다. 포인트 부족으로 인해 구독을 진행할 수 없습니다. " +
                            "유료 구독으로 전환하시겠습니까? 책 ID: %s, 구독 ID: %s",
                            subscriptionFailed.getBookId() != null ? subscriptionFailed.getBookId().getId() : "N/A",
                            subscriptionFailed.getId() != null ? subscriptionFailed.getId().toString() : "N/A"
                        );
                        
                        user.setMseeage(suggestionMessage);
                        repository().save(user);
                        
                        System.out.println("Fee conversion suggestion sent to user: " + userId);
                        System.out.println("Message: " + suggestionMessage);
                    });
                } else {
                    System.out.println("User ID is null in subscription failed event");
                }
            } else {
                System.out.println("SubscriptionFailed event or User ID is null");
            }
            
        } catch (Exception e) {
            System.err.println("Error in guideFeeConversionSuggestion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root