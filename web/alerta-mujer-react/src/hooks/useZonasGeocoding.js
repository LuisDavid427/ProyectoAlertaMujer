// src/hooks/useZonasGeocoding.js
import { useState, useEffect } from 'react';

export function useZonasGeocoding(zonasAgrupadas) {
    const [nombresZonas, setNombresZonas] = useState({});

    useEffect(() => {
        let isMounted = true;
        const obtenerNombresZonas = async () => {
            const nuevosNombres = { ...nombresZonas };
            let huboCambios = false;

            for (const zona of zonasAgrupadas) {
                if (!nuevosNombres[zona.id]) {
                    try {
                        const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${zona.centro.lat}&lon=${zona.centro.lon}&zoom=16`;
                        const response = await fetch(url, { headers: { 'Accept-Language': 'es' } });
                        const data = await response.json();
                        
                        if (data && data.address) {
                            const barrio = data.address.neighbourhood || data.address.suburb || data.address.city_district || "Zona no especificada";
                            const ciudad = data.address.city || data.address.town || "Ciudad desconocida";
                            const departamento = data.address.state || "";
                            const pais = data.address.country || "País no identificado";
                            
                            nuevosNombres[zona.id] = {
                                ubicacionDetallada: `${barrio}, ${ciudad}, ${pais}`,
                                ciudadDepto: departamento ? `${ciudad}, ${departamento}` : ciudad,
                                pais: pais
                            };
                        } else {
                            nuevosNombres[zona.id] = { ubicacionDetallada: "Ubicación sin registrar", ciudadDepto: "Desconocido", pais: "Desconocido" };
                        }
                        huboCambios = true;
                        await new Promise(resolve => setTimeout(resolve, 1000));
                    } catch (error) {
                        console.error("Error obteniendo ubicación:", error);
                    }
                }
            }
            if (huboCambios && isMounted) setNombresZonas(nuevosNombres);
        };

        if (zonasAgrupadas.length > 0) obtenerNombresZonas();

        return () => { isMounted = false; };
    }, [zonasAgrupadas]);

    return nombresZonas;
}