package edu.mondragon.pbl.gertuko.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketId;
    
    @Column(nullable = true)
    private LocalDateTime FechaEntregaEsperada;

    @Column(nullable = true)
    private String descripcionTicket;

    @ManyToOne
    @JoinColumn(name = "clienteId", referencedColumnName = "usuarioId")
    private User cliente;

    @ManyToOne
    @JoinColumn(name = "vendedorId", referencedColumnName = "usuarioId")
    private User vendedor;
    
    @Column(nullable = false)
    private boolean estado;
}
