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
        Optional<Point> optionalPoint = pointRepository.findByUserIdAndSubscriptionId(
            request.getUserId(), request.getSubscriptionId()
        );

        optionalPoint.ifPresent(point -> {
            point.buyPoint(request.getAmount());
            pointRepository.save(point);
        });
    }

    // 포인트 조회
    @GetMapping("/search")
    public GetPointQuery searchPoint(@RequestParam Long userId, @RequestParam Long subscriptionId) {
        return pointRepository.findByUserIdAndSubscriptionId(userId, subscriptionId)
            .map(point -> {
                GetPointQuery result = new GetPointQuery();
                result.setId(point.getId());
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
        Optional<Point> optionalPoint = pointRepository.findByUserIdAndSubscriptionId(
            request.getUserId(), request.getSubscriptionId()
        );

        if (optionalPoint.isPresent()) {
            try {
                Point point = optionalPoint.get();
                point.usePoint(request.getAmount());
                pointRepository.save(point);
                return ResponseEntity.ok("포인트가 성공적으로 차감되었습니다.");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저의 포인트 정보가 없습니다.");
        }
    }
}
