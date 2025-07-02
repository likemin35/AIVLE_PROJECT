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
        point.setIsSubscription(false);
        point.setIsPurchase(false);
        point.setUserName(userRegistered.getUserName());

        int basePoint = 1000;
        if (Boolean.TRUE.equals(userRegistered.getIsKt())) {
            basePoint += 5000;
        }

        point.setPoint(basePoint);
        repository().save(point);

        // ❌ 이 부분 제거 또는 주석처리
        // RegisterPointGained event = new RegisterPointGained(point);
        // event.setPoint(String.valueOf(point.getPoint()));
        // event.setIsSubscription(point.getIsSubscription());
        // event.publishAfterCommit();

        System.out.println("✅ 포인트 지급 완료: userId=" + point.getUserId() +
                ", point=" + point.getPoint() + "P");
    }

    // 구독 시 포인트 차감 로직
    public static void decreasePoint(DecreasePoint decreasePoint) {
        Long userId = decreasePoint.getUserId();
        Long bookId = decreasePoint.getBookId(); // ✅ bookId 추가
        int requiredPoint = 1100;

        System.out.println(">>> [포인트 차감 요청] userId=" + userId + ", bookId=" + bookId + ", 필요포인트=" + requiredPoint);

        // 1. 사용자의 포인트 조회 (isSubscription=false 기준)
        List<Point> userPoints = repository().findAllByUserIdAndIsSubscription(userId, false);

        if (userPoints.isEmpty()) {
            System.out.println("❌ 포인트 정보 없음: 유저 ID " + userId);

            // ✅ OutOfPoint 이벤트에 bookId 포함
            OutOfPoint event = new OutOfPoint();
            event.setUserId(userId);
            event.setBookId(bookId); // ✅ bookId 설정
            event.setPoint(requiredPoint);
            event.setIsSubscription(false);
            event.publishAfterCommit();

            System.out.println(">>> [이벤트 발행] OutOfPoint: userId=" + userId + ", bookId=" + bookId);
            return;
        }

        // 최신 포인트 사용
        userPoints.sort(Comparator.comparing(Point::getPointId).reversed());
        Point point = userPoints.get(0);

        System.out.println(">>> 현재 포인트: " + point.getPoint() + "P");

        // 2. 포인트 부족 여부 체크
        if (point.getPoint() < requiredPoint) {
            System.out.println("❌ 포인트 부족: 현재 " + point.getPoint() + "P, 필요 " + requiredPoint + "P");

            // ✅ OutOfPoint 이벤트에 bookId 포함
            OutOfPoint event = new OutOfPoint(point);
            event.setPoint(requiredPoint);
            event.setUserId(point.getUserId());
            event.setBookId(bookId); // ✅ bookId 설정
            event.setIsSubscription(point.getIsSubscription());
            event.publishAfterCommit();

            System.out.println(">>> [이벤트 발행] OutOfPoint: userId=" + userId + ", bookId=" + bookId);
        } else {
            // 포인트 차감
            point.setPoint(point.getPoint() - requiredPoint);
            repository().save(point);

            System.out.println("✅ 포인트 차감 완료: " + point.getPoint() + "P 남음");

            // ✅ PointDecreased 이벤트에 bookId 포함
            PointDecreased event = new PointDecreased(point);
            event.setPoint(requiredPoint);
            event.setUserId(point.getUserId());
            event.setBookId(bookId); // ✅ bookId 설정
            event.setIsSubscription(point.getIsSubscription());
            event.publishAfterCommit();

            System.out.println(">>> [이벤트 발행] PointDecreased: userId=" + userId + ", bookId=" + bookId + ", 차감="
                    + requiredPoint + "P");
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

    public static void chargePoint(ChargePoint chargePoint) {
        Long userId = chargePoint.getUserId();
        Integer amount = chargePoint.getAmount();

        System.out.println(">>> [포인트 충전 요청] userId=" + userId + ", 충전액=" + amount + "P");

        // 사용자의 포인트 조회
        List<Point> userPoints = repository().findAllByUserIdAndIsSubscription(userId, false);

        if (userPoints.isEmpty()) {
            // 새 포인트 레코드 생성
            Point newPoint = Point.builder()
                    .userId(userId)
                    .isSubscription(false)
                    .point(amount)
                    .isPurchase(false)
                    .build();
            repository().save(newPoint);
            System.out.println("✅ 새 포인트 생성: " + amount + "P");
        } else {
            // 최신 포인트에 충전
            userPoints.sort(Comparator.comparing(Point::getPointId).reversed());
            Point point = userPoints.get(0);

            System.out.println(">>> 충전 전 포인트: " + point.getPoint() + "P");
            point.setPoint(point.getPoint() + amount);
            repository().save(point);
            System.out.println("✅ 포인트 충전 완료: " + point.getPoint() + "P");
        }
    }
}
// >>> DDD / Aggregate Root
