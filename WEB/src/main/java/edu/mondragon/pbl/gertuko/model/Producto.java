package edu.mondragon.pbl.gertuko.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productoId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "vendedorId", referencedColumnName = "usuarioId")
    private User vendedor;
    
    @Column(nullable = true)
    private String imagen;

    @Column(nullable = true)
    private Double mediaEstrellas;

    @ManyToOne
    @JoinColumn(name = "etiquetaId", referencedColumnName = "etiquetaId") 
    private Etiqueta etiqueta;
    
    @Column(nullable = false)
    private Double precio;
}
