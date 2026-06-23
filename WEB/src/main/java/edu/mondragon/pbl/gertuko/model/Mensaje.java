package edu.mondragon.pbl.gertuko.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "mensajes")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mensajeId;

    @ManyToOne
    @JoinColumn(name = "chatId", referencedColumnName = "chatId")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "remitenteId", referencedColumnName = "usuarioId")
    private User remitente;

    @Column(nullable = false)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(nullable = false)
    private boolean leido;
}
