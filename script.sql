PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS mesas;

CREATE TABLE mesas (
    id_mesa INTEGER PRIMARY KEY AUTOINCREMENT,
    numero INTEGER NOT NULL UNIQUE CHECK (numero > 0),
    capacidad INTEGER NOT NULL DEFAULT 4 CHECK (capacidad > 0),
    estado TEXT NOT NULL DEFAULT 'Disponible' CHECK (estado IN ('Disponible', 'Ocupada')),
    ubicacion TEXT NOT NULL DEFAULT 'Salón principal'
);

CREATE TABLE pedidos (
    id_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
    id_mesa INTEGER NOT NULL,
    platillo TEXT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    precio_unitario REAL NOT NULL CHECK (precio_unitario >= 0),
    subtotal REAL NOT NULL CHECK (subtotal >= 0),
    estado TEXT NOT NULL DEFAULT 'Pendiente' CHECK (estado IN ('Pendiente', 'Entregado')),
    pagado INTEGER NOT NULL DEFAULT 0 CHECK (pagado IN (0, 1)),
    metodo_pago TEXT CHECK (metodo_pago IS NULL OR metodo_pago IN ('Efectivo', 'Terminal')),
    fecha_hora TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_pago TEXT,
    CONSTRAINT fk_pedidos_mesa FOREIGN KEY (id_mesa)
        REFERENCES mesas (id_mesa)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_pedidos_mesa_pagado ON pedidos (id_mesa, pagado);

INSERT INTO mesas (numero, capacidad, estado, ubicacion) VALUES
(1, 4, 'Ocupada', 'Salón principal'),
(2, 2, 'Ocupada', 'Terraza'),
(3, 6, 'Ocupada', 'Salón principal'),
(4, 4, 'Disponible', 'Ventana'),
(5, 2, 'Disponible', 'Barra'),
(6, 4, 'Disponible', 'Salón principal'),
(7, 2, 'Disponible', 'Terraza'),
(8, 6, 'Disponible', 'Salón principal'),
(9, 4, 'Disponible', 'Ventana');

INSERT INTO pedidos (id_mesa, platillo, cantidad, precio_unitario, subtotal, estado, pagado) VALUES
((SELECT id_mesa FROM mesas WHERE numero = 1), 'Tacos al Pastor', 2, 85.00, 170.00, 'Entregado', 0),
((SELECT id_mesa FROM mesas WHERE numero = 1), 'Agua de Horchata', 2, 35.00, 70.00, 'Entregado', 0),
((SELECT id_mesa FROM mesas WHERE numero = 2), 'Enchiladas Verdes', 1, 120.00, 120.00, 'Pendiente', 0),
((SELECT id_mesa FROM mesas WHERE numero = 3), 'Pozole Rojo', 2, 110.00, 220.00, 'Entregado', 0),
((SELECT id_mesa FROM mesas WHERE numero = 3), 'Flan Napolitano', 1, 45.00, 45.00, 'Pendiente', 0);
