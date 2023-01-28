package org.seng302.utilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.seng302.TestApplication;
import org.seng302.models.Role;
import org.seng302.models.User;
import org.seng302.models.Address;
import org.seng302.repositories.UserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = TestApplication.class)
class AdminUtilsTests {

    private UserRepository userRepository;
    private AdminUtils adminUtils;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        adminUtils = new AdminUtils();
        ReflectionTestUtils.setField(adminUtils, "DGAA_EMAIL", "dgaa@test.com");
        ReflectionTestUtils.setField(adminUtils, "DGAA_PASSWORD", "testpasword123");
    }

    @Test
    void testCreateDGAA() throws NoSuchAlgorithmException {
        Mockito.when(userRepository.findAllByRole(Role.DGAA)).thenReturn(new ArrayList<>());
        adminUtils.checkForDefaultGlobalAdmin(userRepository);
        Mockito.verify(userRepository, Mockito.times(1)).findAllByRole(Role.DGAA);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void testAlreadyExistingDGAA() throws NoSuchAlgorithmException {
        List<User> adminList = new ArrayList<>();
        Address dgaaAddr = new Address(null, null, null, null, null, "New Zealand", "8041");
        adminList.add(new User("Test", "DGAA", dgaaAddr, "dgaa@test.com", "testpasword123", Role.DGAA));
        Mockito.when(userRepository.findAllByRole(Role.DGAA)).thenReturn(adminList);

        adminUtils.checkForDefaultGlobalAdmin(userRepository);
        Mockito.verify(userRepository, Mockito.times(1)).findAllByRole(Role.DGAA);
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any());
    }

}
