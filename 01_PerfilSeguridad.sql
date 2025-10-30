CREATE DATABASE IF NOT EXISTS  PerfilSeguridad;
use PerfilSeguridad;

create table CredencialAcceso(
id bigint auto_increment,
eliminado boolean default(false),
hashPassword varchar(255) not null,
salt varchar(64) not null,
ultimoCambio datetime default current_timestamp not null,
requiereReset boolean not null default(false),
primary key (id),
constraint ck_credencial_eliminado check (eliminado in (0, 1)),
constraint ck_credencial_reset check (requiereReset in (0, 1))
);

create table Usuarios(
id bigint auto_increment,
eliminado boolean default(false) not null,
username varchar(30) unique not null,
email varchar(120) unique not null,
activo boolean not null default(false),
fechaRegistro datetime default current_timestamp not null,
credencial bigint unique not null,
primary key (id),
constraint ck_usuarios_email_formato check (email like '%_@_%._%'),
constraint ck_longitud_username check (char_length(username)>= 8),
constraint ck_usuarios_eliminado check (eliminado in (0, 1)),
constraint ck_usuarios_activo check (activo in (0, 1)),
constraint fk_usuario_credencialacceso foreign key (credencial) references CredencialAcceso(id) on delete cascade on update cascade
);