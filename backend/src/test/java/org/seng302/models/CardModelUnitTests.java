package org.seng302.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.seng302.TestApplication;
import org.seng302.models.requests.NewCardRequest;
import org.springframework.test.context.ContextConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.bind.ValidationException;

@ContextConfiguration(classes = TestApplication.class)
class CardModelUnitTests {


    private User user;
    private Card card;

    @BeforeEach
    void setup() throws Exception {
        user = new User("Rayna", "YEP", "Dalgety", "", "" , "rdalgety3@ocn.ne.jp","2006-03-30","+7 684 622 5902",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ATQWJM");
    }

    @Test
    void testCardCreation_goodInput_success() throws ValidationException {
        String title = "Card";
        String desc = "Desc";
        String keywords = "Test, Two";
        MarketplaceSection section = MarketplaceSection.FORSALE;
        NewCardRequest cardRequest = new NewCardRequest(user.getId(), title, desc, keywords, section);

        card = new Card(cardRequest, user);

        assertThat(card.getUser()).isEqualTo(user);
        assertThat(card.getTitle()).isEqualTo(title);
        assertThat(card.getDescription()).isEqualTo(desc);
        assertThat(card.getKeywords()).isEqualTo(keywords);
        assertThat(card.getSection()).isEqualTo(section);

    }

    @Test
    void testCardCreation_nullTitle_throwsException() throws ValidationException {
        String title = null;
        String desc = "Desc";
        String keywords = "Test, Two";
        MarketplaceSection section = MarketplaceSection.FORSALE;
        NewCardRequest cardRequest = new NewCardRequest(user.getId(), title, desc, keywords, section);

        try {
            card = new Card(cardRequest, user);
            assertThat(card).isNull();  //fails test if validation doesn't work
        } catch (ValidationException e) {
            assertThat(e.getMessage()).isEqualTo("Title cannot be null");
        }
    }

    @Test
    void testCardCreation_shortTitle_throwsException() throws ValidationException {
        String title = "t";
        String desc = "Desc";
        String keywords = "Test, Two";
        MarketplaceSection section = MarketplaceSection.FORSALE;
        NewCardRequest cardRequest = new NewCardRequest(user.getId(), title, desc, keywords, section);
        try {
            card = new Card(cardRequest, user);
            assertThat(card).isNull();  //fails test if validation doesn't work
        } catch (ValidationException e) {
            assertThat(e.getMessage()).isEqualTo("Title length is too short");
        }
    }

    @Test
    void testCardCreation_longTitle_throwsException() throws ValidationException {
        String title = "dsaddsajdsajdklasdjklsajdlasdsdsadadadasdasdsaasdaa";
        String desc = "Desc";
        String keywords = "Test, Two";
        MarketplaceSection section = MarketplaceSection.FORSALE;
        NewCardRequest cardRequest = new NewCardRequest(user.getId(), title, desc, keywords, section);

        try {
            card = new Card(cardRequest, user);
            assertThat(card).isNull();  //fails test if validation doesn't work
        } catch (ValidationException e) {
            assertThat(e.getMessage()).isEqualTo("Title length is too long");
        }
    }

    @Test
    void testCardCreation_maxTitle_successful() throws ValidationException {
        String title = "dsaddsajdsajdklasdjklsajd";
        String desc = "Desc";
        String keywords = "Test, Two";
        MarketplaceSection section = MarketplaceSection.FORSALE;
        NewCardRequest cardRequest = new NewCardRequest(user.getId(), title, desc, keywords, section);

        card = new Card(cardRequest, user);

        assertThat(card.getUser()).isEqualTo(user);
        assertThat(card.getTitle()).isEqualTo(title);
        assertThat(card.getDescription()).isEqualTo(desc);
        assertThat(card.getKeywords()).isEqualTo(keywords);
        assertThat(card.getSection()).isEqualTo(section);

    }

    @Test
    void testCardCreation_minTitle_successful() throws ValidationException {
        String title = "dd";
        String desc = "Desc";
        String keywords = "Test, Two";
        MarketplaceSection section = MarketplaceSection.FORSALE;
        NewCardRequest cardRequest = new NewCardRequest(user.getId(), title, desc, keywords, section);

        card = new Card(cardRequest, user);

        assertThat(card.getUser()).isEqualTo(user);
        assertThat(card.getTitle()).isEqualTo(title);
        assertThat(card.getDescription()).isEqualTo(desc);
        assertThat(card.getKeywords()).isEqualTo(keywords);
        assertThat(card.getSection()).isEqualTo(section);

    }

    @Test
    void testCardCreation_noSection_throwsException() throws ValidationException {
        String title = "dd";
        String desc = "Desc";
        String keywords = "Test, Two";
        MarketplaceSection section = null;
        NewCardRequest cardRequest = new NewCardRequest(user.getId(), title, desc, keywords, section);

        try {
            card = new Card(cardRequest, user);
            assertThat(card).isNull(); //fails test if validation doesn't work
        } catch (ValidationException e) {
            assertThat(e.getMessage()).isEqualTo("Marketplace section is missing");
        }

    }

}
