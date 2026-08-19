const API_URL = 'http://192.168.1.22:8080/api/auth';

export async function loginApp(email, password) {
    const respuesta = await fetch(`${API_URL}/login`, {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json' 
        },
        body: JSON.stringify({ email, password })
    });

    const contentType = respuesta.headers.get("content-type");
    let resultado;
    
    if (contentType && contentType.includes("application/json")) {
        resultado = await respuesta.json();
    }

    if (!respuesta.ok) {
        throw new Error(resultado?.error || resultado?.mensaje || "Credenciales incorrectas");
    }

    return resultado;
}

export function cerrarSesion() {
    sessionStorage.removeItem('sesionAlertaMujer');
    sessionStorage.removeItem('token');
}