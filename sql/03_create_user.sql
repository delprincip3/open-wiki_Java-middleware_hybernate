-- Creazione dell'utente per l'applicazione
CREATE USER IF NOT EXISTS 'openwiki_user'@'localhost' IDENTIFIED BY 'openwiki_password';
GRANT ALL PRIVILEGES ON openwiki.* TO 'openwiki_user'@'localhost';
FLUSH PRIVILEGES; 