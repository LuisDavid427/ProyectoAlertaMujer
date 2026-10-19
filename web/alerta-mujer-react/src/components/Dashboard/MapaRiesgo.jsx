// src/components/Dashboard/MapaRiesgo.jsx
import React from 'react';
import { MapContainer, TileLayer, CircleMarker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';

export default function MapaRiesgo({ alertas }) {
    // Filtramos para asegurarnos de que solo se mapeen alertas con latitud y longitud válidas
    const alertasValidas = alertas.filter(a => a.latitud && a.longitud);

    // Coordenadas centrales por defecto (Neiva, Huila)
    const centroPorDefecto = [2.9273, -75.2818];

    return (
        <div style={{ marginBottom: '20px', borderRadius: '10px', overflow: 'hidden', border: '1px solid #e2e8f0' }}>
            <MapContainer 
                center={centroPorDefecto} 
                zoom={13} 
                style={{ height: '400px', width: '100%', zIndex: 1 }}
            >
                {/* Capa base del mapa (OpenStreetMap) */}
                <TileLayer
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                />

                {/* Dibujamos las zonas de riesgo */}
                {alertasValidas.map((alerta) => (
                    <CircleMarker 
                        key={alerta.idAlerta || alerta.id_alerta} 
                        center={[alerta.latitud, alerta.longitud]} 
                        radius={25} // Tamaño de la zona afectada
                        pathOptions={{ 
                            color: 'transparent', 
                            fillColor: '#e63d7b', // Tu color corporativo rojo/rosado
                            fillOpacity: 0.3      // Opacidad baja para crear efecto de "calor" al superponerse
                        }}
                    >
                        <Popup>
                            <strong>Víctima:</strong> {alerta.nombreVictima || alerta.nombre_usuario}<br/>
                            <strong>Fecha:</strong> {new Date(alerta.fecha || alerta.timestamp).toLocaleString()}<br/>
                            <strong>Estado:</strong> {alerta.estadoAlerta || 'Activa'}
                        </Popup>
                    </CircleMarker>
                ))}
            </MapContainer>
        </div>
    );
}