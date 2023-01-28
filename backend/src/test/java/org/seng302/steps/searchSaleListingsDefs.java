package org.seng302.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.controllers.ListingController;
import org.seng302.finders.ListingSpecifications;
import org.seng302.finders.ProductFinder;
import org.seng302.models.*;
import org.seng302.models.requests.BusinessListingSearchRequest;
import org.seng302.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.data.jpa.domain.Specification.where;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


public class searchSaleListingsDefs extends CucumberSpringConfiguration {

    private MockMvc mockMvc;

    @InjectMocks
    private ListingController listingController;

    @MockBean
    private ListingRepository listingRepository;

    // Since listings is being mocked in other tests, need a repository to do spring tests on
    @Autowired
    private ListingRepository realListingRepository;

    @MockBean
    private ListingSpecifications listingSpecifications;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductFinder productFinder;

    private ObjectMapper mapper;

    private Page<Listing> pagedListings;
    private MockHttpServletResponse response;

    private User user;
    private Business business;
    private Product product1;
    private Product product2;
    private Listing listing1;
    private Listing listing2;
    private Inventory inventory1;
    private Inventory inventory2;
    private List<Listing> listings;

    @Before
    public void setup() throws Exception {

        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        user = new User("New", "User", addr, "testemail@email.com", "testpassword", Role.USER);
        user.setId(1L);

        userRepository.save(user);

        Address businessAddress = new Address(null, null, null, null, "New Zealand", null);
        business = new Business("TestBusiness", "Test Description", businessAddress, BusinessType.RETAIL_TRADE);
        business.createBusiness(user);
        business.setId(1L);

        businessRepository.save(business);


        mapper = new ObjectMapper();
        listingRepository = Mockito.mock(ListingRepository.class);
        listingSpecifications = Mockito.mock(ListingSpecifications.class);

        this.mockMvc = MockMvcBuilders.standaloneSetup(        listingController = new ListingController(listingRepository, businessRepository, userRepository,
                inventoryRepository, null, null, null,
                null, mapper)).build();
    }

    @Given("there are sale listings available")
    public void thereAreSaleListingsAvailable() {
        Calendar calendar = Calendar.getInstance();
        Date dateCreated = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date dateCloses = calendar.getTime();

        Inventory inventory = new Inventory("77-9986231", 1, 20, 2.0, 10.0, dateCreated,dateCloses, dateCloses, dateCloses);
        Listing listing1 = new Listing(inventory, 12, 12.00, "test more info", dateCreated, dateCloses);
        Listing listing2 = new Listing(inventory, 8, 10.00, "test more info", dateCreated, dateCloses);
        List<Listing> listingList = new ArrayList<>();
        listingList.add(listing1);
        listingList.add(listing2);
        pagedListings = new PageImpl<>(listingList);
        Mockito.when(listingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagedListings);

        Page<Listing> results = listingRepository.findAll(where(listingSpecifications.hasPriceSet()).and(listingSpecifications.hasClosingDateSet()), Pageable.unpaged());

        assertThat(results.getTotalElements()).isEqualTo(pagedListings.getTotalElements());
    }

    @When("the user makes a request to look at current listings")
    public void theUserMakesARequestToLookAtCurrentListings() throws Exception {
        BusinessListingSearchRequest request = new BusinessListingSearchRequest();
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("count", "5");
        requestParams.add("offset", "1");
        requestParams.add("sortDirection", "asc");

        response = mockMvc.perform(post("/businesses/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .params(requestParams)
                .content(mapper.writeValueAsString(request))).andReturn().getResponse();

        assertThat(response).isNotNull();
    }


    @Then("the user successfully retrieves them")
    public void theUserSuccessfullyRetrievesThem() throws UnsupportedEncodingException {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isNotNull();
    }

    //AC6

    @Given("there are listings with name {string} and {string}")
    @Transactional
    public void thereAreListingWithNameAnd(String name1, String name2) {
        product1 = new Product("07-4957067", business, name1, "Soup, Plastic", "Good Manufacturer", 14.69, new Date());
        product2 = new Product("07-4957066", business, name2, "Buckwheat, Organic", "Bad Manufacturer", 1.26, new Date());

        productRepository.save(product1);
        productRepository.save(product2);

        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.set(2022, 1, 1);
        Date laterDate = afterCalendar.getTime();

        Calendar beforeCalendar = Calendar.getInstance();
        afterCalendar.set(2020, 1, 1);
        Date beforeDate = beforeCalendar.getTime();

        realListingRepository.deleteAll();
        realListingRepository.flush();

        inventory1 = new Inventory("07-4957067", 1, 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);
        inventoryRepository.save(inventory1);

        listing1 = new Listing(inventory1, 5, 2.0, "Seller may be interested in offers", new Date(), laterDate);
        realListingRepository.save(listing1);

        inventory2 = new Inventory("07-4957066", 1, 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);
        inventoryRepository.save(inventory2);

        listing2 = new Listing(inventory2, 10, 10.0, "Seller may be interested in offers", new Date(), laterDate);
        realListingRepository.save(listing2);

        List<Listing> products = realListingRepository.findAll();


        boolean containsProduct1 = products.stream().anyMatch(o -> o.equals(listing1));
        boolean containsProduct2 = products.stream().anyMatch(o -> o.equals(listing2));
        Assertions.assertTrue(containsProduct1 && containsProduct2);
        Assertions.assertEquals(2, products.size());

    }

    @When("the user searches for a listing with name {string}")
    @Transactional
    public void theUserSearchesForAListingWithName(String name1) {
        Specification<Listing> matches = productFinder.findProduct(name1);

        listings = realListingRepository.findAll(matches);

        Assertions.assertFalse(listings.isEmpty());
    }

    @Then("only the listing with name {string} is in the search result")
    @Transactional
    public void onlyTheListingWithNameIsInTheSearchResult(String name1) {
        boolean containsProduct1 = listings.stream().anyMatch(o -> o.getInventoryItem().getProduct().getName().equals(name1));
        Assertions.assertTrue(containsProduct1);
        Assertions.assertEquals(1, listings.size());
    }

    @Then("listings with name {string} and {string} is in the search result")
    @Transactional
    public void listingsWithNameAndIsInTheSearchResult(String name1, String name2) {
        boolean containsProduct1 = listings.stream().anyMatch(o -> o.getInventoryItem().getProduct().getName().equals(name1));
        boolean containsProduct2 = listings.stream().anyMatch(o ->  o.getInventoryItem().getProduct().getName().equals(name1));
        Assertions.assertTrue(containsProduct1 && containsProduct2);
        Assertions.assertEquals(2, listings.size());
    }
}
