ALTER TABLE users
    ALTER COLUMN country_code TYPE VARCHAR(2) USING TRIM(country_code);
