# 📚 Open Wiki Java Middleware

Questo è il middleware Java per l'applicazione Open Wiki, un sistema che permette di cercare, salvare e gestire articoli da Wikipedia. Il middleware gestisce l'interazione con l'API di Wikipedia e il salvataggio degli articoli nel database.

## 🌟 Caratteristiche Principali

- 🔍 Ricerca avanzata di articoli su Wikipedia
- 📚 Gestione completa degli articoli salvati (CRUD)
- 🌟 Articolo del giorno con aggiornamento automatico
- 🔄 Sistema di aggiornamento e sincronizzazione articoli
- 🗑️ Gestione eliminazione articoli
- 🔐 Integrazione con sistema di autenticazione
- 🌐 Supporto multilingua (IT/EN)

## ⚠️ Importante: Componenti Richiesti

Questo è solo il middleware dell'applicazione. Per il funzionamento completo sono necessari:

1. **Frontend React** - Per l'interfaccia utente:  
   [@delprincip3/open-wiki_front](https://github.com/delprincip3/open-wiki_front.git)
   - Gestisce l'interfaccia utente
   - Implementa la ricerca in tempo reale
   - Visualizza gli articoli salvati
   - Fornisce funzionalità di modifica e eliminazione

2. **Flask Authentication Service** - Per l'autenticazione:  
   [@delprincip3/open-wiki_autentication-signup](https://github.com/delprincip3/open-wiki_autentication-signup.git)
   - Gestisce registrazione e login utenti
   - Fornisce token JWT per l'autenticazione
   - Gestisce le sessioni utente
   - Implementa il recupero password

## 🛠️ Stack Tecnologico

### Backend
- Java 17
- Javalin (Framework web leggero)
- Hibernate (ORM)
- Jackson (Serializzazione JSON)
- MySQL (Database)
- SLF4J (Logging)
- JUnit (Testing)

### Dipendenze
- `com.fasterxml.jackson.core:jackson-databind`
- `io.javalin:javalin`
- `org.hibernate:hibernate-core`
- `mysql:mysql-connector-java`
- `org.slf4j:slf4j-simple`

## 🚀 Setup e Installazione

### Prerequisiti
- Java 17 o superiore
- MySQL 8.0 o superiore
- Maven 3.6 o superiore
- Git

### 1. Clona il repository
```bash
git clone https://github.com/tuoUsername/open-wiki-middleware.git
cd open-wiki-middleware
```

### 2. Configura il database MySQL
```sql
CREATE DATABASE openwiki;
USE openwiki;

CREATE TABLE articles (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
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
```

### 3. Configurazione Ambiente
Crea il file `application.properties` nella cartella `src/main/resources`:
```properties
# Hibernate
hibernate.dialect=org.hibernate.dialect.MySQLDialect
hibernate.connection.driver_class=com.mysql.cj.jdbc.Driver
hibernate.connection.url=jdbc:mysql://localhost:3306/openwiki
hibernate.connection.username=your_username
hibernate.connection.password=your_password
hibernate.show_sql=true
hibernate.hbm2ddl.auto=update

# Server
server.port=8080
cors.allowed.origins=http://localhost:5174

# Wikipedia API
wiki.api.url=https://it.wikipedia.org/w/api.php
```

### 4. Build e Avvio
```bash
# Installa le dipendenze e compila
mvn clean install

# Avvia l'applicazione
mvn exec:java
```

## 📝 API Reference

### Endpoints Wikipedia
```http
GET /api/wikipedia/search?query={query}&limit={limit}
GET /api/wikipedia/article/{title}
GET /api/wikipedia/featured
```

### Endpoints Articoli
```http
POST /api/articles
GET /api/articles
PUT /api/articles/{id}
DELETE /api/articles/{id}
```

### Esempi di Richieste

#### Salva un Articolo
```http
POST /api/articles
Content-Type: application/json

{
  "title": "Titolo Articolo",
  "content": "Contenuto dell'articolo...",
  "image_url": "https://example.com/image.jpg",
  "page_id": "12345",
  "wiki_url": "https://it.wikipedia.org/wiki/Titolo_Articolo"
}
```

## 🗄️ Struttura del Progetto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── openwiki/
│   │           ├── controller/    # Controller API
│   │           │   ├── AuthController.java
│   │           │   └── WikiController.java
│   │           ├── service/       # Logica di business
│   │           │   └── WikiService.java
│   │           ├── model/         # Modelli dati (Entità Hibernate)
│   │           │   ├── Article.java
│   │           │   └── WikiSearchResult.java
│   │           ├── repository/    # Repository Hibernate
│   │           │   └── ArticleRepository.java
│   │           ├── config/       # Configurazioni
│   │           │   └── HibernateConfig.java
│   │           └── middleware/   # Middleware
│   │               └── AuthMiddleware.java
│   └── resources/
│       └── application.properties
```

## 📝 Note Importanti

- Il middleware richiede Java 17 o superiore
- Hibernate gestisce automaticamente la creazione delle tabelle del database
- È necessario avere MySQL installato e configurato
- Assicurarsi che tutti i componenti dell'applicazione siano in esecuzione
- Il servizio si avvia sulla porta 8080 di default
- Configurare correttamente il CORS per l'integrazione con il frontend
- Gestire correttamente le proprietà di Hibernate in produzione

## 🐛 Debug e Logging

Il middleware utilizza SLF4J per il logging. I log vengono scritti sia su console che su file:
- `logs/application.log` - Log applicativi
- `logs/error.log` - Log degli errori

## 👥 Contributi

I contributi sono benvenuti! Per modifiche importanti:

1. Fai una fork del repository
2. Crea un branch per la tua feature (`git checkout -b feature/AmazingFeature`)
3. Committa le modifiche (`git commit -m 'Add some AmazingFeature'`)
4. Pusha sul branch (`git push origin feature/AmazingFeature`)
5. Apri una Pull Request

## 📄 Licenza

Distribuito sotto licenza MIT. Vedi `LICENSE` per maggiori informazioni.

## 📧 Contatti

Del Principe - [@github](https://github.com/delprincip3)

Project Link: [https://github.com/delprincip3/open-wiki-middleware](https://github.com/delprincip3/open-wiki-middleware) 

## 🌐 API Utilizzate

### Wikipedia API
L'applicazione utilizza le seguenti API di Wikipedia:

#### 1. API di Ricerca
```http
GET https://it.wikipedia.org/w/api.php?action=query&list=search&format=json
```
Utilizzata per la ricerca degli articoli. Parametri principali:
- `srsearch`: termine di ricerca
- `srlimit`: numero massimo di risultati
- `sroffset`: offset per la paginazione

#### 2. API Contenuto Articolo
```http
GET https://it.wikipedia.org/w/api.php?action=query&prop=extracts|pageimages|info
```
Utilizzata per ottenere il contenuto completo di un articolo. Parametri:
- `titles`: titolo dell'articolo
- `explaintext`: per ottenere il testo pulito
- `exintro`: per ottenere solo l'introduzione
- `pithumbsize`: dimensione dell'immagine in evidenza

#### 3. API Articolo del Giorno
```http
GET https://it.wikipedia.org/w/api.php?action=featuredfeed&feed=featured
```
Utilizzata per ottenere l'articolo in evidenza del giorno.

### Flask Authentication API
Endpoints del servizio di autenticazione:

```http
POST /api/auth/signup
POST /api/auth/login
GET /api/auth/user
POST /api/auth/logout
```

Utilizzati per:
- Registrazione nuovo utente
- Login e generazione token
- Verifica sessione utente
- Logout e invalidazione token

### Integrazione delle API

Il middleware gestisce l'integrazione tra:
1. **Frontend → Wikipedia**: 
   - Ricerca articoli in tempo reale
   - Recupero contenuto completo
   - Fetch articolo del giorno

2. **Frontend → Auth Service**:
   - Gestione sessioni utente
   - Validazione token
   - Autenticazione richieste

3. **Frontend → Database**:
   - Salvataggio articoli
   - Recupero articoli salvati
   - Aggiornamento e eliminazione 