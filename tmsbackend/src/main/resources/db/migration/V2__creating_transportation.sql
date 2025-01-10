CREATE TABLE TransportationCompany (
    id VARCHAR(255) PRIMARY KEY,
    companyName VARCHAR(255) NOT NULL,
    pointOfContact VARCHAR(255),
    contactNumber VARCHAR(20),
    email VARCHAR(255),
    addressLine1 VARCHAR(255),
    addressLine2 VARCHAR(255),
    state VARCHAR(255),
    city VARCHAR(255),
    pinCode VARCHAR(20),
    status VARCHAR(50)
);

-- Table for Vehicles
CREATE TABLE Vehicles (
    id VARCHAR(255) PRIMARY KEY,
    vehicleNumber VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    rcBookUrl VARCHAR(255),
    status VARCHAR(50),
    companyId VARCHAR(255), -- Foreign key to link to TransportationCompany
    FOREIGN KEY (companyId) REFERENCES TransportationCompany(id) ON DELETE CASCADE
);


-- Table for Drivers
CREATE TABLE Driver (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contactNumber VARCHAR(20),
    drivingLicenseUrl VARCHAR(255),
    status VARCHAR(50),
    companyId VARCHAR(255),  -- Foreign key to link to TransportationCompany
    FOREIGN KEY (companyId) REFERENCES TransportationCompany(id) ON DELETE CASCADE
);