INSERT
    INTO
        customer(
            email,
            full_name
        )
    VALUES(
        'john.doe@example.com',
        'John Doe'
    ),
    (
        'jane.smith@example.com',
        'Jane Smith'
    );

INSERT
    INTO
        category(name)
    VALUES('Books'),
    ('Electronics');

INSERT
    INTO
        product(
            name,
            description,
            price
        )
    VALUES(
        'Clean Code',
        'A book about writing cleaner code',
        29.99
    ),
    (
        'Smartphone XYZ',
        'A phone that does stuff',
        499.99
    );
