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
// @RequestMapping(value="/users")
@Transactional
public class UserController {

    @Autowired
    UserRepository userRepository;

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
