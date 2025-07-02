// 🔍 GetSubscriptionController.java
package millie.infra;

import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/getSubscription")
public class GetSubscriptionController {

    @Autowired
    private GetSubscriptionRepository getSubscriptionRepository;

    // 📋 사용자별 구독 정보 조회
    @GetMapping("/user/{userId}")
    public List<GetSubscription> getSubscriptionsByUser(@PathVariable Long userId) {
        return getSubscriptionRepository.findByUserId(userId);
    }

    // 📋 사용자별 현재 대여 중인 책 조회
    @GetMapping("/user/{userId}/active")
    public List<GetSubscription> getActiveSubscriptionsByUser(@PathVariable Long userId) {
        return getSubscriptionRepository.findByUserIdAndIsSubscription(userId, true);
    }

    // 📋 특정 책의 구독 정보 조회
    @GetMapping("/book/{bookId}")
    public List<GetSubscription> getSubscriptionsByBook(@PathVariable Long bookId) {
        return getSubscriptionRepository.findByBookId(bookId);
    }

    // 📋 사용자의 특정 책 구독 정보 조회
    @GetMapping("/user/{userId}/book/{bookId}")
    public GetSubscription getUserBookSubscription(
            @PathVariable Long userId, 
            @PathVariable Long bookId) {
        return getSubscriptionRepository.findByUserIdAndBookId(userId, bookId);
    }

    // 📋 전체 구독 정보 조회
    @GetMapping("/all")
    public Iterable<GetSubscription> getAllSubscriptions() {
        return getSubscriptionRepository.findAll();
    }
}