ALTER TABLE deliverychallan
    ADD COLUMN vehicleid character varying(255) DEFAULT NULL;

ALTER TABLE deliverychallan
    ADD CONSTRAINT fk_vehicle_id
        FOREIGN KEY (vehicleid)
            REFERENCES vehicles(id)
            ON DELETE RESTRICT;

ALTER TABLE deliverychallan
    ADD COLUMN driverid character varying(255) DEFAULT NULL;

ALTER TABLE deliverychallan
    ADD CONSTRAINT fk_driver_id
        FOREIGN KEY (driverid)
            REFERENCES driver(id)
            ON DELETE RESTRICT;

ALTER TABLE deliverychallan
    ADD COLUMN transportationcompanyid character varying(255) DEFAULT NULL;

ALTER TABLE deliverychallan
    ADD CONSTRAINT fk_transportation_company_id
        FOREIGN KEY (transportationcompanyid)
            REFERENCES transportationcompany(id)
            ON DELETE RESTRICT;