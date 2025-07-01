package millie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import millie.domain.Manuscript;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManuscriptResponse {

    private Long bookId;
    private String title;
    private String content;
    private String status;
    private String authorId;
    private boolean isApprove;

    public static ManuscriptResponse from(Manuscript manuscript) {
        return new ManuscriptResponse(
            manuscript.getBookId(),
            manuscript.getTitle(),
            manuscript.getContent(),
            manuscript.getStatus().name(),
            manuscript.getAuthorId().getId(), 
            manuscript.getAuthorId().isApprove()
        );
    }
}
