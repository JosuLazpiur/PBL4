package edu.mondragon.pbl.gertuko.model;

import java.util.ArrayList;
import java.util.List;

public class CestaWrapper {
    private List<Cesta> cestas = new ArrayList<>();

    public List<Cesta> getCestas() {
        return cestas;
    }

    public void setCestas(List<Cesta> cestas) {
        this.cestas = cestas;
    }
}