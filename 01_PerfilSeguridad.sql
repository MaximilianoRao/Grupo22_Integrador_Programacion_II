CREATE DATABASE IF NOT EXISTS  PerfilSeguridad;
use PerfilSeguridad;

create table CredencialAcceso(
id bigint auto_increment,
eliminado boolean default(false),
hashPassword varchar(255) not null,
salt varchar(64) not null,
ultimoCambio datetime default current_timestamp not null,
requiereReset boolean not null default(false),
primary key (id)
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
constraint fk_usuario_credencialacceso foreign key (credencial) references CredencialAcceso(id) on delete cascade on update cascade
);