package millie.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AuthorStatus {

    @Id
    private Long authorId;  // 문자열인 이유는 AuthorId 내부에 String id가 있기 때문

    private Boolean isApprove;
}
