-- Inserimento di alcuni dati di esempio
INSERT INTO saved_articles (user_id, title, content, image_url, page_id, wiki_url, date_downloaded) 
VALUES 
(4, 'Roma', 'Roma è la capitale d''Italia...', 'https://example.com/roma.jpg', '123', 'https://it.wikipedia.org/wiki/Roma', NOW()),
(4, 'Firenze', 'Firenze è un comune italiano...', 'https://example.com/firenze.jpg', '124', 'https://it.wikipedia.org/wiki/Firenze', NOW()); 