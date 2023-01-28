package org.seng302.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InventoryRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BusinessRepository businessRepository;

    private Business business1;

    private Product testProd1;

    private Inventory testInvent1;

    private Date dateBefore;
    private Date dateAfter;

    @BeforeEach
    void setUp() {
        businessRepository.deleteAll();
        businessRepository.flush();
        productRepository.deleteAll();
        productRepository.flush();
        inventoryRepository.deleteAll();
        inventoryRepository.flush();
        entityManager.clear();

        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.set(2022, 1, 1);
        dateAfter = afterCalendar.getTime();

        Calendar beforeCalendar = Calendar.getInstance();
        afterCalendar.set(2020, 1, 1);
        dateBefore = beforeCalendar.getTime();

        //Need to setup business and product for referential integrity
        assertThat(businessRepository).isNotNull();
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Brazil","39960-000");
        Business b1 = new Business("AnotherBusiness", "A business", a1, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);

        entityManager.persist(b1);
        entityManager.flush();


        assertThat(productRepository).isNotNull();
        testProd1 = new Product("07-4957066", b1, "Spoon", "Soup, Plastic", "Good Manufacturer",  14.69, new Date());
        productRepository.save(testProd1);

        assertThat(inventoryRepository).isNotNull();
        testInvent1 = new Inventory("07-4957066", 1, 5, 2.0, 10.0, dateBefore, dateAfter, dateAfter, dateAfter);
        inventoryRepository.save(testInvent1);

    }


    @Test
    void saveInventory() {
        Inventory inventoryItem = inventoryRepository.findInventoryByIdAndBusinessId(testInvent1.getId(), testInvent1.getBusinessId());

        Assertions.assertEquals(testInvent1.getId(), inventoryItem.getId());
        assertThat(testInvent1.getProductId()).isEqualTo(inventoryItem.getProductId());
        assertThat(testInvent1.getQuantity()).isEqualTo(inventoryItem.getQuantity());

        // Test if insertion is properly inserting values.
        List<Inventory> inventoryItems = inventoryRepository.findInventoryByProductIdAndBusinessId("07-4957066", 1);
        assertThat(inventoryItems.size()).isEqualTo(1);

        //Test insertion properly works
        inventoryItems = inventoryRepository.findInventoryByBusinessId(1);
        assertThat(inventoryItems.size()).isEqualTo(1);
        Inventory anotherInventory = new Inventory("07-4957066", 1, 2, 3.0, 6.0, dateBefore, dateAfter, dateAfter, dateAfter);
        inventoryRepository.save(anotherInventory);
        inventoryItems = inventoryRepository.findInventoryByBusinessId(1);
        assertThat(inventoryItems.size()).isEqualTo(2);

        // Inserting the same product id and business id.
        inventoryRepository.save(anotherInventory);
        inventoryItems = inventoryRepository.findInventoryByBusinessId(1);
        assertThat(inventoryItems.size()).isEqualTo(2);
    }
}
