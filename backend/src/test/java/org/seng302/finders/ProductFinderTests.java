package org.seng302.finders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.Main;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.seng302.models.requests.NewListingRequest;
import org.seng302.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;


import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



@SpringBootTest(classes = Main.class)
@ContextConfiguration(classes = TestApplication.class)
class ProductFinderTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductFinder productFinder;


    private Product product1;
    private Product product2;
    private Listing listing1;
    private Listing listing2;
    private Inventory inventory1;
    private Inventory inventory2;

    private User user;
    private Business business;

    @BeforeEach
    void setup() throws Exception {

        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        user = new User("New", "User", addr, "testemail@email.com", "testpassword", Role.USER);

        userRepository.save(user);

        Address businessAddress = new Address(null, null, null, null, "New Zealand", null);
        business = new Business("TestBusiness", "Test Description", businessAddress, BusinessType.RETAIL_TRADE);
        business.createBusiness(user);

        businessRepository.save(business);

        //needed when creating inventory items since ID gets changed every time test runs
        Long businessId = businessRepository.findAll().get(0).getId();


        product1 = new Product("07-4957067", business, "Chinese Food", "Soup, Plastic", "Good Manufacturer", 14.69, new Date());
        product2 = new Product("07-4957066", business, "Thai Food", "Buckwheat, Organic", "Bad Manufacturer", 1.26, new Date());

        productRepository.save(product1);
        productRepository.save(product2);

        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.set(2022, 1, 1);
        Date laterDate = afterCalendar.getTime();

        Calendar beforeCalendar = Calendar.getInstance();
        afterCalendar.set(2020, 1, 1);
        Date beforeDate = beforeCalendar.getTime();

        inventory1 = new Inventory("07-4957067", businessId, 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);
        inventoryRepository.save(inventory1);

        NewListingRequest newListingRequest = new NewListingRequest(inventory1.getId(), 2, 2.99, "more info", laterDate);
        listing1 = new Listing(inventory1, 5, 2.0, "Seller may be interested in offers", new Date(), laterDate);
        listingRepository.save(listing1);

        inventory2 = new Inventory("07-4957066", businessId, 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);
        inventoryRepository.save(inventory2);
        afterCalendar.set(2022, 2, 2);
        Date anotherLaterDate = afterCalendar.getTime();
        listing2 = new Listing(inventory2, 10, 10.0, "Seller may be interested in offers", new Date(), anotherLaterDate);
        listingRepository.save(listing2);
    }


    @Test
    @Transactional //Trying to retrieve product images, so this needs to be added to help fetch images
    void testProductFindSingleNameReturnsOneProduct() throws Exception {
        Specification<Listing> spec = productFinder.findProduct("Chinese Food");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testProductFindOrReturnsTwoProduct() throws Exception {
        Specification<Listing> spec = productFinder.findProduct("Chinese OR Thai");
        List<Listing> products = listingRepository.findAll(spec);
        boolean containsProduct1 = products.stream().anyMatch(o -> o.equals(listing1));
        boolean containsProduct2 = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(containsProduct1 && containsProduct2);
        Assertions.assertEquals(2, products.size());
    }

    @Test
    @Transactional
    void testProductFindAndReturnsOneProduct() throws Exception {
        Specification<Listing> spec = productFinder.findProduct("Chinese AND Food");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testProductFindWordWithSpacesReturnsOneProduct() throws Exception {
        Specification<Listing> spec = productFinder.findProduct("Chinese Food");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testProductFindBadSearchReturnsNone() throws Exception {
        Specification<Listing> spec = productFinder.findProduct("djsakldjsakl");
        List<Listing> products = listingRepository.findAll(spec);
        Assertions.assertEquals(0, products.size());
    }

    @Test
    @Transactional
    void testProductFindWithQuotesReturnsOneProduct() throws Exception {
        Specification<Listing> spec = productFinder.findProduct("\"Chinese Food\"");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testProductFindWithQuotesWithSpaceReturnsNothing() throws Exception {
        Specification<Listing> spec = productFinder.findProduct("\"Chinese Food \"");
        List<Listing> products = listingRepository.findAll(spec);

        Assertions.assertEquals(0, products.size());
    }

    @Test
    @Transactional
    void testProductFindWithQuotesNotMatchReturnsResult() throws Exception {
        Specification<Listing> spec = productFinder.findProduct("\"Chinese Foo\"");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

}
