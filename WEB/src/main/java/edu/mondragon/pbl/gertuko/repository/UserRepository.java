package edu.mondragon.pbl.gertuko.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.mondragon.pbl.gertuko.model.Rol;
import edu.mondragon.pbl.gertuko.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByUsuarioId(int usuarioId);
    User findByEmail(String email);
    User findByTelefono(String telefono);
    List<User> findByRol(Rol rol);
}