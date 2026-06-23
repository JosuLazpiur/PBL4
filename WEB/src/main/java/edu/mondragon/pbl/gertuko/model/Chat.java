package edu.mondragon.pbl.gertuko.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chatId;

    @ManyToOne
    @JoinColumn(name = "user1Id", referencedColumnName = "usuarioId")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2Id", referencedColumnName = "usuarioId")
    private User user2;

    @Column(nullable = false)
    private LocalDateTime ultimaActualizacion;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Mensaje> mensajes;

    @JsonIgnore
    public String getUltimoMensaje() {
        if (mensajes == null || mensajes.isEmpty()) {
            return "";
        }
        return mensajes.get(mensajes.size() - 1).getTexto(); // Asumiendo que Mensaje tiene getTexto()
    }

}
