package org.seng302.repositories;

import org.seng302.models.Role;
import org.seng302.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a single user by email (because emails are unique), if they exist in the database.
     * @param email address to be queried.
     * @return a single user should be returned if the email exists.
     */
    User findUserByEmail(String email);

    User findUserById(long id);

    ArrayList<User> findUsersByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);

    ArrayList<User> findUsersByFirstNameOrLastName(String firstName, String lastName, Pageable pageable);

    ArrayList<User> findUsersByNickname(String nickName, Pageable pageable);

    List<User> findAllByRole(Role role);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE User u SET u.role = :role WHERE u.id = :id")
    void updateUserRole(@Param(value = "id") long id, @Param(value = "role") Role role);

    /**
     * Retrieve user objects that match specification as a Page object
     * @param spec
     * @param pageable
     * @return
     */
    Page<User> findAll(Specification<User> spec, Pageable pageable);


}
