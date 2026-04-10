// src/main.jsx

import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'

// Aquí nacen tus estilos globales para TODA la aplicación
import './css/variables.css'
import './css/layout.css'
import './css/buttons.css'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)