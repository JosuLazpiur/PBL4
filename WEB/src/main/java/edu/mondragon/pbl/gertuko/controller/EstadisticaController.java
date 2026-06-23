package edu.mondragon.pbl.gertuko.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EstadisticaController {

    @GetMapping("/estadistica")
    public String mostrarFormulario() {
        return "view/estadistica";
    }

    @PostMapping("/estadistica/calcular")
    public String calcularVentas(@RequestParam double ventas, @RequestParam double puestoActual,
            @RequestParam double puestoNuevo, Model model) {
        double fActual = 0.43 * Math.pow(puestoActual, -1.46);
        double fNuevo = 0.43 * Math.pow(puestoNuevo, -1.46);
        double resultado = (ventas / fActual) * fNuevo;

        model.addAttribute("resultado", Math.round(resultado * 100.0) / 100.0);

        // Datos para el gráfico
        List<Integer> puestos = new ArrayList<>();
        List<Double> estimaciones = new ArrayList<>();
        for (int p = 1; p <= 20; p++) {
            double f = 0.43 * Math.pow(p, -1.46);
            double v = (ventas / fActual) * f;
            puestos.add(p);
            estimaciones.add(Math.round(v * 100.0) / 100.0);
        }

        model.addAttribute("puestos", puestos);
        model.addAttribute("ventasEstimadas", estimaciones);

        return "view/estadistica";
    }
}