package com.example.EngLang.Controller;

import com.example.EngLang.Entity.User;
import com.example.EngLang.Exception.UserValidationException;
import com.example.EngLang.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth") // Temel yol
@CrossOrigin(origins = "http://localhost:5173") // Frontend'inizin çalıştığı adres
public class AuthController {

    private final AuthService authService;

    // Yeni kullanıcı kaydı
    // POST: /auth/register
    // Body: { "username": "testuser", "password": "password123", "gmail": "test@example.com" }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            authService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Kullanıcı başarıyla kaydedildi.");
        } catch (UserValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Kayıt sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    // Kullanıcı girişi
    // POST: /auth/login
    // Body: { "gmail": "test@example.com", "password": "password123" }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        try {
            User user = authService.loginUser(loginRequest.getGmail(), loginRequest.getPassword());
            // Giriş başarılı olursa, kullanıcı objesini (ID dahil) frontend'e döndür.
            // Gerçek uygulamada güvenlik için hassas bilgileri (şifre gibi) göndermemeye dikkat edin.
            return ResponseEntity.ok(user);
        } catch (UserValidationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Giriş sırasında bir hata oluştu: " + e.getMessage());
        }
    }
}