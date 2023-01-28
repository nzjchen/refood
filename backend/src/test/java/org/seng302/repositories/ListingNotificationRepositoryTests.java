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
class ListingNotificationRepositoryTests {

    @Autowired
    private ListingNotificationRepository listingNotificationRepository;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException {
        listingNotificationRepository.deleteAll();
        listingNotificationRepository.flush();
        listingRepository.deleteAll();
        listingRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        Calendar calendar = Calendar.getInstance();
        Date dateCreated = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date dateCloses = calendar.getTime();

        user = new User("Rayna", "YEP", "Dalgety", "Universal", "", "rdalgety3@ocn.ne.jp", "2006-03-30", "+7 684 622 5902", null, "ATQWJM");
        user.setId(1L);
        userRepository.save(user);

        Business business = new Business("Business Name", "Description here.", null, BusinessType.RETAIL_TRADE);
        entityManager.persist(business);
        entityManager.flush();

        Product product = new Product("77-9986231", business, "Seedlings", "Buckwheat, Organic", "Nestle", 1.26, new Date());
        entityManager.persist(product);
        entityManager.flush();

        Inventory inventory = new Inventory("77-9986231", 1, 10, 2.0, 20.0, dateCreated,dateCloses, dateCloses, dateCloses);
        entityManager.persist(inventory);
        entityManager.flush();

        Listing listing = new Listing(inventory, 5, 2.0, "Seller may be interested in offers", new Date(), new Date());
        listingRepository.save(listing);

        ListingNotification notification = new ListingNotification(user, listing, NotificationStatus.BOUGHT);
        listingNotificationRepository.save(notification);
    }

    /**
     * Tests that the number of notifications received is correct (1)
     */
    @Test
    void findListingNotificationByUserId_correctNumberOfNotifications() {
        List<ListingNotification> notificationList = listingNotificationRepository.findListingNotificationsByUserId(user.getId());
        assertThat(notificationList.size()).isEqualTo(1);
    }
}
