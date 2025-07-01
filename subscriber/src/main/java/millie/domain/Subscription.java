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
    }

    public static SubscriptionRepository repository() {
        SubscriptionRepository subscriptionRepository = SubscriberApplication.applicationContext.getBean(
            SubscriptionRepository.class
        );
        return subscriptionRepository;
    }

    //<<< Clean Arch / Port Method
    public static void failSubscription(OutOfPoint outOfPoint) {
        System.out.println("Processing subscription failure due to out of points...");
        
        try {
            // 포인트 부족으로 인한 구독 실패 처리
            if (outOfPoint != null) {
                System.out.println("OutOfPoint event received - User: " + outOfPoint.getUserId() + 
                                 ", SubscriptionId: " + outOfPoint.getSubscriptionId());
                
                // 새로운 구독 실패 기록 생성
                Subscription failedSubscription = new Subscription();
                failedSubscription.setIsSubscription(false);
                
                // UserId 설정 - OutOfPoint의 userId는 Long 타입
                if (outOfPoint.getUserId() != null) {
                    UserId userId = new UserId();
                    userId.setId(outOfPoint.getUserId());
                    failedSubscription.setUserId(userId);
                }
                
                // BookId 설정 (기본값으로 설정, 실제로는 어디서 가져와야 하는지 확인 필요)
                BookId bookId = new BookId();
                bookId.setId(1L); // 기본값, 실제 구현에서는 적절한 값 설정 필요
                failedSubscription.setBookId(bookId);
                
                // 현재 시간으로 실패 시간 설정
                failedSubscription.setRentalstart(new Date());
                failedSubscription.setRentalend(new Date());
                
                // 저장
                repository().save(failedSubscription);
                System.out.println("Subscription failure recorded with ID: " + failedSubscription.getId());

                // 구독 실패 이벤트 발행
                SubscriptionFailed subscriptionFailed = new SubscriptionFailed(failedSubscription);
                subscriptionFailed.publishAfterCommit();
                
                System.out.println("SubscriptionFailed event published");
            }
            
        } catch (Exception e) {
            System.err.println("Error in failSubscription: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root