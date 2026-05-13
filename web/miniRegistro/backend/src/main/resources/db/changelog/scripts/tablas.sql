-- liquibase formatted sql

-- changeset luisdavid:1
-- ==========================================
-- FASE 1: TABLAS MAESTRAS E INDEPENDIENTES
-- ==========================================

create table politicas_contrasenas (
    id_politica int primary key auto_increment,
    nombre_politica varchar(100) unique not null,
    minlongitud int default 8,
    maxlongitud int default 20,
    requiere_mayusculas tinyint(1) default 1,
    requiere_numeros tinyint(1) default 1,
    requiere_simbolos tinyint(1) default 1,
    caducidad_dias int default 90,
    esta_activa tinyint(1) default 1
);

create table permisos (
    id_permiso int primary key auto_increment,
    nombre_permiso varchar(50) unique not null,
    descripcion varchar(255)
);

create table configuraciones_seguridad (
    id_configuracion int primary key auto_increment,
    nombre_configuracion varchar(100) unique not null,
    valor_configuracion varchar(100),
    descripcion varchar(255)
);

create table unidades_respuestas (
    id_unidad int primary key auto_increment,
    nombre_unidad varchar(250) not null,
    tipo_unidad varchar(150) not null,
    telefono varchar(30) not null,
    email varchar(150)
);

create table usuarios (
    id_usuario int primary key auto_increment,
    nombre varchar(255) not null,
    email varchar(150) unique,
    contrasena_hash varchar(255) not null,
    fecha_nacimiento datetime,
    estado_usuario tinyint(1) default 1,
    fecha_creacion datetime default current_timestamp,
    fecha_actualizacion datetime default current_timestamp on update current_timestamp,
    ultimo_acceso datetime
);


-- changeset luisdavid:2
-- ==========================================
-- FASE 2: TABLAS CON DEPENDENCIAS DIRECTAS
-- ==========================================

create table roles (
    id_rol int primary key auto_increment,
    nombre_rol varchar(50) unique not null,
    descripcion varchar(255),
    id_politica int null,
    foreign key (id_politica) references politicas_contrasenas(id_politica)
);

create table alertas (
    id_alerta int primary key auto_increment,
    id_usuario int not null,
    fecha datetime default current_timestamp,
    mensaje varchar(250) not null,
    estado_alerta varchar(50) default 'activa',
    foreign key (id_usuario) references usuarios(id_usuario)
);

create table dispositivos (
    id_dispositivo int primary key auto_increment,
    id_usuario int not null,
    modelo_dispositivo varchar(100),
    os_version varchar(50),
    token_fcm varchar(255),
    notificaciones_activas tinyint(1) default 1,
    fecha_registro datetime default current_timestamp,
    foreign key (id_usuario) references usuarios(id_usuario) on delete cascade
);

create table log_errores (
    id_error int primary key auto_increment,
    fecha datetime default current_timestamp,
    id_usuario int,
    tipoerror varchar(100),
    descripcion varchar(500),
    ip_origen varchar(50),
    foreign key (id_usuario) references usuarios(id_usuario)
);


-- changeset luisdavid:3
-- ==========================================
-- FASE 3: TRIÁNGULO DE SEGURIDAD Y TABLAS INTERMEDIAS
-- ==========================================

create table evidencias (
    id_evidencia int primary key auto_increment,
    id_alerta int not null,
    url varchar(255) not null,
    tipo varchar(50) not null,
    fecha_envio datetime default current_timestamp,
    foreign key (id_alerta) references alertas(id_alerta) on delete cascade
);

create table sesion_usuarios (
    id_sesion int primary key auto_increment,
    id_usuario int not null,
    id_dispositivo int not null,
    fechainicio datetime default current_timestamp,
    fechafin datetime null,
    ip_origen varchar(50),
    estadosesion varchar(50) default 'activa',
    foreign key (id_usuario) references usuarios(id_usuario) on delete cascade,
    foreign key (id_dispositivo) references dispositivos(id_dispositivo) on delete cascade
);

create table ubicaciones (
    id_ubicacion bigint primary key auto_increment,
    id_alerta int not null,
    latitud decimal(10, 7) not null,
    longitud decimal(10, 7) not null,
    velocidad decimal(5, 2) null,
    precision_gps decimal(5, 2) null,
    fecha_hora_registro datetime default current_timestamp,
    foreign key (id_alerta) references alertas(id_alerta) on delete cascade
);

create table usuarios_roles (
    id_ur int primary key auto_increment,
    id_usuario int not null,
    id_rol int not null,
    fechaasignacion datetime default current_timestamp,
    foreign key (id_usuario) references usuarios(id_usuario),
    foreign key (id_rol) references roles(id_rol)
);

create table roles_permisos (
    id_rp int primary key auto_increment,
    id_rol int not null,
    id_permiso int not null,
    fechaasignacion datetime default current_timestamp,
    foreign key (id_rol) references roles(id_rol) on delete cascade,
    foreign key (id_permiso) references permisos(id_permiso) on delete cascade
);

create table usuarios_configuraciones (
    id_uc int primary key auto_increment,
    id_usuario int not null,
    id_configuracion int not null,
    valor_usuario varchar(100) not null,
    foreign key (id_usuario) references usuarios(id_usuario),
    foreign key (id_configuracion) references configuraciones_seguridad(id_configuracion),
    unique key uq_usuario_config (id_usuario, id_configuracion)
);

create table eleccion_unidades (
    id_eu int primary key auto_increment,
    id_usuario int not null,
    id_unidad int not null,
    foreign key (id_usuario) references usuarios(id_usuario),
    foreign key (id_unidad) references unidades_respuestas(id_unidad)
);

create table usuarios_contactos (
    id_uc int primary key auto_increment,
    id_usuario int not null,
    id_contacto int not null,
    foreign key (id_usuario) references usuarios(id_usuario),
    foreign key (id_contacto) references usuarios(id_usuario)
);