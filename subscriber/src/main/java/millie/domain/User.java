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
import millie.domain.BuySubscriptionCommand;

@Entity
@Table(name = "User_table")
@Data
// <<< DDD / Aggregate Root
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
    private String userName;
    private String phoneNumber;
    private Boolean isPurchase;
    private String message;
    @Embedded
    private UserId userId;

    @PostPersist
    public void onPostPersist() {
        UserRegistered userRegistered = new UserRegistered(this);
        userRegistered.publishAfterCommit();
    }

    public static UserRepository repository() {
        UserRepository userRepository = SubscriberApplication.applicationContext.getBean(
                UserRepository.class);
        return userRepository;
    }

    public void buySubscription(BuySubscriptionCommand command) {
        this.setIsPurchase(true); // 유료 구독 처리
        SubscriptionBought event = new SubscriptionBought(this);
        event.publishAfterCommit();
    }

    // >>> Clean Arch / Port Method
    // <<< Clean Arch / Port Method
    public void cancelSubscription(CancelSubscriptionCommand cancelSubscriptionCommand) {
        // 구독 상태 해제
        this.setIsPurchase(false);

        // 구독 취소 이벤트 발행
        SubscriptionCanceled subscriptionCanceled = new SubscriptionCanceled(this);
        subscriptionCanceled.publishAfterCommit();
    }

    // >>> Clean Arch / Port Method

    // <<< Clean Arch / Port Method
    public static void guideFeeConversionSuggestion(SubscriptionFailed subscriptionFailed) {
        Long userId = subscriptionFailed.getUserId();

        repository().findById(userId).ifPresent(user -> {
            user.setMessage("KT 걷다가서재 이용 시 요금제 전환을 추천합니다.");
            repository().save(user);

            // 콘솔 출력 확인용
            System.out.println(
                    "요금 전환 안내: 사용자 ID " + user.getId() +
                            " → 메시지 저장 완료: " + user.getMessage());
        });
    }
    // >>> Clean Arch / Port Method

}
// >>> DDD / Aggregate Root
