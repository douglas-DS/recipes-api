------------------------------------------------------------------------------------------------------------------------
-- MOCK DATA
------------------------------------------------------------------------------------------------------------------------
-- Insert products (20 products)
INSERT INTO products (name, price_in_cents) VALUES
('Organic Flour 1kg', 450),
('Free Range Eggs (12 pack)', 650),
('Whole Milk 1L', 320),
('Unsalted Butter 250g', 480),
('Vanilla Extract 50ml', 890),
('Baking Powder 100g', 280),
('Sea Salt 500g', 350),
('Brown Sugar 1kg', 420),
('Olive Oil 500ml', 750),
('Fresh Basil 50g', 300),
('Mozzarella Cheese 200g', 580),
('Tomato Sauce 400g', 220),
('Pizza Dough 500g', 380),
('Ground Beef 500g', 980),
('Onion 1kg', 180),
('Garlic Bulb', 120),
('Bell Peppers 500g', 340),
('Pasta 500g', 250),
('Parmesan Cheese 100g', 680),
('Heavy Cream 250ml', 290);

-- Insert recipes (7 recipes)
INSERT INTO recipes (name) VALUES
('Classic Chocolate Chip Cookies'),
('Margherita Pizza'),
('Beef Bolognese Pasta'),
('Vanilla Cupcakes'),
('Herb Butter'),
('Simple Tomato Pasta'),
('Garlic Bread');

-- Insert recipe ingredients
-- Recipe 1: Classic Chocolate Chip Cookies
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES
(1, 1, 300, 'g'),    -- Flour
(1, 2, 2, 'pint'),   -- Eggs (using 2 eggs)
(1, 4, 125, 'g'),    -- Butter
(1, 8, 150, 'g'),    -- Brown Sugar
(1, 5, 5, 'ml'),     -- Vanilla Extract
(1, 6, 5, 'g'),      -- Baking Powder
(1, 7, 2, 'g');      -- Salt

-- Recipe 2: Margherita Pizza
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES
(2, 13, 500, 'g'),   -- Pizza Dough
(2, 12, 200, 'g'),   -- Tomato Sauce
(2, 11, 200, 'g'),   -- Mozzarella
(2, 10, 20, 'g'),    -- Fresh Basil
(2, 9, 15, 'ml');    -- Olive Oil

-- Recipe 3: Beef Bolognese Pasta
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES
(3, 14, 500, 'g'),   -- Ground Beef
(3, 15, 100, 'g'),   -- Onion
(3, 16, 2, 'pint'),  -- Garlic (2 cloves)
(3, 12, 400, 'g'),   -- Tomato Sauce
(3, 18, 500, 'g'),   -- Pasta
(3, 9, 30, 'ml');    -- Olive Oil

-- Recipe 4: Vanilla Cupcakes
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES
(4, 1, 200, 'g'),    -- Flour
(4, 2, 2, 'pint'),   -- Eggs
(4, 3, 125, 'ml'),   -- Milk
(4, 4, 100, 'g'),    -- Butter
(4, 8, 100, 'g'),    -- Sugar
(4, 5, 10, 'ml'),    -- Vanilla
(4, 6, 8, 'g');      -- Baking Powder

-- Recipe 5: Herb Butter
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES
(5, 4, 250, 'g'),    -- Butter
(5, 10, 30, 'g'),    -- Fresh Basil
(5, 16, 1, 'pint'),  -- Garlic
(5, 7, 3, 'g');      -- Salt

-- Recipe 6: Simple Tomato Pasta
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES
(6, 18, 500, 'g'),   -- Pasta
(6, 12, 400, 'g'),   -- Tomato Sauce
(6, 16, 2, 'pint'),  -- Garlic
(6, 9, 25, 'ml'),    -- Olive Oil
(6, 19, 50, 'g');    -- Parmesan

-- Recipe 7: Garlic Bread
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES
(7, 4, 100, 'g'),    -- Butter
(7, 16, 3, 'pint'),  -- Garlic
(7, 10, 10, 'g');    -- Fresh Basil

-- Insert carts (5 carts with calculated totals)
INSERT INTO carts (total_in_cents) VALUES
(0),  -- Cart 1
(0),  -- Cart 2
(0),  -- Cart 3
(0),     -- Cart 4 (empty)
(0);  -- Cart 5

-- Insert cart items
-- Cart 1: 3 items (1 recipe + 2 products)
INSERT INTO cart_items (cart_id, recipe_id, product_id) VALUES
(1, 1, NULL),        -- Chocolate Chip Cookies recipe
(1, NULL, 3),        -- Milk
(1, NULL, 5);        -- Vanilla Extract

UPDATE carts SET total_in_cents = 4730 WHERE id = 1;

-- Cart 2: 4 items (2 recipes + 2 products)
INSERT INTO cart_items (cart_id, recipe_id, product_id) VALUES
(2, 2, NULL),        -- Margherita Pizza recipe
(2, 3, NULL),        -- Beef Bolognese recipe
(2, NULL, 20),       -- Heavy Cream
(2, NULL, 19);       -- Parmesan Cheese

UPDATE carts SET total_in_cents = 5700 WHERE id = 2;

-- Cart 3: 2 items (1 recipe + 1 product)
INSERT INTO cart_items (cart_id, recipe_id, product_id) VALUES
(3, 4, NULL),        -- Vanilla Cupcakes recipe
(3, NULL, 17);       -- Bell Peppers

UPDATE carts SET total_in_cents = 3830 WHERE id = 3;

-- Cart 4: 0 items (empty cart)

-- Cart 5: 5 items (3 recipes + 2 products)
INSERT INTO cart_items (cart_id, recipe_id, product_id) VALUES
(5, 5, NULL),        -- Herb Butter recipe
(5, 6, NULL),        -- Simple Tomato Pasta recipe
(5, 7, NULL),        -- Garlic Bread recipe
(5, NULL, 15),       -- Onion
(5, NULL, 14);       -- Ground Beef

UPDATE carts SET total_in_cents = 5330 WHERE id = 5;