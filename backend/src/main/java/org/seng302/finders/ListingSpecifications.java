package org.seng302.finders;


import org.seng302.models.Listing;
import org.seng302.models.requests.BusinessListingSearchRequest;
import org.springframework.data.jpa.domain.Specification;


/**
 * Helper class to help build complex queries for business listings.
 * Methods will return specifications that will be used when querying the repository to retrieve results
 * matching the given conditions.
 */
public class ListingSpecifications {

    private final BusinessListingSearchRequest request;

    public ListingSpecifications(BusinessListingSearchRequest request) {
        this.request = request;
    }

    /**
     * Builds a new specification query to get listings between two prices, or greater/smaller than one price point.
     * @return a specification that the repository will use to query the database with.
     */
    public Specification<Listing> hasPriceSet() {
        return (root, cq, cb) -> {
            if (request.getMinPrice() != null && request.getMaxPrice() != null) {
                return cb.between(root.get("price"), request.getMinPrice(), request.getMaxPrice());
            }
            else if (request.getMinPrice() != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice());
            }
            else if (request.getMaxPrice() != null) {
                return cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice());
            }

            return null;
        };
    }

    /**
     * Builds a new specification query to get listings between two closing dates, or before/after than one date.
     * @return a specification that the repository will use to query the database with.
     */
    public Specification<Listing> hasClosingDateSet() {
        return (root, cq, cb) -> {
            if (request.getMinClosingDate() != null && request.getMaxClosingDate() != null) {
                return cb.between(root.get("closes"), request.getMinClosingDate(), request.getMaxClosingDate());
            }
            else if (request.getMinClosingDate() != null) {
                return cb.greaterThanOrEqualTo(root.get("closes"), request.getMinClosingDate());
            }
            else if (request.getMaxClosingDate() != null) {
                return cb.lessThanOrEqualTo(root.get("closes"), request.getMaxClosingDate());
            }

            return null;
        };
    }

    /**
     * Builds a new specification query to get listings that the owner's business type of the listing is present
     * in the given array.
     * @return a specification that the repository will use to query to the database with.
     */
    public Specification<Listing> hasBusinessTypes() {
        return (root, cq, cb) -> cb.in(root.get("inventoryItem").get("product").get("business").get("businessType"))
                                                .value(request.getBusinessTypes());
    }
}
