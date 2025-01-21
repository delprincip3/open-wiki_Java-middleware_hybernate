# Configurazione Database OpenWiki

Questa cartella contiene gli script SQL necessari per configurare il database dell'applicazione OpenWiki.

## Ordine di Esecuzione

1. `01_create_database.sql` - Crea il database
2. `02_create_tables.sql` - Crea le tabelle necessarie
3. `03_create_user.sql` - Crea l'utente dell'applicazione
4. `04_sample_data.sql` - (Opzionale) Inserisce dati di esempio

## Istruzioni per l'Esecuzione

1. Accedi a MySQL come root:
```bash
mysql -u root -p
```

2. Esegui gli script nell'ordine indicato:
```bash
source /path/to/01_create_database.sql
source /path/to/02_create_tables.sql
source /path/to/03_create_user.sql
source /path/to/04_sample_data.sql
```

## Credenziali Default

### Database MySQL
- Database: `openwiki`
- Username: `openwiki_user`
- Password: `openwiki_password`

### Credenziali Applicazione Testate
Per testare l'applicazione, è possibile utilizzare le seguenti credenziali:
- Username: `delprincip3`
- Password: `12345678`

**Note**: 
- In ambiente di produzione, modificare le credenziali del database con valori sicuri.
- Le credenziali di test sono funzionanti con il servizio di autenticazione Flask. 