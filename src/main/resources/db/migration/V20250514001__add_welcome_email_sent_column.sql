ALTER TABLE users 
ADD COLUMN welcome_email_sent BOOLEAN NOT NULL DEFAULT FALSE;

-- Pour les utilisateurs existants, marquez l'email comme déjà envoyé
UPDATE users 
SET welcome_email_sent = TRUE;