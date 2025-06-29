package millie.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import millie.PointApplication;

@Entity
@Table(name = "Point_table")
@Data
//<<< DDD / Aggregate Root
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer point;

    private Boolean isSubscribe;

    private Long userId;

    private Long subscriptionId;

    public static PointRepository repository() {
        PointRepository pointRepository = PointApplication.applicationContext.getBean(PointRepository.class);
        return pointRepository;
    }

    // 회원가입 시 포인트 지급 (기본 1000포인트)
    public static void gainRegisterPoint(UserRegistered userRegistered) {
        Point point = new Point();
        point.setUserId(userRegistered.getId());
        point.setSubscriptionId(null);
        point.setIsSubscribe(false);

        int basePoint = 1000;
        if ("KT".equalsIgnoreCase(userRegistered.getTelco())) {
            basePoint += 5000; // KT 고객은 5000포인트 추가
        }

        point.setPoint(basePoint);
        repository().save(point);

        RegisterPointGained event = new RegisterPointGained(point);
        event.setPoint(String.valueOf(point.getPoint()));
        event.setIsSubscription(String.valueOf(point.getIsSubscribe()));
        event.publishAfterCommit();
    }


    // 구독 시 포인트 차감 로직 (포인트 부족 시 OutOfPoint 이벤트)
    public static void decreasePoint(SubscriptionApplied subscriptionApplied) {
        repository().findByUserIdAndSubscriptionId(subscriptionApplied.getUserId(), subscriptionApplied.getId())
            .ifPresent(point -> {
                int requiredPoint = 100; // 구독에 필요한 포인트 예시

                if (point.getPoint() < requiredPoint) {
                    // 포인트 부족
                    OutOfPoint event = new OutOfPoint(point);
                    event.setPoint(requiredPoint);
                    event.setUserId(point.getUserId());
                    event.setSubscriptionId(point.getSubscriptionId());
                    event.publishAfterCommit();
                } else {
                    // 포인트 차감
                    point.setPoint(point.getPoint() - requiredPoint);
                    point.setIsSubscribe(true); // 구독 완료 처리
                    repository().save(point);

                    PointDecreased event = new PointDecreased(point);
                    event.setPoint(requiredPoint);
                    event.setUserId(point.getUserId());
                    event.publishAfterCommit();
                }
            });
    }

    //  포인트 구매 처리 (Command 기반)
    public void buyPoint(int amount) {
        this.point += amount;

        PointBought event = new PointBought(this);
        event.setPoint(amount);
        event.setUserId(this.userId);
        event.publishAfterCommit();
    }

    //  포인트 차감 처리 (예: 결제, 상품 구매 시)
    public void usePoint(int amount) {
        if (this.point < amount) {
            throw new RuntimeException("잔여 포인트가 부족합니다.");
        }

        this.point -= amount;
    }
}
//>>> DDD / Aggregate Root
