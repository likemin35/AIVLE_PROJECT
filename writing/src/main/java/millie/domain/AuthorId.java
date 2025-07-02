package millie.domain;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthorId implements Serializable {

    private Long id;
    private boolean isApprove;

    protected AuthorId() {
        this.id = null;
    }

    public AuthorId(Long id) {
        this.id = id;
    }

    public AuthorId(Long id, boolean isApprove) {
        this.id = id;
        this.isApprove = isApprove;
    }

    public Long getId() {
        return id;
    }

    public boolean isApprove() {
    return this.isApprove;
    }

    public void setIsApprove(boolean isApprove) {
        this.isApprove = isApprove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorId)) return false;
        AuthorId authorId = (AuthorId) o;
        return isApprove == authorId.isApprove && Objects.equals(id, authorId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isApprove);
    }
}
