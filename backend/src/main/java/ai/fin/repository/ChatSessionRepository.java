package ai.fin.repository;

import ai.fin.entities.ChatSession;
import ai.fin.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {

    List<ChatSession> findByUserOrderByUpdatedAtDesc(User user);

    List<ChatSession> findByUser_IdOrderByUpdatedAtDesc(String userId);

    void deleteAllByUser(User user);

    void deleteAllByUser_Id(String userId);

    Optional<ChatSession> findByIdAndUser(String id, User user);

    Optional<ChatSession> findByIdAndUser_Id(String sessionId, String userId);
}