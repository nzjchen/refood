package org.seng302.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.CucumberRunnerTest;
import org.seng302.controllers.UserController;
import org.seng302.finders.UserFinder;
import org.seng302.models.Role;
import org.seng302.models.User;
import org.seng302.models.Address;
import org.seng302.models.requests.LoginRequest;
import org.seng302.repositories.UserRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = CucumberRunnerTest.class)
@WebMvcTest(controllers = UserController.class)
public class loginStepDefs {


    @InjectMocks
    private UserController userController;

    @MockBean
    private UserRepository userRepository;

    private UserFinder userFinder;

    LoginRequest loginRequest;
    User user;
    ResponseEntity<String> result;
    HttpSession sesh;
    
    @Before
    public void setup() throws Exception {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        this.user = new User("John", "Smith", addr, "johnsmith@yahoo.com", "Potato1!", Role.USER);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.userController = new UserController(this.userRepository, this.userFinder);
    }

    @Given("User attempts to login")
    public void userAttemptsToLogin() {
    }

    @When("They enter with email {string} and password {string}")
    public void theyEnterWithEmailAndPassword(String email, String password) throws Exception {
        loginRequest = new LoginRequest(email, password);
        Mockito.when(userRepository.findUserByEmail((loginRequest.getEmail()))).thenReturn(user);
        sesh = Mockito.mock(HttpSession.class);
        result = userController.loginUser(loginRequest, sesh);
    }

    @Then("They are redirected to their profile")
    public void theyAreRedirectedToTheirProfile() throws UnsupportedEncodingException {
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }


    @When("They enter an email with an invalid format, such that {string}")
    public void theyEnterAnEmailWithAnInvalidFormatSuchThat(String email) throws NoSuchAlgorithmException, JsonProcessingException {
        loginRequest = new LoginRequest(email, "Potato1!");
        sesh = Mockito.mock(HttpSession.class);
        result = userController.loginUser(loginRequest, sesh);
    }

    @Then("They are given a warning that their email format is invalid")
    public void theyAreGivenAWarningThatTheirEmailFormatIsInvalid() {
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @When("They enter an email with the valid format {string} and password {string}")
    public void theyEnterAnEmailWithTheValidFormatAndPassword(String email, String password) throws NoSuchAlgorithmException, JsonProcessingException {
        loginRequest = new LoginRequest(email, password);
        Mockito.when(userRepository.findUserByEmail("johnsmith@yahoo.com")).thenReturn(user);
        sesh = Mockito.mock(HttpSession.class);
        result = userController.loginUser(loginRequest, sesh);
    }

    @Then("They are given a warning that either their password or email is incorrect")
    public void theyAreGivenAWarningThatEitherTheirPasswordOrEmailIsIncorrect() {
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @When("They enter only the password {string}")
    public void theyEnterOnlyThePassword(String password) throws NoSuchAlgorithmException, JsonProcessingException {
        loginRequest = new LoginRequest(null, password);
        Mockito.when(userRepository.findUserByEmail("johnsmith@yahoo.com")).thenReturn(user);
        sesh = Mockito.mock(HttpSession.class);
        result = userController.loginUser(loginRequest, sesh);
    }

    @Then("They are given a warning that the email field is empty")
    public void theyAreGivenAWarningThatTheEmailFieldIsEmpty() {
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @When("They enter only the email {string}")
    public void theyEnterOnlyTheEmail(String email) throws NoSuchAlgorithmException, JsonProcessingException {
        loginRequest = new LoginRequest(email, "");
        Mockito.when(userRepository.findUserByEmail("johnsmith@yahoo.com")).thenReturn(user);
        sesh = Mockito.mock(HttpSession.class);
        result = userController.loginUser(loginRequest, sesh);
    }

    @Then("They are given a warning that the password field is empty")
    public void theyAreGivenAWarningThatThePasswordFieldIsEmpty() {
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @When("They enter nothing, but attempt to logging anyway")
    public void theyEnterNothingButAttemptToLoggingAnyway() throws NoSuchAlgorithmException, JsonProcessingException {
        loginRequest = new LoginRequest(null, "");
        Mockito.when(userRepository.findUserByEmail("johnsmith@yahoo.com")).thenReturn(user);
        sesh = Mockito.mock(HttpSession.class);
        result = userController.loginUser(loginRequest, sesh);
    }

    @Then("They are given a warning that both email and password fields are empty")
    public void theyAreGivenAWarningThatBothEmailAndPasswordFieldsAreEmpty() {
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Given("User is logged in")
    public void userIsLoggedIn() throws NoSuchAlgorithmException, JsonProcessingException {
        loginRequest = new LoginRequest("johnsmith@yahoo.com", "Potato1!");
        Mockito.when(userRepository.findUserByEmail((loginRequest.getEmail()))).thenReturn(user);
        sesh = Mockito.mock(HttpSession.class);
        result = userController.loginUser(loginRequest, sesh);
    }

    @When("They press log out")
    public void theyPressLogOut() {
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

    @Then("They successfully logout and their token session disappears")
    public void theySuccessfullyLogoutAndTheirTokenSessionDisappears() {
        result = userController.logoutUser(null, sesh);
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }
}
