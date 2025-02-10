-- Create users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(30) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    failed_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    last_login TIMESTAMP
);

-- Create sessions table
CREATE TABLE sessions (
    session_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    token VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT true
);

-- Create emails table
CREATE TABLE emails (
    email_id SERIAL PRIMARY KEY,
    from_user VARCHAR(30) REFERENCES users(username),
    to_user VARCHAR(30) REFERENCES users(username),
    subject VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    sent_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SENT', 'DRAFT', 'DELETED')),
    is_read BOOLEAN DEFAULT false
);

-- Create username_history table
CREATE TABLE username_history (
    history_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    old_username VARCHAR(30) NOT NULL,
    new_username VARCHAR(30) NOT NULL,
    changed_at TIMESTAMP NOT NULL
);

-- Create indexes for performance
CREATE INDEX idx_username_lower ON users (LOWER(username));
CREATE INDEX idx_sessions_token ON sessions(token);
CREATE INDEX idx_sessions_user ON sessions(user_id);
CREATE INDEX idx_emails_from_user ON emails(from_user);
CREATE INDEX idx_emails_to_user ON emails(to_user);
CREATE INDEX idx_emails_sent_date ON emails(sent_date);
CREATE INDEX idx_emails_status ON emails(status);

-- Create function to auto-expire sessions
CREATE OR REPLACE FUNCTION cleanup_expired_sessions()
RETURNS void AS $$
BEGIN
    UPDATE sessions 
    SET is_active = false 
    WHERE expires_at < CURRENT_TIMESTAMP 
    AND is_active = true;
END;
$$ LANGUAGE plpgsql;

-- Create a scheduled job to run cleanup every hour
CREATE EXTENSION IF NOT EXISTS pg_cron;
SELECT cron.schedule('0 * * * *', 'SELECT cleanup_expired_sessions()');

-- Create function to prevent duplicate active sessions
CREATE OR REPLACE FUNCTION deactivate_old_sessions()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE sessions 
    SET is_active = false 
    WHERE user_id = NEW.user_id 
    AND session_id != NEW.session_id 
    AND is_active = true;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to deactivate old sessions when new one is created
CREATE TRIGGER deactivate_old_sessions_trigger
AFTER INSERT ON sessions
FOR EACH ROW
EXECUTE FUNCTION deactivate_old_sessions();

-- Create function to track username changes
CREATE OR REPLACE FUNCTION track_username_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.username != OLD.username THEN
        INSERT INTO username_history (
            user_id, 
            old_username, 
            new_username, 
            changed_at
        ) VALUES (
            OLD.user_id,
            OLD.username,
            NEW.username,
            CURRENT_TIMESTAMP
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for username change tracking
CREATE TRIGGER track_username_changes_trigger
BEFORE UPDATE ON users
FOR EACH ROW
WHEN (NEW.username IS DISTINCT FROM OLD.username)
EXECUTE FUNCTION track_username_changes();
