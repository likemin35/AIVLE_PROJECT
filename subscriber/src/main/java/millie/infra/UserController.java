package millie.infra;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import millie.dto.GetSubscription;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/users")
@Transactional
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        Iterable<User> iterable = userRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/users/{id}/buysubscription", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public User buySubscription(
            @PathVariable(value = "id") Long id,
            @RequestBody BuySubscriptionCommand buySubscriptionCommand,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        System.out.println("##### /user/buySubscription  called #####");
        Optional<User> optionalUser = userRepository.findById(id);

        optionalUser.orElseThrow(() -> new Exception("No Entity Found"));
        User user = optionalUser.get();
        user.buySubscription(buySubscriptionCommand);

        userRepository.save(user);
        return user;
    }

    @RequestMapping(value = "/users/{id}/cancelsubscription", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public User cancelSubscription(
            @PathVariable(value = "id") Long id,
            @RequestBody CancelSubscriptionCommand cancelSubscriptionCommand,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        System.out.println("##### /user/cancelSubscription  called #####");
        Optional<User> optionalUser = userRepository.findById(id);

        optionalUser.orElseThrow(() -> new Exception("No Entity Found"));
        User user = optionalUser.get();
        user.cancelSubscription(cancelSubscriptionCommand);

        userRepository.save(user);
        return user;
    }

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @GetMapping("/userId/{userId}")
    public List<GetSubscription> getActiveSubscriptionsByUserId(@PathVariable Long userId) {
        System.out.println("📘 사용자 " + userId + "의 현재 구독 중인 책 목록 조회");

        UserId embeddedUserId = new UserId(userId);
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdAndIsSubscription(embeddedUserId, true);

        return subscriptions.stream().map(subscription -> {
            GetSubscription dto = new GetSubscription();
            dto.setId(subscription.getId());
            dto.setUserId(subscription.getUserId().getId());
            dto.setBookId(subscription.getBookId().getId());
            dto.setIsSubscription(subscription.getIsSubscription());
            dto.setRentalStart(subscription.getRentalstart());
            dto.setRentalEnd(subscription.getRentalend());
            dto.setWebUrl(subscription.getWebUrl());
            return dto;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public User registerUser(
            @RequestBody User user,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        System.out.println("사용자 등록 호출됨");

        // 기본값 세팅 (선택)
        user.setIsPurchase(false);
        user.setMessage(null);

        // 저장
        return userRepository.save(user);
    }
}

// >>> Clean Arch / Inbound Adaptor
