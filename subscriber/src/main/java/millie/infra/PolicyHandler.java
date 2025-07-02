package millie.infra;

import java.util.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.transaction.Transactional;

import millie.SubscriberApplication;
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

    // 🔄 GetSubscription ReadModel 서비스 추가
    @Autowired
    GetSubscriptionService getSubscriptionService;

    private void processSubscription(Long userId, Long bookId) {
        Subscription subscription = new Subscription();
        subscription.setUserId(new UserId(userId));
        subscription.setBookId(new BookId(bookId));
        subscription.setIsSubscription(true);

        Date rentalStart = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(rentalStart);
        cal.add(Calendar.DAY_OF_MONTH, 30);
        subscription.setRentalstart(rentalStart);
        subscription.setRentalend(cal.getTime());

        String webUrl = "https://cdn.millie.com/book/pdf/" + bookId + ".pdf";
        subscription.setWebUrl(webUrl);

        subscriptionRepository.save(subscription);

        SubscriptionApplied applied = new SubscriptionApplied(subscription);
        applied.publishAfterCommit();

        // 🔄 GetSubscription ReadModel 업데이트
        getSubscriptionService.updateOnSubscriptionApplied(applied);

        System.out.println(">>> 구독 처리 완료: userId = " + userId + ", bookId = " + bookId);
    }

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

                case "PointDecreased": {
                    Long userId = Long.valueOf(event.get("userId").toString());
                    Long bookId = Long.valueOf(event.get("bookId").toString());
                    System.out.println(">>> [구독 성공] PointDecreased 수신: userId = " + userId + ", bookId = " + bookId);
                    processSubscription(userId, bookId);
                    break;
                }

                case "OutOfPoint": {
                    System.out.println(">>> [수신] OutOfPoint 이벤트");
                    OutOfPoint outOfPoint = mapper.convertValue(event, OutOfPoint.class);
                    Subscription.failSubscription(outOfPoint); // 내부에서 SubscriptionFailed 발행
                    break;
                }

                case "SubscriptionFailed": {
                    System.out.println(">>> [수신] SubscriptionFailed 이벤트");
                    SubscriptionFailed subscriptionFailed = mapper.convertValue(event, SubscriptionFailed.class);
                    User.guideFeeConversionSuggestion(subscriptionFailed);

                    // 🔄 GetSubscription ReadModel 업데이트 (실패 기록)
                    getSubscriptionService.updateOnSubscriptionFailed(subscriptionFailed);
                    break;
                }

                case "SubscriptionCanceled": {
                    System.out.println(">>> [수신] SubscriptionCanceled 이벤트");
                    SubscriptionCanceled canceled = mapper.convertValue(event, SubscriptionCanceled.class);
                    if (!canceled.validate())
                        return;

                    // 오직 유저의 유료 구독 상태만 false로 설정
                    Long userId = Long.valueOf(canceled.getUserId());
                    userRepository.findById(userId).ifPresent(user -> {
                        user.setIsPurchase(false);
                        userRepository.save(user);
                        System.out.println(">>> 유료 구독 상태 취소 완료: userId = " + userId);
                    });

                    break;
                }

                case "ApplySubscription": {
                    System.out.println(">>> [수신] ApplySubscription 이벤트");
                    Long applyUserId = Long.valueOf(event.get("userId").toString());
                    Long applyBookId = Long.valueOf(event.get("bookId").toString());

                    Optional<User> optionalUser = userRepository.findById(applyUserId);
                    if (optionalUser.isEmpty()) {
                        System.out.println(">>> 유저 없음: userId = " + applyUserId);
                        return;
                    }

                    User applyUser = optionalUser.get();
                    if (Boolean.TRUE.equals(applyUser.getIsPurchase())) {
                        // 바로 구독 처리
                        processSubscription(applyUserId, applyBookId);
                        System.out.println(">>> 구독권 있음: 즉시 대여 성공");
                    } else {
                        // 포인트 차감 요청 발행
                        Map<String, Object> pointEvent = Map.of(
                                "eventType", "DecreasePoint",
                                "userId", applyUserId,
                                "bookId", applyBookId);

                        ObjectMapper outMapper = new ObjectMapper();
                        String payload = outMapper.writeValueAsString(pointEvent);

                        KafkaProcessor processor = SubscriberApplication.applicationContext
                                .getBean(KafkaProcessor.class);
                        processor.outboundTopic().send(
                                org.springframework.messaging.support.MessageBuilder
                                        .withPayload(payload)
                                        .setHeader("type", "DecreasePoint") // ✅ type 헤더 명시적 설정
                                        .setHeader(org.springframework.messaging.MessageHeaders.CONTENT_TYPE,
                                                org.springframework.util.MimeTypeUtils.APPLICATION_JSON)
                                        .build());

                        System.out.println(">>> 구독권 없음: DecreasePoint 요청 전송 완료");
                    }
                    break;
                }

                case "UserRegistered": {
                    System.out.println(">>> [수신] UserRegistered 이벤트");
                    UserRegistered userRegistered = mapper.convertValue(event, UserRegistered.class);
                    if (!userRegistered.validate()) // ✅ 유효성 검증 추가
                        return;

                    User user = new User();
                    user.setEmail(userRegistered.getEmail());
                    user.setUserName(userRegistered.getUserName());
                    user.setPhoneNumber(userRegistered.getPhoneNumber());
                    user.setPassword(userRegistered.getPassword()); // ✅ 비밀번호 설정 추가
                    user.setIsPurchase(false);
                    user.setIsKt(userRegistered.getIsKt());

                    User savedUser = userRepository.save(user);
                    // 🔧 UserId 설정 추가
                    savedUser.setUserId(new UserId(savedUser.getId()));
                    userRepository.save(savedUser);

                    System.out.println(">>> 사용자 등록 완료: userId = " + savedUser.getId() +
                            ", userName = " + savedUser.getUserName() +
                            ", password = [보안상 숨김]"); // ✅ 보안을 위해 비밀번호는 로그에 출력하지 않음

                    // ✅ 여기에 RegisterPointGained 이벤트 발행 추가
                    int basePoint = 1000;
                    if (Boolean.TRUE.equals(savedUser.getIsKt())) {
                        basePoint += 5000;
                    }

                    Map<String, Object> pointEvent = Map.of(
                            "eventType", "RegisterPointGained",
                            "userId", savedUser.getId(),
                            "point", String.valueOf(basePoint),
                            "isKt", savedUser.getIsKt());

                    ObjectMapper outMapper = new ObjectMapper();
                    String payload = outMapper.writeValueAsString(pointEvent);

                    KafkaProcessor processor = SubscriberApplication.applicationContext.getBean(KafkaProcessor.class);
                    processor.outboundTopic().send(
                            org.springframework.messaging.support.MessageBuilder.withPayload(payload).build());

                    System.out.println(
                            ">>> RegisterPointGained 이벤트 발행 완료: userId=" + savedUser.getId() + ", point=" + basePoint);
                    break;
                }

                default:
                    System.out.println(">>> [무시됨] 알 수 없는 이벤트 타입: " + eventType);
            }

        } catch (Exception e) {
            System.err.println(">>> [오류] 이벤트 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}