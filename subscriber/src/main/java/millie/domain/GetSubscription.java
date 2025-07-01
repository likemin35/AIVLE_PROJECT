package millie.domain;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "GetSubscription_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSubscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Boolean isSubscription;  // 책을 대여했는지 확인
    private Date rentalstart;        // 대여 시작일
    private Date rentalend;          // 대여 종료일
    private String webURL;           // 웹 URL
    private Long bookId;             // 책 ID
    private Long userId;             // 사용자 ID
}