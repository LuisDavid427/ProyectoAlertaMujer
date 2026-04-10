// src/components/Header.jsx
import { Link } from 'react-router-dom'; // 1. Importamos Link
import logoImg from '../assets/logo.png';

export default function Header({ vistaAdmin = false, onLogout }) {
    return (
        <header>
            <div className="container nav-container">
                <div className="logo">
                    {/* 2. Cambiamos <a href="/"> por <Link to="/"> */}
                    <Link to="/" style={{ textDecoration: 'none', color: 'inherit', display: 'flex', alignItems: 'center', gap: '12px' }}>
                        <img 
                            src={logoImg} 
                            alt="Alerta Mujer" 
                            style={{ height: '45px', width: 'auto', objectFit: 'contain' }} // <-- 1. Le ponemos un tamaño decente
                        />
                        <span>
                            {vistaAdmin ? 'Alerta Mujer | Panel Administrativo' : 'Alerta Mujer'}
                        </span>
                    </Link>
                </div>
                
                <div className="header-actions">
                    {vistaAdmin ? (
                        <button className="btn-secondary" onClick={onLogout}>
                            Cerrar Sesión
                        </button>
                    ) : (
                        <>
                            <a href="#" className="btn-top">Descargar APK</a>
                            {/* 3. Cambiamos <a href="/login"> por <Link to="/login"> */}
                            <Link to="/login" className="btn-secondary">Acceder</Link>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}