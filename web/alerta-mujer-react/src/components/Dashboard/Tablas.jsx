// src/components/Dashboard/Tablas.jsx
import React from 'react';

export const TablaAlertas = ({ datos }) => {
    // Si datos es undefined o null, evitamos que .map() rompa la app
    if (!datos || datos.length === 0) return <p>No hay alertas para mostrar.</p>;

    return (
        <table className="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Víctima</th>
                    <th>Mensaje</th>
                    <th>Fecha</th>
                    <th>Estado</th>
                </tr>
            </thead>
            <tbody>
                {datos.map((alerta) => (
                    <tr key={alerta.id_alerta}>
                        <td>#{alerta.id_alerta}</td>
                        <td><strong>{alerta.nombre_victima}</strong></td>
                        <td>{alerta.mensaje}</td>
                        <td>{new Date(alerta.fecha).toLocaleString()}</td>
                        <td>
                            <span className={`badge ${alerta.estado_alerta === 'activa' ? 'bg-danger' : 'bg-secondary'}`}>
                                {alerta.estado_alerta.toUpperCase()}
                            </span>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export const TablaUsuarios = ({ datos }) => {
    if (!datos || datos.length === 0) return <p>No hay usuarios registrados.</p>;

    return (
        <table className="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Email</th>
                    <th>Estado</th>
                </tr>
            </thead>
            <tbody>
                {datos.map((u) => (
                    <tr key={u.idUsuario}>
                        <td>{u.idUsuario}</td>
                        <td>{u.nombre}</td>
                        <td>{u.email}</td>
                        <td>
                            <span className={`badge ${u.activo ? 'bg-success' : 'bg-danger'}`}>
                                {u.activo ? 'Activo' : 'Inactivo'}
                            </span>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};