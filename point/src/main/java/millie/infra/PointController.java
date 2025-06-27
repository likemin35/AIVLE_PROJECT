package millie.infra;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/points")
@Transactional
public class PointController {

    @Autowired
    PointRepository pointRepository;

    @PostMapping("/purchase")
    public void purchasePoint(@RequestBody PurchasePointRequest request) {
        Optional<Point> optionalPoint = pointRepository.findByUserIdAndSubscriptionId(
            request.getUserId(), request.getSubscriptionId()
        );

        optionalPoint.ifPresent(point -> {
            point.buyPoint(request.getAmount()); // 도메인 로직 호출
            pointRepository.save(point); // 변경사항 저장
        });
    }
}
