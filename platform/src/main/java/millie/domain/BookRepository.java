package millie.domain;

import java.util.Date;
import java.util.List;
import millie.domain.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "books", path = "books")
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    @Query(
        value = "select book " +
        "from Book book " +
        "where(:id is null or book.id = :id) and (:bookName is null or book.bookName like %:bookName%) and (:category is null or book.category like %:category%) and (book.isBestSeller = :isBestSeller) and (:authorName is null or book.authorName like %:authorName%) and (:조회수 is null or book.조회수 = :조회수) and (:point is null or book.point = :point)"
    )
    List<Book> viewBook(
        Long id,
        String bookName,
        String category,
        Boolean isBestSeller,
        String authorName,
        Integer 조회수,
        Integer point,
        Pageable pageable
    );
}
