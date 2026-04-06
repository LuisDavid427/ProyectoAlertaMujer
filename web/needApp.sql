



USE db_alerta_mujer; -- Cambia por el nombre real de tu DB

-- 1. Primero creamos una política por defecto
INSERT INTO politicas_contrasenas (nombre_politica, minlongitud, maxlongitud, requiere_mayusculas, requiere_numeros, requiere_simbolos)
VALUES ('Politica Estandar', 8, 20, 1, 1, 1);

-- 2. Ahora insertamos el rol 'USUARIA' vinculado a esa política
-- Usamos (SELECT id_politica FROM politicas_contrasenas LIMIT 1) para capturar el ID automáticamente
INSERT INTO roles (nombre_rol, descripcion, id_politica)
VALUES ('USUARIA', 'Usuarias de la aplicación móvil Alerta Mujer', (SELECT id_politica FROM politicas_contrasenas WHERE nombre_politica = 'Politica Estandar'));

-- 3. Opcional: Rol para el Admin
INSERT INTO roles (nombre_rol, descripcion, id_politica)
VALUES ('ADMIN', 'Administrador del sistema web', (SELECT id_politica FROM politicas_contrasenas WHERE nombre_politica = 'Politica Estandar'));


DESCRIBE roles;


select * from usuarios;

DELIMITER //

CREATE PROCEDURE sp_validar_login_usuario(
    IN p_email VARCHAR(150),
    IN p_pass VARCHAR(255)
)
BEGIN
    SELECT 
        id_usuario, 
        nombre, 
        email
    FROM usuarios 
    WHERE email = p_email             -- Filtro por correo ingresado en la app
      AND contrasena_hash = p_pass    -- Filtro por el hash de la contraseña
      AND estado_usuario = 1          -- Valida que la cuenta no esté suspendida
    LIMIT 1; 
END //

DELIMITER ;


call sp_validar_login_usuario('', '');