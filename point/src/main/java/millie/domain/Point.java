package millie.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import millie.PointApplication;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Point_table")
// <<< DDD / Aggregate Root
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointId;

    private Integer point;

    private Boolean isPurchase;

    private Long userId;

    private Boolean isSubscription;

    private String userName;

    public static PointRepository repository() {
        PointRepository pointRepository = PointApplication.applicationContext.getBean(PointRepository.class);
        return pointRepository;
    }

    // 회원가입 시 포인트 지급 (기본 1000포인트)
    public static void gainRegisterPoint(UserRegistered userRegistered) {
        Point point = new Point();
        point.setUserId(userRegistered.getUserId());
        point.setIsSubscription(false); // ✅ Boolean 인자 넘김
        point.setIsPurchase(false);
        point.setUserName(userRegistered.getUserName());

        int basePoint = 1000;
        if (Boolean.TRUE.equals(userRegistered.getIsKt())) { // ✅ isKt로 변경
            basePoint += 5000;
        }

        point.setPoint(basePoint);
        repository().save(point);

        RegisterPointGained event = new RegisterPointGained(point);
        event.setPoint(String.valueOf(point.getPoint()));
        event.setIsSubscription(point.getIsSubscription()); // ✅ Boolean 넘김
        event.publishAfterCommit();
    }

    // 구독 시 포인트 차감 로직
    public static void decreasePoint(SubscriptionApplied subscriptionApplied) {
        Long userId = subscriptionApplied.getUserId();
        Boolean isSubscription = subscriptionApplied.getIsSubscription();
        int requiredPoint = 100;

        // 1. 현재 isSubscription 값과 일치하는 포인트 목록 조회
        List<Point> targetList = repository().findAllByUserIdAndIsSubscription(userId, isSubscription);

        Point point;

        if (!targetList.isEmpty()) {
            targetList.sort(Comparator.comparing(Point::getPointId).reversed());
            point = targetList.get(0); // 최신 포인트 사용
        } else {
            // 2. 없다면 isSubscription=false인 포인트를 복제
            List<Point> baseList = repository().findAllByUserIdAndIsSubscription(userId, false);
            if (baseList.isEmpty()) {
                System.out.println("❌ 포인트 정보 없음: 유저 ID " + userId);
                return;
            }

            baseList.sort(Comparator.comparing(Point::getPointId).reversed());
            Point base = baseList.get(0);

            point = new Point();
            point.setUserId(userId);
            point.setIsSubscription(true);
            point.setPoint(base.getPoint());
            point.setIsPurchase(base.getIsPurchase());

            repository().save(point);
        }

        // 3. 정기 구독자 → 차감 없음
        if (Boolean.TRUE.equals(point.getIsSubscription())) {
            PointDecreased event = new PointDecreased(point);
            event.setPoint(0);
            event.setUserId(point.getUserId());
            event.setIsSubscription(point.getIsSubscription());
            event.publishAfterCommit();
            return;
        }

        // 4. 포인트 부족 여부 체크
        if (point.getPoint() < requiredPoint) {
            OutOfPoint event = new OutOfPoint(point);
            event.setPoint(requiredPoint);
            event.setUserId(point.getUserId());
            event.setIsSubscription(point.getIsSubscription());
            event.publishAfterCommit();
        } else {
            point.setPoint(point.getPoint() - requiredPoint);
            repository().save(point);

            PointDecreased event = new PointDecreased(point);
            event.setPoint(requiredPoint);
            event.setUserId(point.getUserId());
            event.setIsSubscription(point.getIsSubscription());
            event.publishAfterCommit();
        }
    }

    // 포인트 충전 처리
    public void buyPoint(int amount) {
        this.point += amount;

        PointBought event = new PointBought(this);
        event.setPoint(amount);
        event.setUserId(this.userId);
        event.publishAfterCommit();
    }

    // 포인트 차감 처리
    public void usePoint(int amount) {
        if (this.point < amount) {
            throw new RuntimeException("잔여 포인트가 부족합니다.");
        }

        this.point -= amount;
    }
}
// >>> DDD / Aggregate Root
