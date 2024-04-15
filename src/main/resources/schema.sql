CREATE TABLE IF NOT EXISTS USER_SQL
(
    uuid        UUID DEFAULT UUID() PRIMARY KEY,
    username    VARCHAR(250) NOT NULL,
    email       VARCHAR(250) UNIQUE,
    password    VARCHAR(250),
    role        VARCHAR(250)
    );