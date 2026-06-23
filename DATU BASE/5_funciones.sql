
DELIMITER //

CREATE FUNCTION ticket_5_productos(p_ticketID SMALLINT UNSIGNED)
RETURNS TINYINT
DETERMINISTIC
BEGIN
    DECLARE total_productos INT;

    SELECT COUNT(*) INTO total_productos
    FROM cestas
    WHERE ticketId = p_ticketID;

    IF total_productos > 5 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
//

DELIMITER ;

DELIMITER $$

CREATE TRIGGER actualizar_media_valoracion
AFTER INSERT ON valoraciones
FOR EACH ROW
BEGIN
    UPDATE productos
    SET mediaEstrellas = (
        SELECT round(AVG(estrellas), 2)
        FROM valoraciones
        WHERE productoId = NEW.productoId
    )
    WHERE productoId = NEW.productoId;
END;
$$

DELIMITER ;