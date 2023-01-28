package org.seng302.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.TestApplication;
import org.seng302.models.requests.NewCardRequest;
import org.springframework.test.context.ContextConfiguration;
import static org.assertj.core.api.Assertions.assertThat;



@ContextConfiguration(classes = TestApplication.class)
class NotificationModelUnitTests {


    private User user;
    private Card card;
    private Notification notification;
    @BeforeEach
    void setup() throws Exception {
        user = new User("Bob", "", "Loblaw", "", "", "bblaw@email.com", "2006-03-30","+7 684 622 5902", new Address(null, null, null, null, "New Zealand", null), "ATQWJM");
        user.setId(1);
        NewCardRequest cardRequest = new NewCardRequest(user.getId(), "Card Title", "Desc", "Test, Two", MarketplaceSection.FORSALE);

        card = new Card(cardRequest, user);
        notification = new Notification(user.getId(), card.getId(), card.getTitle(), card.getDisplayPeriodEnd());
    }

    @Test
    void testSetStatusDeleted() throws Exception {
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.EXPIRED);
        notification.setDeleted();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.DELETED);
    }

}
