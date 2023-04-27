package org.seng302.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests of the wishlistItem repository.
 */
@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
class WishlistItemRepositoryTests {
    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private BusinessRepository businessRepository;

    private WishlistItem wishlistItem1;
    private WishlistItem wishlistItem2;
    private WishlistItem wishlistItem3;
    private List<WishlistItem> wishlistItemListUser;
    private List<WishlistItem> wishlistItemsBusiness;
    private List<WishlistItem> wishlistItemsAll;
    private List<WishlistItem> wishlistItemList;
    private Business business1;
    private Business business2;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        wishlistItemRepository.deleteAll();
        wishlistItemRepository.flush();
        wishlistItemListUser = new ArrayList<>();
        wishlistItemsBusiness = new ArrayList<>();
        wishlistItemsAll = new ArrayList<>();
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        business1 = new Business("testBusiness", "test description", addr, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        business2 = new Business("testBusiness", "test description", addr, BusinessType.RETAIL_TRADE);
        businessRepository.save(business1);
        businessRepository.save(business2);
        wishlistItemList = new ArrayList<>();
        assertThat(wishlistItemRepository).isNotNull();
        wishlistItem3 = new WishlistItem(Long.valueOf(2), business1);
        wishlistItem1 = new WishlistItem(Long.valueOf(1), business2);
        wishlistItem2 = new WishlistItem(Long.valueOf(1), business1);
        System.out.println(wishlistItem1);
        wishlistItemRepository.save(wishlistItem1);
        wishlistItemRepository.save(wishlistItem2);
        wishlistItemRepository.save(wishlistItem3);
        wishlistItemListUser.add(wishlistItem1);
        wishlistItemListUser.add(wishlistItem2);
        wishlistItemsBusiness.add(wishlistItem2);
        wishlistItemsBusiness.add(wishlistItem3);
        wishlistItemsAll.add(wishlistItem1);
        wishlistItemsAll.add(wishlistItem2);
        wishlistItemsAll.add(wishlistItem3);
        wishlistItemList.add(wishlistItem1);
        wishlistItemList.add(wishlistItem2);
    }

    @Test
    void findWishListItemsByUserIdSuccessful() {
        assertThat(wishlistItemRepository.findWishlistItemsByUserId(Long.valueOf(1))).isEqualTo((wishlistItemList));
    }

    @Test
    void findWishListItemsByUserIdItemIsNull() {
        List<WishlistItem> notFound = wishlistItemRepository.findWishlistItemsByUserId(5);
        assertThat(notFound.size()).isZero();
    }

    @Test
    void findSingleWishlistItemByIdSuccessful() {
        WishlistItem found = wishlistItemRepository.findWishlistItemById(7);
        assertThat(found).isEqualTo(wishlistItem1);
    }

    @Test
    void findNonExistentWishlistItemIsNull() {
        WishlistItem notFound = wishlistItemRepository.findWishlistItemById(10);
        assertThat(notFound).isNull();
    }

    @Test
    void findAllWishListItemsSuccessful() {
        assertThat(wishlistItemRepository.findAll()).isEqualTo((wishlistItemsAll));
    }

    @Test
    void findWishListItemsByBusinessIdSuccessful() {
        assertThat(wishlistItemRepository.findWishlistItemByBusiness(business1)).isEqualTo((wishlistItemsBusiness));
    }

    @Test
    void findWishListItemsByBusinessItemIsNull() {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        Business fakeBusiness = new Business("fakeBusiness", "fake description", addr, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        businessRepository.save(fakeBusiness);
        List<WishlistItem> notFound = wishlistItemRepository.findWishlistItemByBusiness(fakeBusiness);
        assertThat(notFound.size()).isZero();
    }
}
