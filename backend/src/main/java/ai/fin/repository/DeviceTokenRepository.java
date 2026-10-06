package ai.fin.repository;

import ai.fin.entities.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, String> {

    Optional<DeviceToken> findByToken(String token);

    List<DeviceToken> findByUser_Id(String userId);

    @Modifying
    int deleteByTokenAndUser_Id(String token, String userId);

    @Modifying
    void deleteAllByUser_Id(String userId);

    @Modifying
    @Query("delete from DeviceToken d where d.token in :tokens")
    int deleteAllByTokenIn(@Param("tokens") Collection<String> tokens);

    @Modifying
    @Query("delete from DeviceToken d where d.lastSeenAt < :cutoff")
    int deleteStale(@Param("cutoff") Instant cutoff);
}