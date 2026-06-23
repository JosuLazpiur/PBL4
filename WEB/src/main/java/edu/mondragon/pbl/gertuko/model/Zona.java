package edu.mondragon.pbl.gertuko.model;

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
@Table(name = "zonas")
public class Zona {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int zonaId;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "provinciaId", referencedColumnName = "provinciaId")
    private Provincia provincia;
}
