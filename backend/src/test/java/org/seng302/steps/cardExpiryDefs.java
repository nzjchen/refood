package org.seng302.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.seng302.models.Address;
import org.seng302.models.Card;
import org.seng302.models.MarketplaceSection;
import org.seng302.models.User;
import org.seng302.models.requests.NewCardRequest;

import javax.xml.bind.ValidationException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class cardExpiryDefs {
    private User testUser;
    private NewCardRequest cardRequest;
    private Card card;
    private Address addr;
    private Date firstDate;
    private Date secondDate;

    @Given("An existing card has just expired")
    public void anExistingCardHasJustExpired() throws NoSuchAlgorithmException, ValidationException {
        addr = new Address(null, null, null, null, null, "Australia", "12345");
        testUser = new User("Rayna", "YEP", "Dalgety", "", "" , "rdalgety3@ocn.ne.jp","2006-03-30","+7 684 622 5902",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ATQWJM");
        testUser.setId(1);
        cardRequest = new NewCardRequest(testUser.getId(), "Card Title", "Desc", "Test, Two", MarketplaceSection.FORSALE);
        card = new Card(cardRequest, testUser);
        card.setDisplayPeriodEnd(new Date());
    }

    @When("I choose to extend it")
    public void iChooseToExtendIt() {
        firstDate = card.getDisplayPeriodEnd();
        card.updateDisplayPeriodEndDate();
        secondDate = card.getDisplayPeriodEnd();
    }

    @Then("The expiry date for the card is extended and the card is still there")
    public void theExpiryDateForTheCardIsExtendedAndTheCardIsStillThere() {
        assertThat(firstDate).isNotEqualTo(secondDate);
    }
}
