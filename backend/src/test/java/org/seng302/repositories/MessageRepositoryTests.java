package org.seng302.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.seng302.models.requests.NewCardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import javax.xml.bind.ValidationException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests of the message repository.
 */

@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
class MessageRepositoryTests {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    private Message testMessage1;
    private Message testMessage2;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException, ValidationException {
        messageRepository.deleteAll();
        messageRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        assertThat(messageRepository).isNotNull();
        assertThat(userRepository).isNotNull();

        Address a1 = new Address(null, null, null, null, "New Zealand", null);
        Address a2 = new Address(null, null, null, null, "Australia", null);
        Address a3 = new Address(null, null, null, null, "China", null);
        Address a4 = new Address(null, null, null, null, "Russia", null);

        User testSender1 = new User("Gomez", "YEP", "Addams","Gom", "Father","gom3zz@addamsfam.com","1921-10-08","+86 123 456 7890",a1, "zWkb3AeLn3lc");
        User testSender2 = new User("Morticia", "YEP", "Addams","Mort", "Mother","motherofmych1ldren@addamsfam.com","1927-05-06","+86 123 456 7890",a2, "HGD0nAJNjSD");
        User testReceiver1 = new User("Pugsley", "YEP", "Addams","Pugs", "Son","littled3mon@addamsfam.com","1969-08-10","+86 123 456 7890",a3, "zWkb3AeLn3ld");
        User testReceiver2 = new User("Wednesday", "YEP", "Addams","Wed", "Daughter","crossb0w@addamsfam.com","1959-06-05","+86 123 456 7890",a4, "HGD0nAJNjSC");
        userRepository.save(testSender1);
        userRepository.save(testSender2);
        userRepository.save(testReceiver1);
        userRepository.save(testReceiver2);

        String keywords = "card, test, asdf";
        NewCardRequest newCardRequest1 = new NewCardRequest(1, "Card title 1", "Card description 1", keywords, MarketplaceSection.FORSALE);
        NewCardRequest newCardRequest2 = new NewCardRequest(2, "Card title 2", "Card description 2", keywords, MarketplaceSection.FORSALE);
        Card testCard1 = new Card(newCardRequest1, testReceiver1);
        Card testCard2 = new Card(newCardRequest2, testReceiver2);
        cardRepository.save(testCard1);
        cardRepository.save(testCard2);

        Date date = new Date();
        testMessage1 = new Message(testSender1, testReceiver1, testCard1, "Message description 1", date);
        testMessage2 = new Message(testSender2, testReceiver2, testCard2, "Message description 2", date);
        messageRepository.save(testMessage1);
        messageRepository.save(testMessage2);
    }

    /**
     * Test findMessageById Expects that:
     * The previously saved testMessages are
     * returned by findMessageById(testMessages.id)
     */
    @Test
    void findMessageByIdExpectsEquals() {
        Message message1 = messageRepository.findMessageById(testMessage1.getId());
        assertThat(message1).isEqualTo(testMessage1);
        Message message2 = messageRepository.findMessageById(testMessage2.getId());
        assertThat(message2).isEqualTo(testMessage2);
    }

    /**
     * Test deleteMessageById Expects that:
     * The sent message is
     * deleted by deleteMessageById(testMessage1.id)
     */
    @Test
    void deleteMessageByIdExpectsNullObject() {
        messageRepository.deleteMessageById(testMessage1.getId());
        Message message1 = messageRepository.findMessageById(testMessage1.getId());
        assertThat(message1).isNull();
    }
}
