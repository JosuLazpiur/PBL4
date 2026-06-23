package edu.mondragon.pbl.gertuko.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import edu.mondragon.pbl.gertuko.config.SecurityConfig;
import edu.mondragon.pbl.gertuko.model.User;
import edu.mondragon.pbl.gertuko.repository.UserRepository;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) {
        BCryptPasswordEncoder passwordEncoder = SecurityConfig.passwordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("Usuario o contraseña invalidos");
        }

        return new UserDetailsImpl(user);
    }
    
}
