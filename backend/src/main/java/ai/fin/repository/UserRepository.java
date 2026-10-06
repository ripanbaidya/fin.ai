package ai.fin.repository;

import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // @Query("select u from User u where u.accountStatus= :status and u.deletionRequestedAt < :threshold")
    List<User> findAllByAccountStatusAndDeletionRequestedAtBefore(AccountStatus status, Instant threshold);

    /**
     * Counts the number of users with the specified role and account status.
     *
     * @param role          role of the user
     * @param accountStatus account status of the user
     * @return the count of users matching the criteria
     */
    long countByRoleAndAccountStatus(Role role, AccountStatus accountStatus);
}
