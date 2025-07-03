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
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/points")
@Transactional
@CrossOrigin(origins = "*") // CORS 설정 추가
public class PointController {

    @Autowired
    PointRepository pointRepository;

    // 포인트 충전 API
    @PostMapping("/charge")
    public ResponseEntity<Map<String, Object>> chargePoint(@RequestBody ChargePointRequest request) {
        try {
            ChargePoint chargePoint = new ChargePoint();
            chargePoint.setUserId(request.getUserId());
            chargePoint.setAmount(request.getAmount());
            
            Point.chargePoint(chargePoint);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "포인트 충전이 완료되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "포인트 충전 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 포인트 구매
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchasePoint(@RequestBody PurchasePointRequest request) {
        try {
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

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "포인트 구매가 완료되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "포인트 구매 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 포인트 조회 - 프론트엔드 기대 형식에 맞춤
    @GetMapping("/search")
    public ResponseEntity<GetPointQuery> searchPoint(@RequestParam Long userId, @RequestParam Boolean isSubscription) {
        try {
            List<Point> pointList = pointRepository.findAllByUserIdAndIsSubscription(userId, isSubscription);

            if (pointList.isEmpty()) {
                // 포인트가 없는 경우 0포인트 기본 객체 반환
                GetPointQuery result = new GetPointQuery();
                result.setPointId(null);
                result.setPoint(0);
                result.setUserId(userId);
                result.setIsSubscription(isSubscription);
                result.setIsPurchase(false);
                return ResponseEntity.ok(result);
            }

            pointList.sort(Comparator.comparing(Point::getPointId).reversed());
            Point point = pointList.get(0);

            GetPointQuery result = new GetPointQuery();
            result.setPointId(point.getPointId());
            result.setPoint(point.getPoint());
            result.setUserId(point.getUserId());
            result.setIsSubscription(point.getIsSubscription());
            result.setIsPurchase(point.getIsPurchase());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "포인트 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/use") // 포인트 차감
    public ResponseEntity<Map<String, Object>> usePoint(@RequestBody PurchasePointRequest request) {
        try {
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
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "해당 유저의 포인트 정보가 없습니다.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "정기 구독자: 포인트 차감 없음");
                return ResponseEntity.ok(response);
            }

            if (point.getPoint() < amount) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "포인트 부족");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            point.setPoint(point.getPoint() - amount);
            pointRepository.save(point);

            System.out.println("✅ 포인트 차감 후 상태: " + point);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "포인트가 성공적으로 차감되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "포인트 사용 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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