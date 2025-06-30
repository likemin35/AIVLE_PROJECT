package millie.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import millie.PointApplication;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Point_table")
//<<< DDD / Aggregate Root
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointId;

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
        point.setUserId(userRegistered.getUserId());
        point.setSubscriptionId(null);
        point.setIsSubscribe(false);

        int basePoint = 1000;
        if ("KT".equalsIgnoreCase(userRegistered.getTelecom())) {
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
        Long userId = subscriptionApplied.getUserId();
        Long subscriptionId = subscriptionApplied.getSubscriptionId();
        int requiredPoint = 100;

        // 1. 현재 유저의 해당 subscriptionId에 대한 포인트 정보 조회
        Optional<Point> optionalPoint = repository().findByUserIdAndSubscriptionId(userId, subscriptionId);

        Point point = null;

        if (optionalPoint.isPresent()) {
            point = optionalPoint.get();
        } else {
            // 2. 없으면 -> subscriptionId = null 로 되어 있는 포인트 정보 기반 복제
            Optional<Point> basePointOpt = repository().findByUserIdAndSubscriptionId(userId, null);
            if (basePointOpt.isPresent()) {
                Point base = basePointOpt.get();

                point = new Point();
                point.setUserId(userId);
                point.setSubscriptionId(subscriptionId);
                point.setPoint(base.getPoint());
                point.setIsSubscribe(base.getIsSubscribe());

                // DB에 새 엔트리 생성
                repository().save(point);
            } else {
                // 유저에 대한 기본 포인트도 없음 → 에러 처리
                System.out.println("❌ 포인트 정보 없음: 유저 ID " + userId);
                return;
            }
        }

        // 3. 정기 구독자인 경우 차감 없이 통과
        if (Boolean.TRUE.equals(point.getIsSubscribe())) {
            PointDecreased event = new PointDecreased(point);
            event.setPoint(0);
            event.setUserId(point.getUserId());
            event.setSubscriptionId(point.getSubscriptionId());
            event.publishAfterCommit();
            return;
        }

        // 4. 차감 가능한지 체크
        if (point.getPoint() < requiredPoint) {
            OutOfPoint event = new OutOfPoint(point);
            event.setPoint(requiredPoint);
            event.setUserId(point.getUserId());
            event.setSubscriptionId(point.getSubscriptionId());
            event.publishAfterCommit();
        } else {
            point.setPoint(point.getPoint() - requiredPoint);
            repository().save(point);

            PointDecreased event = new PointDecreased(point);
            event.setPoint(requiredPoint);
            event.setUserId(point.getUserId());
            event.setSubscriptionId(point.getSubscriptionId());
            event.publishAfterCommit();
        }
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
