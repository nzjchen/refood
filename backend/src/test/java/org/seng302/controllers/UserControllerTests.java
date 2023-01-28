package org.seng302.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.TestApplication;
import org.seng302.finders.UserFinder;
import org.seng302.models.Address;
import org.seng302.models.Image;
import org.seng302.models.Role;
import org.seng302.models.User;
import org.seng302.models.requests.LoginRequest;
import org.seng302.models.responses.UserIdResponse;
import org.seng302.models.requests.NewUserRequest;
import org.seng302.repositories.UserRepository;
import org.seng302.utilities.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


@ContextConfiguration(classes = TestApplication.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private UserController userController;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserFinder userFinder;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private FileService fileService;

    private User user;
    List<User> users;
    private Image image1;
    MockMultipartFile userImage;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException, IOException {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        image1 = new Image("new image", "0", "../../../resources/media.images/testlettuce.jpeg", "");

        user = new User("John", "Smith", addr, "johnsmith99@gmail.com", "1337-H%nt3r2", Role.USER);
        user.addUserImage(image1);
        user.setId(1L);
        userRepository.save(user);


        users = new ArrayList<User>();
        Address a1 = new Address("1", "Kropf Court", "Jequitinhonha", null, "Brazil", "39960-000");
        User user1 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", a1, "1337-H%nt3r2");
        User user2 = new User("Jennifer", "Riley", "Smith", "Jenny",
                "Likes long walks on the beach", "jenthar95@gmail.com",
                "1995-05-27", "+64 3 555 0129", a1, "1337-H%nt3r2");
        User user3 = new User("Oliver", "Alfred", "Smith", "Ollie",
                "Likes long walks on the beach", "ollie69@gmail.com",
                "1969-04-27", "+64 3 555 0129", a1, "1337-H%nt3r2");

        users.add(user1);
        users.add(user2);
        users.add(user3);
        File data = ResourceUtils.getFile("src/test/resources/media/images/testlettuce.jpeg");
        assertThat(data).exists();
        byte[] bytes = FileCopyUtils.copyToByteArray(data);
        userImage = new MockMultipartFile("filename", "test.jpg", MediaType.IMAGE_JPEG_VALUE, bytes);
    }

    @Test
    void testSuccessfulRegistration() throws Exception {
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Brazil","39960-000");
        NewUserRequest newUserRequest = new NewUserRequest("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", a1, "1337-H%nt3r2");
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(newUserRequest)))
                        .andExpect(status().isCreated());

        // With bare minimum requirements.
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("John", null, "Smith", null,
                                                        null, "johnsmith99@gmail.com", "1999-04-27", null, minAddress, "1337-H%nt3r2");
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isCreated());

    }

    @Test
    void testRegistrationWithInvalidAddress() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", null, "1337-H%nt3r2");
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isBadRequest());

        Address address = new Address(null, null, null, null, null, null);
        newUserRequest.setHomeAddress(address);
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isBadRequest());

        Address missingCountryAddress = new Address("1","Kropf Court","Jequitinhonha", null, null,"39960-000");
        newUserRequest.setHomeAddress(missingCountryAddress);
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testRegistrationWithInvalidUserDetails() throws Exception {
        Address address = new Address(null, null, null, null, "Canada", null);
        NewUserRequest invalidEmailUser = new NewUserRequest("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", null,
                "1999-04-27", "+64 3 555 0129", address, "1337-H%nt3r2");

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(invalidEmailUser)))
                .andExpect(status().isBadRequest());

        invalidEmailUser.setEmail("johnsmith99gmail.com");
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(invalidEmailUser)))
                .andExpect(status().isBadRequest());

        NewUserRequest noFirstName = new NewUserRequest(null, null, "Smith", null,
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", null, address, "1337-H%nt3r2");
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(noFirstName)))
                .andExpect(status().isBadRequest());

        NewUserRequest noLastName = new NewUserRequest("John", null, null, null,
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", null, address, "1337-H%nt3r2");
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(noLastName)))
                .andExpect(status().isBadRequest());

        NewUserRequest noDob = new NewUserRequest("John", null, "Smith", null,
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                null, null, address, "1337-H%nt3r2");
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(noDob)))
                .andExpect(status().isBadRequest());

        NewUserRequest noPassword = new NewUserRequest("John", null, "Smith", null,
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", null, address, null);
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(noPassword)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithExistingEmail() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("John", null, "Smith", null,
                null, "johnsmith99@gmail.com", "1999-04-27", null, minAddress, "1337-H%nt3r2");

        Mockito.when(userRepository.findUserByEmail(minUserRequest.getEmail())).thenReturn(user);
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isConflict());

    }

    @Test
    @WithMockUser(roles="USER")
    void testGetExistingUser() throws Exception {
        Address a1 = new Address("1", "Kropf Court", "Jequitinhonha", null, "Brazil", "39960-000");
        User user = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", a1, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user);
        MvcResult userFound = mockMvc.perform(get("/users/{id}",0)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andReturn();
        Assertions.assertEquals(userFound.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    @WithMockUser(roles="USER")
    void testGettingNonExistingUser() throws Exception {
        MvcResult userNotFound = mockMvc.perform(get("/users/{id}", 0))
                .andReturn();
        Assertions.assertEquals(userNotFound.getResponse().getStatus(), HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    void testUnauthorizedGettingUser() throws Exception {
        MvcResult userNotFound = mockMvc.perform(get("/users/{id}", 0))
                .andReturn();
        Assertions.assertEquals(userNotFound.getResponse().getStatus(), HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void testGoodLogin() throws Exception {
        Address a1 = new Address("1", "Kropf Court", "Jequitinhonha", null, "Brazil", "39960-000");
        User user = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", a1, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserByEmail("johnsmith99@gmail.com")).thenReturn(user);
        LoginRequest loginReq = new LoginRequest(user.getEmail(), "1337-H%nt3r2");
        MvcResult success = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginReq)))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    void testBadPasswordLogin() throws Exception {
        Address a1 = new Address("1", "Kropf Court", "Jequitinhonha", null, "Brazil", "39960-000");
        User user = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", a1, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserByEmail("johnsmith99@gmail.com")).thenReturn(user);
        LoginRequest loginReq = new LoginRequest(user.getEmail(), "BABABOOEY");
        MvcResult success = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginReq)))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testNonExistingUserLogin() throws Exception {
        LoginRequest loginReq = new LoginRequest("Bazinga", "BABABOOEY");
        MvcResult success = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginReq)))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser(roles="USER")
    void testGoodUserSearch() throws Exception {
        Mockito.when(userRepository.findAll()).thenReturn(this.users);
        MvcResult results = mockMvc.perform(get("/users/search")
                .param("searchQuery", "Smith")
                .param("pageNum", String.valueOf(0)))
                .andReturn();
        Assertions.assertEquals(results.getResponse().getStatus(), HttpStatus.OK.value());

    }

    @Test
    @WithMockUser(roles="USER")
    void testGoodUserSearchWithSortString() throws Exception {
        Mockito.when(userRepository.findAll()).thenReturn(users);
        MvcResult results = mockMvc.perform(get("/users/search")
                .param("searchQuery", "Smith")
                .param("pageNum", String.valueOf(0))
                .param("sortString", "emailDesc"))
                .andReturn();
        Assertions.assertEquals(results.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    @WithMockUser(roles="USER")
    void testUserSearchWithBadSortString() throws Exception {
        Mockito.when(userRepository.findAll()).thenReturn(users);
        MvcResult results = mockMvc.perform(get("/users/search")
                .param("searchQuery", "Smith")
                .param("pageNum", String.valueOf(0))
                .param("sortString", "badSortString"))
                .andReturn();
        Assertions.assertEquals(results.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser(roles="DGAA")
    void testGoodUserAdmin() throws Exception {
        Address a1 = new Address("1", "Kropf Court", "Jequitinhonha", null, "Brazil", "39960-000");
        User user = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", a1, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user);
        MvcResult success =  mockMvc.perform(put("/users/{id}/makeAdmin", 0))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    @WithMockUser(roles="DGAA")
    void testBadIdUserAdmin() throws Exception {
        MvcResult success =  mockMvc.perform(put("/users/{id}/makeAdmin", 0))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    void testNoTokenUserAdmin() throws Exception {
        MvcResult success =  mockMvc.perform(put("/users/{id}/makeAdmin", 0))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithMockUser(roles="USER")
    void testNoAuthorityUserAdmin() throws Exception {
        MvcResult success =  mockMvc.perform(put("/users/{id}/makeAdmin", 0))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.FORBIDDEN.value());
    }



    @Test
    @WithMockUser(roles="DGAA")
    void testGoodUserRevokeAdmin() throws Exception {
        Address a1 = new Address("1", "Kropf Court", "Jequitinhonha", null, "Brazil", "39960-000");
        User user = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", a1, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user);
        MvcResult success =  mockMvc.perform(put("/users/{id}/revokeAdmin", 0))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.OK.value());
    }


    @Test
    @WithMockUser(roles="DGAA")
    void testBadIdUserRevokeAdmin() throws Exception {
        MvcResult success =  mockMvc.perform(put("/users/{id}/revokeAdmin", 0))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    void testNoTokenUserRevokeAdmin() throws Exception {
        MvcResult success =  mockMvc.perform(put("/users/{id}/revokeAdmin", 0))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithMockUser(roles="USER")
    void testNoAuthorityUserRevokeAdmin() throws Exception {
        MvcResult success =  mockMvc.perform(put("/users/{id}/revokeAdmin", 0))
                .andReturn();
        Assertions.assertEquals(success.getResponse().getStatus(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testNoAuthGetUser() throws Exception {
        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="USER")
    void testSuccessfulGetUser() throws Exception {
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        MvcResult result = mockMvc.perform(get("/users/{id}", user.getId())).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getResponse().getContentAsString()).isEqualTo(mapper.writeValueAsString(user));

    }

    @Test
    @WithMockUser(roles="USER")
    void testInvalidGetUser() throws Exception {
        Mockito.when(userRepository.findUserById(100)).thenReturn(null);
        MvcResult result = mockMvc.perform(get("/users/{id}", user.getId())).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(result.getResponse().getContentAsString()).isEmpty();

    }

    @Test
    void testUnauthorizedMakeAdmin() throws Exception {
        mockMvc.perform(put("/users/{id}/makeAdmin", user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="USER")
    void testForbiddenMakeAdmin() throws Exception {
        mockMvc.perform(put("/users/{id}/makeAdmin", user.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="GAA")
    void testForbiddenMakeAdminAsGAA() throws Exception {
        mockMvc.perform(put("/users/{id}/makeAdmin", user.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="DGAA")
    void testNotAcceptableMakeAdmin() throws Exception {
        Mockito.when(userRepository.findUserById(1000)).thenReturn(null);
        mockMvc.perform(put("/users/{id}/makeAdmin", user.getId()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(roles="DGAA")
    void testSuccessfulMakeAdmin() throws Exception {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        User adminUser = new User("New", "DGAA", addr, "dgaaEmail@email.com", "dgaa123", Role.DGAA);
        adminUser.setId(5);

        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        mockMvc.perform(put("/users/{id}/makeAdmin", user.getId())
                .sessionAttr("user", adminUser))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthorizedRevokeAdmin() throws Exception {
        mockMvc.perform(put("/users/{id}/revokeAdmin", user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="USER")
    void testForbiddenRevokeAdmin() throws Exception {
        mockMvc.perform(put("/users/{id}/revokeAdmin", user.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="GAA")
    void testForbiddenRevokeAdminAsGAA() throws Exception {
        mockMvc.perform(put("/users/{id}/revokeAdmin", user.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="DGAA")
    void testNotAcceptableRevokeAdmin() throws Exception {
        Mockito.when(userRepository.findUserById(1000)).thenReturn(null);
        mockMvc.perform(put("/users/{id}/revokeAdmin", user.getId()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(roles="DGAA")
    void testConflictRevokeAdmin() throws Exception {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        User adminUser = new User("New", "DGAA", addr, "dgaaEmail@email.com", "dgaa123", Role.DGAA);
        adminUser.setId(5);

        Mockito.when(userRepository.findUserById(adminUser.getId())).thenReturn(adminUser);
        mockMvc.perform(put("/users/{id}/revokeAdmin", adminUser.getId())
                .sessionAttr("user", adminUser))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles="DGAA")
    void testSuccessfulRevokeAdmin() throws Exception {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        User adminUser = new User("New", "DGAA", addr, "dgaaEmail@email.com", "dgaa123", Role.DGAA);
        adminUser.setId(5);

        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        mockMvc.perform(put("/users/{id}/revokeAdmin", user.getId())
                .sessionAttr("user", adminUser))
                .andExpect(status().isOk());
    }

    @Test
    void testBadRequestLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());
        Mockito.when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(null);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        Mockito.when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(user);
        loginRequest.setPassword("incorrectpassword");
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        Mockito.when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(null);
        LoginRequest nullRequest = new LoginRequest(null, null);
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(nullRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest(user.getEmail(), "1337-H%nt3r2");
        Mockito.when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(mapper.writeValueAsString(loginRequest))).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        UserIdResponse response = new UserIdResponse(user);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(mapper.writeValueAsString(response));
    }

    @Test
    void testNoAuthAddUserImage() throws Exception {
        mockMvc.perform(multipart("/users/{id}/images", user.getId())
                .file(userImage))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="USER")
    void testSuccessfulAddUserImage() throws Exception {
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        mockMvc.perform(multipart("/users/{id}/images", user.getId())
                .file(userImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isCreated());

        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        User gaaUser = new User("test", "GAA", addr, "fake@fakemail.com", "testpass", Role.GAA);

        mockMvc.perform(multipart("/users/{id}/images", user.getId())
                .file(userImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, gaaUser))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles="USER")
    void testBadPathFoUserImages() throws Exception {
        // Non-existent user.
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(null);
        mockMvc.perform(multipart("/users/{id}/images", user.getId())
                .file(userImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(roles="USER")
    void testUnsuccessfulAddUserImageNoImageSupplied() throws Exception {
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        MockMultipartFile noImageFile = new MockMultipartFile("filename", null, null, (byte[]) null);
        mockMvc.perform(multipart("/users/{id}/images", user.getId())
                .file(noImageFile)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    void testUnsuccessfulAddUserImageIncorrectFileType() throws Exception {
        byte[] badTypeBytes = "Hello World".getBytes();
        MockMultipartFile badTypeFile = new MockMultipartFile("filename", "", MediaType.TEXT_HTML_VALUE, badTypeBytes);
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        mockMvc.perform(multipart("/users/{id}/images", user.getId())
                .file(badTypeFile)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    void testUnsuccessfulAddUserImageForbidden() throws Exception {
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        User anotherUser = new User("test", "user", addr, "fake@fakemail.com", "testpass", Role.USER);
        anotherUser.setId(2);
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        mockMvc.perform(multipart("/users/{id}/images", user.getId())
                .file(userImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser))
                .andExpect(status().isForbidden());
    }

    // User image tests
    @Test
    @WithMockUser(roles="USER")
    void testBadImageIdSetUserImage() throws Exception {
        // Wrong image id
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        mockMvc.perform(put("/users/{id}/images/100/makeprimary", user.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(roles="USER")
    void testBadUserIdSetUserImage() throws Exception {
        //wrong user id
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        mockMvc.perform(put("/users/50/images/{imageId}/makeprimary", image1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user));
    }

    @Test
    void testModifyUser_NotLoggedIn() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("John", null, "Smith", null,
                null, "johnsmith99@gmail.com", "1999-04-27", null, minAddress, "1337-H%nt3r2");
        mockMvc.perform(put("/users/{id}", 0)
                .contentType("application/json")
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username="johnsmith99@gmail.com", password="1337-H%nt3r2", roles="USER")
    void testModifyUser_NonExistingUser() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("John", null, "Smith", null,
                null, "johnsmith99@gmail.com", "1999-04-27", null, minAddress, "1337-H%nt3r2");
        User user1 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", minAddress, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(null);
        mockMvc.perform(put("/users/{id}", 0)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user1)
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(roles="USER")
    void testForbiddenSetUserImage() throws Exception {
        //forbidden user
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        user.addUserImage(image1);
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        User forbidden = new User("test", "user", addr, "test@gmail.com", "password", Role.USER);
        mockMvc.perform(put("/users/{id}/images/{imageId}/makeprimary", user.getId(), image1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, forbidden))
                .andExpect(status().isForbidden());
    }

    @Test
    void noAuthSetImage() throws Exception {
    // no authentication
    mockMvc.perform(put("/users/{id}/images/{imageId}/makeprimary", user.getId(), image1.getId()))
            .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles="USER")
    void testSetProductImageSuccessful() throws Exception {
        String prevPrimaryPath = user.getPrimaryImagePath();
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        user.addUserImage(image1);
        mockMvc.perform(put("/users/{id}/images/{imageId}/makeprimary", user.getId(), image1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isOk());
        Assertions.assertNotEquals(prevPrimaryPath, userRepository.findUserById(user.getId()).getPrimaryImagePath());
        Assertions.assertEquals(userRepository.findUserById(user.getId()).getPrimaryImagePath(), user.getPrimaryImagePath());
    }

    @Test
    @WithMockUser(roles="USER")
    void testSetProductImageDgaaSuccessful() throws Exception {
        String prevPrimaryPath = user.getPrimaryImagePath();
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        user.addUserImage(image1);
        // DGAA not owner of business
        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        User gaa = new User("test", "GAA", addr, "test@tester.com", "password", Role.GAA);
        mockMvc.perform(put("/users/{id}/images/{imageId}/makeprimary", user.getId(), image1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, gaa))
                .andExpect(status().isOk());
        Assertions.assertNotEquals(prevPrimaryPath, userRepository.findUserById(user.getId()).getPrimaryImagePath());
        Assertions.assertEquals(userRepository.findUserById(user.getId()).getPrimaryImagePath(), user.getPrimaryImagePath());

    }

    @WithMockUser(username="johnsmith99@gmail.com", password="1337-H%nt3r2", roles="USER")
    void testModifyUser_undefinedReqBody() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest(null, null, null, null,
                null, null, null, null, null, null);
        User user1 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", minAddress, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user1);
        mockMvc.perform(put("/users/{id}", 0)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user1)
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username="omar@mail.com", password="1337-H%nt3r2", roles="USER")
    void testModifyUser_ConflictingEmails() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("John", null, "Smith", null,
                null, "johnsmith99@gmail.com", "1999-04-27", null, minAddress, "1337-H%nt3r2");
        User user1 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", minAddress, "1337-H%nt3r2");
        User user2 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "omar@mail.com",
                "1999-04-27", "+64 3 555 0129", minAddress, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user2);
        Mockito.when(userRepository.findUserByEmail("johnsmith99@gmail.com")).thenReturn(user1);
        mockMvc.perform(put("/users/{id}", 0)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user2)
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isConflict());
    }


    @Test
    @WithMockUser(username="johnsmith99@gmail.com", password="1337-H%nt3r2", roles="USER")
    void testModifyUser_SuccessfulUpdate() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("Amogus", "Sus", "McSussy", null,
                "Fabian's worst student", "amcsussy@gmail.com", "1999-04-27", "+64 3 555 0130", minAddress, "1337-H%nt3r2");
        User user1 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", minAddress, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user1);

        mockMvc.perform(put("/users/{id}", 0)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user1)
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="johnsmith99@gmail.com", password="1337-H%nt3r2", roles="USER")
    void testModifyUser_SuccessfulNamesUpdate() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("Amogus", "Sus", "McSussy", null,
                "Fabian's worst student", "amcsussy@gmail.com", "1999-04-27", "+64 3 555 0130", minAddress, "1337-H%nt3r2");
        User user1 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", minAddress, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user1);
        Assertions.assertEquals("John", user1.getFirstName());
        Assertions.assertEquals("Hector", user1.getMiddleName());
        Assertions.assertEquals("Smith", user1.getLastName());
        Assertions.assertEquals("Jonny", user1.getNickname());
        mockMvc.perform(put("/users/{id}", 0)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user1)
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isOk());
        Assertions.assertEquals(user1.getFirstName(), minUserRequest.getFirstName());
        Assertions.assertEquals(user1.getMiddleName(), minUserRequest.getMiddleName());
        Assertions.assertEquals(user1.getLastName(), minUserRequest.getLastName());
        Assertions.assertEquals(user1.getNickname(), minUserRequest.getNickname());
    }

    @Test
    @WithMockUser(username="johnsmith99@gmail.com", password="1337-H%nt3r2", roles="USER")
    void testModifyUser_SuccessfulEmailUpdate() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("Amogus", "Sus", "McSussy", null,
                "Fabian's worst student", "amcsussy@gmail.com", "1999-04-27", "+64 3 555 0130", minAddress, "1337-H%nt3r2");
        User user1 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", minAddress, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user1);
        Assertions.assertEquals("johnsmith99@gmail.com", user1.getEmail());
        mockMvc.perform(put("/users/{id}", 0)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user1)
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isOk());
        Assertions.assertEquals(user1.getEmail(), minUserRequest.getEmail());
    }

    @Test
    @WithMockUser(username="johnsmith99@gmail.com", password="1337-H%nt3r2", roles="USER")
    void testModifyUser_SuccessfulPhoneUpdate() throws Exception {
        Address minAddress = new Address(null, null, null, null, "Canada", null);
        NewUserRequest minUserRequest = new NewUserRequest("Amogus", "Sus", "McSussy", null,
                "Fabian's worst student", "amcsussy@gmail.com", "1999-04-27", "+64 3 555 0130", minAddress, "1337-H%nt3r2");
        User user1 = new User("John", "Hector", "Smith", "Jonny",
                "Likes long walks on the beach", "johnsmith99@gmail.com",
                "1999-04-27", "+64 3 555 0129", minAddress, "1337-H%nt3r2");
        Mockito.when(userRepository.findUserById(0)).thenReturn(user1);
        Assertions.assertEquals("+64 3 555 0129", user1.getPhoneNumber());
        mockMvc.perform(put("/users/{id}", 0)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user1)
                .content(mapper.writeValueAsString(minUserRequest)))
                .andExpect(status().isOk());
        Assertions.assertEquals(user1.getPhoneNumber(), minUserRequest.getPhoneNumber());
    }
}
