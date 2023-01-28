package org.seng302.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.seng302.TestApplication;
import org.seng302.models.Address;
import org.seng302.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;

/**
 * Integration tests of the user repository.
 */

@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        userRepository.deleteAll();
        userRepository.flush();

        assertThat(userRepository).isNotNull();
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Brazil","39960-000");
        Address a2 = new Address("620","Sutherland Lane","Dalai", null,"China", null);
        testUser = new User("Wileen", "YEP", "Tilsley","Diverse", "hybrid orchestration","wtilsley0@rakuten.co.jp","1921-10-08","+86 815 603 3959",a1, "zWkb3AeLn3lc");
        anotherUser = new User("Gannon", "YEP", "Tynemouth", "Exclusive", "6th generation intranet", "gtynemouth1@indiatimes.com","1996-03-31","+62 140 282 1784",a2,"HGD0nAJNjSD");
        userRepository.save(testUser);
        userRepository.save(anotherUser);
    }

    @Test
    void findUsers() {
        User found = userRepository.findUserByEmail("wtilsley0@rakuten.co.jp");
        assertThat(found.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(found.getPassword()).isEqualTo(testUser.getPassword());
        assertThat(found.getId()).isEqualTo(testUser.getId());

        User notFound = userRepository.findUserByEmail("nonexistingemail@aol.com");
        assertThat(notFound).isNull();

    }


}
