package ai.fin.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "chat_sessions",
        indexes = {
                @Index(name = "idx_chat_sessions_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ChatSession extends BaseEntity {

    /*
     * The default title for a new chat session is "New Chat".
     * This can be changed by the user after the session is created.
     */
    @Column(name = "title", length = 255, nullable = false)
    private String title = "New chat";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "session",
            orphanRemoval = true, // Remove all the messages when the session is deleted
            cascade = CascadeType.ALL, // Cascade all operations to the messages
            fetch = FetchType.LAZY // Fetch the messages when the session is loaded
    )
    @JsonManagedReference // To prevent infinite recursion, Parent side is marked as managed
    private List<ChatMessage> messages = new ArrayList<>();
}