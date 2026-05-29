CREATE TABLE IF NOT EXISTS food_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id VARCHAR(50) NOT NULL,
    quantity DOUBLE NOT NULL,
    unit VARCHAR(20) NOT NULL,
    purchase_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    storage_location VARCHAR(50),
    is_consumed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cuisine_type VARCHAR(50),
    meal_type VARCHAR(20),
    difficulty VARCHAR(10),
    prep_time INT,
    cook_time INT,
    calories INT,
    protein DOUBLE,
    fat DOUBLE,
    carbs DOUBLE,
    instructions TEXT,
    tags VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS waste_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_name VARCHAR(255) NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    quantity DOUBLE NOT NULL,
    unit VARCHAR(20) NOT NULL,
    estimated_cost DECIMAL(10, 2) NOT NULL DEFAULT 0,
    waste_reason VARCHAR(255),
    waste_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS nutrition_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_date DATE NOT NULL,
    meal_time VARCHAR(20) NOT NULL,
    calories INT NOT NULL DEFAULT 0,
    protein DOUBLE NOT NULL DEFAULT 0,
    fat DOUBLE NOT NULL DEFAULT 0,
    carbs DOUBLE NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
