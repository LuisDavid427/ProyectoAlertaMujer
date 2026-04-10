async function ejecutarLogin() {
    // 1. Sincronizamos los IDs con login.html
    // En tu login.html los IDs son 'email' y 'password'
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');

    // Validación básica antes del envío
    if (!emailInput.value || !passwordInput.value) {
        alert("Por favor, completa todos los campos.");
        return;
    }

    // El objeto debe coincidir exactamente con los campos de LoginRequest.java
    const datos = { 
        email: emailInput.value, 
        password: passwordInput.value 
    };

    try {
        // 2. Ajustamos la URL al nuevo AuthController (/api/auth/login)
        const respuesta = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify(datos)
        });

        const contentType = respuesta.headers.get("content-type");
        let resultado;
        if (contentType && contentType.includes("application/json")) {
            resultado = await respuesta.json();
        }

        if (respuesta.ok) {
            // Guardamos el pase de abordar temporal en el navegador
            sessionStorage.setItem('sesionAlertaMujer', 'activa');
            
            alert("¡Bienvenida! " + (resultado?.mensaje || "Acceso concedido"));
            
            // Usamos replace() en lugar de href para que la página de login no se quede en el historial
            window.location.replace("dashboard.html"); 
        } else {
            // Manejo de errores (401 Unauthorized u otros)
            const errorMsg = resultado?.error || resultado?.mensaje || "Credenciales incorrectas";
            lanzarPantallaError(errorMsg);
        }
    } catch (error) {
        console.error("Error de conexión:", error);
        lanzarPantallaError("No se pudo conectar con el servidor de Alerta Mujer. Verifica que Spring Boot esté activo.");
    }
}

/**
 * Muestra una alerta visual personalizada si los elementos existen en el DOM,
 * de lo contrario usa un alert estándar.
 */
function lanzarPantallaError(msg) {
    const txtError = document.getElementById('texto-error');
    const pantalla = document.getElementById('pantalla-error');
    const capa = document.getElementById('capa-oscura');
    
    if (txtError && pantalla && capa) {
        txtError.innerText = msg;
        pantalla.style.display = 'block';
        capa.style.display = 'block';
    } else {
        alert("Atención: " + msg);
    }
}

function cerrarError() {
    const pantalla = document.getElementById('pantalla-error');
    const capa = document.getElementById('capa-oscura');
    if (pantalla && capa) {
        pantalla.style.display = 'none';
        capa.style.display = 'none';
    }
}

function limpiarCampos() {
    document.querySelectorAll('.input-field').forEach(input => input.value = "");
}