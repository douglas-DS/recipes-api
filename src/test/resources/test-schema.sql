DROP TABLE IF EXISTS carts CASCADE;
CREATE TABLE carts(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    total_in_cents BIGINT NOT NULL
);

DROP TABLE IF EXISTS products CASCADE;
CREATE TABLE products(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_in_cents BIGINT NOT NULL
);


DROP TABLE IF EXISTS recipes CASCADE;
CREATE TABLE recipes(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS recipe_ingredients CASCADE;
CREATE TABLE recipe_ingredients(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    recipe_id INT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id),
    quantity DECIMAL NOT NULL,
    unit VARCHAR(20) NOT NULL,
    CHECK (unit IN ('g', 'kg', 'l', 'ml', 'tsp', 'tbsp', 'cup', 'pint', 'quart', 'gal', 'oz', 'lb'))
);

DROP TABLE IF EXISTS cart_items CASCADE;
CREATE TABLE cart_items(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    cart_id INT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id INT REFERENCES products(id),
    recipe_id INT REFERENCES recipes(id),
    CHECK (product_id IS NOT NULL OR recipe_id IS NOT NULL)
);