INSERT INTO tarifa (numero_vueltas, tiempo_maximo_minutos, duracion_total_minutos, precio)
VALUES 
    (10, 10, 30, 15000),
    (15, 15, 35, 20000),
    (20, 20, 40, 25000);


INSERT INTO kart (modelo, codigo) VALUES
('Sodikart RT8', 'K001'),
('Sodikart RT8', 'K002'),
('Sodikart RT8', 'K003'),
('Sodikart RT8', 'K004'),
('Sodikart RT8', 'K005'),
('Sodikart RT8', 'K006'),
('Sodikart RT8', 'K007'),
('Sodikart RT8', 'K008'),
('Sodikart RT8', 'K009'),
('Sodikart RT8', 'K010'),
('Sodikart RT8', 'K011'),
('Sodikart RT8', 'K012'),
('Sodikart RT8', 'K013'),
('Sodikart RT8', 'K014'),
('Sodikart RT8', 'K015');


INSERT INTO descuento_por_cantidad (min_personas, max_personas, porcentaje) VALUES
(1, 2, 0),
(3, 5, 10),
(6, 10, 20),
(11, 15, 30);

INSERT INTO descuento_frecuente (min_visitas, max_visitas, porcentaje) VALUES
(7, 9999, 30), 
(5, 6, 20),
(2, 4, 10),
(0, 1, 0);



INSERT INTO cliente (rut, nombre, email, telefono, fecha_nacimiento) VALUES
('11.111.111-1', 'Juan Pérez', 'juan.perez@example.com', '+56912345678', '1990-05-12'),
('22.222.222-2', 'María González', 'maria.gonzalez@example.com', '+56923456789', '1985-09-18'),
('33.333.333-3', 'Pedro Soto', 'pedro.soto@example.com', '+56934567890', '2000-12-25'),
('44.444.444-4', 'Ana Rojas', 'ana.rojas@example.com', '+56945678901', '1995-07-16'),
('55.555.555-5', 'Luis Fernández', 'luis.fernandez@example.com', '+56956789012', '1988-03-29'),
('66.666.666-6', 'Claudia Muñoz', 'claudia.munoz@example.com', '+56967890123', '1992-08-15'),
('77.777.777-7', 'Carlos Díaz', 'carlos.diaz@example.com', '+56978901234', '1979-10-12'),
('88.888.888-8', 'Fernanda Salas', 'fernanda.salas@example.com', '+56989012345', '1999-01-01'),
('99.999.999-9', 'Diego Herrera', 'diego.herrera@example.com', '+56990123456', '2002-11-01'),
('10.101.010-0', 'Isabel Castillo', 'isabel.castillo@example.com', '+56901234567', '1983-06-29'),
('11.111.111-2', 'Martín Torres', 'martin.torres@example.com', '+56911111111', '1997-02-20'),
('12.121.212-3', 'Sofía Ramírez', 'sofia.ramirez@example.com', '+56922222222', '1993-11-05'),
('13.131.313-4', 'Javier Morales', 'javier.morales@example.com', '+56933333333', '1980-04-10'),
('14.141.414-5', 'Valentina Rivas', 'valentina.rivas@example.com', '+56944444444', '2001-09-30'),
('15.151.515-6', 'Andrés Guzmán', 'andres.guzman@example.com', '+56955555555', '1975-01-22'),
('16.161.616-7', 'Laura Sánchez', 'fondo123456789.5@gmail.com', '+56966666666', '1990-07-07');

INSERT INTO tarifa_esp (fecha_especial) VALUES 
    ('2025-01-01'),  -- Año Nuevo
    ('2025-03-28'),  -- Viernes Santo
    ('2025-03-29'),  -- Sábado Santo
    ('2025-05-01'),  -- Día del Trabajador
    ('2025-05-21'),  -- Día de las Glorias Navales
    ('2025-06-29'),  -- San Pedro y San Pablo
    ('2025-07-16'),  -- Virgen del Carmen
    ('2025-08-15'),  -- Asunción de la Virgen
    ('2025-09-18'),  -- Independencia Nacional
    ('2025-09-19'),  -- Día de las Glorias del Ejército
    ('2025-10-12'),  -- Encuentro de Dos Mundos
    ('2025-10-31'),  -- Día de las Iglesias Evangélicas
    ('2025-11-01'),  -- Día de Todos los Santos
    ('2025-12-08'),  -- Inmaculada Concepción
    ('2025-12-25');  -- Navidad
