package edu.mondragon.pbl.gertuko.service;

import java.util.List;
import java.util.Map;
import edu.mondragon.pbl.gertuko.model.Mensaje;
import edu.mondragon.pbl.gertuko.model.User;

public interface MensajeService {
    List<Mensaje> findMensajesNoLeidos(Integer chatId, User receptor);
    void save(Mensaje m);
    public Map<Integer, Integer> getMensajesNoLeidosRecibidos(Integer usuarioId);
}
