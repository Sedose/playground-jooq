CREATE
    TABLE
        manager(
            manager_id INTEGER NOT NULL,
            name VARCHAR(100) NOT NULL,
            salary INTEGER NOT NULL,
            CONSTRAINT pk_manager PRIMARY KEY(manager_id)
        );

CREATE
    TABLE
        employee(
            employee_id INTEGER NOT NULL,
            name VARCHAR(100) NOT NULL,
            salary INTEGER NOT NULL,
            manager_id INTEGER,
            CONSTRAINT pk_employee PRIMARY KEY(employee_id),
            CONSTRAINT fk_employee_manager_id FOREIGN KEY(manager_id) REFERENCES manager(manager_id)
        );

INSERT
    INTO
        manager(
            manager_id,
            name,
            salary
        )
    VALUES(
        1,
        'Sam',
        60000
    ),
    (
        2,
        'Max',
        90000
    );

INSERT
    INTO
        employee(
            employee_id,
            name,
            salary,
            manager_id
        )
    VALUES(
        1,
        'Joe',
        70000,
        1
    ),
    (
        2,
        'Henry',
        80000,
        2
    );
