package edu.mondragon.pbl.gertuko.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.mondragon.pbl.gertuko.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Ticket findByTicketId(int ticketId);
    List<Ticket> findByVendedor_UsuarioId(Integer vendedorId);
    List<Ticket> findByCliente_UsuarioId(Integer clienteId);
}
