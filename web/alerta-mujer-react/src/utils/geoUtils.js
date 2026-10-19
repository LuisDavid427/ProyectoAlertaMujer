// src/utils/geoUtils.js
export const calcularDistancia = (lat1, lon1, lat2, lon2) => {
    const R = 6371; 
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLon/2) * Math.sin(dLon/2);
    return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
};

export const agruparAlertasPorZona = (alertas) => {
    const grupos = [];
    const alertasUbicacion = alertas.filter(a => a.latitud && a.longitud);

    alertasUbicacion.forEach(alerta => {
        let asignado = false;
        for (let grupo of grupos) {
            const dist = calcularDistancia(grupo.centro.lat, grupo.centro.lon, alerta.latitud, alerta.longitud);
            if (dist <= 2.0) { 
                grupo.alertas.push(alerta);
                asignado = true;
                break;
            }
        }
        if (!asignado) {
            grupos.push({
                id: `zona-${alerta.idAlerta || Date.now()}`,
                centro: { lat: alerta.latitud, lon: alerta.longitud },
                alertas: [alerta]
            });
        }
    });
    return grupos;
};