package millie.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuthorId implements Serializable {

    private Long id;
    private boolean isApprove;

    protected AuthorId() {
        this.id = null;
        this.isApprove = false;
    }

    public AuthorId(Long id) {
        this.id = id;
        this.isApprove = false;
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

    // ✅ setter 메서드 추가
    public void setIsApprove(boolean isApprove) {
        this.isApprove = isApprove;
    }

    // ✅ 편의 메서드 추가
    public boolean getIsApprove() {
        return this.isApprove;
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

    @Override
    public String toString() {
        return "AuthorId{" +
                "id=" + id +
                ", isApprove=" + isApprove +
                '}';
    }
}