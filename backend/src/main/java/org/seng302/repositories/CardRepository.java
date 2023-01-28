package org.seng302.repositories;

import org.seng302.models.Card;
import org.seng302.models.MarketplaceSection;
import org.seng302.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * JPA Repository for the Card entity/table.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Get Card by it's ID
     *
     * @param id unique identifier of the card
     * @return Card that matches the id (if any).
     */
    Card findCardById(long id);

    /**
     * Gets cards with a matching keywords string
     *
     * @param keywords a string of space separated list of keywords.
     * @return List<Card> a list of cards matching keywords
     */
    List<Card> findCardsByKeywords(String keywords);

    /**
     * Search for cards with marketplace section type:
     * FORSALE, WANTED, EXCHANGE
     *
     * @param section enum of MarketplaceSection
     * @param pageable helper Pageable object that is passed in the controller class.
     * @return List<Card> a list of cards matching section
     */
    Page<Card> findAllBySection(MarketplaceSection section, Pageable pageable);

    List<Card> findAllByDisplayPeriodEndBefore(Date date);

    /**
     * Gets all cards from a specific user
     *
     * @param user User that we want to retrieve cards from
     * @return List<Card> a list of cards created by user
     */
    List<Card> findCardsByUser(User user);
    /**
     * Delete a Card by it's ID
     *
     * @param id unique identifier of the card
     * @return long number of records deleted
     */
    @Transactional
    long deleteCardById(long id);
}
