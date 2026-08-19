const API_BASE_URL = 'http://192.168.1.22:8080/api/dashboard';

export const obtenerDatosDashboard = async (vista, busqueda = '') => {
    try {
        const token = sessionStorage.getItem('token') || localStorage.getItem('token');
        const qParam = encodeURIComponent(busqueda || '');

        const respuesta = await fetch(`${API_BASE_URL}/${vista}?q=${qParam}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        });

        if (!respuesta.ok) {
            console.error(`Error HTTP ${respuesta.status} en ${vista}`);
            return [];
        }

        const datos = await respuesta.json();
        return Array.isArray(datos) ? datos : [];
    } catch (error) {
        console.error("Error en la conexión con Spring Boot:", error);
        return [];
    }
};