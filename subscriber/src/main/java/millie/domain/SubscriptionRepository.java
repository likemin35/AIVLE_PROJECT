package millie.domain;
import java.util.Optional;
import java.util.Date;
import java.util.List;
import millie.domain.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "subscriptions",
    path = "subscriptions"
)
public interface SubscriptionRepository
    extends PagingAndSortingRepository<Subscription, Long> {
    
    @Query(
    value = "select subscription " +
            "from Subscription subscription " +
            "where (:id is null or subscription.id = :id) " +
            "and (:isSubscription is null or subscription.isSubscription = :isSubscription) " +
            "and (:rentalstart is null or subscription.rentalstart = :rentalstart) " +
            "and (:rentalend is null or subscription.rentalend = :rentalend) " +
            "and (:webUrl is null or subscription.webUrl like %:webUrl%) " +
            "and (:bookId is null or subscription.bookId = :bookId) " +
            "and (:userId is null or subscription.userId = :userId)"
    )
    List<Subscription> getSubscription(
        Long id,
        Boolean isSubscription,
        Date rentalstart,
        Date rentalend,
        String webUrl,
        BookId bookId,
        UserId userId,
        Pageable pageable
    );
    
    Optional<Subscription> findByUserId(UserId userId);
    List<Subscription> findByUserIdAndIsSubscription(UserId userId, Boolean isSubscription);
}