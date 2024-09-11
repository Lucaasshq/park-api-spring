package com.LucasH.park_api.repository;

import com.LucasH.park_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
   Optional<Usuario> findByUsername(String username);

   @Query("select Usuario.role from Usuario u where u.username like :username")
   Usuario.Role findRoleByUsername(String username);
}
