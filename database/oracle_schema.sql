-- CUSTOMERS
CREATE TABLE customers (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    phone VARCHAR2(15),
    email VARCHAR2(100)
);

-- ROOMS
CREATE TABLE rooms (
    room_id NUMBER PRIMARY KEY,
    type VARCHAR2(50),
    price NUMBER,
    status VARCHAR2(20)
);

-- BOOKINGS
CREATE TABLE bookings (
    booking_id NUMBER PRIMARY KEY,
    customer_id NUMBER,
    room_id NUMBER,
    number_of_people NUMBER DEFAULT 1,
    check_in DATE,
    check_out DATE,
    total_amount NUMBER,
    payment_status VARCHAR2(20),
    payment_id VARCHAR2(100),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_room FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE SEQUENCE cust_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE book_seq START WITH 1 INCREMENT BY 1;

-- CUSTOMER AUTO ID
CREATE OR REPLACE TRIGGER cust_trigger
BEFORE INSERT ON customers
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT cust_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
END;
/

-- BOOKING AUTO ID
CREATE OR REPLACE TRIGGER book_trigger
BEFORE INSERT ON bookings
FOR EACH ROW
BEGIN
    IF :NEW.booking_id IS NULL THEN
        SELECT book_seq.NEXTVAL INTO :NEW.booking_id FROM dual;
    END IF;
END;
/

INSERT INTO rooms VALUES (101, 'Single', 1500, 'Available');
INSERT INTO rooms VALUES (102, 'Single', 1500, 'Available');
INSERT INTO rooms VALUES (201, 'Double', 2500, 'Available');
INSERT INTO rooms VALUES (202, 'Double', 2500, 'Available');
INSERT INTO rooms VALUES (301, 'Deluxe', 4000, 'Available');
INSERT INTO rooms VALUES (401, 'Suite', 6500, 'Available');

COMMIT;

CREATE TABLE hotels (
    hotel_id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    location VARCHAR2(100),
    rating NUMBER(2,1)
);

CREATE TABLE hotel_admin (
    id NUMBER PRIMARY KEY,
    username VARCHAR2(50),
    password VARCHAR2(50),
    hotel_id NUMBER
);

ALTER TABLE rooms ADD hotel_id NUMBER;
ALTER TABLE bookings ADD hotel_id NUMBER;

ALTER TABLE hotels RENAME COLUMN name TO hotel_name;
ALTER TABLE hotels RENAME COLUMN location TO address;
ALTER TABLE hotel_admin RENAME COLUMN id TO admin_id;

INSERT INTO hotels (hotel_id, hotel_name, address, rating) VALUES (1, 'Taj', 'Mumbai', 4.6);
INSERT INTO hotels (hotel_id, hotel_name, address, rating) VALUES (2, 'Oberoi', 'Delhi', 4.4);

INSERT INTO hotel_admin VALUES (1, 'taj', '123', 1);
INSERT INTO hotel_admin VALUES (2, 'oberoi', '123', 2);

COMMIT;

UPDATE rooms SET hotel_id = 1 WHERE room_id IN (101, 102);
UPDATE rooms SET hotel_id = 2 WHERE room_id IN (201, 202);

COMMIT;

ALTER TABLE rooms
ADD CONSTRAINT fk_rooms_hotel
FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id);

ALTER TABLE bookings
ADD CONSTRAINT fk_bookings_hotel
FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id);

UPDATE bookings b
SET hotel_id = (
    SELECT r.hotel_id FROM rooms r
    WHERE r.room_id = b.room_id
);

ALTER TABLE hotel_admin
ADD CONSTRAINT fk_admin_hotel
FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id);

UPDATE rooms SET hotel_id = 1 WHERE room_id = 301;
UPDATE rooms SET hotel_id = 2 WHERE room_id = 401;

COMMIT;

CREATE TABLE users (
    user_id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    email VARCHAR2(100) UNIQUE,
    password VARCHAR2(100)
);

CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER user_trigger
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF :NEW.user_id IS NULL THEN
        SELECT user_seq.NEXTVAL INTO :NEW.user_id FROM dual;
    END IF;
END;
/

ALTER TABLE bookings ADD user_id NUMBER;

ALTER TABLE bookings
ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

INSERT INTO users (name, email, password)
VALUES ('Vikas', 'vikas@gmail.com', '123');

COMMIT;

SELECT * FROM customers ORDER BY id DESC;
SELECT * FROM bookings ORDER BY booking_id DESC;
SELECT room_id, status FROM rooms ORDER BY room_id;
SELECT * FROM users;
SELECT * FROM hotels;
SELECT * FROM rooms;
SELECT * FROM hotel_admin;
SELECT * FROM bookings;
