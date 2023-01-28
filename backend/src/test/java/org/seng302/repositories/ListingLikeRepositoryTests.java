package org.seng302.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ListingLikeRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ListingLikeRepository listingLikeRepository;

    private User user;
    private Listing listing;
    private ListingLike like;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException {
        listingRepository.deleteAll();
        listingRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        listingLikeRepository.deleteAll();
        listingLikeRepository.flush();

        user = new User("Rayna", "YEP", "Dalgety", "Universal", "", "rdalgety3@ocn.ne.jp", "2006-03-30", "+7 684 622 5902", null, "ATQWJM");
        user.setId(1L);
        userRepository.save(user);

        Calendar calendar = Calendar.getInstance();
        Date dateCreated = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date dateCloses = calendar.getTime();

        Business business = new Business("Business Name", "Description here.", null, BusinessType.RETAIL_TRADE);
        entityManager.persist(business);
        entityManager.flush();

        Product product = new Product("77-9986231", business, "Seedlings", "Buckwheat, Organic", "Nestle", 1.26, new Date());
        entityManager.persist(product);
        entityManager.flush();

        Inventory inventory = new Inventory("77-9986231", 1, 10, 2.0, 20.0, dateCreated,dateCloses, dateCloses, dateCloses);
        entityManager.persist(inventory);
        entityManager.flush();

        listing = new Listing(inventory, 5, 2.0, "Seller may be interested in offers", new Date(), new Date());
        listingRepository.save(listing);

        ListingLike like = new ListingLike(user, listing);
        listingLikeRepository.save(like);
    }

    @Test
    void findListingLikesByUserId_correctNumberOfLikes() {
        List<ListingLike> likes = listingLikeRepository.findListingLikesByUserId(user.getId());
        assertThat(likes.size()).isEqualTo(1);
    }

    @Test
    void findListingLikesByUserIdAndListingId() {
        ListingLike like = listingLikeRepository.findListingLikeByListingIdAndUserId(listing.getId(), user.getId());
        assertThat(like).isNotNull();
    }
}
