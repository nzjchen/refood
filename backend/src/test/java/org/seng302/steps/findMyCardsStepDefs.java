package org.seng302.steps;


import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.CucumberRunnerTest;
import org.seng302.controllers.CardController;
import org.seng302.controllers.UserController;
import org.seng302.models.*;
import org.seng302.models.requests.NewCardRequest;
import org.seng302.repositories.CardRepository;
import org.seng302.repositories.NotificationRepository;
import org.seng302.repositories.UserRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.http.HttpSession;
import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = CucumberRunnerTest.class)
@WebMvcTest(controllers = UserController.class)
public class findMyCardsStepDefs {



    @InjectMocks
    private CardController cardController;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationRepository notificationRepository;


    User user;
    ResponseEntity<String> result;
    HttpSession sesh;

    @Before
    public void setup() throws Exception {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        user = new User("Bob", "Smith", addr, "johnsmith@yahoo.com", "Potato1!", Role.USER);


        cardRepository = Mockito.mock(CardRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        cardController = new CardController(userRepository, cardRepository, notificationRepository);

        NewCardRequest card = new NewCardRequest(this.user.getId(), "card", "description", "keyword", MarketplaceSection.FORSALE);
        Card card1 = new Card(card, user);
        Card card2 = new Card(card, user);
        List<Card> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);

        Mockito.when(cardRepository.findCardsByUser(user)).thenReturn(cards);
    }


    @Given("A user named {string} exists with {int} cards")
    public void a_user_named_exists_with_cards(String userName, Integer numCards) throws ValidationException {
        NewCardRequest card = new NewCardRequest(this.user.getId(), "card", "description", "keyword", MarketplaceSection.FORSALE);
        assertThat(this.user.getFirstName()).isEqualTo(userName);
        assertThat(cardRepository.findCardsByUser(this.user).size()).isEqualTo(numCards);
    }

    @When("The user retrieves his own cards")
    public void the_user_retrieves_his_own_cards() throws Exception {
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        result = cardController.getUserCards(this.user.getId());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Then("The {int} cards are shown")
    public void the_cards_are_shown(Integer numCards) throws JSONException {
        JSONArray response = new JSONArray(result.getBody());
        assertThat(response.length()).isEqualTo(2);
    }

    @Then("They are owned by the user {string}")
    public void they_are_owned_by_the_user(String userName) throws JSONException {
        //Not cleanest code, but response comes in JSON so need to deconstruct and get values
        JSONArray response = new JSONArray(result.getBody());
        for(int i = 0; i < response.length(); i++) {
            Object userResponse = response.getJSONObject(i).get("user");

            //For some reason everything after the first instance of an object gets set to ID, so check first instance of each user
            if(userResponse.getClass().equals(JSONObject.class)) {
                assertThat(((JSONObject) userResponse).getString("firstName")).isEqualTo(userName);
            }

        }
    }

}
