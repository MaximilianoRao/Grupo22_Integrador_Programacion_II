-- Usar la base de datos
USE PerfilSeguridad;

-- Limpiar tablas
DELETE FROM Usuarios;
DELETE FROM CredencialAcceso;

-- Reiniciar AUTO_INCREMENT (opcional)
ALTER TABLE CredencialAcceso AUTO_INCREMENT = 1;
ALTER TABLE Usuarios AUTO_INCREMENT = 1;


-- Limpiar tablas (opcional, útil para reiniciar datos)
DELETE FROM Usuarios;
DELETE FROM CredencialAcceso;

-- Insertar credenciales y capturar IDs
INSERT INTO CredencialAcceso (hashPassword, salt, requiereReset)
VALUES ('a94a8fe5ccb19ba61c4c0873d391e987982fbbd3', 'abc123salt', false);
SET @cred1 = LAST_INSERT_ID();

INSERT INTO CredencialAcceso (hashPassword, salt, requiereReset)
VALUES ('5f4dcc3b5aa765d61d8327deb882cf99', 'salt567', false);
SET @cred2 = LAST_INSERT_ID();

INSERT INTO CredencialAcceso (hashPassword, salt, requiereReset)
VALUES ('098f6bcd4621d373cade4e832627b4f6', 'salt890', true);
SET @cred3 = LAST_INSERT_ID();

-- Insertar usuarios usando las variables
INSERT INTO Usuarios (username, email, activo, credencial)
VALUES
('juanperez', 'juanperez@example.com', true, @cred1),
('maria_lopez', 'maria.lopez@example.com', false, @cred2),
('admin_user', 'admin@example.com', true, @cred3);





















































