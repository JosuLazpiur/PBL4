CREATE USER 'admin'@'localhost' IDENTIFIED BY 'adminpass';
GRANT SELECT, INSERT, UPDATE, DELETE ON *.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;

CREATE USER 'lector'@'localhost' IDENTIFIED BY 'lectorpass';
GRANT SELECT ON *.* TO 'lector'@'localhost';
FLUSH PRIVILEGES;