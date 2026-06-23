package edu.mondragon.pbl.gertuko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.mondragon.pbl.gertuko.model.Chat;
import edu.mondragon.pbl.gertuko.model.Mensaje;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.repository.ChatRepository;
import edu.mondragon.pbl.gertuko.repository.MensajeRepository;
import edu.mondragon.pbl.gertuko.repository.UserRepository;
import edu.mondragon.pbl.gertuko.service.MensajeService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private MensajeService mensajeService;
    

    // Mostrar lista de chats ordenados por última actualización
    @GetMapping("/chats")
    public String verChats(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User actual = userRepository.findByUsername(userDetails.getUsername());
        List<Chat> chats = chatRepository.findByUser1OrUser2OrderByUltimaActualizacionDesc(actual, actual);

        Map<Integer, Mensaje> ultimosMensajes = new HashMap<>();
        for (Chat chat : chats) {
            Mensaje ultimoMensaje = mensajeRepository.findTopByChatOrderByFechaEnvioDesc(chat);
            ultimosMensajes.put(chat.getChatId(), ultimoMensaje);
        }

        // Añado solo el contador de no leídos RECIBIDOS
        Map<Integer, Integer> mensajesNoLeidos = mensajeService.getMensajesNoLeidosRecibidos(actual.getUsuarioId());

        model.addAttribute("mensajesNoLeidos", mensajesNoLeidos);
        model.addAttribute("misChats", chats);
        model.addAttribute("ultimosMensajes", ultimosMensajes);
        model.addAttribute("actual", actual);
        return "view/chat/mischats";
    }

    // Mostrar chat con usuario (crea chat si no existe)
    @GetMapping("/chat/{userId}")
    public String verChatConUsuario(@PathVariable Integer userId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User actual = userRepository.findByUsername(userDetails.getUsername());
        if (actual.getUsuarioId() == userId) {
            return "redirect:/chats";
        }
        User otro = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar chat entre ambos sin importar orden de usuarios
        Chat chat = chatRepository.findChatByUsers(actual, otro)
                .orElseGet(() -> {
                    Chat nuevo = new Chat();
                    nuevo.setUser1(actual);
                    nuevo.setUser2(otro);
                    nuevo.setUltimaActualizacion(ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDateTime());
                    return chatRepository.save(nuevo);
                });

        List<Mensaje> mensajes = mensajeRepository.findByChatOrderByFechaEnvioAsc(chat);

        model.addAttribute("chat", chat);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("actual", actual);
        model.addAttribute("otro", otro);
        model.addAttribute("nuevoMensaje", new Mensaje());
        return "view/chat/chat";
    }
}
