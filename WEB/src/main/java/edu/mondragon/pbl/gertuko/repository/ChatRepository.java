package edu.mondragon.pbl.gertuko.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import edu.mondragon.pbl.gertuko.model.Chat;
import edu.mondragon.pbl.gertuko.model.User;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByUser1OrUser2OrderByUltimaActualizacionDesc(User user1, User user2);
    @Query("SELECT c FROM Chat c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Optional<Chat> findChatByUsers(@Param("user1") User user1, @Param("user2") User user2);
}

