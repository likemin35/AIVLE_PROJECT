package millie.infra;

import java.util.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import millie.config.kafka.KafkaProcessor;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void handleKafkaEvents(@Payload String eventString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Map<String, Object> event = mapper.readValue(eventString, Map.class);

            String eventType = (String) event.get("eventType");
            if (eventType == null) {
                System.out.println(">>> [경고] eventType 누락됨. 메시지 무시됨: " + eventString);
                return;
            }

            switch (eventType) {

                case "PointDecreased":
                    Long userId = Long.valueOf(event.get("userId").toString());
                    Long bookId = Long.valueOf(event.get("bookId").toString());

                    System.out.println(">>> [구독 성공] PointDecreased 수신: userId = " + userId + ", bookId = " + bookId);

                    Subscription subscription = new Subscription();
                    subscription.setUserId(new UserId(userId));
                    subscription.setBookId(new BookId(bookId));
                    subscription.setIsSubscription(true);

                    // 대여 기간 설정
                    Date rentalStart = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(rentalStart);
                    cal.add(Calendar.DAY_OF_MONTH, 30);
                    Date rentalEnd = cal.getTime();

                    subscription.setRentalstart(rentalStart);
                    subscription.setRentalend(rentalEnd);

                    String webUrl = "https://cdn.millie.com/book/pdf/" + bookId + ".pdf";
                    subscription.setWebUrl(webUrl);

                    Subscription.repository().save(subscription);
                    SubscriptionApplied applied = new SubscriptionApplied(subscription);
                    applied.publishAfterCommit();
                    break;

                case "OutOfPoint":
                    System.out.println(">>> [수신] OutOfPoint 이벤트");

                    OutOfPoint outOfPoint = mapper.convertValue(event, OutOfPoint.class);
                    Subscription.failSubscription(outOfPoint);
                    break;

                case "SubscriptionFailed":
                    System.out.println(">>> [수신] SubscriptionFailed 이벤트");

                    SubscriptionFailed subscriptionFailed = mapper.convertValue(event, SubscriptionFailed.class);
                    User.guideFeeConversionSuggestion(subscriptionFailed);
                    break;

                case "SubscriptionCanceled":
                    System.out.println(">>> [수신] SubscriptionCanceled 이벤트");

                    SubscriptionCanceled canceled = mapper.convertValue(event, SubscriptionCanceled.class);
                    if (!canceled.validate())
                        return;

                    UserId uid = new UserId(Long.valueOf(canceled.getUserId()));
                    Optional<Subscription> subscriptionOptional = subscriptionRepository.findByUserId(uid);

                    if (subscriptionOptional.isPresent()) {
                        Subscription s = subscriptionOptional.get();
                        s.setIsSubscription(false);
                        subscriptionRepository.save(s);
                        System.out.println(">>> 구독 상태 취소 완료: userId = " + canceled.getUserId());
                    } else {
                        System.out.println(">>> 해당 userId에 대한 구독 정보 없음: " + canceled.getUserId());
                    }
                    break;

                case "UserRegistered":
                    System.out.println(">>> [수신] UserRegistered 이벤트");

                    UserRegistered userRegistered = mapper.convertValue(event, UserRegistered.class);
                    if (!userRegistered.validate())
                        return;

                    User user = new User();
                    user.setId(userRegistered.getId());
                    user.setEmail(userRegistered.getEmail());
                    user.setUserName(userRegistered.getUserName());
                    user.setPhoneNumber(userRegistered.getPhoneNumber());
                    user.setIsPurchase(false); // 초기값

                    userRepository.save(user);
                    System.out.println(">>> 유저 등록 완료: userId = " + user.getId());
                    break;

                case "SubscriptionBought":
                    System.out.println(">>> [수신] SubscriptionBought 이벤트");

                    SubscriptionBought bought = mapper.convertValue(event, SubscriptionBought.class);
                    if (!bought.validate())
                        return;

                    UserId userIdObj = bought.getUserId();
                    if (userIdObj == null || userIdObj.getId() == null)
                        return;

                    userRepository.findById(userIdObj.getId()).ifPresent(founduser -> {
                        founduser.setIsPurchase(true);
                        userRepository.save(founduser);
                        System.out.println(">>> 구독권 구매 상태 반영 완료: userId = " + founduser.getId());
                    });
                    break;

                default:
                    System.out.println(">>> [무시됨] 알 수 없는 이벤트 타입: " + eventType);
            }

        } catch (Exception e) {
            System.out.println(">>> Kafka 이벤트 파싱 실패:");
            e.printStackTrace();
        }
    }
}
// >>> Clean Arch / Inbound Adaptor
