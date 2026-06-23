package edu.mondragon.pbl.gertuko.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import edu.mondragon.pbl.gertuko.model.Rol;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.model.Zona;
import edu.mondragon.pbl.gertuko.repository.UserRepository;
import edu.mondragon.pbl.gertuko.repository.ZonaRepository;
import edu.mondragon.pbl.gertuko.service.UserServiceImpl;
import edu.mondragon.pbl.gertuko.service.VerificationService;
import jakarta.servlet.http.HttpSession;

@Controller
public class RegistroController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private ZonaRepository zonaRepository;

    @GetMapping("/registro_user")
    public String mostrarRegistroUser(Model model, HttpSession session) {
        Boolean allowed = (Boolean) session.getAttribute("allowVerifyCode");

        if (allowed != null && allowed) {
            if (session.getAttribute("errorContrasena") != null) {
                model.addAttribute("errorContrasena", session.getAttribute("errorContrasena"));
                session.removeAttribute("errorContrasena");
            }
            if (session.getAttribute("errorEmail") != null) {
                model.addAttribute("errorEmail", session.getAttribute("errorEmail"));
                session.removeAttribute("errorEmail");
            }
            if (session.getAttribute("errorUsuario") != null) {
                model.addAttribute("errorUsuario", session.getAttribute("errorUsuario"));
                session.removeAttribute("errorUsuario");
            }
            session.removeAttribute("allowVerifyCode");

            return "view/login_register/register/registro_user";
        } else {
            session.setAttribute("errorEntrada", "No puedes entrar por ahi.");
            return "redirect:/registro";
        }
    }


    @PostMapping("/registro_user")
    public ModelAndView paso2Registro(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            HttpSession session) {

        if (!contraseñaSegura(password)) {
            session.setAttribute("username", username);
            session.setAttribute("email", email);
            session.removeAttribute("password");
            session.setAttribute("descripcion", descripcion);
            session.setAttribute("allowVerifyCode", true);
            session.setAttribute("errorContrasena", "registro.errorContrasena");
            return new ModelAndView("redirect:/registro_user");
        }

        if (userRepository.findByEmail(email) != null) {
            session.setAttribute("username", username);
            session.removeAttribute("email");
            session.setAttribute("password", password);
            session.setAttribute("descripcion", descripcion);
            session.setAttribute("allowVerifyCode", true);
            session.setAttribute("errorEmail", "registro.errorEmail");
            return new ModelAndView("redirect:/registro_user");
        }

        if (userRepository.findByUsername(username) != null) {
            session.setAttribute("password", password);
            session.setAttribute("email", email);
            session.removeAttribute("username");
            session.setAttribute("descripcion", descripcion);
            session.setAttribute("allowVerifyCode", true);
            session.setAttribute("errorUsuario", "registro.errorUsuario");
            return new ModelAndView("redirect:/registro_user");
        }

        String nombre = (String) session.getAttribute("nombre");
        String apellido = (String) session.getAttribute("apellido");
        String telefono = (String) session.getAttribute("telefono");
        String rolStr = (String) session.getAttribute("rol");
        Integer zonaId = (Integer) session.getAttribute("zonaId");
        String imagenNombreTemporal = (String) session.getAttribute("imagenNombreTemporal");

        if (nombre == null || apellido == null || telefono == null || rolStr == null || zonaId == null) {
            session.setAttribute("errorDatosSesion", "registro.errorDatosSesion");
            return new ModelAndView("redirect:/registro");
        }

        Rol rol = Rol.valueOf(rolStr);
        Zona zona = zonaRepository.findById(zonaId).orElse(null);
        if (zona == null) {
            session.setAttribute("errorZona", "registro.errorZona");
            return new ModelAndView("redirect:/registro");
        }

        User user = User.builder()
                .nombre(nombre)
                .apellido(apellido)
                .telefono(telefono)
                .username(username)
                .email(email)
                .password(password)
                .rol(rol)
                .zona(zona)
                .descripcion(rol == Rol.VENDEDOR ? descripcion : null)
                .imagen(imagenNombreTemporal) // puede ser null
                .build();

        session.setAttribute("user", user);
        session.setAttribute("email", email);
        session.removeAttribute("nombre");
        session.removeAttribute("apellido");
        session.removeAttribute("telefono");
        session.removeAttribute("rol");
        session.removeAttribute("zonaId");

        verificationService.sendVerificationCode(email);
        session.setAttribute("allowVerifyCode", true);

        return new ModelAndView("redirect:/verificar");
    }

    @GetMapping("/verificar")
    public String mostrarVerificar(Model model, HttpSession session) {
        Boolean allowed = (Boolean) session.getAttribute("allowVerifyCode");
        if (allowed != null && allowed) {
            if (session.getAttribute("errorVerificar") != null) {
                model.addAttribute("errorVerificar", session.getAttribute("errorVerificar"));
                session.removeAttribute("errorVerificar");
            }

            return "view/login_register/verificar/verificar";
        } else {
            session.setAttribute("errorEntrada", "No puedes entrar por ahi.");
            return "redirect:/registro";
        }
    }

    @PostMapping("/verificar")
    public ModelAndView verificarCodigo(
            @RequestParam("char1") String char1,
            @RequestParam("char2") String char2,
            @RequestParam("char3") String char3,
            @RequestParam("char4") String char4,
            @RequestParam("char5") String char5,
            @RequestParam("char6") String char6,
            HttpSession session) {

        String code = char1 + char2 + char3 + char4 + char5 + char6;
        String email = (String) session.getAttribute("email");
        User user = (User) session.getAttribute("user");
        String imagenNombreTemporal = (String) session.getAttribute("imagenNombreTemporal");

        if (user == null || email == null) {
            return new ModelAndView("redirect:/registro");
        }

        if (verificationService.verifyCode(email, code)) {
            if (imagenNombreTemporal != null) {
                try {
                    Path origen = Paths.get(System.getProperty("user.dir"), "uploads", "tmp", "usuarios", imagenNombreTemporal);
                    Path destino = Paths.get(System.getProperty("user.dir"), "uploads", "usuarios", imagenNombreTemporal);

                    if (!Files.exists(destino.getParent())) {
                        Files.createDirectories(destino.getParent());
                    }

                    Files.move(origen, destino);
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errorImagen", "Error al mover la imagen.");
                    return new ModelAndView("redirect:/verificar");
                }
            }

            userServiceImpl.saveUser(user);
            session.removeAttribute("user");
            session.removeAttribute("imagenNombreTemporal");
            session.removeAttribute("imagenNombreTemporal");
            session.setAttribute("info", "login.creado");


            
            return new ModelAndView("redirect:/login");
        } else {
            session.setAttribute("allowVerifyCode", true);
            session.setAttribute("errorVerificar", "registro.errorVerificar");
            return new ModelAndView("redirect:/verificar");
        }
    }

    private boolean contraseñaSegura(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        return password.matches(regex);
    }
}
