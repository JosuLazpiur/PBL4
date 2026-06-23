package edu.mondragon.pbl.gertuko.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import edu.mondragon.pbl.gertuko.model.Estado;
import edu.mondragon.pbl.gertuko.model.Etiqueta;
import edu.mondragon.pbl.gertuko.model.Producto;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.model.Zona;
import edu.mondragon.pbl.gertuko.repository.EtiquetaRepository;
import edu.mondragon.pbl.gertuko.repository.ProductoRepository;
import edu.mondragon.pbl.gertuko.repository.UserRepository;
import edu.mondragon.pbl.gertuko.repository.ValoracionRepository;
import edu.mondragon.pbl.gertuko.repository.ZonaRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @GetMapping("/home")
    public String home(HttpSession session, Principal principal, Model model) {
        if (session.getAttribute("errorEntrada") != null) {
            model.addAttribute("errorEntrada", "Mal");
            session.removeAttribute("errorEntrada");
        }

        if (session.getAttribute("user") == null && principal != null) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username);
            if (user != null) {
                session.setAttribute("user", user);
            }
        }

        Integer etiquetaId = (Integer) session.getAttribute("etiquetaId");
        Integer zonaId = (Integer) session.getAttribute("zonaId");
        List<Producto> productos;
        List<Etiqueta> etiquetas = etiquetaRepository.findAll();
        List<Zona> zonas = zonaRepository.findAll();

        if (etiquetaId != null && zonaId != null) {
            productos = productoRepository.findByEtiqueta_EtiquetaIdAndVendedor_Zona_ZonaIdAndEstadoNotOrderByMediaEstrellasDesc(etiquetaId, zonaId, Estado.AGOTADO);
        } else if (etiquetaId != null) {
            productos = productoRepository.findByEtiqueta_EtiquetaIdAndEstadoNotOrderByMediaEstrellasDesc(etiquetaId, Estado.AGOTADO);
        } else if (zonaId != null) {
            productos = productoRepository.findByVendedor_Zona_ZonaIdAndEstadoNotOrderByMediaEstrellasDesc(zonaId, Estado.AGOTADO);
        } else {
            productos = productoRepository.findByEstadoNotOrderByMediaEstrellasDesc(Estado.AGOTADO);
        }

        Map<Integer, Integer> cantidadValoraciones = new HashMap<>();
        for (Producto producto : productos) {
            int cantidad = valoracionRepository.countByProducto_ProductoId(producto.getProductoId());
            cantidadValoraciones.put(producto.getProductoId(), cantidad);
        }

        model.addAttribute("valoracionesMap", cantidadValoraciones);
        session.setAttribute("etiquetas", etiquetas);
        session.setAttribute("zonas", zonas);
        model.addAttribute("productos", productos);
        return "view/home";
    }

    @PostMapping("/producto/filtrar")
    public ModelAndView filtrarProducto(
                                        @RequestParam("etiqueta") Integer etiquetaId, 
                                        @RequestParam("zona") Integer zonaId,
                                        HttpSession session) {
        if (etiquetaId == 0) {
            if (session.getAttribute("etiquetaId") != null) {
                session.removeAttribute("etiquetaId");
            }
        } else {
            session.setAttribute("etiquetaId", etiquetaId);
        }

        if (zonaId == 0) {
            if (session.getAttribute("zonaId") != null) {
                session.removeAttribute("zonaId");
            }
        } else {
            session.setAttribute("zonaId", zonaId);
        }

        return new ModelAndView("redirect:/home");
    }
}
