package com.example.EngLang.Repository;

import com.example.EngLang.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGmail(String gmail); // Gmail'e göre kullanıcı bulma
    boolean existsByGmail(String gmail); // Gmail'in veritabanında olup olmadığını kontrol etme
}