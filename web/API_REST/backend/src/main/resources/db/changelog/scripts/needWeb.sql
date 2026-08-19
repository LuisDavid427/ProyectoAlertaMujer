--liquibase formatted sql

--changeset luis_david:1
-- 1. insertar el rol de administrador
insert into roles (nombre_rol, descripcion) 
values ('administrador', 'acceso total al panel de gestion de alerta mujer');

-- 2. insertar permisos de ejemplo
insert into permisos (nombre_permiso, descripcion)
values 
('acceso_panel', 'permite entrar al dashboard administrativo'),
('gestion_alertas', 'permite visualizar y gestionar alertas sos');

-- 3. vincular rol con permisos
insert into roles_permisos (id_rol, id_permiso) values (1, 1), (1, 2);

-- 4. insertar el usuario administrador
insert into usuarios (nombre, email, contrasena_hash, fecha_nacimiento, estado_usuario)
values ('luis david conde sanchez', 'luisdavidconde@gmail.com', 'admin', '2007-07-08', 1);

-- 5. asignar el rol de administrador
insert into usuarios_roles (id_usuario, id_rol)
values (1, 2);

--changeset luis_david:2 enddelimiter://
-- 6. crear el procedimiento almacenado para login admin
create procedure sp_validar_login_admin(
    in p_email varchar(150),
    in p_pass varchar(255)
)
begin
    select 
        u.id_usuario,
        u.nombre,
        u.email
    from usuarios u
    inner join usuarios_roles ur on u.id_usuario = ur.id_usuario
    inner join roles r on ur.id_rol = r.id_rol
    where u.email = p_email             
      and u.contrasena_hash = p_pass    
      and r.nombre_rol = 'administrador' 
      and u.estado_usuario = 1          
    limit 1;
end //

create procedure sp_obtener_tokens_protectores(
    in p_id_victima int
)
begin
    select d.token_fcm
    from dispositivos d
    join usuarios_contactos uc on d.id_usuario = uc.id_contacto
    where uc.id_usuario = p_id_victima 
      and d.notificaciones_activas = 1;
end //

-- sp para usuarios: lista todo y permite busqueda por nombre, email o id
create procedure sp_listar_usuarios_dashboard(in p_busqueda varchar(100))
begin
    select id_usuario, nombre, email, estado_usuario as activo 
    from usuarios
    where (p_busqueda is null or p_busqueda = '' 
       or nombre like concat('%', p_busqueda, '%') 
       or email like concat('%', p_busqueda, '%') 
       or id_usuario = cast(p_busqueda as unsigned))
    order by fecha_creacion desc;
end //

-- sp para alertas: lista todo con el nombre de la victima y filtros
create procedure sp_listar_alertas_dashboard(in p_busqueda varchar(100))
begin
    select a.id_alerta, u.nombre as nombre_victima, a.mensaje, a.estado_alerta, a.fecha
    from alertas a
    join usuarios u on a.id_usuario = u.id_usuario
    where (p_busqueda is null or p_busqueda = '' 
       or u.nombre like concat('%', p_busqueda, '%') 
       or a.mensaje like concat('%', p_busqueda, '%') 
       or a.id_alerta = cast(p_busqueda as unsigned))
    order by a.fecha desc;
end //


--changeset luis_david:3 enddelimiter://
drop procedure if exists sp_listar_usuarios_dashboard //
create procedure sp_listar_usuarios_dashboard(in p_busqueda varchar(100))
begin
    set p_busqueda = ifnull(trim(p_busqueda), '');

    select id_usuario, nombre, email, estado_usuario as activo 
    from usuarios
    where p_busqueda = '' 
       or nombre like concat('%', p_busqueda, '%') 
       or email like concat('%', p_busqueda, '%') 
       or (p_busqueda regexp '^[0-9]+$' and id_usuario = cast(p_busqueda as unsigned))
    order by fecha_creacion desc;
end //

drop procedure if exists sp_listar_alertas_dashboard //
create procedure sp_listar_alertas_dashboard(in p_busqueda varchar(100))
begin
    set p_busqueda = ifnull(trim(p_busqueda), '');

    select a.id_alerta, u.nombre as nombre_victima, a.mensaje, a.estado_alerta, a.fecha
    from alertas a
    join usuarios u on a.id_usuario = u.id_usuario
    where p_busqueda = '' 
       or u.nombre like concat('%', p_busqueda, '%') 
       or a.mensaje like concat('%', p_busqueda, '%') 
       or a.estado_alerta like concat('%', p_busqueda, '%')
       or (p_busqueda regexp '^[0-9]+$' and a.id_alerta = cast(p_busqueda as unsigned))
    order by a.fecha desc;
end //