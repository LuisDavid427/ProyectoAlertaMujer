--liquibase formatted sql

--changeset luis_david:3
-- 1. Crear política por defecto
INSERT INTO politicas_contrasenas (nombre_politica, minlongitud, maxlongitud, requiere_mayusculas, requiere_numeros, requiere_simbolos)
VALUES ('Politica Estandar', 8, 20, 1, 1, 1);

-- 2. Insertar el rol 'USUARIA' vinculado a la política creada
INSERT INTO roles (nombre_rol, descripcion, id_politica)
VALUES ('USUARIA', 'Usuarias de la aplicacion movil Alerta Mujer', (SELECT id_politica FROM politicas_contrasenas WHERE nombre_politica = 'Politica Estandar'));

--changeset luis_david:4 endDelimiter://
-- 3. Procedimiento para Login de Usuarias de la App
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
    WHERE email = p_email             
      AND contrasena_hash = p_pass    
      AND estado_usuario = 1          
    LIMIT 1; 
END //