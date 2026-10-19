// src/pages/Dashboard/EstadisticasView.jsx
import { useMemo } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

export default function EstadisticasView({ zonasAgrupadas, nombresZonas }) {
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
        <div className="estadisticas-container">
            {datosGraficoPorPais.length === 0 ? (
                <div className="card shadow-sm text-center" style={{ padding: '40px', color: '#64748b' }}>
                    No hay alertas con ubicación registradas para generar estadísticas.
                </div>
            ) : (
                datosGraficoPorPais.map((grupoPais, idx) => (
                    <div key={idx} className="card shadow-sm" style={{ marginBottom: '30px', padding: '25px', borderTop: '5px solid #e63d7b' }}>
                        <h3 style={{ color: '#8d6f9b', fontSize: '18px', marginBottom: '20px' }}>
                            🌍 Incidencia Consolidada en: {grupoPais.pais}
                        </h3>
                        <div style={{ width: '100%', height: 320 }}>
                            <ResponsiveContainer>
                                <BarChart data={grupoPais.datos} margin={{ top: 10, right: 10, left: -20, bottom: 35 }}>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#85ade0" />
                                    <XAxis dataKey="ubicacion" tick={{ fontSize: 12, fill: '#475569', fontWeight: 'bold' }} interval={0} />
                                    <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: '#64748b' }} />
                                    <Tooltip cursor={{ fill: '#f8f8f8' }} contentStyle={{ borderRadius: '8px', border: '1px solid #fdfdfd', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }} />
                                    <Bar dataKey="cantidadAlertas" name="Total Alertas SOS Emitidas" fill="#e63d7b" radius={[4, 4, 0, 0]} barSize={50} />
                                    <Bar dataKey="cantidadUsuarios" name="Usuarias Afectadas (Únicas)" fill="#3b82f6" radius={[4, 4, 0, 0]} barSize={50} />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}