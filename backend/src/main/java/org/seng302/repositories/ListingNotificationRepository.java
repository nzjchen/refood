package org.seng302.repositories;

import org.seng302.models.Listing;
import org.seng302.models.ListingNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ListingNotificationRepository extends JpaRepository<ListingNotification, Long> {

    List<ListingNotification> findListingNotificationsByUserId(long id);

    /**
     * Finds all listing notifications for a business with given id
     * @param id Business ID
     * @return List<ListingNotification> of the business
     */
    List<ListingNotification> findListingNotificationByBusinessId(long id);

    ListingNotification findListingNotificationsByUserIdAndListing(long id, Listing listing);

    List<ListingNotification> findListingNotificationsByListing(Listing listing);

    /**
     * Get listingNotification by it's ID
     * @param id listing notification's ID
     * @return Listing Notification
     */
    ListingNotification findListingNotificationById(long id);
}
