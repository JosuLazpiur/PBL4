package edu.mondragon.pbl.gertuko.model;

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
@Table(name = "cestas")
public class Cesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cestaId;

    @ManyToOne
    @JoinColumn(name = "productoId", referencedColumnName = "productoId")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "ticketId", referencedColumnName = "ticketId")
    private Ticket ticket;

    @Column(nullable = false)
    private Integer cantidad;
}
