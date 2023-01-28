package org.seng302.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.TestApplication;
import org.seng302.finders.BusinessFinder;
import org.seng302.models.*;
import org.seng302.models.requests.BusinessIdRequest;
import org.seng302.models.requests.NewBusinessRequest;
import org.seng302.models.requests.UserIdRequest;
import org.seng302.repositories.BoughtListingRepository;
import org.seng302.repositories.BusinessRepository;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ResourceUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests relating to the REST Business Controller.
 */
@WebMvcTest(controllers = BusinessController.class)
@ContextConfiguration(classes = TestApplication.class)
class BusinessControllerTests {

    @Autowired
    MockMvc mvc;
    @InjectMocks
    BusinessController businessController;
    @MockBean
    BusinessRepository businessRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    BoughtListingRepository boughtListingRepository;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BusinessFinder businessFinder;
    @MockBean
    FileService fileService;

    User ownerUser;
    User adminUser;
    User user;
    Business business;
    MockMultipartFile businessImage;
    Image image1;

    @BeforeEach
    public void setup() throws NoSuchAlgorithmException, IOException {
        ownerUser = new User("Rayna", "YEP", "Dalgety", "Universal", "zero tolerance task-force" , "rdalgety3@ocn.ne.jp","2006-03-30","+7 684 622 5902",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ATQWJM");
        ownerUser.setId(1L);
        user = new User("Elwood", "YEP", "Altamirano", "Visionary", "mobile capacity", "ealtamirano8@phpbb.com","1927-02-28","+381 643 240 6530",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ItqVNvM2JBA");
        user.setId(2L);
        adminUser = new User("Gannon", "YEP", "Tynemouth", "Exclusive", "6th generation intranet", "gtynemouth1@indiatimes.com","1996-03-31","+62 140 282 1784",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"HGD0nAJNjSD");
        adminUser.setId(3L);

        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Brazil","39960-000");
        business = new Business("Business1", "Test Business 1", a1, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1L);
        business.createBusiness(ownerUser);
        business.getAdministrators().add(adminUser);
        assertThat(business.getAdministrators().size()).isEqualTo(2);

        File data = ResourceUtils.getFile("src/test/resources/media/images/testlettuce.jpeg");
        assertThat(data).exists();
        byte[] bytes = FileCopyUtils.copyToByteArray(data);
        businessImage = new MockMultipartFile("filename", "test.jpg", MediaType.IMAGE_JPEG_VALUE, bytes);
        image1 = new Image("test", "0", "fakepath", "fakethumbnailpath");
        business.addBusinessImage(image1);
    }

    @Test
    void testNoAuthMakeUserAdmin() throws Exception {
        UserIdRequest userIdReq = new UserIdRequest(user.getId());
        mvc.perform(put("/businesses/{id}/makeAdministrator", business.getId())
                .contentType("application/json").sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(userIdReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testMakeUserAdmin() throws Exception {
        UserIdRequest userIdReq = new UserIdRequest(user.getId());

        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);

        mvc.perform(put("/businesses/{id}/makeAdministrator", business.getId())
                .contentType("application/json").sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(userIdReq)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void makeOwnerAdmin() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        // Attempt to make owner admin.
        Mockito.when(userRepository.findUserById(ownerUser.getId())).thenReturn(ownerUser);
        UserIdRequest ownerUserId = new UserIdRequest(ownerUser.getId());
        mvc.perform(put("/businesses/{id}/makeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(ownerUserId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void makeNullAdmin() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        // Attempt to make owner admin.
        Mockito.when(userRepository.findUserById(10)).thenReturn(null);
        mvc.perform(put("/businesses/{id}/makeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(10)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void makeAdminOfNonExistentBusiness() throws Exception {
        Mockito.when(userRepository.findUserById(ownerUser.getId())).thenReturn(ownerUser);
        UserIdRequest ownerUserId = new UserIdRequest(ownerUser.getId());

        // Test for non-existent business.
        mvc.perform(put("/businesses/{id}/makeAdministrator", 100)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(ownerUserId)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void makeAdminWithNoneOwnerPrivileges() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        // Non-primary admin user test.
        mvc.perform(put("/businesses/{id}/makeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="GAA") // ownerUser - only for auth purposes.
    void makeAdminWithNoDGAAPrivileges() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        // Non-primary admin user test.
        mvc.perform(put("/businesses/{id}/makeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .content(mapper.writeValueAsString(ownerUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testNoAuthRemoveUserAdmin() throws Exception {
        UserIdRequest userIdReq = new UserIdRequest(user.getId());
        mvc.perform(put("/businesses/{id}/removeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(userIdReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testRemoveUserAdmin() throws Exception {
        UserIdRequest adminUserIdReq = new UserIdRequest(adminUser.getId());

        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(userRepository.findUserById(adminUserIdReq.getUserId())).thenReturn(adminUser);

        mvc.perform(put("/businesses/{id}/removeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(adminUserIdReq)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testRemovePrimaryAdminAsAdmin() throws Exception {
        // Attempt to remove primary admin's rights.
        Mockito.when(userRepository.findUserById(ownerUser.getId())).thenReturn(ownerUser);
        UserIdRequest ownerUserId = new UserIdRequest(ownerUser.getId());
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(put("/businesses/{id}/removeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(ownerUserId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testRemoveNullAsAdmin() throws Exception {
        // Attempt to remove primary admin's rights.
        Mockito.when(userRepository.findUserById(ownerUser.getId())).thenReturn(null);
        UserIdRequest ownerUserId = new UserIdRequest(ownerUser.getId());
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(put("/businesses/{id}/removeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(ownerUserId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testRemoveNonAdminAsAdmin() throws Exception {
        // Attempt to remove primary admin's rights.
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        UserIdRequest userId = new UserIdRequest(user.getId());
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(put("/businesses/{id}/removeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(userId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testRemoveAdminFromNoneExistingBusiness() throws Exception {
        // Test for non-existent business.
        Mockito.when(userRepository.findUserById(ownerUser.getId())).thenReturn(ownerUser);
        UserIdRequest ownerUserId = new UserIdRequest(ownerUser.getId());
        mvc.perform(put("/businesses/{id}/removeAdministrator", 100)
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(ownerUserId)))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testRemoveNonePrimaryAdmin() throws Exception {
        // Non-primary admin user test.
        Mockito.when(userRepository.findUserById(ownerUser.getId())).thenReturn(ownerUser);
        UserIdRequest ownerUserId = new UserIdRequest(ownerUser.getId());
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(put("/businesses/{id}/removeAdministrator", business.getId())
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .content(mapper.writeValueAsString(ownerUserId)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetBusiness_returnUnauthorized() throws Exception {
        mvc.perform(get("/businesses/{id}", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testGetBusiness_returnNotAcceptable() throws Exception {
        Mockito.when(businessRepository.findBusinessById(99)).thenReturn(null);
        mvc.perform(get("/businesses/{id}", 99))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void testGetBusiness_returnOk() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);

        MvcResult result = mvc.perform(get("/businesses/{id}", business.getId()))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getResponse().getContentAsString())
                .isEqualTo(mapper.writeValueAsString(business));

    }

    @Test
    @WithMockUser
    void testCreateBusiness_returnCreated() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("Some Business", "Some Description", business.getAddress(), BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);

        mvc.perform(post("/businesses")
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void testInvalidCreateBusiness_returnBadRequest() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest(null, "Some Description", business.getAddress(), BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        mvc.perform(post("/businesses")
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser
    void testCreateBusinessWithNullBusinessType() throws Exception  {
        NewBusinessRequest testBusiness = new NewBusinessRequest("Valid Name", "Some Description", business.getAddress(), null);
        mvc.perform(post("/businesses")
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testActAsBusinessUserOwns() throws Exception {
        BusinessIdRequest businessIdRequest = new BusinessIdRequest(business.getId());
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(post("/actasbusiness")
                .contentType("application/json").sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(businessIdRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testActAsNewBusiness() throws Exception {
        BusinessIdRequest businessIdRequest = new BusinessIdRequest(0);
        Mockito.when(businessRepository.findBusinessById(0)).thenReturn(business);
        mvc.perform(post("/actasbusiness")
                .contentType("application/json").sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(businessIdRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="rdalgety3@ocn.ne.jp", password="ATQWJM", roles="USER") // ownerUser - only for auth purposes.
    void testActAsBusinessDoesntExist() throws Exception {
        BusinessIdRequest businessIdRequest = new BusinessIdRequest(business.getId());
        Mockito.when(businessRepository.findBusinessById(2)).thenReturn(null);
        mvc.perform(post("/actasbusiness")
                .contentType("application/json").sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser)
                .content(mapper.writeValueAsString(businessIdRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNoSessionBusinessSearch() throws Exception {
        mvc.perform(get("/businesses/search")
                .param("query", "Pizza"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testSuccessfulBusinessSearch() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("query", "Business1");
        requestParams.add("type", "");
        requestParams.add("page", "0");

        List<Business> businessList = new ArrayList<>();
        businessList.add(business);
        Mockito.when(businessRepository.findAll()).thenReturn(businessList);

        mvc.perform(get("/businesses/search")
                .params(requestParams))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testSuccessfulBusinessSearchWithSortParameter() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("query", "Business1");
        requestParams.add("type", "");
        requestParams.add("page", "0");
        requestParams.add("sortString", "countryDesc");

        List<Business> businessList = new ArrayList<>();
        businessList.add(business);
        Mockito.when(businessRepository.findAll()).thenReturn(businessList);

        mvc.perform(get("/businesses/search")
                .params(requestParams))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testBusinessSearchWithBadSortParameter() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("query", "Business1");
        requestParams.add("type", "");
        requestParams.add("page", "0");
        requestParams.add("sortString", "badSortString");

        List<Business> businessList = new ArrayList<>();
        businessList.add(business);

        mvc.perform(get("/businesses/search")
                .params(requestParams))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void loggedInBusinessSearch_newBusinessType_returns200() throws Exception {
        mvc.perform(get("/businesses/search")
                .param("query", "Pizza")
                .param("type", "AGRICULTURE FORESTRY AND FISHING")
                .param("page", "0")
                .param("sortString", ""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void loggedInBusinessSearch_newBusinessType2_returns200() throws Exception {
        mvc.perform(get("/businesses/search")
                .param("query", "Pizza")
                .param("type", "INFORMATION MEDIA AND TELECOMMUNICATION")
                .param("page", "0")
                .param("sortString", ""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void loggedInBusinessSearch_InvalidBusinessType_returns200() throws Exception {
        mvc.perform(get("/businesses/search")
                .param("query", "Pizza")
                .param("type", ".,/.,1!#@@%^$^&*(())sauiul';';")
                .param("page", "0")
                .param("sortString", ""))
                .andExpect(status().isOk());
    }


    @Test
    void getTypesMethod_GetsAllTypes() {
        List<BusinessType> types = businessController.getAllTypes();
        Assertions.assertEquals(types.size(), BusinessType.values().length);
    }

    @Test
    @WithMockUser
    void getBusinessTypes_loggedIn_returns200() throws Exception {
        mvc.perform(get("/businesses/types")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isOk());
    }

    @Test
    void getBusinessTypes_noAuth_returns401() throws Exception {
        mvc.perform(get("/businesses/types")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void getSales_noAuth_returns401() throws Exception {
        mvc.perform(get("/businesses/{id}/sales", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getSales_badId_returns406() throws Exception {
        Mockito.when(businessRepository.findBusinessById(99)).thenReturn(null);
        mvc.perform(get("/businesses/{id}/sales", 99))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void getSales_AsAdmin_returns200() throws Exception {
        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(get("/businesses/{id}/sales", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getSales_notAdmin_returns403() throws Exception {
        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(get("/businesses/{id}/sales", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void successfulAddBusinessImage() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(multipart("/businesses/{businessId}/images", business.getId())
                .file(businessImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isCreated());

        mvc.perform(multipart("/businesses/{businessId}/images", business.getId())
                .file(businessImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser))
                .andExpect(status().isCreated());

        Address addr = new Address(null, null, null, null, null, "Australia", "12345");
        User gaaUser = new User("test", "GAA", addr, "fake@fakemail.com", "testpass", Role.GAA);

        mvc.perform(multipart("/businesses/{businessId}/images", business.getId())
                .file(businessImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, gaaUser))
                .andExpect(status().isCreated());
    }

    @Test
    void noAuthAddBusinessImage() throws Exception {
        mvc.perform(multipart("/businesses/{businessId}/images", business.getId())
                .file(businessImage)).andExpect(status().isUnauthorized());
    }

    @Test
    void forbiddenAddBusinessImage() throws Exception {
        mvc.perform(multipart("/businesses/{businessId}/images", business.getId())
                .file(businessImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void badPathForBusinessImages() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(null);
        mvc.perform(multipart("/businesses/{businessId}/images", business.getId())
                .file(businessImage)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void noImageSuppliedBusinessImages() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        MockMultipartFile noImageFile = new MockMultipartFile("filename", null, null, (byte[]) null);
        mvc.perform(multipart("/businesses/{businessId}/images", business.getId())
                .file(noImageFile)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void badFileTypeBusinessImages() throws Exception {
        byte[] badTypeBytes = "Hello World".getBytes();
        MockMultipartFile badTypeFile = new MockMultipartFile("filename", "", MediaType.TEXT_HTML_VALUE, badTypeBytes);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(multipart("/businesses/{businessId}/images", business.getId())
                .file(badTypeFile)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateBusinessPrimaryImage_noAuth_returns401() throws Exception {
        Image image1 = new Image("new image", "0", "../../../resources/media.images/testlettuce.jpeg", "");
        Image image2 = new Image("new image", "1", "../../../resources/media.images/testlettuce2.jpeg", "");
        business.addBusinessImage(image1);
        business.addBusinessImage(image2);
        business.setPrimaryImagePath(image1.getFileName());
        mvc.perform(put("/businesses/{businessId}/images/{imageId}/makeprimary", business.getId(), image2.getId()))
                .andExpect(status().isUnauthorized());
        assertThat(business.getPrimaryImagePath().split("/")[1]).isNotEqualTo(image2.getId());
    }

    @Test
    @WithMockUser
    void updateBusinessPrimaryImage_validRequest_returns200() throws Exception {
        Image image1 = new Image("new image", "0", "../../../resources/media.images/testlettuce.jpeg", "");
        Image image2 = new Image("new image", "1", "../../../resources/media.images/testlettuce2.jpeg", "");
        business.addBusinessImage(image1);
        business.addBusinessImage(image2);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(put("/businesses/{businessId}/images/{imageId}/makeprimary", business.getId(), image2.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isOk());
        String primaryPath = business.getPrimaryImagePath();
        assertThat(String.valueOf(primaryPath.charAt(primaryPath.length()-1))).isEqualTo(String.valueOf(image2.getId()));
    }

    @Test
    @WithMockUser
    void updateBusinessPrimaryImage_notAdmin_returns403() throws Exception {
        Image image1 = new Image("new image", "0", "../../../resources/media.images/testlettuce.jpeg", "");
        Image image2 = new Image("new image", "1", "../../../resources/media.images/testlettuce2.jpeg", "");
        business.addBusinessImage(image1);
        business.addBusinessImage(image2);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(put("/businesses/{businessId}/images/{imageId}/makeprimary", business.getId(), image2.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isForbidden());
        assertThat(business.getPrimaryImagePath()).isNull();
    }


    @Test
    @WithMockUser
    void updateBusinessPrimaryImage_dgaa_returns200() throws Exception {
        Image image1 = new Image("new image", "0", "../../../resources/media.images/testlettuce.jpeg", "");
        Image image2 = new Image("new image", "1", "../../../resources/media.images/testlettuce2.jpeg", "");
        business.addBusinessImage(image1);
        business.addBusinessImage(image2);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        user.setRole(Role.DGAA);
        mvc.perform(put("/businesses/{businessId}/images/{imageId}/makeprimary", business.getId(), image2.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isOk());
        String primaryPath = business.getPrimaryImagePath();
        assertThat(String.valueOf(primaryPath.charAt(primaryPath.length()-1))).isEqualTo(String.valueOf(image2.getId()));
    }

    @Test
    @WithMockUser
    void updateBusinessPrimaryImage_badImageId_returns406() throws Exception {
        Image image1 = new Image("new image", "0", "../../../resources/media.images/testlettuce.jpeg", "");
        Image image2 = new Image("new image", "1", "../../../resources/media.images/testlettuce2.jpeg", "");
        business.addBusinessImage(image1);
        business.addBusinessImage(image2);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(put("/businesses/{businessId}/images/{imageId}/makeprimary", business.getId(), "2")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser))
                .andExpect(status().isNotAcceptable());
        assertThat(business.getPrimaryImagePath()).isNull();
    }


    @Test
    @WithMockUser
    void updateBusinessPrimaryImage_badBusinessId_returns406() throws Exception {
        Image image1 = new Image("new image", "0", "../../../resources/media.images/testlettuce.jpeg", "");
        Image image2 = new Image("new image", "1", "../../../resources/media.images/testlettuce2.jpeg", "");
        business.addBusinessImage(image1);
        business.addBusinessImage(image2);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(null);
        mvc.perform(put("/businesses/{businessId}/images/{imageId}/makeprimary", business.getId(), image1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser))
                .andExpect(status().isNotAcceptable());
        assertThat(business.getPrimaryImagePath()).isNull();
    }




    @Test
    void modifyBusiness_noAuth_returns401() throws Exception {
        mvc.perform(put("/businesses/{id}/modify", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void modifyBusiness_notPrimaryAdmin_returns200() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("Business", "Some Description", business.getAddress(), BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void modifyBusiness_noBusinessReturns406_returns403() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("Business", "Some Description", business.getAddress(), BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(null);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockUser
    void modifyBusiness_validUpdate_updatesCorrectly() throws Exception {
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "New Zealand","39960-000");

        NewBusinessRequest testBusiness = new NewBusinessRequest("Updated", "Updated", a1, BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isOk());

        Assertions.assertEquals(testBusiness.getName(), business.getName());
        Assertions.assertEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertEquals(testBusiness.getAddress(), business.getAddress());
        Assertions.assertEquals(testBusiness.getBusinessType(), business.getBusinessType());
    }

    @Test
    @WithMockUser
    void modifyBusiness_emptyName_returns400() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("", "Updated", business.getAddress(), BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());

        Assertions.assertNotEquals(testBusiness.getName(), business.getName());
        Assertions.assertNotEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertNotEquals(testBusiness.getBusinessType(), business.getBusinessType());
    }

    @Test
    @WithMockUser
    void modifyBusiness_longName_returns400() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "Updated", business.getAddress(), BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());

        Assertions.assertNotEquals(testBusiness.getName(), business.getName());
        Assertions.assertNotEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertNotEquals(testBusiness.getBusinessType(), business.getBusinessType());
    }

    @Test
    @WithMockUser
    void modifyBusiness_maxNameLength_returns200() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "Updated", business.getAddress(), BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isOk());

        Assertions.assertEquals(testBusiness.getName(), business.getName());
        Assertions.assertEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertEquals(testBusiness.getBusinessType(), business.getBusinessType());
    }

    @Test
    @WithMockUser
    void modifyBusiness_maxDescription_returns200() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("Updated", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", business.getAddress(), BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isOk());

        Assertions.assertEquals(testBusiness.getName(), business.getName());
        Assertions.assertEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertEquals(testBusiness.getBusinessType(), business.getBusinessType());
    }

    @Test
    @WithMockUser
    void modifyBusiness_longDescription_returns400() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("Updated", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", business.getAddress(), BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());

        Assertions.assertNotEquals(testBusiness.getName(), business.getName());
        Assertions.assertNotEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertNotEquals(testBusiness.getBusinessType(), business.getBusinessType());
    }

    @Test
    @WithMockUser
    void modifyBusiness_noBusinessType_returns400() throws Exception {
        NewBusinessRequest testBusiness = new NewBusinessRequest("Updated", "Updated", business.getAddress(), null);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());

        Assertions.assertNotEquals(testBusiness.getName(), business.getName());
        Assertions.assertNotEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertNotEquals(testBusiness.getBusinessType(), business.getBusinessType());
    }


    @Test
    @WithMockUser
    void modifyBusiness_noCountry_returns400() throws Exception {
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, null,"39960-000");

        NewBusinessRequest testBusiness = new NewBusinessRequest("Updated", "Updated", a1, BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());


        Assertions.assertNotEquals(testBusiness.getName(), business.getName());
        Assertions.assertNotEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertNotEquals(testBusiness.getBusinessType(), business.getBusinessType());
    }

    @Test
    @WithMockUser
    void modifyBusiness_emptyCountry_returns400() throws Exception {
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "","39960-000");
        NewBusinessRequest testBusiness = new NewBusinessRequest("Updated", "Updated", a1, BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());

        Assertions.assertNotEquals(testBusiness.getName(), business.getName());
        Assertions.assertNotEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertNotEquals(testBusiness.getBusinessType(), business.getBusinessType());
        Assertions.assertNotEquals(testBusiness.getAddress(), business.getAddress());
    }

    @Test
    @WithMockUser
    void modifyBusiness_maxCountryName_returns200() throws Exception {
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Al Jumahiriyah al Arabiyah al Libiyah ash Shabiyah al Ishtirakiyah al Uzma","39960-000");
        NewBusinessRequest testBusiness = new NewBusinessRequest("Updated", "Updated", a1, BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isOk());

        Assertions.assertEquals(testBusiness.getName(), business.getName());
        Assertions.assertEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertEquals(testBusiness.getBusinessType(), business.getBusinessType());
        Assertions.assertEquals(testBusiness.getAddress(), business.getAddress());
    }

    @Test
    @WithMockUser
    void modifyBusiness_longCountryName_returns400() throws Exception {
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Al Jumahiriyah al Arabiyah al Libiyah ash Shabiyah al Ishtirakiyah al Uzmaa","39960-000");
        NewBusinessRequest testBusiness = new NewBusinessRequest("Updated", "Updated", a1, BusinessType.RETAIL_TRADE);

        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(put("/businesses/{id}/modify", business.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(testBusiness))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());

        Assertions.assertNotEquals(testBusiness.getName(), business.getName());
        Assertions.assertNotEquals(testBusiness.getDescription(), business.getDescription());
        Assertions.assertNotEquals(testBusiness.getBusinessType(), business.getBusinessType());
        Assertions.assertNotEquals(testBusiness.getAddress(), business.getAddress());
    }

    @Test
    @WithMockUser
    void removeBusinessImageBadImageId() throws Exception {
        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(delete("/businesses/{businessId}/images/{imageId}/", business.getId(), image1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void removeBusinessImageBadBusinessId() throws Exception {
        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(null);
        mvc.perform(delete("/businesses/{businessId}/images/{imageId}/", business.getId(), image1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, ownerUser))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void removeBusinessImageWrongUser() throws Exception {
        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        mvc.perform(delete("/businesses/{businessId}/images/{imageId}/", business.getId(), image1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isForbidden());
    }
}
