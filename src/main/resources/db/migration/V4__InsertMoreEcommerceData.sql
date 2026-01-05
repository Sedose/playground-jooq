WITH inserted_categories AS(
    INSERT
        INTO
            category(name)
        VALUES('Computers'),
        ('Games') RETURNING category_id,
        name
),
inserted_customer1 AS(
    INSERT
        INTO
            customer(
                email,
                full_name
            )
        VALUES(
            'alice@example.com',
            'Alice Wonderland'
        ) RETURNING customer_id
),
inserted_customer2 AS(
    INSERT
        INTO
            customer(
                email,
                full_name
            )
        VALUES(
            'bob@example.com',
            'Bob Builder'
        ) RETURNING customer_id
),
laptop_product AS(
    INSERT
        INTO
            product(
                name,
                description,
                price
            )
        VALUES(
            'Laptop 2025',
            'Powerful laptop for everyday tasks',
            999.99
        ) RETURNING product_id,
        price
),
gaming_console_product AS(
    INSERT
        INTO
            product(
                name,
                description,
                price
            )
        VALUES(
            'Game Console X',
            'Next generation gaming console',
            299.99
        ) RETURNING product_id,
        price
),
product_categories AS(
    INSERT
        INTO
            product_category(
                product_id,
                category_id
            ) SELECT
                laptop_product.product_id,
                (
                    SELECT
                        category_id
                    FROM
                        inserted_categories
                    WHERE
                        name = 'Computers'
                )
            FROM
                laptop_product
        UNION ALL SELECT
                gaming_console_product.product_id,
                (
                    SELECT
                        category_id
                    FROM
                        inserted_categories
                    WHERE
                        name = 'Games'
                )
            FROM
                gaming_console_product RETURNING 1
),
created_order AS(
    INSERT
        INTO
            customer_order(
                customer_id,
                total_amount
            ) SELECT
                inserted_customer1.customer_id,
                1299.98
            FROM
                inserted_customer1 RETURNING customer_order_id
),
order_items_1 AS(
    INSERT
        INTO
            order_item(
                customer_order_id,
                product_id,
                quantity,
                unit_price
            ) SELECT
                created_order.customer_order_id,
                laptop_product.product_id,
                1,
                laptop_product.price
            FROM
                created_order,
                laptop_product RETURNING 1
),
another_order AS(
    INSERT
        INTO
            customer_order(
                customer_id,
                total_amount
            ) SELECT
                inserted_customer2.customer_id,
                gaming_console_product.price
            FROM
                inserted_customer2,
                gaming_console_product RETURNING customer_order_id
),
order_items_2 AS(
    INSERT
        INTO
            order_item(
                customer_order_id,
                product_id,
                quantity,
                unit_price
            ) SELECT
                another_order.customer_order_id,
                gaming_console_product.product_id,
                1,
                gaming_console_product.price
            FROM
                another_order,
                gaming_console_product RETURNING 1
) SELECT
    (
        SELECT
            COUNT(*)
        FROM
            product_categories
    ),
    (
        SELECT
            COUNT(*)
        FROM
            order_items_1
    ),
    (
        SELECT
            COUNT(*)
        FROM
            order_items_2
    );
