// src/pages/Login/Login.jsx

import { useState } from 'react';
import Header from '../../components/Header';
import ErrorModal from '../../components/ErrorModal';
import { loginApp } from '../../services/authService';
import './login.css'; // Importamos su CSS exclusivo

export default function Login() {
    // Variables de Estado (La memoria de React)
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    
    // Estados para controlar nuestra ventana modal de errores
    const [isErrorOpen, setIsErrorOpen] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    // Función que se ejecuta al darle al botón "Iniciar Sesión"
    const handleSubmit = async (e) => {
        e.preventDefault(); // Evita que la página se recargue

        // 1. Validación básica
        if (!email || !password) {
            setErrorMessage("Por favor, completa todos los campos.");
            setIsErrorOpen(true);
            return;
        }

        // 2. Intentar conectar con Spring Boot
        try {
            // Llamamos a nuestro servicio limpio
            const resultado = await loginApp(email, password);
            
            // Guardamos la sesión
            sessionStorage.setItem('sesionAlertaMujer', 'activa');
            
            // Redirigimos al Dashboard (por ahora usando el método nativo del navegador)
            window.location.replace("/dashboard");
            
        } catch (error) {
            // Si falla, abrimos el modal con el mensaje de error de Spring Boot
            setErrorMessage(error.message);
            setIsErrorOpen(true);
        }
    };

    return (
        <>
            {/* Reutilizamos el Header, vistaAdmin en false porque no estamos logueados */}
            <Header vistaAdmin={false} />

            <main className="container login-main">
                <div className="login-card card">
                    <span className="badge" style={{ marginBottom: '15px', display: 'inline-block' }}>
                        Acceso Seguro
                    </span>
                    <h2>Bienvenid@</h2>
                    <p className="subtitle" style={{ marginBottom: '30px' }}>
                        Ingresa tus datos para gestionar el sistema
                    </p>
                    
                    {/* El formulario ejecuta handleSubmit al enviarse */}
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            {/* En React, 'for' de HTML pasa a ser 'htmlFor' */}
                            <label htmlFor="email">Correo Electrónico</label>
                            <input 
                                type="email" 
                                id="email" 
                                className="input-field" 
                                placeholder="ejemplo@correo.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)} // Reactualiza el estado en cada tecla
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Contraseña</label>
                            <input 
                                type="password" 
                                id="password" 
                                className="input-field" 
                                placeholder="••••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>

                        <button 
                            type="submit" 
                            className="btn-primary" 
                            style={{ width: '100%', border: 'none', cursor: 'pointer', fontWeight: 600, marginTop: '10px' }}
                        >
                            INICIAR SESIÓN
                        </button>
                    </form>
                </div>
            </main>

            {/* Invocamos nuestro componente para los errores */}
            <ErrorModal 
                isOpen={isErrorOpen} 
                mensaje={errorMessage} 
                onClose={() => setIsErrorOpen(false)} 
            />
        </>
    );
}