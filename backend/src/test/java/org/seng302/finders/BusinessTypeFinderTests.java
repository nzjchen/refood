package org.seng302.finders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.Main;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.seng302.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

@SpringBootTest(classes = Main.class)
@ContextConfiguration(classes = TestApplication.class)
class BusinessTypeFinderTests {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessTypeFinder businessTypeFinder;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private InventoryRepository inventoryRepository;

    private User user;
    private Business businessA;
    private Business businessB;
    private Business businessC;
    private Listing listingA;
    private Listing listingB;
    private Listing listingC;
    private Product productA;
    private Product productB;
    private Product productC;
    private Inventory inventoryA;
    private Inventory inventoryB;
    private Inventory inventoryC;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        user = new User("New", "User", addr, "testemail@email.com", "testpassword", Role.USER);

        userRepository.save(user);

        Address businessAddress = new Address(null, null, "Riccarton" , "Christchurch", "Canterbury", "New Zealand", null);
        businessA = new Business("The Skinder", "Test Description", businessAddress, BusinessType.RETAIL_TRADE);
        businessA.createBusiness(user);

        businessB = new Business("Layo", "Test Description", businessAddress, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        businessB.createBusiness(user);

        businessC = new Business("The Dabshot", "Test Description", businessAddress, BusinessType.CONSTRUCTION);
        businessC.createBusiness(user);

        businessRepository.save(businessA);
        businessRepository.save(businessB);
        businessRepository.save(businessC);
        productA = new Product("40-ABC", businessA, "Spoon", "Soup, Plastic", "Good Manufacturer", 14.69, new Date());
        productB = new Product("50-ALPHA", businessB, "Spoon", "Soup, Plastic", "Good Manufacturer", 14.69, new Date());
        productC = new Product("60-BETA", businessC, "Spoon", "Soup, Plastic", "Good Manufacturer", 14.69, new Date());

        productRepository.save(productA);
        productRepository.save(productB);
        productRepository.save(productC);

        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.set(2022, Calendar.FEBRUARY, 1);
        Date laterDate = afterCalendar.getTime();
        Calendar beforeCalendar = Calendar.getInstance();
        afterCalendar.set(2020, Calendar.FEBRUARY, 1);
        Date beforeDate = beforeCalendar.getTime();
        inventoryA = new Inventory("40-ABC", businessA.getId(), 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);
        inventoryB = new Inventory("50-ALPHA", businessB.getId(), 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);
        inventoryC = new Inventory("60-BETA", businessC.getId(), 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);

        inventoryRepository.save(inventoryA);
        inventoryRepository.save(inventoryB);
        inventoryRepository.save(inventoryC);

        listingA = new Listing(inventoryA, 40, 2.0, "Seller may be interested in offers", new Date(), laterDate);
        listingB = new Listing(inventoryB, 50, 2.0, "Seller may be interested in offers", new Date(), laterDate);
        listingC = new Listing(inventoryC, 60, 2.0, "Seller may be interested in offers", new Date(), laterDate);
        listingRepository.save(listingA);
        listingRepository.save(listingB);
        listingRepository.save(listingC);
    }

    @Test
    @Transactional
    void testBusinessTypeFind_SearchByConstruction_ReturnsBusinessC() {
        Specification<Listing> spec = businessTypeFinder.findListingByBizType('"' + BusinessType.CONSTRUCTION.toString().replace(",", "") + '"');
        Sort sort = Sort.unsorted();
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
        Page<Listing> result = listingRepository.findAll(spec, pageRequest);
        Listing returnedBiz = result.stream().collect(Collectors.toList()).get(0);
        Assertions.assertEquals(listingC.getQuantity(), returnedBiz.getQuantity());
    }

    @Test
    @Transactional
    void testBusinessTypeFind_SearchByAccommodationAndFoodServices_ReturnsBusinessB() {
        Specification<Listing> spec = businessTypeFinder.findListingByBizType('"' + BusinessType.ACCOMMODATION_AND_FOOD_SERVICES.toString().replace(",", "") + '"');
        Sort sort = Sort.unsorted();
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
        Page<Listing> result = listingRepository.findAll(spec, pageRequest);
        Listing returnedBiz = result.stream().collect(Collectors.toList()).get(0);
        Assertions.assertEquals(listingB.getQuantity(), returnedBiz.getQuantity());
    }

    @Test
    @Transactional
    void testBusinessTypeFind_SearchByRetailTrade_ReturnsBusinessA() {
        Specification<Listing> spec = businessTypeFinder.findListingByBizType('"' + BusinessType.RETAIL_TRADE.toString().replace(",", "") + '"');
        Sort sort = Sort.unsorted();
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
        Page<Listing> result = listingRepository.findAll(spec, pageRequest);
        Listing returnedBiz = result.stream().collect(Collectors.toList()).get(0);
        Assertions.assertEquals(listingA.getQuantity(), returnedBiz.getQuantity());
    }
}
