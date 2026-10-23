import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import MapaRiesgo from '../../components/Dashboard/MapaRiesgo';
import Header from '../../components/Header';
import { cerrarSesion } from '../../services/authService';
import { obtenerDatosDashboard } from '../../services/dashboardService';
import { cambiarEstadoUsuarioApi } from '../../services/usuarioService';
import { TablaAlertas, TablaUsuarios } from '../../components/Dashboard/Tablas';
import html2pdf from 'html2pdf.js';

import EstadisticasView from './EstadisticasView.jsx';
import ReportePdfTemplate from './ReportePdfTemplate';
import './dashboard.css';

import { agruparAlertasPorZona } from '../../utils/geoUtils';
import { useZonasGeocoding } from '../../hooks/useZonasGeocoding';
import { useAuth } from '../../context/AuthContext';

export default function Dashboard() {
    const [datos, setDatos] = useState([]);
    const [vistaActual, setVistaActual] = useState('alertas');
    const [busqueda, setBusqueda] = useState('');
    const [filtroEstado, setFiltroEstado] = useState('todos');
    const [cargando, setCargando] = useState(false);
    const [mostrarEstadisticas, setMostrarEstadisticas] = useState(false);

    const navigate = useNavigate();
    const { logout } = useAuth();

    const cargarDatos = async () => {
        setCargando(true);
        const resultado = await obtenerDatosDashboard(vistaActual, busqueda);
        setDatos(resultado);
        setCargando(false);
    };

    useEffect(() => {
        cargarDatos();
    }, [vistaActual, busqueda]);

    const datosFiltrados = useMemo(() => {
        if (!datos || datos.length === 0) return [];

        return datos.filter(item => {
            if (filtroEstado === 'todos') return true;

            if (vistaActual === 'alertas') {
                const estadoItem = (item.estado || item.estado_alerta || '').toLowerCase();
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

    const handleBloquearLocal = async (idUsuario, estadoActual) => {
        const nuevoEstado = !estadoActual;
        try {
            const respuesta = await cambiarEstadoUsuarioApi(idUsuario, nuevoEstado);
            if (respuesta) {
                setDatos(prevDatos =>
                    prevDatos.map(user => {
                        const currentId = user.id_usuario || user.idUsuario || user.id;
                        if (currentId === idUsuario) {
                            return { ...user, activo: nuevoEstado };
                        }
                        return user;
                    })
                );
            } else {
                alert('El servidor no pudo actualizar el estado.');
            }
        } catch (error) {
            console.error('Error crítico al cambiar estado en backend:', error);
        }
    };

    const manejarCambioVista = (e) => {
        setDatos([]);
        setVistaActual(e.target.checked ? 'usuarios' : 'alertas');
        setBusqueda('');
        setFiltroEstado('todos');
    };

    const descargarReportePdf = () => {
        const elemento = document.getElementById('reporte-estadisticas-pdf');
        const opciones = {
            margin: 10,
            filename: 'Reporte_Zonas_AlertaMujer.pdf',
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2, useCORS: true },
            jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
        };
        html2pdf().from(elemento).set(opciones).save();
    };

    const zonasAgrupadas = useMemo(() => agruparAlertasPorZona(datosFiltrados), [datosFiltrados]);
    const nombresZonas = useZonasGeocoding(zonasAgrupadas);

    const manejarCierreSesion = () => {
        cerrarSesion();
        logout();
        navigate('/login');
    };

    return (
        <>
            <Header
                vistaAdmin={true}
                onLogout={manejarCierreSesion}
                onDescargarPdf={descargarReportePdf}
            />

            <main className="container">
                <div className="dashboard-header">
                    <h2>{mostrarEstadisticas ? 'Análisis Estadístico Global' : 'Administrar'}</h2>
                    {!mostrarEstadisticas && (
                        <div className={`toggle-container ${vistaActual === 'usuarios' ? 'usuarios-active' : ''}`}>
                            <span className={`toggle-label ${vistaActual === 'alertas' ? 'active-alertas' : ''}`}>Alertas</span>
                            <label className="switch">
                                <input type="checkbox" checked={vistaActual === 'usuarios'} onChange={manejarCambioVista} />
                                <span className="slider"></span>
                            </label>
                            <span className={`toggle-label ${vistaActual === 'usuarios' ? 'active-usuarios' : ''}`}>Usuarios</span>
                        </div>
                    )}
                </div>

                <div className="toolbar card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px', marginBottom: '20px' }}>
                    <div style={{ display: 'flex', gap: '10px', flex: 1, minWidth: '280px', alignItems: 'center' }}>
                        {!mostrarEstadisticas && (
                            <>
                                <input
                                    type="text"
                                    className="input-field search-bar"
                                    placeholder={vistaActual === 'alertas' ? 'Buscar por código o víctima...' : 'Buscar por nombre o correo...'}
                                    value={busqueda}
                                    onChange={(e) => setBusqueda(e.target.value)}
                                />
                                <select className="input-field" value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)}>
                                    <option value="todos">Todos los registros</option>
                                    <option value="activos">{vistaActual === 'alertas' ? '🔴 Solo Activ@s' : '✅ Solo Activos'}</option>
                                    {vistaActual === 'usuarios' && <option value="inactivos">❌ Solo Inactivos</option>}
                                </select>
                            </>
                        )}

                        {vistaActual === 'alertas' && (
                            <button
                                onClick={() => setMostrarEstadisticas(!mostrarEstadisticas)}
                                style={{
                                    background: mostrarEstadisticas ? '#afa8a8' : '#fcc1de',
                                    color: 'white', border: 'none', padding: '10px 20px',
                                    borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', marginLeft: 'auto'
                                }}
                            >
                                {mostrarEstadisticas ? '← Volver a las Alertas' : '📊 Ver Estadísticas Globales'}
                            </button>
                        )}
                    </div>
                </div>

                {!mostrarEstadisticas ? (
                    <div className="card shadow-sm">
                        <div style={{ marginBottom: '15px' }}>
                            <span className="badge" style={{ background: vistaActual === 'alertas' ? 'var(--secondary-color, #f44336)' : '#4caf50', color: 'white' }}>
                                {vistaActual === 'alertas' ? '🔴 Registro SOS' : '👥 Directorio'}
                            </span>
                        </div>
                        <div className="table-container">
                            {cargando ? (
                                <div className="text-center p-5">Consultando base de datos...</div>
                            ) : vistaActual === 'alertas' ? (
                                <>
                                    {datosFiltrados.length > 0 && <MapaRiesgo alertas={datosFiltrados} />}
                                    <TablaAlertas datos={datosFiltrados} />
                                </>
                            ) : (
                                <TablaUsuarios datos={datosFiltrados} onBloquearLocal={handleBloquearLocal} />
                            )}
                        </div>
                    </div>
                ) : (
                    <EstadisticasView zonasAgrupadas={zonasAgrupadas} nombresZonas={nombresZonas} />
                )}
            </main>

            <ReportePdfTemplate zonasAgrupadas={zonasAgrupadas} nombresZonas={nombresZonas} />
        </>
    );
}
