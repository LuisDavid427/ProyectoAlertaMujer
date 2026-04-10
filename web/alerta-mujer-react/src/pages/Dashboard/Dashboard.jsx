// src/pages/Dashboard/Dashboard.jsx

import { useState, useEffect } from 'react';
import Header from '../../components/Header';
import { cerrarSesion } from '../../services/authService';
import './dashboard.css';

export default function Dashboard() {
    // --- 1. SEGURIDAD DE SESIÓN ---
    // useEffect se ejecuta apenas la página intenta cargar
    useEffect(() => {
        if (sessionStorage.getItem('sesionAlertaMujer') !== 'activa') {
            window.location.replace('/login');
        }
    }, []);

    // --- 2. ESTADOS DE REACT (Memoria del componente) ---
    const [vistaActual, setVistaActual] = useState('alertas'); // 'alertas' o 'usuarios'
    const [busqueda, setBusqueda] = useState('');
    const [filtroFecha, setFiltroFecha] = useState('');
    const [filtroEstado, setFiltroEstado] = useState('todos');

    // --- 3. LÓGICA DE CONTROL ---
    const manejarCambioVista = (e) => {
        // Si el switch está checked, es usuarios. Si no, es alertas.
        setVistaActual(e.target.checked ? 'usuarios' : 'alertas');
        
        // Limpiamos los filtros como lo tenías en tu Vanilla JS
        setBusqueda('');
        setFiltroEstado('todos');
        setFiltroFecha('');
    };

    const manejarCierreSesion = (e) => {
        e.preventDefault();
        cerrarSesion(); // Llama a la función del servicio
        window.location.replace('/login');
    };

    return (
        <>
            {/* Header en modo Admin, le pasamos la función para que el botón funcione */}
            <Header vistaAdmin={true} onLogout={manejarCierreSesion} />

            <main className="container">
                <div className="dashboard-header">
                    <h2>Administrar</h2>
                    
                    <div className="toggle-container">
                        <span className={`toggle-label ${vistaActual === 'alertas' ? 'active-alertas' : ''}`}>
                            Alertas
                        </span>
                        
                        <label className="switch">
                            <input 
                                type="checkbox" 
                                checked={vistaActual === 'usuarios'} 
                                onChange={manejarCambioVista} 
                            />
                            <span className="slider"></span>
                        </label>
                        
                        <span className={`toggle-label ${vistaActual === 'usuarios' ? 'active-usuarios' : ''}`}>
                            Usuarios
                        </span>
                    </div>
                </div>

                {/* --- BARRA DE HERRAMIENTAS DINÁMICA --- */}
                <div className="toolbar card">
                    <input 
                        type="text" 
                        className="input-field search-bar" 
                        placeholder={vistaActual === 'alertas' ? "Buscar alerta por código o ubicación..." : "Buscar usuario por nombre o correo..."}
                        value={busqueda}
                        onChange={(e) => setBusqueda(e.target.value)}
                    />
                    
                    <input 
                        type="date" 
                        className="input-field date-filter" 
                        value={filtroFecha}
                        onChange={(e) => setFiltroFecha(e.target.value)}
                    />
                    
                    <select 
                        className="input-field" 
                        style={{ width: 'auto', cursor: 'pointer' }}
                        value={filtroEstado}
                        onChange={(e) => setFiltroEstado(e.target.value)}
                    >
                        <option value="todos">Todos los registros</option>
                        {/* El texto de la opción 2 cambia mágicamente según la vista */}
                        <option value="activos">
                            {vistaActual === 'alertas' ? '🔴 Solo Activ@s' : '🔴 Emitiendo Alerta Actual'}
                        </option>
                    </select>
                </div>

                {/* --- CONTENIDO PRINCIPAL DINÁMICO --- */}
                <div className="card">
                    <div style={{ marginBottom: '15px' }}>
                        {/* El color de fondo y texto del badge cambia según la vista */}
                        <span 
                            className="badge" 
                            style={{ 
                                background: vistaActual === 'alertas' ? 'var(--secondary-color)' : '#4caf50',
                                color: vistaActual === 'alertas' ? 'var(--primary-color)' : 'white'
                            }}
                        >
                            {vistaActual === 'alertas' ? '🔴 Activas' : '👥 Usuarios'}
                        </span>
                    </div>
                    
                    <h3>
                        {vistaActual === 'alertas' ? 'Registro de Alertas SOS' : 'Registro de Usuarios'}
                    </h3>
                    
                    <p style={{ color: 'var(--text-light)', marginTop: '5px' }}>
                        {vistaActual === 'alertas' 
                            ? 'Visualización en tiempo real de las alertas de emergencia emitidas.' 
                            : 'Visualización de los usuarios de la app Alerta Mujer.'}
                    </p>
                    
                    <div className="placeholder-content">
                        [ La tabla de {vistaActual} se renderizará aquí ]
                    </div>
                </div>
            </main>
        </>
    );
}