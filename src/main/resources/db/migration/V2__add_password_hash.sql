-- Agrega columna password_hash a usuarios para autenticación JWT
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
