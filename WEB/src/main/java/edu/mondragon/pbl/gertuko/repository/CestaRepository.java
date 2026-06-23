package edu.mondragon.pbl.gertuko.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.mondragon.pbl.gertuko.model.Cesta;

public interface CestaRepository extends JpaRepository<Cesta, Integer> {
    Cesta findByCestaId(int cestaId);
    List<Cesta> findByTicket_TicketId(Integer ticketId);
}
