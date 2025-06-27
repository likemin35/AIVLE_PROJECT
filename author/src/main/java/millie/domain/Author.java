package millie.domain;

import millie.domain.AuthorRegistered;
import millie.AuthorApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;
import java.time.LocalDate;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;


@Entity
@Table(name="Author_table")
@Data

//<<< DDD / Aggregate Root
public class Author  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;    
    
    private String email;    
    
    private String authorName;    
    
    private String introduction;    
    
    private String feturedWorks;    
    
    @ElementCollection
    private List<Portfolio> portfolios;    
    
    private Boolean isApprove;

    @PostPersist
    public void onPostPersist(){
        AuthorRegistered authorRegistered = new AuthorRegistered(this);
        authorRegistered.publishAfterCommit();
    }

    public static AuthorRepository repository(){
        AuthorRepository authorRepository = AuthorApplication.applicationContext.getBean(AuthorRepository.class);
        return authorRepository;
    }



//<<< Clean Arch / Port Method
    public void approveAuthor(ApproveAuthorCommand approveAuthorCommand){
        
        //implement business logic here:
        //작가정보를 조회
        repository().findById(this.getId()).ifPresent(author->{
            //작가에 대한 승인 요청이 true일 경우 승인처리 및 이벤트 발행
            if(approveAuthorCommand.getIsApprove() == true){
                this.setIsApprove(approveAuthorCommand.getIsApprove());
                AuthorApproved authorApproved = new AuthorApproved(this);
                authorApproved.publishAfterCommit();
            }
        });
    }
//>>> Clean Arch / Port Method
//<<< Clean Arch / Port Method
    public void disapproveAuthor(DisapproveAuthorCommand disapproveAuthorCommand){
        //작가정보를 조회
        repository().findById(this.getId()).ifPresent(author->{
            //작가에 대한 승인 요청이 false 경우 비승인처리 및 이벤트 발행
            if(disapproveAuthorCommand.getIsApprove() == false){
            AuthorDisApproved authorDisApproved = new AuthorDisApproved(this);
            authorDisApproved.publishAfterCommit();
            }
        });
    }
//>>> Clean Arch / Port Method



}
//>>> DDD / Aggregate Root
