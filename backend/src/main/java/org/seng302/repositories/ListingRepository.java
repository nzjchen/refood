package org.seng302.repositories;

import org.seng302.models.Inventory;
import org.seng302.models.Listing;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;

/**
 * JPA Repository for the Listing entity/table.
 */
@RepositoryRestResource
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {

    Listing findListingById(long id);

    List<Listing> findListingsByInventoryItem(Inventory inventoryItem);

    List<Listing> findAll(Specification<Listing> query);

}
