package org.seng302.repositories;

import org.seng302.models.Business;
import org.seng302.models.WishlistItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    WishlistItem findWishlistItemById(long id);

    List<WishlistItem> findWishlistItemsByUserId(long id, Sort sort);

    List<WishlistItem> findWishlistItemsByUserId(long id);

    List<WishlistItem> findWishlistItemByBusiness(Business business);
}
