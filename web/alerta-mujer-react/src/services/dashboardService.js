const API_BASE_URL = 'http://localhost:8080/api/dashboard'; // Apuntamos al prefijo del dashboard

export const obtenerDatosDashboard = async (vista, busqueda = '') => {
    try {
        // Construimos la URL: ej. http://localhost:8080/api/dashboard/alertas?q=luis
        const respuesta = await fetch(`${API_BASE_URL}/${vista}?q=${busqueda}`);
        
        if (!respuesta.ok) throw new Error(`Error al obtener ${vista}`);
        
        const datos = await respuesta.json();
        
        // Retornamos siempre un array para evitar que el .filter del front explote
        return Array.isArray(datos) ? datos : [];
    } catch (error) {
        console.error("Error en la conexión con Spring Boot:", error);
        return []; // Retornamos array vacío en caso de error
    }
};