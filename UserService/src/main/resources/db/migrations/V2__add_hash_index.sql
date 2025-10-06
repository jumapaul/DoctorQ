
DROP INDEX IF EXISTS idx_email;

CREATE INDEX idx_email_hash ON doctorqusers USING HASH (email);