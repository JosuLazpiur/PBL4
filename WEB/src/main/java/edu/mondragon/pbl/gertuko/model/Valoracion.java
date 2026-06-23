package edu.mondragon.pbl.gertuko.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "valoraciones")
public class Valoracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer valoracionId;

    @ManyToOne
    @JoinColumn(name = "usuarioId", referencedColumnName = "usuarioId")
    private User usuario; // el que hace la valoración

    @ManyToOne
    @JoinColumn(name = "productoId", referencedColumnName = "productoId")
    private Producto producto;

    @Column(nullable = false)
    private int estrellas;

    @Column(nullable = true)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
