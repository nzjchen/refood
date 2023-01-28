package org.seng302.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.TestApplication;
import org.seng302.models.Address;
import org.seng302.models.Card;
import org.seng302.models.User;
import org.seng302.models.requests.NewCardRequest;

import org.seng302.models.MarketplaceSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;

import javax.xml.bind.ValidationException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Integration tests of the user repository.
 */

@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
class CardRepositoryTests {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;


    private Card testCard1;
    private Card testCard2;
    private String keywords;
    private User testUser;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException, ValidationException, ParseException {
        cardRepository.deleteAll();
        cardRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        assertThat(userRepository).isNotNull();
        assertThat(cardRepository).isNotNull();

        Address a1 = new Address(null, null, null, null, "New Zealand", null);
        Address a2 = new Address(null, null, null, null, "Australia", null);
        User testUser = new User("Wileen", "YEP", "Tilsley","Diverse", "hybrid orchestration","wtilsley0@rakuten.co.jp","1921-10-08","+86 815 603 3959",a1, "zWkb3AeLn3lc");
        User user2 = new User("Gannon", "YEP", "Tynemouth", "Exclusive", "6th generation intranet", "gtynemouth1@indiatimes.com","1996-03-31","+62 140 282 1784",a2,"HGD0nAJNjSD");
        userRepository.save(testUser);
        userRepository.save(user2);

        keywords = "card, test, asdf";

        NewCardRequest newCardRequest1 = new NewCardRequest(1, "Card title 1", "Card description 1", keywords, MarketplaceSection.FORSALE);
        NewCardRequest newCardRequest2 = new NewCardRequest(2, "Card title 2", "Card description 2", keywords, MarketplaceSection.FORSALE);

        testCard1 = new Card(newCardRequest1, testUser);
        cardRepository.save(testCard1);

        testCard2 = new Card(newCardRequest2, user2);

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = format.parse("1/1/2021");
        testCard2.setDisplayPeriodEnd(date);
        cardRepository.save(testCard2);

    }

    /**
     * test findCardById Expects that
     * the previously saved testCards are
     *
     * returned by findCardById(testCards.id)
     */
    @Test
    void findCardByIdExpectsEquals() {
        Card card1 = cardRepository.findCardById(testCard1.getId());
        assertThat(card1).isEqualTo(testCard1);

        Card card2 = cardRepository.findCardById(testCard2.getId());
        assertThat(card2).isEqualTo(testCard2);
    }

    /**
     * test findCardsByKeywords Expects that:
     * findCardsByKeywords(testCards.getKeywords)
     *
     * returns all card with matching keywords (2)
     */
    @Test
    void findInventoryByCardTypeExpectsEquals() {
        List<Card> cardList = cardRepository.findCardsByKeywords(keywords);
        assertThat(cardList.size()).isEqualTo(2);

    }

    /**
     * test findInventoryBySection Expects that:
     * findInventoryBySection(MarketplaceSection.FORSALE)
     *
     * returns all cards in the marketplace FORSALE section (2)
     */
    @Test
    void findInventoryBySectionExpectsList() {
        Pageable mockPageable = PageRequest.of(0, 10, Sort.by(List.of(new Sort.Order(Sort.Direction.DESC, "created").ignoreCase())));
        Page<Card> cardList = cardRepository.findAllBySection(MarketplaceSection.FORSALE, mockPageable);
        assertThat(cardList.getTotalElements()).isEqualTo(2);
    }

    /**
     * test findInventoryBySection Expects that:
     * findInventoryBySection(MarketplaceSection.FORSALE)
     *
     * returns all cards in the marketplace WANTED section (0)
     */
    @Test
    void findInventoryBySectionExpectsEmptyList() {
        Pageable mockPageable = PageRequest.of(1, 10, Sort.by(List.of(new Sort.Order(Sort.Direction.DESC, "created").ignoreCase())));
        Page<Card> cardList = cardRepository.findAllBySection(MarketplaceSection.WANTED, mockPageable);
        assertThat(cardList.getTotalElements()).isZero();
    }

    /**
     * test deleteCardId Expects that
     * the previously saved testCard is
     *
     * deleted by deleteCardById(testCard.id)
     */
    @Test
    void deleteCardByIdExpectsEmptyList() {
        cardRepository.deleteCardById(testCard1.getId());
        Card card1 = cardRepository.findCardById(testCard1.getId());
        assertThat(card1).isNull();
    }

    /**
     * Test that expects the card repository method findAllByDisplayPeriodEndBefore(Date)
     * returns all the expired cards.
     */
    @Test
    void findExpiredCards() {
        Date date = new Date();
        List<Card> cards = cardRepository.findAllByDisplayPeriodEndBefore(date);
        assertThat(cards.size()).isEqualTo(1);
    }

    /**
     * Edge case test for the repository method retrieving all
     * expired cards where 0 is always expected as the expiry date is
     * set to 2020.
     */
    @Test
    void findExpiredCardsExpectNone() {
        Date current = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.set(Calendar.YEAR, 2020);
        Date date = calendar.getTime();
        List<Card> cards = cardRepository.findAllByDisplayPeriodEndBefore(date);
        assertThat(cards.size()).isZero();
    }
}
