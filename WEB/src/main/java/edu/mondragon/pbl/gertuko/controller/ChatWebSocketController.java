package edu.mondragon.pbl.gertuko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import edu.mondragon.pbl.gertuko.model.Chat;
import edu.mondragon.pbl.gertuko.model.Mensaje;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.repository.ChatRepository;
import edu.mondragon.pbl.gertuko.repository.MensajeRepository;
import edu.mondragon.pbl.gertuko.repository.UserRepository;
import edu.mondragon.pbl.gertuko.service.MensajeService;
import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MensajeService mensajeService;

    @MessageMapping("/chat.send.{destino}")
    public void sendMessage(@DestinationVariable String destino, Mensaje mensaje, SimpMessageHeaderAccessor headerAccessor) {
        Authentication authentication = (Authentication) headerAccessor.getUser();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String username = authentication.getName();

        User remitente = userRepository.findByUsername(username);
        User receptor = userRepository.findByUsername(destino);

        if (remitente == null || receptor == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Chat chat = chatRepository.findChatByUsers(remitente, receptor)
                .orElseGet(() -> {
                    Chat nuevo = new Chat();
                    nuevo.setUser1(remitente);
                    nuevo.setUser2(receptor);
                    nuevo.setUltimaActualizacion(ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDateTime());
                    return chatRepository.save(nuevo);
                });

        mensaje.setRemitente(remitente);
        mensaje.setChat(chat);
        mensaje.setFechaEnvio(ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDateTime());

        mensajeRepository.save(mensaje);

        chat.setUltimaActualizacion(ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDateTime());
        chatRepository.save(chat);

        messagingTemplate.convertAndSendToUser(receptor.getUsername(), "/queue/messages", mensaje);
        messagingTemplate.convertAndSendToUser(remitente.getUsername(), "/queue/messages", mensaje);
        messagingTemplate.convertAndSendToUser(receptor.getUsername(), "/queue/notificaciones", mensaje);

        // Solo enviar a /contador-mischats si el destino no es el remitente (para no duplicar en quien lo envía)
        if (!mensaje.getRemitente().getUsername().equals(destino)) {
            int noLeidos = mensajeRepository.countByChatAndLeidoFalseAndRemitente(
                mensaje.getChat(),
                mensaje.getRemitente()
            );

            // Escapar comillas dobles y caracteres especiales para evitar problemas en el JSON manual
            String textoEscapado = mensaje.getTexto()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");

            String json = String.format(
                "{\"chatId\":%d,\"noLeidos\":%d,\"texto\":\"%s\"}",
                mensaje.getChat().getChatId(),
                noLeidos,
                textoEscapado
            );

            messagingTemplate.convertAndSendToUser(
                destino,
                "/queue/contador-mischats",
                json
            );
        }
    }

    @MessageMapping("/chat.leido.{chatId}")
    public void marcarComoLeido(@DestinationVariable Integer chatId, Principal principal) {
        User actual = userRepository.findByUsername(principal.getName());
        List<Mensaje> mensajesNoLeidos = mensajeService.findMensajesNoLeidos(chatId, actual);
        
        for (Mensaje m : mensajesNoLeidos) {
            m.setLeido(true);
            mensajeService.save(m);

            messagingTemplate.convertAndSendToUser(
                m.getRemitente().getUsername(),
                "/queue/leidos",
                m.getMensajeId()
            );
        }

        if (!mensajesNoLeidos.isEmpty()) {
            Mensaje ultimoMensaje = mensajesNoLeidos.get(mensajesNoLeidos.size() - 1);
            
            // Este es el envío crítico para /mischats
            messagingTemplate.convertAndSendToUser(
                ultimoMensaje.getRemitente().getUsername(),
                "/queue/leidos-mischats",
                ultimoMensaje.getMensajeId().toString() // Asegurar que es String
            );
        }
    }
}
