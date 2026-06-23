package edu.mondragon.pbl.gertuko.controller;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.LocaleResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller 
public class LocaleController {

    @Autowired
    private LocaleResolver localeResolver;

    @GetMapping("/locale/{lang}")
    public String changeLanguage(
        @PathVariable("lang")
        String language,
        HttpServletRequest request,
        HttpServletResponse response){
        
        Locale locale = Locale.forLanguageTag(language);
        localeResolver.setLocale(request, response, locale);

        String referer = request.getHeader("Referer");
        if (referer == null) return "redirect:/";
        return "redirect:" + referer;
    }

}
