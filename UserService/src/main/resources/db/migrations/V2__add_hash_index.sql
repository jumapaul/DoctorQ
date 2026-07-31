
DROP INDEX IF EXISTS idx_email;

CREATE INDEX idx_email_hash ON postgres USING HASH (email);