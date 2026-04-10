// src/App.jsx

import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

// Importamos tus 3 pantallas principales
import Home from './pages/Home/Home.jsx';
import Login from './pages/Login/Login.jsx';
import Dashboard from './pages/Dashboard/Dashboard.jsx';

export default function App() {
  return (
    // BrowserRouter envuelve todo para habilitar la navegación
    <BrowserRouter>
      <Routes>
        {/* Ruta pública: El Inicio */}
        <Route path="/" element={<Home />} />
        
        {/* Ruta pública: El Formulario de Acceso */}
        <Route path="/login" element={<Login />} />
        
        {/* Ruta protegida: El Panel (La seguridad real está dentro del componente) */}
        <Route path="/dashboard" element={<Dashboard />} />
        
        {/* Ruta comodín: Si el usuario escribe una URL que no existe, lo mandamos al inicio */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}