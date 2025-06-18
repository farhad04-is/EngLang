// src/Context/AuthContext.jsx
import React, { createContext, useState, useEffect } from 'react';
import axios from 'axios';

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null); // Kullanıcı bilgilerini saklamak için

  useEffect(() => {
    // Uygulama yüklendiğinde localStorage'dan token veya kullanıcı durumunu kontrol et
    const storedToken = localStorage.getItem('authToken');
    const storedUser = localStorage.getItem('user');
    if (storedToken && storedUser) {
      setIsAuthenticated(true);
      setUser(JSON.parse(storedUser));
      // Axios için Authorization başlığını ayarla
      axios.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
    }
  }, []);

  const login = async (gmail, password) => {
    try {
      const response = await axios.post('http://localhost:8082/auth/login', { gmail, password });
      const { token, username, userId } = response.data; // API'den dönen veriye göre ayarla

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify({ username, gmail, userId })); // Kullanıcı bilgilerini kaydet
      setIsAuthenticated(true);
      setUser({ username, gmail, userId });
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`; // Axios için başlığı ayarla
      return { success: true, message: 'Login successful!' };
    } catch (error) {
      console.error('Login failed:', error);
      setIsAuthenticated(false);
      setUser(null);
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      delete axios.defaults.headers.common['Authorization'];
      return { success: false, message: error.response?.data?.message || 'Invalid credentials' };
    }
  };

  const logout = () => {
    setIsAuthenticated(false);
    setUser(null);
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    delete axios.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};