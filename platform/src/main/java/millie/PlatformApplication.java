package millie;

import millie.config.kafka.KafkaProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationContext;

import millie.domain.Published;
import millie.domain.Book;
import millie.domain.BookRegistered;
import millie.domain.BadgeGranted;
import org.springframework.context.ApplicationEventPublisher;
// import java.util.UUID;

@SpringBootApplication
@EnableBinding(KafkaProcessor.class)
@EnableFeignClients
public class PlatformApplication {

    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext =
            SpringApplication.run(PlatformApplication.class, args);
            
        
        System.out.println(">>> Local Kafka Event Publishing Test Started <<<");
        try {
            // --- BookRegistered 이벤트 발행 코드 ---
            BookRegistered bookRegisteredEvent = new BookRegistered();
            bookRegisteredEvent.setId(2L);
            bookRegisteredEvent.setBookName("Test Book Title (BookRegistered)2");
            bookRegisteredEvent.setCategory("테스트 카테고리2");
            bookRegisteredEvent.setIsBestSeller(false);
            bookRegisteredEvent.setSummaryContent("안녕2");
            // 필요한 다른 필드 설정

            bookRegisteredEvent.publish(); // <-- AbstractEvent의 publish() 호출

            System.out.println(">>> BookRegistered event prepared and published: "
                                + "ID: " + bookRegisteredEvent.getId()
                                + ", BookName: " + bookRegisteredEvent.getBookName() + " <<<");

            System.out.println("--- Waiting a moment before next event ---");
            Thread.sleep(500); // 두 이벤트가 너무 가깝게 발행되는 것을 피하기 위해 잠시 대기

            // --- 새로운 BadgeGranted 이벤트 발행 코드 ---
            BadgeGranted badgeGrantedEvent = new BadgeGranted();
            badgeGrantedEvent.setId(101L); // 예시 ID
            badgeGrantedEvent.setBookName("테스트 도서명2"); // 예시 도서명
            badgeGrantedEvent.setCategory("테스트 카테고리2");
            badgeGrantedEvent.setIsBestSeller(true); // 예시 베스트셀러 여부
            badgeGrantedEvent.setSubscriptionCount(5000); // 예시 구독자 수
            badgeGrantedEvent.setViews(20); // 예시 조회수

            badgeGrantedEvent.publish();
            System.out.println(">>> BadgeGranted event prepared and published: "
                                + "ID: " + badgeGrantedEvent.getId()
                                + ", BookName: " + badgeGrantedEvent.getBookName()
                                + ", IsBestSeller: " + badgeGrantedEvent.getIsBestSeller()
                                + ", SubscriptionCount: " + badgeGrantedEvent.getSubscriptionCount()
                                + ", Views: " + badgeGrantedEvent.getViews() + " <<<");
            // ------------------------------------------

        } catch (Exception e) {
            System.err.println("!!! Error during Kafka event test: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(">>> Local Kafka Event Publishing Test Finished <<<");
    }

}
