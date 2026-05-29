-- Seed data for FrigoBrain
-- Dates are relative to CURRENT_DATE so the demo data stays fresh

-- Food Items (10 items)
INSERT INTO food_items (name, category_id, quantity, unit, purchase_date, expiry_date, price, storage_location, is_consumed)
VALUES
('Broccoli', 'vegetables', 500.0, 'g', DATEADD('DAY', -7, CURRENT_DATE), DATEADD('DAY', 2, CURRENT_DATE), 3.99, 'fridge', false),
('Chicken Breast', 'meat', 1.0, 'kg', DATEADD('DAY', -3, CURRENT_DATE), DATEADD('DAY', 5, CURRENT_DATE), 8.99, 'fridge', false),
('Whole Milk', 'dairy', 2.0, 'L', DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', 3, CURRENT_DATE), 4.49, 'fridge', false),
('Free-Range Eggs', 'dairy', 12.0, 'pcs', DATEADD('DAY', -1, CURRENT_DATE), DATEADD('DAY', 20, CURRENT_DATE), 6.99, 'fridge', false),
('Red Apples', 'fruits', 2.0, 'kg', DATEADD('DAY', -7, CURRENT_DATE), DATEADD('DAY', 7, CURRENT_DATE), 5.99, 'fridge', false),
('Atlantic Salmon', 'seafood', 500.0, 'g', DATEADD('DAY', -2, CURRENT_DATE), DATEADD('DAY', 3, CURRENT_DATE), 12.99, 'fridge', false),
('Cheddar Cheese', 'dairy', 200.0, 'g', DATEADD('DAY', -10, CURRENT_DATE), DATEADD('DAY', 15, CURRENT_DATE), 7.49, 'fridge', false),
('Roma Tomatoes', 'vegetables', 500.0, 'g', DATEADD('DAY', -4, CURRENT_DATE), DATEADD('DAY', 5, CURRENT_DATE), 3.49, 'fridge', false),
('Greek Yogurt', 'dairy', 1.0, 'L', DATEADD('DAY', -6, CURRENT_DATE), DATEADD('DAY', 2, CURRENT_DATE), 5.99, 'fridge', false),
('Jasmine Rice', 'pantry', 5.0, 'kg', DATEADD('DAY', -30, CURRENT_DATE), DATEADD('DAY', 60, CURRENT_DATE), 10.99, 'pantry', false);

-- Recipes (10 recipes)
INSERT INTO recipes (name, cuisine_type, meal_type, difficulty, prep_time, cook_time, calories, protein, fat, carbs, instructions, tags)
VALUES
('番茄炒蛋 (Scrambled Eggs with Tomatoes)', '家常菜', 'lunch', 'easy', 10, 5, 220, 14.0, 16.0, 8.0,
 '1. Beat eggs with a pinch of salt.\n2. Cut tomatoes into wedges.\n3. Scramble eggs in hot oil until just set, remove.\n4. Stir-fry tomatoes until soft.\n5. Return eggs, mix briefly, season with salt and sugar.',
 'eggs,tomatoes,chinese,quick'),
('Kung Pao Chicken', '川菜', 'dinner', 'medium', 20, 15, 380, 28.0, 22.0, 18.0,
 '1. Dice chicken breast and marinate with soy sauce and cornstarch.\n2. Toast peanuts in a dry pan.\n3. Stir-fry chicken until golden.\n4. Add dried chilies and Sichuan peppercorns.\n5. Pour in sauce (soy, vinegar, sugar) and toss with peanuts.',
 'chicken,peanuts,chinese,spicy'),
('Grilled Salmon with Asparagus', '粤菜', 'dinner', 'medium', 10, 20, 420, 35.0, 24.0, 6.0,
 '1. Season salmon fillet with salt, pepper, and lemon juice.\n2. Trim asparagus and toss with olive oil.\n3. Grill salmon skin-side down for 4 min, flip.\n4. Grill asparagus alongside.\n5. Serve with a lemon butter drizzle.',
 'salmon,asparagus,western,healthy,grill'),
('Classic Caesar Salad', '轻食', 'lunch', 'easy', 15, 0, 280, 12.0, 20.0, 14.0,
 '1. Wash and chop romaine lettuce.\n2. Grate parmesan cheese.\n3. Make dressing: mix mayo, lemon juice, garlic, anchovy paste.\n4. Toss lettuce with dressing and croutons.\n5. Top with parmesan shavings.',
 'salad,lettuce,parmesan,western,no-cook'),
('Chicken and Vegetable Stir-fry', '家常菜', 'dinner', 'easy', 15, 10, 320, 30.0, 12.0, 22.0,
 '1. Slice chicken breast into thin strips.\n2. Chop broccoli, bell peppers, and carrots.\n3. Stir-fry chicken in hot wok until cooked.\n4. Add vegetables and stir-fry for 3 min.\n5. Add soy sauce and oyster sauce, toss well.',
 'chicken,vegetables,broccoli,stir-fry,chinese'),
('Hearty Vegetable Soup', '粤菜', 'lunch', 'easy', 10, 30, 180, 6.0, 4.0, 32.0,
 '1. Dice onions, carrots, celery, and tomatoes.\n2. Saute aromatics in olive oil.\n3. Add vegetable broth and bring to a boil.\n4. Add remaining vegetables and simmer 20 min.\n5. Season with herbs and serve.',
 'vegetables,soup,healthy,western,vegan'),
('Classic Omelette', '家常菜', 'breakfast', 'easy', 5, 10, 250, 18.0, 18.0, 2.0,
 '1. Whisk eggs with salt, pepper, and a splash of milk.\n2. Heat butter in a non-stick pan.\n3. Pour eggs and let set slightly.\n4. Add cheese and desired fillings.\n5. Fold omelette and slide onto plate.',
 'eggs,cheese,breakfast,western,quick'),
('蛋炒饭 (Egg Fried Rice)', '家常菜', 'lunch', 'easy', 10, 10, 380, 12.0, 14.0, 52.0,
 '1. Use day-old rice for best texture.\n2. Scramble eggs in hot oil, remove.\n3. Stir-fry diced vegetables and ham.\n4. Add rice and stir-fry, breaking clumps.\n5. Return eggs, season with soy sauce and spring onions.',
 'rice,eggs,chinese,fried-rice,leftovers'),
('Berry Fruit Smoothie', '轻食', 'breakfast', 'easy', 5, 0, 200, 8.0, 4.0, 36.0,
 '1. Add yogurt, mixed berries, and banana to blender.\n2. Pour in milk or juice.\n3. Add honey to taste.\n4. Blend until smooth.\n5. Pour into glass and enjoy.',
 'berries,yogurt,breakfast,drink,no-cook,quick'),
('Baked Chicken Thighs with Root Vegetables', '鲁菜', 'dinner', 'medium', 15, 45, 480, 38.0, 26.0, 28.0,
 '1. Season chicken thighs with herbs and garlic.\n2. Chop potatoes, carrots, and onions.\n3. Toss vegetables in olive oil.\n4. Arrange chicken and veggies on baking tray.\n5. Bake at 200C for 40-45 min until golden.',
 'chicken,vegetables,potatoes,baked,western');

-- Waste Records (sample data for statistics)
INSERT INTO waste_records (food_name, category_name, quantity, unit, estimated_cost, waste_reason, waste_date)
VALUES
('Lettuce', 'vegetables', 1.0, 'pcs', 2.49, 'Wilted before use', DATEADD('DAY', -30, CURRENT_DATE)),
('Milk', 'dairy', 0.5, 'L', 1.12, 'Expired', DATEADD('DAY', -25, CURRENT_DATE)),
('Strawberries', 'fruits', 250.0, 'g', 3.99, 'Moldy', DATEADD('DAY', -20, CURRENT_DATE)),
('Ground Beef', 'meat', 300.0, 'g', 4.50, 'Expired', DATEADD('DAY', -15, CURRENT_DATE)),
('Spinach', 'vegetables', 200.0, 'g', 2.99, 'Wilted', DATEADD('DAY', -10, CURRENT_DATE)),
('Yogurt', 'dairy', 200.0, 'ml', 1.50, 'Expired', DATEADD('DAY', -7, CURRENT_DATE)),
('Cherry Tomatoes', 'vegetables', 250.0, 'g', 2.99, 'Moldy', DATEADD('DAY', -5, CURRENT_DATE)),
('Bread', 'pantry', 0.5, 'loaf', 1.75, 'Stale', DATEADD('DAY', -3, CURRENT_DATE));

-- Nutrition Logs (sample data for last 7 days)
INSERT INTO nutrition_logs (log_date, meal_time, calories, protein, fat, carbs)
VALUES
(DATEADD('DAY', -6, CURRENT_DATE), 'breakfast', 350, 15.0, 12.0, 48.0),
(DATEADD('DAY', -6, CURRENT_DATE), 'lunch', 520, 28.0, 18.0, 62.0),
(DATEADD('DAY', -6, CURRENT_DATE), 'dinner', 680, 42.0, 24.0, 58.0),
(DATEADD('DAY', -5, CURRENT_DATE), 'breakfast', 280, 10.0, 8.0, 42.0),
(DATEADD('DAY', -5, CURRENT_DATE), 'lunch', 610, 32.0, 20.0, 68.0),
(DATEADD('DAY', -5, CURRENT_DATE), 'dinner', 550, 35.0, 18.0, 45.0),
(DATEADD('DAY', -4, CURRENT_DATE), 'breakfast', 420, 20.0, 16.0, 52.0),
(DATEADD('DAY', -4, CURRENT_DATE), 'lunch', 480, 25.0, 14.0, 58.0),
(DATEADD('DAY', -4, CURRENT_DATE), 'dinner', 720, 45.0, 28.0, 62.0),
(DATEADD('DAY', -3, CURRENT_DATE), 'breakfast', 310, 12.0, 10.0, 44.0),
(DATEADD('DAY', -3, CURRENT_DATE), 'lunch', 580, 30.0, 22.0, 55.0),
(DATEADD('DAY', -3, CURRENT_DATE), 'dinner', 640, 38.0, 20.0, 60.0),
(DATEADD('DAY', -2, CURRENT_DATE), 'breakfast', 380, 18.0, 14.0, 46.0),
(DATEADD('DAY', -2, CURRENT_DATE), 'lunch', 450, 22.0, 12.0, 64.0),
(DATEADD('DAY', -2, CURRENT_DATE), 'dinner', 590, 36.0, 22.0, 50.0),
(DATEADD('DAY', -1, CURRENT_DATE), 'breakfast', 330, 14.0, 10.0, 48.0),
(DATEADD('DAY', -1, CURRENT_DATE), 'lunch', 560, 28.0, 16.0, 70.0),
(DATEADD('DAY', -1, CURRENT_DATE), 'dinner', 700, 44.0, 26.0, 55.0);
