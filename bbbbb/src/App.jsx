// src/App.js
import React, { useState, useContext, useEffect } from 'react';
import EngLangAnimation from './EngLangAnimation'; // Animasyon bileşenini import et
import MainContent from './MainContent'; // Ana içerik bileşenini import et
import { AuthContext } from './Context/AuthContext'; // AuthContext'i import et
import { SignupForm } from './SignupForm'; // SignupForm'u import et
import 'bootstrap/dist/css/bootstrap.min.css';
function App() {
  const [showIntroAnimation, setShowIntroAnimation] = useState(true);
  const { isAuthenticated, user } = useContext(AuthContext); // AuthContext'ten isAuthenticated ve user'ı al

  // Animasyon tamamlandığında veya kullanıcı zaten giriş yapmışsa animasyonu kapat
  useEffect(() => {
    // Eğer kullanıcı zaten giriş yapmışsa, animasyonu atla
    if (isAuthenticated) {
      setShowIntroAnimation(false);
    }
  }, [isAuthenticated]); // isAuthenticated değiştiğinde bu efekti tekrar çalıştır

  // Bu fonksiyon, animasyon tamamlandığında EngLangAnimation bileşeni tarafından çağrılır.
  const handleAnimationComplete = () => {
    console.log("Animasyon bitti!");
    setShowIntroAnimation(false); // Animasyonu gizle
  };

  return (
    <div>
      {/* Eğer `showIntroAnimation` true ise, EngLangAnimation'ı render et. */}
      {showIntroAnimation ? (
        <EngLangAnimation onAnimationComplete={handleAnimationComplete} />
      ) : (
        // Animasyon bittiyse (yani `showIntroAnimation` false ise),
        // AuthContext'teki `isAuthenticated` durumuna göre doğru içeriği göster.
        isAuthenticated ? (
          <MainContent />
        ) : (
          <SignupForm onSignupSuccess={() => { /* AuthContext zaten isAuthenticated'ı güncelleyeceği için burada ek bir işlem gerekmez. */ }} />
        )
      )}
    </div>
  );
}

export default App;