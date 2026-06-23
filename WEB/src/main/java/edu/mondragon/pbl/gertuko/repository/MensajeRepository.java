package edu.mondragon.pbl.gertuko.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import edu.mondragon.pbl.gertuko.model.Chat;
import edu.mondragon.pbl.gertuko.model.Mensaje;
import edu.mondragon.pbl.gertuko.model.User;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
    List<Mensaje> findByChatOrderByFechaEnvioAsc(Chat chat);
    List<Mensaje> findByChatChatIdAndLeidoFalse(Integer chatId);
    Mensaje findTopByChatOrderByFechaEnvioDesc(Chat chat);

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.chat = :chat AND m.leido = false AND m.remitente = :remitente")
    int countByChatAndLeidoFalseAndRemitente(@Param("chat") Chat chat,@Param("remitente") User remitente);
    
    @Query("SELECT m.chat.chatId, COUNT(m) FROM Mensaje m WHERE m.leido = false AND (m.chat.user1.usuarioId = :usuarioId OR m.chat.user2.usuarioId = :usuarioId) AND m.remitente.usuarioId != :usuarioId GROUP BY m.chat.chatId")
    List<Object[]> countMensajesNoLeidosRecibidosPorUsuario(@Param("usuarioId") Integer usuarioId);
}

