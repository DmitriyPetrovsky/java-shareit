INSERT INTO statuses (name)
SELECT * FROM (VALUES
                   ('WAITING'),
                   ('APPROVED'),
                   ('REJECTED'),
                   ('CANCELED')
                  ) AS tmp(name)
WHERE NOT EXISTS (
    SELECT name FROM statuses WHERE statuses.name = tmp.name
);