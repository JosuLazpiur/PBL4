package edu.mondragon.pbl.gertuko.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class IndexController {
    


    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        session.removeAttribute("nombre");
        session.removeAttribute("apellido");
        session.removeAttribute("telefono");
        session.removeAttribute("rol");
        session.removeAttribute("zonaId");

        return "view/index";
    }

    @GetMapping("/login")   
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("loginError") != null) {
            model.addAttribute("loginError", session.getAttribute("loginError"));
            session.removeAttribute("loginError");
        }
        if (session.getAttribute("success") != null) {
            model.addAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("info") != null) {
            model.addAttribute("info", session.getAttribute("info"));
            session.removeAttribute("info");
        }
        return "view/login_register/login/login";
    }
}
