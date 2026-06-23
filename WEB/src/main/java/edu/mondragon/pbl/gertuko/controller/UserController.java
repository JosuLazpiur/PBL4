package edu.mondragon.pbl.gertuko.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.model.Zona;
import edu.mondragon.pbl.gertuko.repository.UserRepository;
import edu.mondragon.pbl.gertuko.repository.ZonaRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model, HttpSession session) {
        List<Zona> zonas = zonaRepository.findAll();
        model.addAttribute("zonas", zonas);

        if (session.getAttribute("errorTelefono") != null) {
            model.addAttribute("errorTelefono", session.getAttribute("errorTelefono"));
            session.removeAttribute("errorTelefono");
        }
        if (session.getAttribute("errorDatosSesion") != null) {
            model.addAttribute("errorDatosSesion", session.getAttribute("errorDatosSesion"));
            session.removeAttribute("errorDatosSesion");
        }
        if (session.getAttribute("errorZona") != null) {
            model.addAttribute("errorZona", session.getAttribute("errorZona"));
            session.removeAttribute("errorZona");
        }
        if (session.getAttribute("imagenFileName") != null) {
            model.addAttribute("imagenFileName", session.getAttribute("imagenFileName"));
            session.removeAttribute("imagenFileName");
        }
        if (session.getAttribute("errorEntrada") != null) {
            model.addAttribute("errorEntrada", session.getAttribute("errorEntrada"));
            session.removeAttribute("errorEntrada");
        }

        return "view/login_register/register/registro";
    }

    @PostMapping("/registro")
    public ModelAndView registro(
        @RequestParam("nombre") String nombre,
        @RequestParam("apellido") String apellido,
        @RequestParam("telefono") String telefono,
        @RequestParam("rol") String rol,
        @RequestParam("zona") int zonaId,
        @RequestParam(value = "imagen", required = false) MultipartFile imagenFile,
        HttpSession session) {

        // Guardar datos en la sesión SIEMPRE, aunque haya error
        session.setAttribute("nombre", nombre);
        session.setAttribute("apellido", apellido);
        session.setAttribute("telefono", telefono);
        session.setAttribute("rol", rol);
        session.setAttribute("zonaId", zonaId);

        // Comprobación de teléfono existente
        if (userRepository.findByTelefono(telefono) != null) {
            session.setAttribute("errorTelefono", "El teléfono ya está registrado.");
            session.removeAttribute("telefono");
            return new ModelAndView("redirect:/registro");
        }

        // Procesar la imagen (si existe)
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String originalFilename = imagenFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String nuevoNombre = UUID.randomUUID().toString() + extension;

                String realPath = System.getProperty("user.dir") + "/uploads/tmp/usuarios";
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
                return new ModelAndView("redirect:/registro");
            }
        } else {
            session.setAttribute("imagenNombreTemporal", null);
        }

        session.setAttribute("allowVerifyCode", true);

        session.removeAttribute("errorTelefono");
        session.removeAttribute("errorDatosSesion");
        session.removeAttribute("errorZona");

        return new ModelAndView("redirect:/registro_user");
    }

    @GetMapping("/user/{id}/edit")
    public String perfil(@PathVariable("id") Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (session.getAttribute("errorTelefono") != null) {
            model.addAttribute("errorTelefono", session.getAttribute("errorTelefono"));
            session.removeAttribute("errorTelefono");
        }
        if (id != user.getUsuarioId()) {
            session.setAttribute("errorEntrada", "Mal");
            return "redirect:/home";
        }

        return "view/perfil";
    }

    @PostMapping("/user/{id}/edit/imagen")
    public ModelAndView editarImagenUsuario(
        @PathVariable("id") Integer id,
        @RequestParam(value = "imagen", required = false) MultipartFile imagenFile,
        HttpSession session) {
        
        User user = userRepository.findByUsuarioId(id);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String realPath = System.getProperty("user.dir") + "/uploads/usuarios";
                Path uploadPath = Paths.get(realPath);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Primero borra la imagen vieja si existe
                if (user.getImagen() != null) {
                    Path oldImagePath = uploadPath.resolve(user.getImagen());
                    Files.deleteIfExists(oldImagePath);
                }

                // Después guarda la nueva imagen
                String originalFilename = imagenFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String nuevoNombre = UUID.randomUUID().toString() + extension;

                Path filePath = uploadPath.resolve(nuevoNombre);
                imagenFile.transferTo(filePath.toFile());

                user.setImagen(nuevoNombre);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        userRepository.save(user);
        session.removeAttribute("user");
        session.setAttribute("user", user);
        
        return new ModelAndView("redirect:/user/"+ id +"/edit");
    }

    @PostMapping("/user/{id}/edit")
    public ModelAndView editarUsuario(
        @PathVariable("id") Integer id,
        @RequestParam("nombre") String nombre,
        @RequestParam("apellido") String apellido,
        @RequestParam("telefono") String telefono,
        @RequestParam(value = "descripcion", required = false) String descripcion,
        HttpSession session) {
        
        User user = userRepository.findByUsuarioId(id);
        user.setNombre(nombre);
        user.setApellido(apellido);
        if (user.getRol().name().equals("VENDEDOR")) {
            user.setDescripcion(descripcion);
        } else {
            user.setDescripcion(null);
        }

        if (userRepository.findByTelefono(telefono) != null && !user.getTelefono().equals(telefono)) {
            session.setAttribute("errorTelefono", telefono);
            userRepository.save(user);
            return new ModelAndView("redirect:/user/"+ id +"/edit");
        }

        userRepository.save(user);
        session.removeAttribute("user");
        session.setAttribute("user", user);
        
        return new ModelAndView("redirect:/user/"+ id +"/edit");
    }
}
