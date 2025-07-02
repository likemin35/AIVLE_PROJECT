package millie.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;

import millie.PointApplication;
import millie.config.kafka.KafkaProcessor;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Map;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    PointRepository pointRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
    }

    // @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='UserRegistered'")
    // public void wheneverUserRegistered_GainRegisterPoint(
    //         @Payload UserRegistered userRegistered) {
    //     UserRegistered event = userRegistered;
    //     System.out.println(
    //             "\n\n##### listener GainRegisterPoint : " + userRegistered + "\n\n");

    //     // Sample Logic //
    //     Point.gainRegisterPoint(event);
    // }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverEvent_HandleAllEvents(
            @Payload String eventString) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Map<String, Object> event = mapper.readValue(eventString, Map.class);
            String eventType = (String) event.get("eventType");

            // ✅ RegisterPointGained 이벤트 처리 추가
            if ("RegisterPointGained".equals(eventType)) {
                System.out.println(">>> [수신] RegisterPointGained 이벤트");

                // UserRegistered 형태로 변환해서 기존 로직 재사용
                UserRegistered userRegistered = new UserRegistered();
                userRegistered.setUserId(Long.valueOf(event.get("userId").toString()));
                userRegistered.setEmail((String) event.get("email"));
                userRegistered.setUserName((String) event.get("userName"));
                userRegistered.setPhoneNumber((String) event.get("phoneNumber"));
                userRegistered.setPassword((String) event.get("password"));
                userRegistered.setIsKt((Boolean) event.get("isKt"));

                // 기존 포인트 지급 로직 호출
                Point.gainRegisterPoint(userRegistered);

                System.out.println(">>> RegisterPointGained 처리 완료: userId=" +
                        userRegistered.getUserId() + ", isKt=" + userRegistered.getIsKt());
            }

            else if ("DecreasePoint".equals(eventType)) {
                System.out.println(">>> [수신] DecreasePoint 이벤트");

                // ✅ DecreasePoint 객체로 변환
                DecreasePoint decreasePoint = new DecreasePoint();
                decreasePoint.setUserId(Long.valueOf(event.get("userId").toString()));
                decreasePoint.setBookId(Long.valueOf(event.get("bookId").toString()));

                // ✅ Point.decreasePoint 호출
                Point.decreasePoint(decreasePoint);

                System.out.println(">>> DecreasePoint 처리 완료: userId=" +
                        decreasePoint.getUserId() + ", bookId=" + decreasePoint.getBookId());
            }

            // ✅ ChargePoint 이벤트 처리
            else if ("ChargePoint".equals(eventType)) {
                System.out.println(">>> [수신] ChargePoint 이벤트");

                // ChargePoint 객체로 변환
                ChargePoint chargePoint = new ChargePoint();
                chargePoint.setUserId(Long.valueOf(event.get("userId").toString()));
                chargePoint.setAmount(Integer.valueOf(event.get("amount").toString()));

                // Point.chargePoint 호출
                Point.chargePoint(chargePoint);

                System.out.println(">>> ChargePoint 처리 완료: userId=" +
                        chargePoint.getUserId() + ", amount=" + chargePoint.getAmount());
            }

        } catch (Exception e) {
            System.err.println(">>> [오류] 이벤트 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
// >>> Clean Arch / Inbound Adaptor