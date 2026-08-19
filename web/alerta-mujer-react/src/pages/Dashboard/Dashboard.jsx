// src/pages/Dashboard/Dashboard.jsx
import { useState, useEffect, useMemo } from 'react';
import Header from '../../components/Header';
import { cerrarSesion } from '../../services/authService';
import { obtenerDatosDashboard } from '../../services/dashboardService';
import { TablaAlertas, TablaUsuarios } from '../../components/Dashboard/Tablas'; 
import './dashboard.css';

export default function Dashboard() {
    // --- 1. SEGURIDAD Y ESTADOS ---
    const [datos, setDatos] = useState([]);
    const [vistaActual, setVistaActual] = useState('alertas'); // 'alertas' o 'usuarios'
    const [busqueda, setBusqueda] = useState('');
    const [filtroEstado, setFiltroEstado] = useState('todos');
    const [cargando, setCargando] = useState(false);

    useEffect(() => {
        if (sessionStorage.getItem('sesionAlertaMujer') !== 'activa') {
            window.location.replace('/login');
        }
    }, []);

    // --- 2. CARGA DE DATOS (API) ---
    const cargarDatos = async () => {
        setCargando(true);
        const resultado = await obtenerDatosDashboard(vistaActual, busqueda);
        
        setDatos(resultado);
        setCargando(false);
    };

    useEffect(() => {
        cargarDatos();
    }, [vistaActual, busqueda]); // Se dispara al cambiar de switch o buscar

    // --- LÓGICA DE FILTRADO CORREGIDA ---

    const datosFiltrados = useMemo(() => {
        if (!datos || datos.length === 0) return [];

        return datos.filter(item => {
            // Si el combo está en "Todos los registros", retorna todo
            if (filtroEstado === 'todos') return true;

            if (vistaActual === 'alertas') {
                // Se evalúa 'estado' (o 'estado_alerta' por seguridad en caso de fallback)
                const estadoItem = (item.estado || item.estado_alerta || '').toLowerCase();
                
                // Si seleccionó "activos", busca si el estado dice "activa" o "activo"
                if (filtroEstado === 'activos') {
                    return estadoItem === 'activa' || estadoItem === 'activo';
                }
                return true;
            }

            if (vistaActual === 'usuarios') {
                return item.activo === (filtroEstado === 'activos');
            }

            return true;
        });
    }, [datos, filtroEstado, vistaActual]);

    // --- 4. HANDLERS ---
    const manejarCambioVista = (e) => {
        setDatos([]); // <--- ESTO ES CLAVE: Limpia los datos viejos
        setVistaActual(e.target.checked ? 'usuarios' : 'alertas');
        setBusqueda('');
        setFiltroEstado('todos');
    };

    return (
        <>
            <Header vistaAdmin={true} onLogout={() => { cerrarSesion(); window.location.replace('/login'); }} />

            <main className="container">
                <div className="dashboard-header">
                        <h2>Administrar</h2>
                        <div className={`toggle-container ${vistaActual === 'usuarios' ? 'usuarios-active' : ''}`}>
                            <span className={`toggle-label ${vistaActual === 'alertas' ? 'active-alertas' : ''}`}>Alertas</span>
                            <label className="switch">
                                <input type="checkbox" checked={vistaActual === 'usuarios'} onChange={manejarCambioVista} />
                                <span className="slider"></span>
                            </label>
                            <span className={`toggle-label ${vistaActual === 'usuarios' ? 'active-usuarios' : ''}`}>Usuarios</span>
                        </div>
                </div>

                <div className="toolbar card">
                    <input 
                        type="text" className="input-field search-bar" 
                        placeholder={vistaActual === 'alertas' ? "Buscar por código o víctima..." : "Buscar por nombre o correo..."}
                        value={busqueda}
                        onChange={(e) => setBusqueda(e.target.value)}
                    />
                    <select className="input-field" value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)}>
                        <option value="todos">Todos los registros</option>
                        <option value="activos">
                            {vistaActual === 'alertas' ? '🔴 Solo Activ@s' : '✅ Solo Activos'}
                        </option>
                        {vistaActual === 'usuarios' && <option value="inactivos">❌ Solo Inactivos</option>}
                    </select>
                </div>

                <div className="card shadow-sm">
                    <div style={{ marginBottom: '15px' }}>
                        <span className="badge" style={{ 
                            background: vistaActual === 'alertas' ? 'var(--secondary-color, #f44336)' : '#4caf50',
                            color: 'white'
                        }}>
                            {vistaActual === 'alertas' ? '🔴 Registro SOS' : '👥 Directorio'}
                        </span>
                    </div>
                    
                    <div className="table-container">
                        {cargando ? (
                            <div className="text-center p-5">Consultando base de datos...</div>
                        ) : vistaActual === 'alertas' ? (
                            <TablaAlertas datos={datosFiltrados} />
                        ) : (
                            <TablaUsuarios datos={datosFiltrados} />
                        )}
                    </div>
                </div>
            </main>
        </>
    );
}