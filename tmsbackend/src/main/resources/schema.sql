--CREATE TABLE District (
--    d_id VARCHAR(255) PRIMARY KEY,
--    name VARCHAR(255) NOT NULL
--);

--CREATE TABLE Taluka (
--    t_id VARCHAR(255) PRIMARY KEY,
--    name VARCHAR(255) NOT NULL,
--    district_id VARCHAR(255) NOT NULL REFERENCES tms.District(d_id) ON DELETE CASCADE
--);

--CREATE TABLE City (
--    c_id VARCHAR(255) PRIMARY KEY,
--    name VARCHAR(255) NOT NULL,
--    taluka_id VARCHAR(255) NOT NULL REFERENCES tms.Taluka(t_id) ON DELETE CASCADE
--);

CREATE TABLE Location (
    l_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    point_of_contact VARCHAR(255),
    district VARCHAR(255),
    taluka VARCHAR(255),
    city VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(15),
    pincode VARCHAR(10),
    address_1 VARCHAR(255),
    address_2 VARCHAR(255)
);

CREATE TABLE Party (
    p_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contactNumber VARCHAR(15),
    addressLine1 VARCHAR(255),
    addressLine2 VARCHAR(255),
    email VARCHAR(255),
    pointOfContact VARCHAR(255),
    pincode VARCHAR(10),
    state VARCHAR(100) NOT NULL,
    taluka VARCHAR(100),
    city VARCHAR(100) NOT NULL
);

CREATE TABLE employees (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contactNumber VARCHAR(15),
    role varchar(255) NOT NULL,
    createdAt BIGINT NOT NULL
)

CREATE TABLE materials (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    passwordHash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    refreshToken VARCHAR(255)
);


select * from users;