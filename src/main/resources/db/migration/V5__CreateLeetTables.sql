CREATE
    TABLE
        person(
            person_id INTEGER NOT NULL,
            last_name VARCHAR(100) NOT NULL,
            first_name VARCHAR(100) NOT NULL,
            CONSTRAINT pk_person PRIMARY KEY(person_id)
        );

CREATE
    TABLE
        address(
            address_id INTEGER NOT NULL,
            person_id INTEGER NOT NULL,
            city VARCHAR(100),
            state VARCHAR(100),
            CONSTRAINT pk_address PRIMARY KEY(address_id),
            CONSTRAINT fk_address_person FOREIGN KEY(person_id) REFERENCES person(person_id)
        );
