package org.seng302.repositories;

import org.seng302.models.ListingLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ListingLikeRepository extends JpaRepository<ListingLike, Long> {

    List<ListingLike> findListingLikesByUserId(long id);

    ListingLike findListingLikeByListingIdAndUserId(long listingId, long userId);

    List<ListingLike> findListingLikeByListingId(long id);
}
