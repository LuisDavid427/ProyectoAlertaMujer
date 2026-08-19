import { useState } from 'react';
import Header from '../../components/Header';
import ErrorModal from '../../components/ErrorModal';
import { loginApp } from '../../services/authService';
import './login.css';

export default function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    
    const [isErrorOpen, setIsErrorOpen] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!email || !password) {
            setErrorMessage("Por favor, completa todos los campos.");
            setIsErrorOpen(true);
            return;
        }

        try {
            const resultado = await loginApp(email, password);
            
            // ⚠️ AQUÍ ESTÁ LA SOLUCIÓN MÁGICA:
            // Guardamos la bandera Y el token JWT que devuelve Spring Boot
            sessionStorage.setItem('sesionAlertaMujer', 'activa');
            if (resultado && resultado.token) {
                sessionStorage.setItem('token', resultado.token);
            }
            
            // Redirigimos al Dashboard
            window.location.replace("/dashboard");
            
        } catch (error) {
            setErrorMessage(error.message);
            setIsErrorOpen(true);
        }
    };

    return (
        <>
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
                    
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="email">Correo Electrónico</label>
                            <input 
                                type="email" 
                                id="email" 
                                className="input-field" 
                                placeholder="ejemplo@correo.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
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

            <ErrorModal 
                isOpen={isErrorOpen} 
                mensaje={errorMessage} 
                onClose={() => setIsErrorOpen(false)} 
            />
        </>
    );
}