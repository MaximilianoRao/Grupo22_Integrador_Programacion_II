-- Usar la base de datos
USE PerfilSeguridad;

-- Limpiar tablas (opcional, útil para reiniciar datos)
DELETE FROM Usuarios;
DELETE FROM CredencialAcceso;

-- Insertar credenciales de acceso
INSERT INTO CredencialAcceso (hashPassword, salt, requiereReset)
VALUES
('a94a8fe5ccb19ba61c4c0873d391e987982fbbd3', 'abc123salt', false),
('5f4dcc3b5aa765d61d8327deb882cf99', 'salt567', false),
('098f6bcd4621d373cade4e832627b4f6', 'salt890', true);

-- Insertar usuarios (cada uno con su credencial 1→1)
INSERT INTO Usuarios (username, email, activo, credencial)
VALUES
('juanperez', 'juanperez@example.com', true, 1),
('maria_lopez', 'maria.lopez@example.com', false, 2),
('admin_user', 'admin@example.com', true, 3);





















































