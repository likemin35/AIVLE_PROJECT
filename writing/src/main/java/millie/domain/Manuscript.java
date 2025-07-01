package millie.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import millie.WritingApplication;
import millie.domain.ManuscriptEdited;
import millie.domain.ManuscriptRegistered;



@Entity
@Table(name = "Manuscript_table")
@Data
//<<< DDD / Aggregate Root
public class Manuscript {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;

    private String title;

    private String content;

    private Status status;

    @Embedded
    private AuthorId authorId;

    @PostPersist
    public void onPostPersist() {
        ManuscriptRegistered manuscriptRegistered = new ManuscriptRegistered(
            this
        );
        manuscriptRegistered.publishAfterCommit();
    }

    @PreUpdate
    public void onPreUpdate() {
        ManuscriptEdited manuscriptEdited = new ManuscriptEdited(this);
        manuscriptEdited.publishAfterCommit();
    }

    public static ManuscriptRepository repository() {
        ManuscriptRepository manuscriptRepository = WritingApplication.applicationContext.getBean(
            ManuscriptRepository.class
        );
        return manuscriptRepository;
    }

    //<<< Clean Arch / Port Method
    public void requestPublish(RequestPublishCommand requestPublishCommand) {
        System.out.println("📌 Request status: " + requestPublishCommand.getStatus());
    
        try {
            Status newStatus = Status.valueOf(requestPublishCommand.getStatus().toUpperCase()); 
            this.status = newStatus;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 상태값입니다: " + requestPublishCommand.getStatus()); 
        }
    
        PublishingRequested event = new PublishingRequested(this);
        event.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
