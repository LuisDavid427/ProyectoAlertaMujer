// src/pages/Dashboard/ReportePdfTemplate.jsx
import { useMemo } from 'react';
import { MapContainer, TileLayer, CircleMarker } from 'react-leaflet';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

export default function ReportePdfTemplate({ zonasAgrupadas, nombresZonas }) {
    
    // 🟢 Lógica de los gráficos de barras adaptada para el reporte PDF
    const datosGraficoPorPais = useMemo(() => {
        const agrupadoPorPais = {};

        zonasAgrupadas.forEach(zona => {
            const infoZona = nombresZonas[zona.id];
            if (!infoZona) return; 

            const pais = infoZona.pais;
            const ciudadDepto = infoZona.ciudadDepto;

            if (!agrupadoPorPais[pais]) agrupadoPorPais[pais] = {};

            if (!agrupadoPorPais[pais][ciudadDepto]) {
                agrupadoPorPais[pais][ciudadDepto] = {
                    ubicacion: ciudadDepto, 
                    cantidadAlertas: 0,
                    usuariasUnicas: new Set()
                };
            }

            agrupadoPorPais[pais][ciudadDepto].cantidadAlertas += zona.alertas.length;
            zona.alertas.forEach(a => {
                agrupadoPorPais[pais][ciudadDepto].usuariasUnicas.add(a.id_usuario || a.idUsuario || a.nombre_usuario || a.nombreVictima);
            });
        });

        return Object.keys(agrupadoPorPais).map(nombrePais => ({
            pais: nombrePais,
            datos: Object.values(agrupadoPorPais[nombrePais]).map(item => ({
                ubicacion: item.ubicacion,
                cantidadAlertas: item.cantidadAlertas,
                cantidadUsuarios: item.usuariasUnicas.size
            })).sort((a, b) => b.cantidadAlertas - a.cantidadAlertas)
        })).sort((a, b) => a.pais.localeCompare(b.pais)); 
    }, [zonasAgrupadas, nombresZonas]);

    return (
        <div style={{ position: 'absolute', top: '-9999px', left: '-9999px' }}>
            <div id="reporte-estadisticas-pdf" style={{ 
                padding: '40px', background: '#ffffff', color: '#1e293b', 
                fontFamily: 'Helvetica, Arial, sans-serif', width: '190mm', margin: '0 auto'
            }}>
                {/* --- SECCIÓN 1: ENCABEZADO DEL REPORTE --- */}
                <div style={{ borderBottom: '3px solid #e63d7b', paddingBottom: '20px', marginBottom: '25px' }}>
                    <h1 style={{ color: '#e63d7b', margin: '0 0 5px 0', fontSize: '24px' }}>ALERTA MUJER</h1>
                    <p style={{ margin: '0', color: '#64748b', fontSize: '13px' }}>Reporte General de Estadísticas, Zonas y Concentración de Riesgo</p>
                </div>

                {/* --- SECCIÓN 2: GRÁFICAS DE ESTADÍSTICAS --- */}
                <div style={{ pageBreakAfter: 'always', marginBottom: '40px' }}>
                    <h3 style={{ color: '#0f172a', fontSize: '16px', borderLeft: '4px solid #e63d7b', paddingLeft: '8px', marginBottom: '20px' }}>
                        Análisis Estadístico Global por Ubicación
                    </h3>

                    {datosGraficoPorPais.length === 0 ? (
                        <p style={{ fontSize: '12px', color: '#94a3b8' }}>No hay datos suficientes para generar las gráficas estadísticas.</p>
                    ) : (
                        datosGraficoPorPais.map((grupoPais, idx) => (
                            <div key={idx} style={{ marginBottom: '30px', padding: '15px', border: '1px solid #e2e8f0', borderRadius: '8px', background: '#f8fafc' }}>
                                <h4 style={{ color: '#8d6f9b', fontSize: '14px', marginBottom: '15px', marginTop: '0' }}>
                                    🌍 Incidencia Consolidada en: {grupoPais.pais}
                                </h4>
                                {/* Nota: Para PDFs con html2pdf, es recomendable usar dimensiones estáticas en píxeles para gráficos */}
                                <div style={{ width: '100%', height: '280px' }}>
                                    <ResponsiveContainer width="100%" height="100%">
                                        <BarChart data={grupoPais.datos} margin={{ top: 10, right: 10, left: -20, bottom: 35 }}>
                                            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#cbd5e1" />
                                            <XAxis dataKey="ubicacion" tick={{ fontSize: 11, fill: '#475569', fontWeight: 'bold' }} interval={0} />
                                            <YAxis allowDecimals={false} tick={{ fontSize: 11, fill: '#64748b' }} />
                                            <Tooltip />
                                            <Bar dataKey="cantidadAlertas" name="Total Alertas SOS" fill="#e63d7b" radius={[4, 4, 0, 0]} barSize={40} />
                                            <Bar dataKey="cantidadUsuarios" name="Usuarias Únicas" fill="#3b82f6" radius={[4, 4, 0, 0]} barSize={40} />
                                        </BarChart>
                                    </ResponsiveContainer>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {/* --- SECCIÓN 3: MAPAS Y CAPTURAS GEOGRÁFICAS --- */}
                <div>
                    <h3 style={{ color: '#0f172a', fontSize: '16px', borderLeft: '4px solid #e63d7b', paddingLeft: '8px', marginBottom: '20px' }}>
                        Capturas Geográficas de Incidentes
                    </h3>
                    
                    {zonasAgrupadas.length === 0 ? (
                        <p style={{ fontSize: '12px', color: '#94a3b8' }}>No hay alertas con ubicación registradas.</p>
                    ) : (
                        zonasAgrupadas.map((zona, index) => (
                            <div 
                                key={zona.id} 
                                style={{ 
                                    marginBottom: '30px', 
                                    pageBreakBefore: index > 0 ? 'always' : 'auto',
                                    pageBreakInside: 'avoid'
                                }}
                            >
                                <div style={{ background: '#f8fafc', padding: '10px', border: '1px solid #e2e8f0', borderRadius: '6px 6px 0 0' }}>
                                    <h4 style={{ margin: '0', fontSize: '13px', color: '#334155' }}>
                                        📍 {nombresZonas[zona.id] ? nombresZonas[zona.id].ubicacionDetallada : `Buscando ubicación exacta...`} 
                                        <span style={{ fontWeight: 'normal', color: '#64748b' }}>
                                            &nbsp;— Concentración: {zona.alertas.length} {zona.alertas.length > 1 ? 'alertas agrupadas' : 'alerta aislada'}
                                        </span>
                                    </h4>
                                </div>
                                
                                <div style={{ height: '220px', width: '100%', border: '1px solid #e2e8f0', borderTop: 'none' }}>
                                    <MapContainer 
                                        center={[zona.centro.lat, zona.centro.lon]} 
                                        zoom={14} 
                                        style={{ height: '100%', width: '100%' }}
                                        zoomControl={false} 
                                        dragging={false}
                                        scrollWheelZoom={false}
                                        doubleClickZoom={false}
                                    >
                                        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
                                        {zona.alertas.map((alerta, i) => (
                                            <CircleMarker 
                                                key={i} 
                                                center={[alerta.latitud, alerta.longitud]} 
                                                radius={10} 
                                                pathOptions={{ color: '#e63d7b', fillColor: '#e63d7b', fillOpacity: 0.6 }}
                                            />
                                        ))}
                                    </MapContainer>
                                </div>

                                <div style={{ padding: '10px', fontSize: '10px', color: '#64748b', border: '1px solid #e2e8f0', borderTop: 'none', borderRadius: '0 0 6px 6px' }}>
                                    <strong>Víctimas en esta ubicación: </strong> 
                                    {zona.alertas.map(a => a.nombreVictima || a.nombre_usuario || "Desconocida").join(', ')}
                                </div>
                            </div>
                        ))
                    )}
                </div>

            </div>
        </div>
    );
}