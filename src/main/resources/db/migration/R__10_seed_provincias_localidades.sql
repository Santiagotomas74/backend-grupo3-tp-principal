-- set MUY minimo. idealmente hay que traer un csv publico con toda la data, pero excede el scope del TP. agregar tantos lugares a manopla acá como sean necesarios 
INSERT OR IGNORE INTO Provincia (id, nombre) VALUES
(1, 'Buenos Aires'),
(2, 'Córdoba'),
(3, 'Santa Fe');

INSERT OR IGNORE INTO Localidad (id, nombre, provincia_id) VALUES
(1, 'José C. Paz', 1),
(2, 'San Miguel', 1),
(3, 'La Plata', 1),

(4, 'Córdoba Capital', 2),
(5, 'Villa Carlos Paz', 2),

(6, 'Rosario', 3),
(7, 'Rafaela', 3);