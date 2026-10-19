// src/components/Header.jsx
import { Link } from 'react-router-dom';
import logoImg from '../assets/logo.png';

export default function Header({ vistaAdmin = false, onLogout, onDescargarPdf }) {
    return (
        <header>
            <div className="container nav-container">
                <div className="logo">
                    <Link to="/" style={{ textDecoration: 'none', color: 'inherit', display: 'flex', alignItems: 'center', gap: '12px' }}>
                        <img 
                            src={logoImg} 
                            alt="Alerta Mujer" 
                            style={{ height: '45px', width: 'auto', objectFit: 'contain' }}
                        />
                        <span>
                            {vistaAdmin ? 'Alerta Mujer | Panel Administrativo' : 'Alerta Mujer'}
                        </span>
                    </Link>
                </div>
                
                <div className="header-actions">
                    {vistaAdmin ? (
                        <>
                            {onDescargarPdf && (
                                <button onClick={onDescargarPdf} className="btn-pdf-header">
                                    📥 Descargar Reporte PDF
                                </button>
                            )}
                            <button className="btn-secondary" onClick={onLogout}>
                                Cerrar Sesión
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="btn-secondary">Acceder</Link>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}