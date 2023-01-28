package org.seng302.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.seng302.TestApplication;
import org.seng302.models.requests.NewCardRequest;
import org.seng302.models.requests.NewMessageRequest;

import org.springframework.test.context.ContextConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.bind.ValidationException;

@ContextConfiguration(classes = TestApplication.class)
class MessageModelUnitTests {


    private User sender;
    private User receiver;
    private Card card;
    private Message message;

    @BeforeEach
    void setup() throws Exception {
        sender = new User("Rayna", "YEP", "Dalgety", "", "" , "rdalgety3@ocn.ne.jp","2006-03-30","+7 684 622 5902",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ATQWJM");
        receiver = new User("Sao", "YEP", "Online", "", "" , "rdalgety69@ocn.ne.jp","2006-03-30","+7 684 622 5902",new Address("32", "Little Flour Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"asdfsd");

        String title = "Card";
        String desc = "Desc";
        String keywords = "Test, Two";
        MarketplaceSection section = MarketplaceSection.FORSALE;
        NewCardRequest cardRequest = new NewCardRequest(receiver.getId(), title, desc, keywords, section);
        card = new Card(cardRequest, receiver);
    }

    @Test
    void testMessageCreation_goodInput_success() throws ValidationException {
        String messageContent = "content";

        NewMessageRequest messageRequest = new NewMessageRequest(card.getId(), messageContent);
        Message newMessage = new Message(messageRequest, sender, receiver, card);

        assertThat(newMessage.getSender()).isEqualTo(sender);
        assertThat(newMessage.getCard()).isEqualTo(card);
        assertThat(newMessage.getReceiver()).isEqualTo(receiver);
        assertThat(newMessage.getDescription()).isEqualTo(messageContent);
    }

    @Test
    void testMessageCreation_nullDescription_throwsException() throws ValidationException {
        String messageContent = null;

        NewMessageRequest messageRequest = new NewMessageRequest(card.getId(), messageContent);


        try {
            message = new Message(messageRequest, sender, receiver, card);
            assertThat(message).isNull();  //fails test if validation doesn't work
        } catch (ValidationException e) {
            assertThat(e.getMessage()).isEqualTo("Message must have a description");
        }
    }

    @Test
    void testMessageCreation_emptyDescription_throwsException() throws ValidationException {
        String messageContent = null;

        NewMessageRequest messageRequest = new NewMessageRequest(card.getId(), messageContent);


        try {
            message = new Message(messageRequest, sender, receiver, card);
            assertThat(message).isNull();  //fails test if validation doesn't work
        } catch (ValidationException e) {
            assertThat(e.getMessage()).isEqualTo("Message must have a description");
        }
    }
}
