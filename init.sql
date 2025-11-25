-- Database initialization script for dropout prevention system
-- Run automatically when PostgreSQL container starts

-- Create interventions table
CREATE TABLE IF NOT EXISTS interventions (
    id SERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    intervention_type VARCHAR(100) NOT NULL,
    intervention_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    assigned_to VARCHAR(100),
    status VARCHAR(50) DEFAULT 'pending',
    district VARCHAR(100),
    risk_score DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create audit logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id SERIAL PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    user_id VARCHAR(100),
    student_id VARCHAR(50),
    details JSONB,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create student risk history table (for tracking changes over time)
CREATE TABLE IF NOT EXISTS risk_history (
    id SERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    risk_score DECIMAL(5,2) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    factors JSONB,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create district statistics cache table
CREATE TABLE IF NOT EXISTS district_stats (
    district VARCHAR(100) PRIMARY KEY,
    total_students INTEGER DEFAULT 0,
    high_risk_count INTEGER DEFAULT 0,
    moderate_risk_count INTEGER DEFAULT 0,
    low_risk_count INTEGER DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_interventions_student_id ON interventions(student_id);
CREATE INDEX idx_interventions_date ON interventions(intervention_date);
CREATE INDEX idx_interventions_district ON interventions(district);
CREATE INDEX idx_interventions_status ON interventions(status);

CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_student_id ON audit_logs(student_id);

CREATE INDEX idx_risk_history_student_id ON risk_history(student_id);
CREATE INDEX idx_risk_history_calculated_at ON risk_history(calculated_at);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for interventions table
CREATE TRIGGER update_interventions_updated_at BEFORE UPDATE ON interventions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data for testing (optional)
-- Uncomment if you want some test data

-- INSERT INTO interventions (student_id, intervention_type, description, assigned_to, district, risk_score)
-- VALUES 
--     ('STU001', 'Counseling', 'Schedule counseling session for attendance issues', 'Teacher Rao', 'Visakhapatnam', 85.5),
--     ('STU002', 'Transport Support', 'Provide transport allowance', 'Admin Kumar', 'Vijayawada', 78.2),
--     ('STU003', 'Academic Support', 'Extra tutoring for math and science', 'Teacher Devi', 'Guntur', 92.1);

-- Insert sample district stats
INSERT INTO district_stats (district, total_students, high_risk_count, moderate_risk_count, low_risk_count)
VALUES 
    ('Visakhapatnam', 885, 206, 444, 235),
    ('Vijayawada', 885, 207, 445, 233),
    ('Guntur', 885, 207, 444, 234),
    ('Tirupati', 884, 206, 444, 234),
    ('Kakinada', 885, 207, 445, 233)
ON CONFLICT (district) DO NOTHING;

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO appuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO appuser;
