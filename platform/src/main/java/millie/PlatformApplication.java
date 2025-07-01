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
            // --- 기존 BookRegistered 이벤트 발행 코드 (주석 처리 또는 제거) ---
            // BookRegistered publishedEvent = new BookRegistered();
            // publishedEvent.setId(2L);
            // publishedEvent.setBookName("Test Book Title");
            // // publishedEvent.setAuthorId(123123);

            // ((ApplicationEventPublisher) applicationContext).publishEvent(publishedEvent);

            // System.out.println(">>> BookRegistered event prepared and published: "
            //                     + "ID: " + publishedEvent.getId() + " <<<");
            // -----------------------------------------------------------


            // --- 새로운 BadgeGranted 이벤트 발행 코드 ---
            BadgeGranted badgeGrantedEvent = new BadgeGranted();
            badgeGrantedEvent.setId(101L); // 예시 ID
            badgeGrantedEvent.setBookName("테스트 도서명"); // 예시 도서명
            badgeGrantedEvent.setIsBestSeller(true); // 예시 베스트셀러 여부
            badgeGrantedEvent.setSubscriptionCount(5000); // 예시 구독자 수
            badgeGrantedEvent.setViews(20); // 예시 조회수

            ((ApplicationEventPublisher) applicationContext).publishEvent(badgeGrantedEvent);

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
