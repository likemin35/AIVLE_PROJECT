package millie.infra;

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

    @PostMapping
    public Subscription createSubscription(@RequestBody Subscription subscription) {
        System.out.println("🛠️ SubscriptionController 호출됨");

        Subscription saved = subscriptionRepository.save(subscription); // @PostPersist 트리거
        return saved;
    }
}
// >>> Clean Arch / Inbound Adaptor
