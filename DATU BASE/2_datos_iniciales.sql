INSERT INTO provincias (nombre) VALUES
('Alava'),
('Guipuzcoa'),
('Vizcaya');

-- Zonas de Álava
INSERT INTO zonas (provinciaId, nombre) VALUES
(1, 'Cuadrilla_de_Vitoria'),
(1, 'Cuadrilla_de_Ayala'),
(1, 'Cuadrilla_de_Laguardia_Rioja_Alavesa'),
(1, 'Cuadrilla_de_la_Llanada_Alavesa'),
(1, 'Cuadrilla_de_Gorbeialdea'),
(1, 'Cuadrilla_de_Anana'),
(1, 'Cuadrilla_de_Campezo_Montaña_Alavesa');

-- Zonas de Guipúzcoa
INSERT INTO zonas (provinciaId, nombre) VALUES
(2, 'Bajo_Bidasoa'),
(2, 'Comarca_de_San_Sebastián'),
(2, 'Oarsoaldea'),
(2, 'Buruntzaldea'),
(2, 'Tolosaldea'),
(2, 'Goierri'),
(2, 'Urola_Costa'),
(2, 'El_Bajo_Deba'),
(2, 'El_Alto_Deba');

-- Zonas de Vizcaya
INSERT INTO	 zonas (provinciaId, nombre) VALUES
(3, 'Gran_Bilbao'),
(3, 'Duranguesado'),
(3, 'Lea_Artibai'),
(3, 'Busturialdea_Urdaibai'),
(3, 'Uribe'),
(3, 'Las_Encartaciones'),
(3, 'Arratia_Nervión');

INSERT INTO etiquetas (nombre) VALUES 
('verdura'),
('hortaliza'),
('fruta'),
('seta_hongo_comestible'),
('legumbre'),
('cereal'),
('pasta'),
('pan'),
('lacteo'),
('carne'),
('pescado'),
('huevo');