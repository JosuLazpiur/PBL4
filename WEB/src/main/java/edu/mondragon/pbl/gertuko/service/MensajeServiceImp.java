package edu.mondragon.pbl.gertuko.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.mondragon.pbl.gertuko.model.Mensaje;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.repository.MensajeRepository;

@Service
public class MensajeServiceImp implements MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Override
    public List<Mensaje> findMensajesNoLeidos(Integer chatId, User receptor) {
        List<Mensaje> mensajesNoLeidos = mensajeRepository.findByChatChatIdAndLeidoFalse(chatId);

        // Filtrar solo los mensajes cuyo remitente NO es el receptor (es decir, mensajes dirigidos a receptor)
        return mensajesNoLeidos.stream()
                .filter(m -> !m.getRemitente().getUsuarioId().equals(receptor.getUsuarioId()))
                .toList();
    }


    @Override
    public void save(Mensaje m) {
       mensajeRepository.save(m);
    }
    
    @Override
    public Map<Integer, Integer> getMensajesNoLeidosRecibidos(Integer usuarioId) {
        List<Object[]> resultados = mensajeRepository.countMensajesNoLeidosRecibidosPorUsuario(usuarioId);
        Map<Integer, Integer> contadores = new HashMap<>();
        resultados.forEach(r -> contadores.put((Integer)r[0], ((Long)r[1]).intValue()));
        return contadores;
    }
}
