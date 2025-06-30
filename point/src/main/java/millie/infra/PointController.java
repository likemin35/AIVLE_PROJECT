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

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping("/points") // 주석 제거해서 공통 prefix로 사용
@Transactional
public class PointController {

    @Autowired
    PointRepository pointRepository;

    // 포인트 구매
    @PostMapping("/purchase")
    public void purchasePoint(@RequestBody PurchasePointRequest request) {
        Long userId = request.getUserId();
        int amount = request.getAmount();

        // 충전 시에는 subscriptionId는 항상 null
        Optional<Point> optionalPoint = pointRepository.findByUserIdAndSubscriptionId(userId, null);

        if (optionalPoint.isPresent()) {
            // 기존 포인트에 누적
            Point point = optionalPoint.get();
            point.setPoint(point.getPoint() + amount);  // 누적
            pointRepository.save(point);
        } else {
            // 없으면 새로 생성
            Point newPoint = Point.builder()
                    .userId(userId)
                    .subscriptionId(null)  //  충전은 항상 null
                    .point(amount)
                    .isSubscribe(false)
                    .build();
            pointRepository.save(newPoint);
        }
    }

    // 포인트 조회
    @GetMapping("/search")
    public GetPointQuery searchPoint(@RequestParam Long userId, @RequestParam Long subscriptionId) {
        return pointRepository.findByUserIdAndSubscriptionId(userId, subscriptionId)
            .map(point -> {
                GetPointQuery result = new GetPointQuery();
                result.setPointId(point.getPointId());
                result.setPoint(point.getPoint());
                result.setUserId(point.getUserId());
                result.setSubscriptionId(point.getSubscriptionId());
                result.setIsSubscribe(point.getIsSubscribe());
                return result;
            })
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "해당 유저의 포인트 정보가 없습니다."
            ));
    }

    @PostMapping("/use") // 포인트 차감
    public ResponseEntity<?> usePoint(@RequestBody PurchasePointRequest request) {
        Long userId = request.getUserId();
        Long subscriptionId = request.getSubscriptionId();
        int amount = request.getAmount();

        // 1. 정확히 일치하는 포인트 정보 조회
        Optional<Point> optionalPoint = pointRepository.findByUserIdAndSubscriptionId(userId, subscriptionId);

        Point point;

        if (optionalPoint.isPresent()) {
            point = optionalPoint.get();
        } else {
            // 2. 없으면 subscriptionId=null 기반으로 복제
            Optional<Point> basePointOpt = pointRepository.findByUserIdAndSubscriptionId(userId, null);
            if (basePointOpt.isPresent()) {
                Point base = basePointOpt.get();

                point = Point.builder()
                        .userId(userId)
                        .subscriptionId(subscriptionId)
                        .point(base.getPoint())
                        .isSubscribe(base.getIsSubscribe())
                        .build();

                pointRepository.save(point);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저의 포인트 정보가 없습니다.");
            }
        }

        // 3. 정기 구독자면 0포인트 차감 처리
        if (Boolean.TRUE.equals(point.getIsSubscribe())) {
            return ResponseEntity.ok("정기 구독자: 포인트 차감 없음");
        }

        // 4. 포인트 차감
        if (point.getPoint() < amount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("포인트 부족");
        }

        point.setPoint(point.getPoint() - amount);
        pointRepository.save(point);

        return ResponseEntity.ok("포인트가 성공적으로 차감되었습니다.");
    }

}