package edu.mondragon.pbl.gertuko.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean tieneMasDe5Productos(int ticketId) {
        String sql = "SELECT ticket_5_productos(?)";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, ticketId);
        return result != null && result == 1;
    }
}