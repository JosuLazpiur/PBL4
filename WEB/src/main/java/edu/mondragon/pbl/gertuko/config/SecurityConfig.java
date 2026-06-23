package edu.mondragon.pbl.gertuko.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import edu.mondragon.pbl.gertuko.service.UserServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] ENDPOINTS_WHITELIST = { "/", "/registro", "/registro_user", "/forgot_password", "/verify_reset_code", "/set_new_password", 
    "/verificar", "/login", "/logout", "/css/*", "/js/*", "/images/*", "/locale/**","/estadistica", "/estadistica/calcular", "/sounds"} ;
    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_FAIL_URL = "/login";
    private static final String DEFAULT_SUCCESS_URL = "/home";

    @Autowired
    private UserServiceImpl userDetailsService;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build();
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
				.loginPage(LOGIN_URL)
				.permitAll()
                .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                .failureUrl(LOGIN_FAIL_URL)      
                .failureHandler((request, response, exception) -> {
                    request.getSession().setAttribute("loginError", "error_login");
                    response.sendRedirect(LOGIN_FAIL_URL);
                })
            )
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")  
                .permitAll()
            );
        return http.build();
    }
}
