package millie.domain;

import java.util.Date;
import java.util.List;
import millie.domain.*;
import java.util.Optional; 
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "points", path = "points")
public interface PointRepository extends PagingAndSortingRepository<Point, Long> {

    @Query(
        value = "select point " +
        "from Point point " +
        "where (:pointId is null or point.pointId = :pointId) " +
        "and (:point is null or point.point = :point) " +
        "and (point.isPurchase = :isPurchase) " +
        "and (:userId is null or point.userId = :userId) " +
        "and (:isSubscription is null or point.isSubscription = :isSubscription)"
    )
    List<Point> getPoint(
        @org.springframework.data.repository.query.Param("pointId") Long pointId,
        @org.springframework.data.repository.query.Param("point") Integer point,
        @org.springframework.data.repository.query.Param("isPurchase") Boolean isPurchase,
        @org.springframework.data.repository.query.Param("userId") Long userId,
        @org.springframework.data.repository.query.Param("isSubscription") Boolean isSubscription,
        Pageable pageable
    );

    List<Point> findAllByUserIdAndIsSubscription(Long userId, Boolean isSubscription);
}