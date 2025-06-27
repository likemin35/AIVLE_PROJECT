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


    public static BookRepository repository() {
        BookRepository bookRepository = PlatformApplication.applicationContext.getBean(
            BookRepository.class
        );
        return bookRepository;
    }

    //<<< Clean Arch / Port Method
    public static void registerBook(Published published) {
        
        Book book = new Book();
        repository().save(book);

        book.setBookName(published.getBookName());
        book.setCategory(published.getCategory());
        
        // book.setIsBestSeller();
        

        // book.setIsBestSeller(published.get);
        BookRegistered bookRegistered = new BookRegistered(book);
        bookRegistered.publishAfterCommit();
     

        /** Example 2:  finding and process
        
        // if published.llmIdmanuscriptId exists, use it
        
        // ObjectMapper mapper = new ObjectMapper();
        // Map<, Object> publishingMap = mapper.convertValue(published.getLlmId(), Map.class);
        // Map<Long, Object> publishingMap = mapper.convertValue(published.getManuscriptId(), Map.class);

        repository().findById(published.get???()).ifPresent(book->{
            
            book // do something
            repository().save(book);

            BookRegistered bookRegistered = new BookRegistered(book);
            bookRegistered.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
