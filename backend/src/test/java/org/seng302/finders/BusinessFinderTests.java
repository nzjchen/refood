package org.seng302.finders;

import org.junit.jupiter.api.*;
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
import org.springframework.web.server.ResponseStatusException;


import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = Main.class)
@ContextConfiguration(classes = TestApplication.class)
class BusinessFinderTests {


    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessFinder businessFinder;


    private User user;
    private Business businessA;
    private Business businessB;
    private Business businessC;


    @BeforeEach
    void setup() throws Exception {

        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        user = new User("New", "User", addr, "testemail@email.com", "testpassword", Role.USER);

        userRepository.save(user);

        Address businessAddress = new Address(null, null, "Riccarton" , "Christchurch", "Canterbury", "New Zealand", null);
        businessA = new Business("The Skinder", "Test Description", businessAddress, BusinessType.RETAIL_TRADE);
        businessA.createBusiness(user);

        businessAddress = new Address(null, null, "Riccarton" , "Christchurch", "Canterbury", "New Zealand", null);
        businessB = new Business("Layo", "Test Description", businessAddress, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        businessB.createBusiness(user);

        businessAddress = new Address(null, null, "Riccarton" , "Christchurch", "Canterbury", "New Zealand", null);
        businessC = new Business("The Dabshot", "Test Description", businessAddress, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        businessC.createBusiness(user);

        businessRepository.save(businessA);
        businessRepository.save(businessB);
        businessRepository.save(businessC);

    }


    @Test
    @Transactional
    void testBusinessFind_BusinessA_ReturnsBusinessA() {
        Specification<Business> spec = businessFinder.findBusinesses("The Skinder", "");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());
        boolean contains = businesses.stream().anyMatch(o -> o.getName().equals(businessA.getName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, businesses.size());
    }

    @Test
    @Transactional
    void testBusinessFind_BusinessB_ReturnsBusinessB()  {
        Specification<Business> spec = businessFinder.findBusinesses("Layo", "");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());
        boolean contains = businesses.stream().anyMatch(o -> o.getName().equals(businessB.getName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, businesses.size());
    }

    @Test
    @Transactional
    void testBusinessFind_FilterBusinessType_ReturnsBusinessesWithType() {
        Specification<Business> spec = businessFinder.findBusinesses("", "Accommodation and Food Services");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());
        boolean containsB = businesses.stream().anyMatch(o -> o.getName().equals(businessB.getName()));
        boolean containsC = businesses.stream().anyMatch(o -> o.getName().equals(businessC.getName()));

        Assertions.assertTrue(containsB && containsC);
        Assertions.assertEquals(2, businesses.size());
    }

    @Test
    @Transactional
    void testBusinessFind_FilterBusinessTypeAndFindBusinessB_ReturnsBusinessB() {
        Specification<Business> spec = businessFinder.findBusinesses("Layo", "Accommodation and Food Services");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());
        boolean containsB = businesses.stream().anyMatch(o -> o.getName().equals(businessB.getName()));

        Assertions.assertTrue(containsB);
        Assertions.assertEquals(1, businesses.size());
    }

    @Test
    @Transactional
    void testBusinessFind_InvalidBusinessType_ThrowsException() {
        Assertions.assertThrows(ResponseStatusException.class, () -> {
            Specification<Business> spec = businessFinder.findBusinesses("Layo", "Accommodation and Food");
        });
    }

    @Test
    @Transactional
    void testBusinessFind_EmptyQuery_ReturnsAllBusinesses() {
        Specification<Business> spec = businessFinder.findBusinesses("", "");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());
        Assertions.assertEquals(3, businesses.size());
    }


    @Test
    @Transactional
    void testBusinessFind_BusinessAWithAnd_ReturnsBusinessA() {
        Specification<Business> spec = businessFinder.findBusinesses("The AND Skinder", "");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());
        boolean contains = businesses.stream().anyMatch(o -> o.getName().equals(businessA.getName()));

        Assertions.assertTrue(contains);
    }

    @Test
    @Transactional
    void testBusinessFind_BusinessAOrBusinessB_ReturnsBothBusinesses() {
        Specification<Business> spec = businessFinder.findBusinesses("Layo OR Skinder", "");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());

        boolean containsA = businesses.stream().anyMatch(o -> o.getName().equals(businessA.getName()));
        boolean containsB = businesses.stream().anyMatch(o -> o.getName().equals(businessB.getName()));

        Assertions.assertTrue(containsA && containsB);
        Assertions.assertEquals(2, businesses.size());
    }

    @Test
    @Transactional
    void testBusinessFind_BusinessAInQuotes_ReturnsBusinessA() {
        Specification<Business> spec = businessFinder.findBusinesses("\"The Sk\"", "");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());

        boolean containsA = businesses.stream().anyMatch(o -> o.getName().equals(businessA.getName()));

        Assertions.assertTrue(containsA);
        Assertions.assertEquals(1, businesses.size());
    }

    @Test
    @Transactional
    void testBusinessFind_BusinessAInQuotesWithSpace_ReturnsNothing() {
        Specification<Business> spec = businessFinder.findBusinesses("\"The Sk \"", "");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<Business> result = businessRepository.findAll(spec, pageRequest);

        List<Business> businesses = result.stream().collect(Collectors.toList());

        Assertions.assertEquals(0, businesses.size());
    }

}