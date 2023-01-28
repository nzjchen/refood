package org.seng302.repositories;


import org.seng302.models.BoughtListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * JPA Repository for the Listing entity/table.
 */
@RepositoryRestResource
public interface BoughtListingRepository extends JpaRepository<BoughtListing, Long> {

    BoughtListing findBoughtListingByListingId(long listingId);


    @Query(
            value = "SELECT * FROM bought_listing where product_business_id = :id",
            nativeQuery = true
    )
    List<BoughtListing> findBoughtListingsByBusinessId(@Param(value = "id") long id);
}

