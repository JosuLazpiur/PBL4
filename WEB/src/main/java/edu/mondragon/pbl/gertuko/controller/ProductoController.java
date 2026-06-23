package edu.mondragon.pbl.gertuko.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import edu.mondragon.pbl.gertuko.model.Estado;
import edu.mondragon.pbl.gertuko.model.Etiqueta;
import edu.mondragon.pbl.gertuko.model.Producto;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.model.Valoracion;
import edu.mondragon.pbl.gertuko.repository.EtiquetaRepository;
import edu.mondragon.pbl.gertuko.repository.ProductoRepository;
import edu.mondragon.pbl.gertuko.repository.ValoracionRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductoController {

    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    // ================================ CREAR ================================ 
    @GetMapping("/producto/create")
    public String mostrarFormularioCrear(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (!user.getRol().name().equals("VENDEDOR")) {
            session.setAttribute("errorEntrada", "No eres vendedor");
            return "redirect:/home";
        }

        model.addAttribute("producto", new Producto());
        model.addAttribute("etiquetas", etiquetaRepository.findAll());
        return "view/producto/producto_form";
    }

    @PostMapping({"/producto/create"})
    public ModelAndView createProduct(
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("estado") String estadoStr,
        @RequestParam(value = "imagen", required = false) MultipartFile imagenFile,
        @RequestParam("etiqueta") int etiquetaId,
        @RequestParam("precio") String precioStr,
        HttpSession session) {
        

        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId).orElse(null);
        Estado estado = Estado.valueOf(estadoStr);
        User user = (User) session.getAttribute("user");

        Double precio = Double.parseDouble(precioStr.replace(",", "."));

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String originalFilename = imagenFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String nuevoNombre = UUID.randomUUID().toString() + extension;

                Producto producto = Producto.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .estado(estado)
                    .imagen(nuevoNombre)
                    .etiqueta(etiqueta)
                    .vendedor(user)
                    .mediaEstrellas(null)
                    .precio(precio)
                    .build();

                productoRepository.save(producto);

                String realPath = System.getProperty("user.dir") + "/uploads/productos";
                Path uploadPath = Paths.get(realPath);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(nuevoNombre);
                imagenFile.transferTo(filePath.toFile());

                session.setAttribute("imagenNombreTemporal", nuevoNombre);
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("imagenFileName", "Error al guardar imagen temporal.");
                return new ModelAndView("redirect:/create_producto");
            }
        } else {
            session.setAttribute("imagenNombreTemporal", null);
        }

        return new ModelAndView("redirect:/producto/list");
    }

    // ================================ EDITAR ================================ 
    @GetMapping("/producto/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (!user.getRol().name().equals("VENDEDOR")) {
            session.setAttribute("errorEntrada", "No eres vendedor");
            return "redirect:/home";
        }

        Producto producto = productoRepository.findByProductoId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("etiquetas", etiquetaRepository.findAll());

        return "view/producto/producto_form";
    }

    @PostMapping("/producto/{id}/edit")
    public ModelAndView editarProducto(
        @PathVariable("id") Integer idProducto,
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("estado") String estadoStr,
        @RequestParam(value = "imagen", required = false) MultipartFile imagenFile,
        @RequestParam("etiqueta") int etiquetaId,
        @RequestParam("precio") String precioStr,
        HttpSession session) {

        Producto producto = productoRepository.findByProductoId(idProducto);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setEstado(Estado.valueOf(estadoStr));
        producto.setEtiqueta(etiquetaRepository.findById(etiquetaId).orElse(null));
        Double precio = Double.parseDouble(precioStr.replace(",", "."));
        producto.setPrecio(precio);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String realPath = System.getProperty("user.dir") + "/uploads/productos";
                Path uploadPath = Paths.get(realPath);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Primero borra la imagen vieja si existe
                if (producto.getImagen() != null) {
                    Path oldImagePath = uploadPath.resolve(producto.getImagen());
                    Files.deleteIfExists(oldImagePath);
                }

                // Después guarda la nueva imagen
                String originalFilename = imagenFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String nuevoNombre = UUID.randomUUID().toString() + extension;

                Path filePath = uploadPath.resolve(nuevoNombre);
                imagenFile.transferTo(filePath.toFile());

                producto.setImagen(nuevoNombre);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        productoRepository.save(producto);
        return new ModelAndView("redirect:/producto/list");
    }


    // ================================ BORRAR ================================
    @GetMapping("producto/{id}/delete")
    public String borrarProducto(@PathVariable("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (!user.getRol().name().equals("VENDEDOR")) {
            session.setAttribute("errorEntrada", "No eres vendedor");
            return "redirect:/home";
        }

        Producto producto = productoRepository.findByProductoId(id);

        if (producto != null && producto.getVendedor().getUsuarioId().equals(user.getUsuarioId())) {
            productoRepository.delete(producto);
        }

        return "redirect:/producto/list";
    }


    // ================================ LISTA ================================
    @GetMapping("/producto/list")
    public String listarProductosDelUsuario(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (!user.getRol().name().equals("VENDEDOR")) {
            session.setAttribute("errorEntrada", "No eres vendedor");
            return "redirect:/home";
        }

        List<Producto> productos = productoRepository.findByVendedor_UsuarioId(user.getUsuarioId());
        model.addAttribute("misProductos", productos);
        
        return "view/producto/producto_list";
    } 

    // ================================ VISTA ================================
    @GetMapping("/producto/{id}/view")
    public String vistaDelProducto(@PathVariable("id") Integer id, HttpSession session, Model model) {
        Producto producto = productoRepository.findByProductoId(id); // Debes tener un método en tu servicio

        if (producto == null) {
            return "redirect:/home"; // O una página de error
        }

        // Obtener valoraciones del producto
        List<Valoracion> valoraciones = valoracionRepository.findByProducto_ProductoId(id); // Si tienes valoraciones

        // Añadir al modelo
        model.addAttribute("producto", producto);
        model.addAttribute("valoraciones", valoraciones);

        return "view/producto/producto_view";
    } 

    @PostMapping("/producto/{id}/valorar")
    public ModelAndView valorarProducto(
        @PathVariable("id") Integer id, 
        @RequestParam(value = "estrellas", required = false) Integer estrellas,
        @RequestParam(value = "comentario", required = false) String comentario,
        HttpSession session) {

        User user = (User) session.getAttribute("user");
        Producto producto = productoRepository.findByProductoId(id);
        
        if (producto.getVendedor().getUsuarioId() == user.getUsuarioId()) {
            session.setAttribute("errorEntrada", "Mal");
            return new ModelAndView("redirect:/home");
        }

        if (estrellas == null) {
            estrellas = 0;
        }

        Valoracion valoracion = Valoracion.builder()
                            .usuario(user)
                            .producto(producto)
                            .estrellas(estrellas)
                            .comentario(comentario)
                            .fecha(ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDateTime())
                            .build();

        valoracionRepository.save(valoracion);

        return new ModelAndView("redirect:/producto/"+ id + "/view");
    } 
}
