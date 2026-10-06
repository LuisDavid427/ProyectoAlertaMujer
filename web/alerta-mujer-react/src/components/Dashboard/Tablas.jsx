// src/components/Dashboard/Tablas.jsx
import React from 'react';

export const TablaAlertas = ({ datos }) => {
    if (!datos || datos.length === 0) return <p>No hay alertas para mostrar.</p>;

    return (
        <table className="table align-middle">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Víctima</th>
                    <th>Mensaje</th>
                    <th>Fecha</th>
                    <th>Estado</th>
                    <th>Ubicación</th>
                </tr>
            </thead>
            <tbody>
                {datos.map((alerta) => (
                    <tr key={alerta.idAlerta}>
                        <td><strong>#{alerta.idAlerta}</strong></td>
                        <td>{alerta.nombreVictima}</td>
                        <td>{alerta.mensaje}</td>
                        <td>{new Date(alerta.fecha).toLocaleString()}</td>
                        <td>
                            <span className={`badge ${alerta.estadoAlerta?.toLowerCase() === 'activa' ? 'bg-danger' : 'bg-secondary'}`}>
                                {alerta.estadoAlerta ? alerta.estadoAlerta.toUpperCase() : 'DESCONOCIDO'}
                            </span>
                        </td>
                        <td>
                            {alerta.latitud && alerta.longitud ? (
                                <a 
                                    href={`https://www.google.com/maps/search/?api=1&query=${alerta.latitud},${alerta.longitud}`} 
                                    target="_blank" 
                                    rel="noopener noreferrer"
                                    style={{
                                        display: 'inline-flex',
                                        alignItems: 'center',
                                        gap: '4px',
                                        padding: '3px 8px',
                                        backgroundColor: '#fff0f3',
                                        color: '#d90429',
                                        border: '1px solid #ffccd5',
                                        borderRadius: '6px',
                                        fontSize: '12px',
                                        fontWeight: '500',
                                        textDecoration: 'none',
                                        whiteSpace: 'nowrap'
                                    }}
                                >
                                    📍 Ver mapa
                                </a>
                            ) : (
                                <span className="text-muted" style={{ fontSize: '13px' }}>Sin ubicación</span>
                            )}
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export const TablaUsuarios = ({ datos, onBloquearLocal }) => {
    if (!datos || datos.length === 0) return <p>No hay usuarios registrados.</p>;

    return (
        <table className="table align-middle">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Email</th>
                    <th>Estado</th>
                    <th>Acciones (Visual)</th>
                </tr>
            </thead>
            <tbody>
                {datos.map((u) => {
                    // Verificamos si es activo (acepta 1, "1" o true por seguridad)
                    const esActivo = u.activo === 1 || u.activo === "1" || u.activo === true;

                    return (
                        <tr key={u.idUsuario}>
                            <td>{u.idUsuario}</td>
                            <td>{u.nombre}</td>
                            <td>{u.email}</td>
                            <td>
                                <span className={`badge ${esActivo ? 'bg-success' : 'bg-danger'}`}>
                                    {esActivo ? 'Activo' : 'Inactivo'}
                                </span>
                            </td>
                            <td>
                                <button
                                    onClick={() => onBloquearLocal(u.idUsuario, esActivo)}
                                    className={`btn btn-sm ${esActivo ? 'btn-outline-danger' : 'btn-outline-success'}`}
                                    style={{ 
                                        fontSize: '12px', 
                                        padding: '5px 12px',
                                        borderRadius: '6px',
                                        cursor: 'pointer',
                                        fontWeight: '600'
                                    }}
                                >
                                    {esActivo ? '🔒 Bloquear' : '✅ Activar'}
                                </button>
                            </td>
                        </tr>
                    );
                })}
            </tbody>
        </table>
    );
};