CREATE TABLE carts(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    total_in_cents BIGINT NOT NULL
);

CREATE TABLE products(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_in_cents BIGINT NOT NULL
);

CREATE TABLE recipes(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE recipe_ingredients(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    recipe_id INT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id),
    quantity DECIMAL NOT NULL,
    unit VARCHAR(20) NOT NULL,
    CHECK (unit IN ('g', 'kg', 'l', 'ml', 'tsp', 'tbsp', 'cup', 'pint', 'quart', 'gal', 'oz', 'lb'))
);

CREATE TABLE cart_items(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    cart_id INT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id INT REFERENCES products(id),
    recipe_id INT REFERENCES recipes(id),
    CHECK (product_id IS NOT NULL OR recipe_id IS NOT NULL)
);

CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_recipe_id ON cart_items(recipe_id);