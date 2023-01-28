package org.seng302.finders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.Main;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.seng302.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = Main.class)
@ContextConfiguration(classes = TestApplication.class)
class UserFinderTests {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFinder userFinder;

    private User userA;
    private User userB;
    private User userC;


    @BeforeEach
    void setup() throws Exception {

        //String firstName, String middleName, String lastName, String nickname, String bio, String email, String dateOfBirth, String phoneNumber, Address homeAddress, String password
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        userA = new User("Bob", "Bobinson", "Wilson", "Bobbee", "i am bob", "bob@bob.com", "01/01/2001", "382910321", addr, "password");
        userB = new User("Robby", "Rob", "Knuckles", "Test", "i am bob", "bobby@bob.com", "01/01/2001", "382910321", addr, "password");
        userC = new User("Tim", "Timothy", "Jeff", "Timmy", "i am bob", "tim@bob.com", "01/01/2001", "382910321", addr, "password");


        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);

    }


    @Test
    @Transactional
    void testUserFind_UserAFirstName_ReturnsUserA() {
        Specification<User> spec = userFinder.findUsers("Bob");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());
        boolean contains = users.stream().anyMatch(o -> o.getFirstName().equals(userA.getFirstName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @Transactional
    void testUserFind_UserAMiddleName_ReturnsUserA() {
        Specification<User> spec = userFinder.findUsers("Bobinson");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());
        boolean contains = users.stream().anyMatch(o -> o.getFirstName().equals(userA.getFirstName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @Transactional
    void testUserFind_UserBLastName_ReturnsUserB() {
        Specification<User> spec = userFinder.findUsers("Knuckles");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());
        boolean contains = users.stream().anyMatch(o -> o.getFirstName().equals(userB.getFirstName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @Transactional
    void testUserFind_UserCNickName_ReturnsUserC() {
        Specification<User> spec = userFinder.findUsers("Timmy");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());
        boolean contains = users.stream().anyMatch(o -> o.getFirstName().equals(userC.getFirstName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, users.size());
    }


    @Test
    @Transactional
    void testUserFind_UserAFirstNameAndLastName_ReturnsUserA() {
        Specification<User> spec = userFinder.findUsers("Bob AND Wilson");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());
        boolean contains = users.stream().anyMatch(o -> o.getFirstName().equals(userA.getFirstName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @Transactional
    void testUserFind_UserAFirstNameAndLastNameNoAnd_ReturnsUserA() {
        Specification<User> spec = userFinder.findUsers("Bob Wilson");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());
        boolean contains = users.stream().anyMatch(o -> o.getFirstName().equals(userA.getFirstName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @Transactional
    void testUserFind_UserAFirstNameOrUserBNickname_ReturnsUserAAndUserB() {
        Specification<User> spec = userFinder.findUsers("Bob OR Test");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());
        boolean containsA = users.stream().anyMatch(o -> o.getFirstName().equals(userA.getFirstName()));
        boolean containsB = users.stream().anyMatch(o -> o.getFirstName().equals(userB.getFirstName()));

        Assertions.assertTrue(containsA && containsB);
        Assertions.assertEquals(2, users.size());
    }


    @Test
    @Transactional
    void testUserFind_BadSearch_ReturnsNone() {
        Specification<User> spec = userFinder.findUsers("dsadsadsadsadsadsadsadsadsadsa");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());

        Assertions.assertEquals(0, users.size());
    }


    @Test
    @Transactional
    void testUserFind_UserAFirstNameInQuotes_ReturnsUserA() {
        Specification<User> spec = userFinder.findUsers("\"Bob\"");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());
        boolean contains = users.stream().anyMatch(o -> o.getFirstName().equals(userA.getFirstName()));

        Assertions.assertTrue(contains);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @Transactional
    void testUserFind_UserAFirstNameInQuoteWithSpace_ReturnsNothing() {
        Specification<User> spec = userFinder.findUsers("\"Bob \"");
        Sort sortType = Sort.by(Sort.Order.asc("id").ignoreCase());
        PageRequest pageRequest = PageRequest.of(0, 10, sortType);
        Page<User> result = userRepository.findAll(spec, pageRequest);

        List<User> users = result.stream().collect(Collectors.toList());

        Assertions.assertEquals(0, users.size());
    }



}