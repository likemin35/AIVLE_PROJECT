package millie.infra;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import javax.transaction.Transactional;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Comparator;

@RestController
@RequestMapping("/points")
@Transactional
@CrossOrigin(origins = "*")
public class PointController {

    @Autowired
    PointRepository pointRepository;

    // 포인트 충전 API 추가
    @PostMapping("/charge")
    public ResponseEntity<?> chargePoint(@RequestBody ChargePointRequest request) {
        try {
            ChargePoint chargePoint = new ChargePoint();
            chargePoint.setUserId(request.getUserId());
            chargePoint.setAmount(request.getAmount());
            
            Point.chargePoint(chargePoint);
            
            return ResponseEntity.ok("포인트 충전이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("포인트 충전 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 포인트 구매
    @PostMapping("/purchase")
    public void purchasePoint(@RequestBody PurchasePointRequest request) {
        Long userId = request.getUserId();
        int amount = request.getAmount();

        // isSubscription=false인 포인트들 중 가장 최신 하나 선택
        List<Point> pointList = pointRepository.findAllByUserIdAndIsSubscription(userId, false);

        if (!pointList.isEmpty()) {
            // 최신 순 정렬
            pointList.sort(Comparator.comparing(Point::getPointId).reversed());
            Point point = pointList.get(0);
            point.setPoint(point.getPoint() + amount); // 누적
            pointRepository.save(point);
        } else {
            // 없으면 새로 생성
            Point newPoint = Point.builder()
                    .userId(userId)
                    .isSubscription(false)
                    .point(amount)
                    .isPurchase(false)
                    .build();
            pointRepository.save(newPoint);
        }
    }

    // 포인트 조회
    @GetMapping("/search")
    public GetPointQuery searchPoint(@RequestParam Long userId, @RequestParam Boolean isSubscription) {
        List<Point> pointList = pointRepository.findAllByUserIdAndIsSubscription(userId, isSubscription);

        if (pointList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저의 포인트 정보가 없습니다.");
        }

        pointList.sort(Comparator.comparing(Point::getPointId).reversed());
        Point point = pointList.get(0);

        GetPointQuery result = new GetPointQuery();
        result.setPointId(point.getPointId());
        result.setPoint(point.getPoint());
        result.setUserId(point.getUserId());
        result.setIsSubscription(point.getIsSubscription());
        result.setIsPurchase(point.getIsPurchase());
        return result;
    }

    @PostMapping("/use") // 포인트 차감
    public ResponseEntity<?> usePoint(@RequestBody PurchasePointRequest request) {
        Long userId = request.getUserId();
        Boolean isSubscription = request.getIsSubscription();
        if (isSubscription == null) isSubscription = false;  // 기본값 처리
        int amount = request.getAmount();

        List<Point> targetList = pointRepository.findAllByUserIdAndIsSubscription(userId, isSubscription);
        Point point;

        if (!targetList.isEmpty()) {
            targetList.sort(Comparator.comparing(Point::getPointId).reversed());
            point = targetList.get(0);
        } else {
            // isSubscription=false로 된 포인트에서 복제
            List<Point> baseList = pointRepository.findAllByUserIdAndIsSubscription(userId, false);
            if (baseList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저의 포인트 정보가 없습니다.");
            }

            baseList.sort(Comparator.comparing(Point::getPointId).reversed());
            Point base = baseList.get(0);

            point = Point.builder()
                    .userId(userId)
                    .isSubscription(isSubscription)
                    .point(base.getPoint())
                    .isPurchase(base.getIsPurchase())
                    .build();

            pointRepository.save(point);
            System.out.println("✅ 새로 저장된 포인트 (복제): " + point);
        }

        if (Boolean.TRUE.equals(point.getIsSubscription())) {
            return ResponseEntity.ok("정기 구독자: 포인트 차감 없음");
        }

        if (point.getPoint() < amount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("포인트 부족");
        }

        point.setPoint(point.getPoint() - amount);
        pointRepository.save(point);

        System.out.println("✅ 포인트 차감 후 상태: " + point);

        return ResponseEntity.ok("포인트가 성공적으로 차감되었습니다.");
    }

    // 요청 DTO 클래스들
    public static class ChargePointRequest {
        private Long userId;
        private Integer amount;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getAmount() { return amount; }
        public void setAmount(Integer amount) { this.amount = amount; }
    }
}