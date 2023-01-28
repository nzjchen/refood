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
class AddressFinderTests {

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
    private AddressFinder addressFinder;


    private Product product1;
    private Product product2;
    private Listing listing1;
    private Listing listing2;
    private Inventory inventory1;
    private Inventory inventory2;

    private User user;
    private Business business;
    private Business business2;

    @BeforeEach
    void setup() throws Exception {

        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        user = new User("New", "User", addr, "testemail@email.com", "testpassword", Role.USER);

        userRepository.save(user);

        Address businessAddress = new Address(null, null, "Riccarton" , "Christchurch", "Canterbury", "New Zealand", null);
        business = new Business("TestBusiness", "Test Description", businessAddress, BusinessType.RETAIL_TRADE);
        business.createBusiness(user);

        Address businessAddress2 = new Address(null, null, "Summer Hill", "Sydney", "New South Wales", "Australia", null);
        business2 = new Business("TestBusiness", "Test Description", businessAddress2, BusinessType.RETAIL_TRADE);
        business2.createBusiness(user);

        businessRepository.save(business);
        businessRepository.save(business2);

        //needed when creating inventory items since ID gets changed every time test runs
        Long businessId = businessRepository.findAll().get(0).getId();
        Long business2Id = businessRepository.findAll().get(1).getId();


        product1 = new Product("07-4957067", business, "Chinese Food", "Soup, Plastic", "Good Manufacturer", 14.69, new Date());
        product2 = new Product("07-4957066", business2, "Thai Food", "Buckwheat, Organic", "Bad Manufacturer", 1.26, new Date());

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

        inventory2 = new Inventory("07-4957066", business2Id, 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);
        inventoryRepository.save(inventory2);
        afterCalendar.set(2022, 2, 2);
        Date anotherLaterDate = afterCalendar.getTime();
        listing2 = new Listing(inventory2, 10, 10.0, "Seller may be interested in offers", new Date(), anotherLaterDate);
        listingRepository.save(listing2);
    }


    @Test
    @Transactional //Trying to retrieve product images, so this needs to be added to help fetch images
    void testAddressFind_SingleCountry_ReturnsOneListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("New Zealand");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_OtherCountry_ReturnsOneListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Australia");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_SingleSuburb_ReturnsOneListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Riccarton");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_OtherSuburb_ReturnsOneListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Summer Hill");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_SingleRegion_ReturnsOneListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Canterbury");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_OtherRegion_ReturnsOneListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("New South Wales");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }


    @Test
    @Transactional
    void testAddressFind_SingleCity_ReturnsOneListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Christchurch");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_OtherCity_ReturnsOneListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Sydney");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    //Test OR
    @Test
    @Transactional
    void testAddressFind_CountryOrCity_ReturnsTwoListings() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("New Zealand OR Sydney");
        List<Listing> products = listingRepository.findAll(spec);
        boolean containsListing1 = products.stream().anyMatch(o -> o.equals(listing1));
        boolean containsListing2 = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(containsListing1 && containsListing2);
        Assertions.assertEquals(2, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_CountryOrCountry_ReturnsTwoListings() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("New Zealand OR Australia");
        List<Listing> products = listingRepository.findAll(spec);
        boolean containsListing1 = products.stream().anyMatch(o -> o.equals(listing1));
        boolean containsListing2 = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(containsListing1 && containsListing2);
        Assertions.assertEquals(2, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_CityOrCountry_ReturnsTwoListings() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Christchurch OR Australia");
        List<Listing> products = listingRepository.findAll(spec);
        boolean containsListing1 = products.stream().anyMatch(o -> o.equals(listing1));
        boolean containsListing2 = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(containsListing1 && containsListing2);
        Assertions.assertEquals(2, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_CityAndCountry_ReturnsListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Christchurch AND New");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains= products.stream().anyMatch(o -> o.equals(listing1));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    //Testing AND
    @Test
    @Transactional
    void testAddressFind_CityAndRegion_ReturnsOtherListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Sydney AND New");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_CountryAndCountry_ReturnsNothing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Australia AND New Zealand");
        List<Listing> products = listingRepository.findAll(spec);

        Assertions.assertEquals(0, products.size());
    }

    @Test
    @Transactional
    void testAddressFind_CountryAndRegion_ReturnsOtherListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("Australia AND New South Wales");
        List<Listing> products = listingRepository.findAll(spec);

        boolean contains = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }


    //Testing Quotes
    @Test
    @Transactional
    void testAddressFind_RegionWithQuotes_ReturnsOtherListing() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("\"New South\"");
        List<Listing> products = listingRepository.findAll(spec);
        boolean contains = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Transactional
    void testAddress_FindWithQuotesThatMatchBoth_ReturnsBothListings() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("\"New \"");
        List<Listing> products = listingRepository.findAll(spec);
        boolean containsListing1 = products.stream().anyMatch(o -> o.equals(listing1));
        boolean containsListing2 = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(containsListing1 && containsListing2);
        Assertions.assertEquals(2, products.size());
    }

    @Test
    @Transactional
    void testAddress_FindWithQuotesThatDontMatch_ReturnsNone() throws Exception {
        Specification<Listing> spec = addressFinder.findAddress("\"New  \""); //two extra spaces, will not match
        List<Listing> products = listingRepository.findAll(spec);
        Assertions.assertEquals(0, products.size());
    }

}
