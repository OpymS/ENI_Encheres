CREATE TRIGGER SetDefaultExpiryDate
ON TOKEN
AFTER INSERT
AS
BEGIN
    UPDATE TOKEN
    SET expiry_date = DATEADD(MINUTE, 20, GETDATE())
    WHERE expiry_date IS NULL
    AND id IN (SELECT id FROM inserted);
END;

