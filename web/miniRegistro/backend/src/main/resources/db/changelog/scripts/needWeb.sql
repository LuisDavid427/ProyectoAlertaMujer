--liquibase formatted sql

--changeset luis_david:1
-- 1. Insertar el Rol de Administrador
INSERT INTO roles (nombre_rol, descripcion) 
VALUES ('administrador', 'Acceso total al panel de gestion de Alerta Mujer');

-- 2. Insertar Permisos de ejemplo
INSERT INTO permisos (nombre_permiso, descripcion)
VALUES 
('ACCESO_PANEL', 'Permite entrar al dashboard administrativo'),
('GESTION_ALERTAS', 'Permite visualizar y gestionar alertas SOS');

-- 3. Vincular Rol con Permisos
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1, 1), (1, 2);

-- 4. Insertar a Luis David
INSERT INTO usuarios (nombre, email, contrasena_hash, fecha_nacimiento, estado_usuario)
VALUES ('Luis David Conde Sanchez', 'luisdavidcondesanchez@gmail.com', 'admin', '2007-07-08', 1);

-- 5. Asignar el Rol de Administrador
INSERT INTO usuarios_roles (id_usuario, id_rol)
SELECT id_usuario, 1 FROM usuarios WHERE email = 'luisdavidcondesanchez@gmail.com';

--changeset luis_david:2 endDelimiter://
-- 6. Crear el Procedimiento Almacenado
CREATE PROCEDURE sp_validar_login_admin(
    IN p_email VARCHAR(150),
    IN p_pass VARCHAR(255)
)
BEGIN
    SELECT 
        u.id_usuario,
        u.nombre,
        u.email
    FROM usuarios u
    INNER JOIN usuarios_roles ur ON u.id_usuario = ur.id_usuario
    INNER JOIN roles r ON ur.id_rol = r.id_rol
    WHERE u.email = p_email             
      AND u.contrasena_hash = p_pass    
      AND r.nombre_rol = 'administrador' 
      AND u.estado_usuario = 1          
    LIMIT 1;
END //