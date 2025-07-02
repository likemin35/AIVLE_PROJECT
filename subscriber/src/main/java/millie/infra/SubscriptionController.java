package millie.infra;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value = "/subscriptions")
@Transactional
public class SubscriptionController {


    @Autowired
    SubscriptionRepository subscriptionRepository;

    // ✅ 구독 생성 API (기존)
    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription) {
        System.out.println("🛠️ SubscriptionController 호출됨");
        Subscription saved = subscriptionRepository.save(subscription); // @PostPersist 트리거
        return saved;
    }

    // ✅ userId로 구독 조회 API (추가)
    @GetMapping("/by-user/{userId}")
    public List<Subscription> getByUserId(@PathVariable Long userId) {
        Optional<Subscription> result = subscriptionRepository.findByUserId(new UserId(userId));
        return result.map(List::of).orElse(List.of());
    }
    
}
// >>> Clean Arch / Inbound Adaptor
