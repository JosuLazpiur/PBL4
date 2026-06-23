package edu.mondragon.pbl.gertuko.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.repository.UserRepository;
import edu.mondragon.pbl.gertuko.service.VerificationService;

@Controller
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/forgot_password")
    public String forgotPassword(HttpSession session, Model model) {
        if (session.getAttribute("errorEmail") != null) {
            model.addAttribute("errorEmail", session.getAttribute("errorEmail"));
            session.removeAttribute("errorEmail");
        }
        if (session.getAttribute("errorEntrada") != null) {
            model.addAttribute("errorEntrada", session.getAttribute("errorEntrada"));
            session.removeAttribute("errorEntrada");
        }

        return "view/login_register/login/forgot_password";
    }

    @GetMapping("/verify_reset_code")
    public String showVerifyCodeForm(@RequestParam(required = false) String email,
            HttpSession session, Model model) {
        Boolean allowed = (Boolean) session.getAttribute("allowVerifyCode");
        if (allowed != null && allowed) {
            if (email != null) {
                model.addAttribute("email", email);
            }
            if (session.getAttribute("errorVerificar") != null) {
                model.addAttribute("errorVerificar", session.getAttribute("errorVerificar"));
                session.removeAttribute("errorVerificar");
            }
            session.removeAttribute("allowVerifyCode");

            return "view/login_register/verificar/verify_reset_code";
        } else {
            session.setAttribute("errorEntrada", "No puedes entrar por ahi.");
            return "redirect:/forgot_password";
        }
    }

    @GetMapping("/set_new_password")
    public String showSetNewPasswordForm(@RequestParam(required = false) String email,
            HttpSession session, Model model) {
        Boolean allowed = (Boolean) session.getAttribute("allowVerifyCode");
        if (allowed != null && allowed) {
            if (email != null) {
                model.addAttribute("email", email);
            }
            if (session.getAttribute("errorUsuarioNo") != null) {
                model.addAttribute("errorUsuarioNo", session.getAttribute("errorUsuarioNo"));
                session.removeAttribute("errorUsuarioNo");
            }
            if (session.getAttribute("errorContrasena") != null) {
                model.addAttribute("errorContrasena", session.getAttribute("errorContrasena"));
                session.removeAttribute("errorContrasena");
            }
            session.removeAttribute("allowVerifyCode");

            return "view/login_register/register/set_new_password";
        } else {
            session.setAttribute("errorEntrada", "No puedes entrar por ahi.");
            return "redirect:/forgot_password";
        }
    }

    @PostMapping("/forgot_password")
    public ModelAndView processForgotPassword(@RequestParam String email, HttpSession session) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            session.setAttribute("errorEmail", "El correo no está registrado.");
            return new ModelAndView("redirect:/forgot_password");
        }

        verificationService.sendVerificationCode(email);
        session.setAttribute("resetEmail", email);
        session.setAttribute("allowVerifyCode", true);

        return new ModelAndView("redirect:/verify_reset_code");
    }

    @PostMapping("/verify_reset_code")
    public ModelAndView verifyResetCode(@RequestParam("char1") String char1,
            @RequestParam("char2") String char2, @RequestParam("char3") String char3,
            @RequestParam("char4") String char4, @RequestParam("char5") String char5,
            @RequestParam("char6") String char6, HttpSession session) {
        String code = char1 + char2 + char3 + char4 + char5 + char6;
        String email = (String) session.getAttribute("resetEmail");

        User user = userRepository.findByEmail(email);

        if (user != null && verificationService.verifyCode(email, code)) {
            verificationService.removeVerificationCode(email);
            session.setAttribute("allowVerifyCode", true);
            return new ModelAndView("redirect:/set_new_password");
        }

        session.setAttribute("errorVerificar", "El código es incorrecto.");
        return new ModelAndView("redirect:/verify_reset_code");
    }

    @PostMapping("/set_new_password")
    public ModelAndView setNewPassword(@RequestParam String newPassword, HttpSession session) {
        String email = (String) session.getAttribute("resetEmail");

        User user = userRepository.findByEmail(email);
        if (user == null) {
            session.setAttribute("errorUsuarioNo", "Usuario no encontrado.");
            return new ModelAndView("redirect:/set_new_password");
        }

        if (!contraseñaSegura(newPassword)) {
            session.setAttribute("errorContrasena", "registro.errorContrasena");
            return new ModelAndView("redirect:/set_new_password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        session.removeAttribute("resetEmail");
        session.setAttribute("success", "Tu contraseña se ha cambiado correctamente.");
        return new ModelAndView("redirect:/login");
    }

    private boolean contraseñaSegura(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        return password.matches(regex);
    }
}
