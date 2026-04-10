// src/pages/Home/Home.jsx

import Header from '../../components/Header';
import Footer from '../../components/Footer';
import './home.css'; // Importamos el CSS exclusivo de esta pantalla

// En React, las imágenes locales se importan así para que Vite las optimice
import heroImg from '../../assets/woman-hero.jpg';

export default function Home() {
    return (
        <>
            {/* Llamamos al componente Header. vistaAdmin={false} porque estamos en la vista pública */}
            <Header vistaAdmin={false} />

            <main>
                {/* --- SECCIÓN HERO --- */}
                <section className="hero container">
                    <div className="hero-content">
                        <span className="badge">✨ Disponible ahora</span>
                        <h1>Protección Personal Inteligente</h1>
                        <p>
                            Alerta Mujer es una aplicación de seguridad personal diseñada para mujeres, 
                            que permite enviar alertas de emergencia con ubicación GPS en tiempo real a 
                            contactos de confianza y autoridades.
                        </p>
                        
                        <div className="hero-btns">
                            {/* Respetando tus directrices: La app es para Android 8+ */}
                            <a href="#" className="btn-primary">Descargar APK (v1.0.1)</a>
                            <a href="#caracteristicas" className="btn-secondary">Ver Características</a>
                        </div>
                        
                        <div className="hero-info">
                            <span>🛡️ 100% Seguro</span>
                            <span>🔒 Cifrado AES-256</span>
                            <span>🕒 24/7 Disponible</span>
                        </div>
                    </div>

                    <div className="hero-image">
                        <div className="image-wrapper">
                            <img src={heroImg} alt="Usuario Alerta Mujer" />
                        </div>
                    </div>
                </section>

                {/* --- SECCIÓN CARACTERÍSTICAS --- */}
                <section id="caracteristicas" className="features container">
                    <h2>Características Principales</h2>
                    <p className="subtitle">Todas las herramientas que necesitas para sentirte segura, en una sola aplicación</p>
                    
                    <div className="features-grid">
                        <div className="card"><i>🚨</i> <h3>Botón SOS</h3> <p>Activación inmediata de alerta con un solo toque.</p></div>
                        <div className="card"><i>📍</i> <h3>Ubicación en Tiempo Real</h3> <p>Envía tu ubicación GPS precisa automáticamente.</p></div>
                        <div className="card"><i>👥</i> <h3>Contactos de Confianza</h3> <p>Gestiona tus contactos de emergencia de forma segura.</p></div>
                        <div className="card"><i>📸</i> <h3>Evidencia Multimedia</h3> <p>Captura fotos y audio como evidencia cifrada.</p></div>
                        <div className="card"><i>📞</i> <h3>Llamada a Autoridades</h3> <p>Conexión directa con servicios de emergencia.</p></div>
                        <div className="card"><i>🔔</i> <h3>Notificaciones Push</h3> <p>Alertas en tiempo real a tus contactos.</p></div>
                        <div className="card"><i>⌛</i> <h3>Historial de Alertas</h3> <p>Registro completo de todas tus alertas.</p></div>
                        <div className="card"><i>🔒</i> <h3>Seguridad AES-256</h3> <p>Cifrado militar para proteger tu información.</p></div>
                    </div>
                </section>

                {/* --- SECCIÓN CÓMO FUNCIONA --- */}
                <section className="how-it-works container">
                    <h2>Cómo Funciona</h2>
                    <p className="subtitle">Protección en 3 simples pasos</p>
                    
                    <div className="steps-container">
                        <div className="step">
                            <div className="circle">1</div> 
                            <h3>Regístrate y Configura</h3> 
                            <p>Crea tu cuenta y añade tus contactos de confianza.</p>
                        </div>
                        <div className="step">
                            <div className="circle">2</div> 
                            <h3>Presiona el Botón SOS</h3> 
                            <p>En caso de emergencia, activa la alerta con un toque.</p>
                        </div>
                        <div className="step">
                            <div className="circle">3</div> 
                            <h3>Ayuda en Camino</h3> 
                            <p>Tus contactos reciben tu ubicación y alerta al instante.</p>
                        </div>
                    </div>
                </section>
            </main>

            {/* Llamamos al componente Footer */}
            <Footer />
        </>
    );
}