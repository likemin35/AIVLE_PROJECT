package millie.domain;

public class AuthorApproved {

    private Long authorId;
    private Boolean isApprove;

    public AuthorApproved() {}

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Boolean getIsApprove() {
        return isApprove;
    }

    public void setIsApprove(Boolean isApprove) {
        this.isApprove = isApprove;
    }
}