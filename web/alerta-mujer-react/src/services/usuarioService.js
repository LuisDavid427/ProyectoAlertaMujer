const API_BASE_URL = 'http://192.168.1.22:8080/api/usuarios';




export async function cambiarEstadoUsuarioApi(idUsuario, nuevoEstado) {
    const token = sessionStorage.getItem('token') || localStorage.getItem('token');
    
    const respuesta = await fetch(`${API_BASE_URL}/${idUsuario}/estado?activo=${nuevoEstado}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        }
    });

    if (!respuesta.ok) {
        throw new Error("No se pudo actualizar el estado del usuario en el servidor");
    }

    // Dependiendo de lo que devuelva tu backend (puede ser JSON o texto plano)
    const contentType = respuesta.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
        return await respuesta.json();
    }
    return true;
}