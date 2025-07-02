package millie.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import millie.PlatformApplication;
import millie.domain.BadgeGranted;
import millie.domain.BookRegistered;
import java.util.Optional;

@Entity
@Table(name = "Book_table")
@Data
//<<< DDD / Aggregate Root
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bookName;

    private String category;

    private Boolean isBestSeller;

    private String authorName;

    private Integer viewCount;

    private Integer point;

    private String summaryContent;


    public static BookRepository repository() {
        BookRepository bookRepository = PlatformApplication.applicationContext.getBean(
            BookRepository.class
        );
        return bookRepository;
    }

    //<<< Clean Arch / Port Method
    public static void registerBook(Published published) {
        
        Book book = new Book();
        
        book.setBookName(published.getBookName());
        book.setCategory(published.getCategory());
        book.setSummaryContent(published.getSummaryContent());
        // book.setAuthorName(published.getAuthorName());
        book.setIsBestSeller(false);
        book.setViewCount(0);
        book.setPoint(published.getPoint());
        
        repository().save(book);

        BookRegistered bookRegistered = new BookRegistered(book);
        bookRegistered.publishAfterCommit();
     
    }
    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void grantBestsellerBadge(SubscriptionApplied subscriptionApplied) {
        
        Long bookId = Long.valueOf(subscriptionApplied.getBookId().toString());
        
        //해당 도서 찾기
        Optional<Book> optionalBook = repository().findById(bookId);
        
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            
            // 대여가 성공적으로 적용된 경우
            if (subscriptionApplied.getIsSubscription()) {
                
                // 대여 시 조회수 증가
                book.setViewCount(book.getViewCount() + 1);
                
                System.out.println(">>> viewCount: " + book.getViewCount());
                System.out.println(">>> isBestSeller: " + book.getIsBestSeller());
                System.out.println(">>> isSubscription: " + subscriptionApplied.getIsSubscription());
                // 베스트셀러 조건 확인 (조회수 30회 이상)
                if (book.getViewCount() >= 30 && !book.getIsBestSeller()) {
                    book.setIsBestSeller(true);
                    
                    BadgeGranted badgeGranted = new BadgeGranted(book);
                    System.out.println(">>> BadgeGranted 이벤트 발행 준비됨");
                    badgeGranted.publishAfterCommit();
                }
                repository().save(book);
            }
        }
    }
//>>> Clean Arch / Port Method
}
//>>> DDD / Aggregate Root
