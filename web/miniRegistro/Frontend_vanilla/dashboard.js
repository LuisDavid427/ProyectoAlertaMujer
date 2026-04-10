// 1. SEGURIDAD DE SESIÓN
// Si no hay sesión, se expulsa al usuario al login inmediatamente.
// IMPORTANTE: Para hacer pruebas de diseño, asegúrate de haber entrado primero por login.html
if (sessionStorage.getItem('sesionAlertaMujer') !== 'activa') {
    window.location.replace('login.html');
}

function alternarVista(interruptor) {
    const estaEnUsuarios = interruptor.checked;
    
    // Capturamos elementos
    const lblAlertas = document.getElementById('lbl-alertas');
    const lblUsuarios = document.getElementById('lbl-usuarios');
    const buscador = document.getElementById('buscador');
    const badgeEstado = document.getElementById('badge-estado');
    const tituloSeccion = document.getElementById('titulo-seccion');
    const descSeccion = document.getElementById('desc-seccion');
    
    // NUEVO: Capturamos el filtro desplegable
    const filtroEstado = document.getElementById('filtroEstado');

    if (estaEnUsuarios) {
        // --- MODO USUARIOS ---
        lblUsuarios.classList.add('active-usuarios');
        lblAlertas.classList.remove('active-alertas');
        buscador.placeholder = "Buscar usuario por nombre o correo...";
        
        // Cambios en la tarjeta
        badgeEstado.innerHTML = "👥 Usuarios";
        badgeEstado.style.background = "#4caf50"; 
        badgeEstado.style.color = "white";
        tituloSeccion.innerText = "Registro de Usuarios";
        descSeccion.innerText = "Visualización de los usuarios de la app Alerta Mujer.";
        
        // NUEVO: Cambiamos el texto del filtro
        filtroEstado.options[1].text = "🔴 Emitiendo Alerta Actual";
        
    } else {
        // --- MODO ALERTAS ---
        lblAlertas.classList.add('active-alertas');
        lblUsuarios.classList.remove('active-usuarios');
        buscador.placeholder = "Buscar alerta por código o ubicación...";
        
        // Cambios en la tarjeta
        badgeEstado.innerHTML = "🔴 Activas";
        badgeEstado.style.background = "var(--secondary-color)"; 
        badgeEstado.style.color = "var(--primary-color)"; 
        tituloSeccion.innerText = "Registro de Alertas SOS";
        descSeccion.innerText = "Visualización en tiempo real de las alertas de emergencia emitidas.";
        
        // NUEVO: Cambiamos el texto del filtro a su estado original
        filtroEstado.options[1].text = "🔴 Solo Activ@s";
    }
    
    // Limpieza de experiencia de usuario al cambiar de pestaña
    buscador.value = ""; 
    filtroEstado.value = "todos"; 
}

// 3. LÓGICA DE CERRAR SESIÓN
function cerrarSesion(evento) {
    if(evento) evento.preventDefault(); 
    
    sessionStorage.removeItem('sesionAlertaMujer');
    window.location.replace('login.html');
}