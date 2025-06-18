// src/main.jsx (or index.js/index.jsx)
import React from 'react'; // <--- ADD THIS LINE
import { createRoot } from 'react-dom/client';

import App from './App.jsx';
import WordProvider from './Context/WordProvider.jsx';
import { AuthProvider } from './Context/AuthContext.jsx'; // Make sure AuthProvider is imported

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <AuthProvider>
      <WordProvider>
        <App />
      </WordProvider>
    </AuthProvider>
  </React.StrictMode>
);