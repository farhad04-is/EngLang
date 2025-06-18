package com.example.EngLang.Service;

import com.example.EngLang.Entity.User;
import com.example.EngLang.Exception.UserValidationException;
import com.example.EngLang.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    // Yeni kullanıcı kaydı
    public User registerUser(User user) {
        // Gmail'in zaten kullanımda olup olmadığını kontrol et
        if (userRepository.existsByGmail(user.getGmail())) {
            throw new UserValidationException("Bu Gmail adresi zaten kullanımda.");
        }
        // Güvenlik yok, şifre doğrudan kaydediliyor (Eğitim amaçlı, gerçekte tavsiye edilmez)
        return userRepository.save(user);
    }

    // Kullanıcı girişi
    public User loginUser(String gmail, String password) {
        // Gmail'e göre kullanıcıyı bul
        Optional<User> userOptional = userRepository.findByGmail(gmail);
        if (userOptional.isEmpty()) {
            throw new UserValidationException("Kullanıcı bulunamadı.");
        }
        User user = userOptional.get();

        // Şifreleri doğrudan karşılaştır (Güvenlik yok, gerçekte tavsiye edilmez)
        if (!password.equals(user.getPassword())) {
            throw new UserValidationException("Yanlış şifre.");
        }
        return user;
    }
}