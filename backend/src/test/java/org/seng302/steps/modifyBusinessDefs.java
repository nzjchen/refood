package org.seng302.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.seng302.controllers.BusinessController;
import org.seng302.models.*;
import org.seng302.models.requests.NewBusinessRequest;
import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;

public class modifyBusinessDefs extends CucumberSpringConfiguration {

    private User user;
    private Business business;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private BusinessController businessController;

    private HttpSession session;

    private NewBusinessRequest request;

    ResponseEntity<String> result;
    @Before
    public void setup() throws NoSuchAlgorithmException {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        user = new User("Wilma", "User", addr, "testemail@email.com", "testpassword", Role.USER);
        user.setId(1L);

        userRepository.save(user);

        Address businessAddress = new Address(null, null, null, null, "New Zealand", null);
        business = new Business("TestBusiness", "Test Description", businessAddress, BusinessType.RETAIL_TRADE);
        business.createBusiness(user);
        business.setId(1L);

        businessRepository.save(business);

        session = Mockito.mock(HttpSession.class);
    }

    @Given("there is a business with an owner {string}")
    public void thereIsABusinessWithAnOwner(String name) {
        Assertions.assertEquals(business.getPrimaryAdministrator().getFirstName(), name);
    }

    @When("the user modifies the business with valid data")
    @Transactional
    public void theUserModifiesTheBusinessWithValidData() {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        request = new NewBusinessRequest("Dabshots", "This is updated", addr, BusinessType.ADMINISTRATIVE_AND_SUPPORT_SERVICES);
        Mockito.when(session.getAttribute(User.USER_SESSION_ATTRIBUTE)).thenReturn(user);

        result = businessController.modifyBusiness(request, business.getId(), session);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Then("the business is updated")
    public void theBusinessIsUpdated() {
        Business updated = businessRepository.findBusinessById(business.getId());
        Assertions.assertEquals(updated.getName(), request.getName());
        Assertions.assertEquals(updated.getBusinessType(), request.getBusinessType());
        Assertions.assertEquals(updated.getDescription(), request.getDescription());
        Assertions.assertEquals(updated.getAddress().getCountry(), request.getAddress().getCountry());
    }

    @When("the user modifies the business to have a name {string}")
    public void theUserModifiesTheBusinessWithAName(String name) {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        request = new NewBusinessRequest(name, "This is updated", addr, BusinessType.ADMINISTRATIVE_AND_SUPPORT_SERVICES);
        Mockito.when(session.getAttribute(User.USER_SESSION_ATTRIBUTE)).thenReturn(user);

        result = businessController.modifyBusiness(request, business.getId(), session);

    }

    @Then("the business is not updated")
    public void theBusinessIsNotUpdated() {
        Business updated = businessRepository.findBusinessById(business.getId());

        Assertions.assertEquals(updated.getName(), business.getName());
        Assertions.assertEquals(updated.getAddress().getCountry(), business.getAddress().getCountry()); //Got to do country because
        Assertions.assertEquals(updated.getDescription(), business.getDescription());
        Assertions.assertEquals(updated.getBusinessType(), business.getBusinessType());
    }

    @Then("a bad request is returned")
    public void aBadRequestIsReturned() {
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @When("the user modifies the business to have a country {string}")
    public void theUserModifiesTheBusinessToHaveACountry(String country) {
        Address addr = new Address(null, null, null, null, null, country, "12345");
        request = new NewBusinessRequest("Business", "This is updated", addr, BusinessType.ADMINISTRATIVE_AND_SUPPORT_SERVICES);
        Mockito.when(session.getAttribute(User.USER_SESSION_ATTRIBUTE)).thenReturn(user);

        result = businessController.modifyBusiness(request, business.getId(), session);
    }

}