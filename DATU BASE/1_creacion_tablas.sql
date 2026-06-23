DROP DATABASE IF EXISTS GERTUKO;
CREATE DATABASE GERTUKO;
USE GERTUKO;

CREATE TABLE provincias (
    provinciaID SMALLINT UNSIGNED,
    nombre VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT pk_provincia PRIMARY KEY(provinciaID)
);

CREATE TABLE zonas (
    zonaID SMALLINT UNSIGNED,
    nombre VARCHAR(20) NOT NULL UNIQUE,
    provinciaID SMALLINT UNSIGNED,
 
    CONSTRAINT pk_zona PRIMARY KEY(zonaID),
    CONSTRAINT fk_provincia FOREIGN KEY(provinciaID)
        REFERENCES provincias(provinciaID)
);

CREATE TABLE etiqueta (
    etiquetaID SMALLINT UNSIGNED,
    nombre VARCHAR(30) NOT NULL UNIQUE,
    CONSTRAINT pk_etiqueta PRIMARY KEY(etiquetaID)
);

CREATE TABLE usuarios ( 
    usuarioID SMALLINT UNSIGNED,
    nombre_usuario VARCHAR(20) NOT NULL UNIQUE,
    contraseña VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(20) NOT NULL,
    apellido VARCHAR(20) NOT NULL,
    descripcion VARCHAR(20),
    tipo BOOLEAN NOT NULL,
    zonaID SMALLINT UNSIGNED,
    CONSTRAINT fk_zona FOREIGN KEY(zonaID)
        REFERENCES zonas(zonaID),
    CONSTRAINT pk_usuario PRIMARY KEY(usuarioID)
);

CREATE TABLE productos (
    productoID SMALLINT UNSIGNED,
    nombre VARCHAR(20) NOT NULL,
    descripcion VARCHAR(100),
    img_src VARCHAR(40),
    precio DECIMAL(4,2) NOT NULL,
    disponibilidad TINYINT,
    usuarioID SMALLINT UNSIGNED,

    CONSTRAINT pk_producto PRIMARY KEY(productoID),
    CONSTRAINT fk_usuario_producto FOREIGN KEY(usuarioID)
        REFERENCES usuarios(usuarioID)
);

CREATE TABLE relacion_etiqueta_producto (
    etiquetaID SMALLINT UNSIGNED,
    productoID SMALLINT UNSIGNED,

    CONSTRAINT pk_relacion_etiqueta_producto PRIMARY KEY(etiquetaID,productoID),
    CONSTRAINT fk_etiqueta FOREIGN KEY(etiquetaID)
        REFERENCES etiqueta(etiquetaID),
    CONSTRAINT fk_producto FOREIGN KEY(productoID)
        REFERENCES productos(productoID)
);


CREATE TABLE valoraciones (
    valoracionID SMALLINT UNSIGNED,
    puntuacion DECIMAL(2,1),
    titulo VARCHAR(40),
    descripcion VARCHAR(300),
    img_src VARCHAR(40),
    productoID SMALLINT UNSIGNED,
    usuarioID SMALLINT UNSIGNED,

    CONSTRAINT pk_valoracion PRIMARY KEY(valoracionID),
    CONSTRAINT fk_usuario_valoracion FOREIGN KEY(usuarioID)
        REFERENCES usuarios(usuarioID),
    CONSTRAINT fk_producto_valoracion FOREIGN KEY(productoID)
        REFERENCES productos(productoID)
);
ALTER TABLE valoraciones
ADD CONSTRAINT check_puntuacion
CHECK (puntuacion IN (0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0));


CREATE TABLE tickets (
    ticketID SMALLINT UNSIGNED,
    fecha DATE,
    compradorID SMALLINT UNSIGNED,
    vendedorID SMALLINT UNSIGNED,

    CONSTRAINT pk_ticket PRIMARY KEY(ticketID),
    CONSTRAINT fk_comprador FOREIGN KEY(compradorID)
        REFERENCES usuarios(usuarioID),
    CONSTRAINT fk_vendedor FOREIGN KEY(vendedorID)
        REFERENCES usuarios(usuarioID)
);

CREATE TABLE cesta (
    ticketID SMALLINT UNSIGNED,
    productoID SMALLINT UNSIGNED,

    CONSTRAINT fk_ticket_linea FOREIGN KEY (ticketID)
        REFERENCES tickets(ticketID),
    CONSTRAINT fk_producto_linea FOREIGN KEY (productoID)
        REFERENCES productos(productoID),
    CONSTRAINT pk_linea PRIMARY KEY(ticketID, productoID)
);


CREATE TABLE chats (
    chatID SMALLINT UNSIGNED,
    vendedorID SMALLINT UNSIGNED,
    compradorID SMALLINT UNSIGNED,

    CONSTRAINT pk_mensaje PRIMARY KEY(chatID),
    CONSTRAINT fk_vendedor_chat FOREIGN KEY(vendedorID)
        REFERENCES usuarios(usuarioID),
    CONSTRAINT fk_comprador_chat FOREIGN KEY(compradorID) 
        REFERENCES usuarios(usuarioID)
);

CREATE TABLE mensajes (
    mensajeID SMALLINT UNSIGNED,
    fecha DATE,
    emisorID SMALLINT UNSIGNED,
    chatID SMALLINT UNSIGNED,

    CONSTRAINT pk_mensaje PRIMARY KEY(mensajeID),
    CONSTRAINT fk_emisor FOREIGN KEY(emisorID)
        REFERENCES usuarios(usuarioID),
    CONSTRAINT fk_chat FOREIGN KEY(chatID)
        REFERENCES chats(chatID)
);

