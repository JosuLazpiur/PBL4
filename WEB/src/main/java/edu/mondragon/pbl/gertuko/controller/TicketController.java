package edu.mondragon.pbl.gertuko.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import edu.mondragon.pbl.gertuko.model.Cesta;
import edu.mondragon.pbl.gertuko.model.CestaWrapper;
import edu.mondragon.pbl.gertuko.model.Estado;
import edu.mondragon.pbl.gertuko.model.Rol;
import edu.mondragon.pbl.gertuko.model.Ticket;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.repository.CestaRepository;
import edu.mondragon.pbl.gertuko.repository.ProductoRepository;
import edu.mondragon.pbl.gertuko.repository.TicketRepository;
import edu.mondragon.pbl.gertuko.repository.UserRepository;
import edu.mondragon.pbl.gertuko.service.TicketService;
import jakarta.servlet.http.HttpSession;

@Controller
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CestaRepository cestaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TicketService ticketService;

    // --------------------
    // LISTAR
    // --------------------
    @GetMapping("/ticket/list")
    public String listarTicketsDelUsuario(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Ticket> tickets;

        if (user.getRol().name().equals("VENDEDOR")) {
            tickets = ticketRepository.findByVendedor_UsuarioId(user.getUsuarioId());
        } else {
            tickets = ticketRepository.findByCliente_UsuarioId(user.getUsuarioId());
        }

        Map<Ticket, List<Cesta>> ticketConCestas = new LinkedHashMap<>();
        Map<Ticket, Double> preciosTotales = new LinkedHashMap<>();
        for (Ticket ticket : tickets) {
            List<Cesta> cestas = cestaRepository.findByTicket_TicketId(ticket.getTicketId());
            ticketConCestas.put(ticket, cestas);

            double total = 0.0;
            for (Cesta cesta : cestas) {
                try {
                    double precio = cesta.getProducto().getPrecio();
                    total += cesta.getCantidad() * precio;
                } catch (NumberFormatException e) {
                    total += 0; // o loguea el error si quieres
                }
            }
            preciosTotales.put(ticket, total);
        }

        model.addAttribute("ticketConCestas", ticketConCestas);
        model.addAttribute("preciosTotales", preciosTotales);

        return "view/tickets/tickets_list.html";
    }

    // --------------------
    // CREAR TICKET
    // --------------------
    @GetMapping("/ticket/create")
    public String mostrarFormularioCrear(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (!user.getRol().name().equals("VENDEDOR")) {
            model.addAttribute("errorEntrada", "errorEntrada");
            return "redirect:/home";
        }

        List<User> clientes = userRepository.findByRol(Rol.CLIENTE); // Solo los de rol CLIENTE

        model.addAttribute("clientes", clientes);
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("productos", productoRepository
                .findByVendedor_UsuarioIdAndEstadoNot(user.getUsuarioId(), Estado.AGOTADO));
        model.addAttribute("cestas", new ArrayList<>());

        return "view/tickets/tickets_form.html";
    }

    @PostMapping("/ticket/create")
    public ModelAndView crearTicket(
            @RequestParam("fechaEntregaEsperada") String fechaEntregaEsperada,
            @RequestParam("descripcionTicket") String descripcionTicket,
            @RequestParam("clienteUsername") String clienteUsername,
            @ModelAttribute("cestaWrapper") CestaWrapper cestaWrapper,
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        List<Cesta> cestas = cestaWrapper.getCestas();

        User cliente = userRepository.findByUsername(clienteUsername);
        if (cliente == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return new ModelAndView("view/tickets/tickets_form");
        }

        LocalDateTime fechaEntregaEsperadaDateTime = LocalDateTime.parse(fechaEntregaEsperada);

        // Crear y guardar ticket
        Ticket ticket = Ticket.builder().FechaEntregaEsperada(fechaEntregaEsperadaDateTime).descripcionTicket(descripcionTicket)
                .cliente(cliente).vendedor(user).estado(false).build();

        Ticket savedTicket = ticketRepository.save(ticket);

        // Asociar cestas y guardar
        for (Cesta cesta : cestas) {
            if (cesta.getProducto() != null && cesta.getCantidad() == null) {
                cesta.setCantidad(1);
            }
            cesta.setTicket(savedTicket);
            cestaRepository.save(cesta);
        }

        // Verificar con función SQL si hay más de 5 productos
        if (ticketService.tieneMasDe5Productos(savedTicket.getTicketId())) {
            // Rollback manual: eliminar cestas y ticket
            List<Cesta> savedCestas =
                    cestaRepository.findByTicket_TicketId(savedTicket.getTicketId());
            cestaRepository.deleteAll(savedCestas);
            ticketRepository.delete(savedTicket);

            model.addAttribute("errorCesta", "No se pueden añadir más de 5 productos distintos.");
            return new ModelAndView("redirect:/ticket/list");
        }

        return new ModelAndView("redirect:/ticket/list");
    }

    // --------------------
    // EDITAR TICKET
    // --------------------
    @GetMapping("/ticket/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model,
            HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (!user.getRol().name().equals("VENDEDOR")) {
            model.addAttribute("errorEntrada", "errorEntrada");
            return "redirect:/home";
        }

        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (optionalTicket.isEmpty()) {
            model.addAttribute("error", "Ticket no encontrado");
            return "redirect:/ticket/list";
        }

        Ticket ticket = optionalTicket.get();
        List<Cesta> cestas = cestaRepository.findByTicket_TicketId(ticket.getTicketId());
        List<User> clientes = userRepository.findByRol(Rol.CLIENTE); // Solo los de rol CLIENTE

        model.addAttribute("clientes", clientes);
        model.addAttribute("ticket", ticket);
        model.addAttribute("cestas", cestas);
        model.addAttribute("productos", productoRepository
                .findByVendedor_UsuarioIdAndEstadoNot(user.getUsuarioId(), Estado.AGOTADO));

        return "view/tickets/tickets_form";
    }

    @PostMapping("/ticket/{id}/edit")
    public ModelAndView editTicket(@PathVariable("id") Integer id,
            @RequestParam("fechaEntregaEsperada") String fechaEntrega,
            @RequestParam("clienteUsername") String clienteUsername,
            @ModelAttribute("cestaWrapper") CestaWrapper cestaWrapper, Model model,
            HttpSession session) {

        List<Cesta> cestas = cestaWrapper.getCestas();

        if (cestas.size() > 5) {
            model.addAttribute("errorCesta", "No se pueden añadir más de 5 productos distintos.");
            return new ModelAndView("redirect:/ticket/list");
        }

        // Buscar ticket existente
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            model.addAttribute("error", "Ticket no encontrado.");
            return new ModelAndView("view/tickets/tickets_form");
        }

        // Buscar cliente
        User cliente = userRepository.findByUsername(clienteUsername);
        if (cliente == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return new ModelAndView("view/tickets/tickets_form");
        }

        // Actualizar campos del ticket sin perder el vendedor
        ticket.setFechaEntregaEsperada(LocalDateTime.parse(fechaEntrega));
        ticket.setCliente(cliente);

        ticketRepository.save(ticket);

        // Opcional: eliminar cestas viejas para evitar duplicados o inconsistencias
        List<Cesta> cestasViejas = cestaRepository.findByTicket_TicketId(ticket.getTicketId());
        cestaRepository.deleteAll(cestasViejas);

        // Guardar nuevas cestas asociadas al ticket
        for (Cesta cesta : cestas) {
            cesta.setTicket(ticket);
            cestaRepository.save(cesta);
        }

        return new ModelAndView("redirect:/ticket/list");
    }

    // --------------------
    // ENTREGAR TICKET
    // --------------------
    @PostMapping("/ticket/{id}/entregar")
    public ModelAndView entregarTicket(@PathVariable("id") Integer id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setEstado(true);
            ticketRepository.save(ticket);
        }
        return new ModelAndView("redirect:/ticket/list");
    }

    // --------------------
    // BORRAR TICKET
    // --------------------
    @PostMapping("/ticket/{id}/delete")
    public String borrarTicket(@PathVariable("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (!user.getRol().name().equals("VENDEDOR")) {
            session.setAttribute("errorEntrada", "No eres vendedor");
            return "redirect:/home";
        }

        Ticket ticket = ticketRepository.findByTicketId(id);

        if (ticket != null && ticket.getVendedor().getUsuarioId().equals(user.getUsuarioId())) {
            // Primero eliminar las cestas asociadas
            List<Cesta> cestas = cestaRepository.findByTicket_TicketId(ticket.getTicketId());
            cestaRepository.deleteAll(cestas);

            // Luego eliminar el ticket
            ticketRepository.delete(ticket);
        }

        return "redirect:/ticket/list";
    }
}
