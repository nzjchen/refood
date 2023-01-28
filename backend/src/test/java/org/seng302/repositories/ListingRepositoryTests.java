package org.seng302.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.TestApplication;
import org.seng302.models.Address;
import org.seng302.models.Listing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

import org.seng302.repositories.InventoryRepository;
import org.seng302.repositories.ListingRepository;
import org.seng302.repositories.ProductRepository;
import org.seng302.models.*;

import java.util.Date;
import java.util.Calendar;
import java.util.List;


/**
 * Integration tests of the user repository.
 */

@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ListingRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;


    private Inventory testInven1;
    private Inventory testInven2;

    private Listing testListing1;

    /**
     * A new listing item requires a new inventory item which in turn requires a new product item...
     * The the new inventory item should be from the same business otherwise we get a data integrity violation
     *
     */

    @BeforeEach
    void setUp() {
        listingRepository.deleteAll();
        listingRepository.flush();
        inventoryRepository.deleteAll();
        inventoryRepository.flush();
        productRepository.deleteAll();
        productRepository.flush();

        Calendar calendar = Calendar.getInstance();
        Date dateCreated = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date dateCloses = calendar.getTime();

        assertThat(inventoryRepository).isNotNull();
        assertThat(inventoryRepository).isNotNull();

        Business business = new Business("Business Name", "Description here.", null, BusinessType.RETAIL_TRADE);
        entityManager.persist(business);
        entityManager.flush();

        Product testProd1 = new Product("77-9986231", business, "Seedlings", "Buckwheat, Organic", "Nestle", 1.26, new Date());
        Product testProd2 = new Product("77-5088639", business, "Foam Cup", "6 Oz", "Edible Objects Ltd.",55.2, new Date());

        productRepository.save(testProd1);
        productRepository.save(testProd2);

        testInven1 = new Inventory("77-9986231", 1, 5, 2.0, 10.0, dateCreated,dateCloses, dateCloses, dateCloses);
        testInven2 = new Inventory("77-5088639", 1, 7, 4.0, 28.0, dateCreated,dateCloses, dateCloses, dateCloses);

        inventoryRepository.save(testInven1);
        inventoryRepository.save(testInven2);

        //test Data for Listings
        testListing1 = new Listing(testInven1, 12, 12.00, "test more info", dateCreated, dateCloses);

        listingRepository.save(testListing1);

    }

    @Test
    void findListingByInventoryItem() {
        // Test if insertion is properly inserting values.
        List<Listing> testCheckListings = listingRepository.findListingsByInventoryItem(testInven1);
        //get the first element of the returned list
        Listing testCheckListing = testCheckListings.get(0);

        //check we are getting the correct listing
        assertThat(testCheckListing).isEqualTo(testListing1);
    }


}
