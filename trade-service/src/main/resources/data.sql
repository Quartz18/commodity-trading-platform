INSERT INTO risk_limit (commodity, max_position)
VALUES ('GOLD', 100.00)
ON CONFLICT (commodity)
DO UPDATE SET max_position = EXCLUDED.max_position;

INSERT INTO risk_limit (commodity, max_position)
VALUES ('SILVER', 500.00)
ON CONFLICT (commodity)
DO UPDATE SET max_position = EXCLUDED.max_position;