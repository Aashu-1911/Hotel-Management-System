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

ALTER TABLE bookings ADD number_of_people NUMBER DEFAULT 1;

ALTER TABLE hotels ADD rating NUMBER(2,1);

UPDATE hotels SET rating = 4.6 WHERE hotel_id = 1;
UPDATE hotels SET rating = 4.4 WHERE hotel_id = 2;
UPDATE hotels SET rating = 4.0 WHERE rating IS NULL;

UPDATE bookings SET number_of_people = 1 WHERE number_of_people IS NULL;

ALTER TABLE bookings
ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

UPDATE hotel_admin
SET username = 'ashish@gmail.com',
    password = '123456'
WHERE admin_id = 1;

COMMIT;
