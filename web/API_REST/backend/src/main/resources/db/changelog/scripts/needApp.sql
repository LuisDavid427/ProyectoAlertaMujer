--liquibase formatted sql

--changeset luis_david:3
-- 1. crear politica por defecto
insert into politicas_contrasenas (nombre_politica, minlongitud, maxlongitud, requiere_mayusculas, requiere_numeros, requiere_simbolos)
values ('politica estandar', 8, 20, 1, 1, 1);

-- 2. insertar el rol 'usuaria' vinculado a la politica creada
insert into roles (nombre_rol, descripcion, id_politica)
values ('usuaria', 'usuarias de la aplicacion movil alerta mujer', (select id_politica from politicas_contrasenas where nombre_politica = 'politica estandar'));

--changeset luis_david:4 enddelimiter://
-- 3. procedimiento para login de usuarias de la app
create procedure sp_validar_login_usuario(
    in p_email varchar(150),
    in p_pass varchar(255)
)
begin
    select 
        id_usuario, 
        nombre, 
        email
    from usuarios 
    where email = p_email             
      and contrasena_hash = p_pass    
      and estado_usuario = 1          
    limit 1; 
end //

create procedure sp_obtener_usuario_contactos(
    in p_id_usuario int
)
begin
    select uc.id_contacto
    from usuarios_contactos uc
    where uc.id_usuario = p_id_usuario;
end //