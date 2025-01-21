-- Creazione della tabella degli articoli
CREATE TABLE IF NOT EXISTS saved_articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    image_url TEXT,
    page_id VARCHAR(50),
    wiki_url TEXT,
    date_downloaded DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
); 