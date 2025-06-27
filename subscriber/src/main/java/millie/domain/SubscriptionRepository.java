package millie.domain;

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
        "where(:id is null or subscription.id = :id) and (subscription.isSubscription = :isSubscription) and (:rentalstart is null or subscription.rentalstart = :rentalstart) and (:endSubscription is null or subscription.endSubscription = :endSubscription) and (:webUrl is null or subscription.webUrl like %:webUrl%) and (:subscribe is null or subscription.subscribe like %:subscribe%) and (:bookId is null or subscription.bookId = :bookId) and (:userId is null or subscription.userId = :userId)"
    )
    List<Subscription> getSubscription(
        Long id,
        Boolean isSubscription,
        Date rentalstart,
        Date endSubscription,
        String webUrl,
        String subscribe,
        BookId bookId,
        UserId userId,
        Pageable pageable
    );
}
