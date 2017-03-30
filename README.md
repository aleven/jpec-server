# jpec-server
una web-app java per la gestione e smistamento della casella di posta elettronica certificata

## Specifiche PEC
Regole tecniche del servizio di trasmissione di documenti informatici mediante posta el ettronica certificata: 
[pec_regole_tecniche_dm_2-nov-2005.pdf](http://www.agid.gov.it/sites/default/files/leggi_decreti_direttive/pec_regole_tecniche_dm_2-nov-2005.pdf)

## Riepilogo Funzionalità (principali)
- monitor ricezione nuova PEC
- notifiche in ricezione/invio nuovi messaggi
- notifica in caso di eventi, errori o anomalie
- condivisione casella PEC nel gruppo di lavoro
- verifica stato PEC inviata
- analisi ricevute PEC per calcolo stato messaggi inviati ed associazione ricevute
- regole per filtri ed azioni scriptabili via groovy
- possibilità di estensione, personalizzazione comportamento regole con aggiunta di plugin (es: Protocollo personalizzato)
- api rest per utilizzo da applicativi aziendali: invio PEC, stato messaggi e ricezione ([API DOC](http://petstore.swagger.io/?url=))
- supporto estrazione segnatura.xml
- archiviazione dati e messaggi in database open
- supporto ad archiviazione messaggi .eml in file system (attivazione opzionale)
- supporto a mailbox PEC multiple

##Requisiti per Installazione
###Sever
* Tomcat 7 o successivi (servlet container with servlet spec >= 3.0)
* Java 7 o successivi
* Database Postgres o MySql
* war applicazione

### Dati Mailbox Posta Certificata
* server POP3/IMAP e SMTP per invio e ricezione PEC
* account (username e password)
* se utilizza SSL
* eventuale configurazione IMAP se si vuole monitorare una cartella specifica
* eventuali regole di esclusione messaggi (letti\non letti)

### File System
* cartella di configurazione dove posizionare le configurazioni delle Mailbox (singola o multipla)
* cartalla di lavoro in scrittura per poter salvare allegati\eml che vengono elaborati dal sistema

### Base Dati
* Dati Accesso Postgres o MySQL (username, password, ip del server) (*)

(*) configurare context.xml del .war

##Requisiti per Compilazione
### Dipendenze
* [webapp-common](https://github.com/aleven/webapp-common)

## Integrazione GDA
### Servizi Protocollo e Documentale
* IP del server per i servizi (da configurare negli opportuni connettori java) (**)
* username e password per effettuare le richieste REST ai servizi (**)

(**) configurare nel codice java per progetto [gdapec-protocollo-client](https://bitbucket.org/comunerivadelgarda/gdapec-protocollo-client)

### Dati Mailbox
* id dell’ufficio (SPORTELLO Protocollo == Attribuzione)
* id dell’ufficio (ID UFFICIO passato == Destinatario)

## Configurazione Base del Programma
fare riferimento alla wiki online [Configurazioni-Necessarie](https://github.com/mattocchi/jpec-server/wiki/Configurazioni-Necessarie)

### Configurazione Generale
* PEC_MAILBOXES_FOLDER: [String] specifica la posizione dei files di configurazione delle mailbox (valore di default ./WEB-INF/)
* PEC_ATTACH_STORE_FOLDER: [String] specifica la cartella dove salvare gli allegati ai messaggi pec inviati (valore di default ./WEB-INF/allegati). ATTENZIONE: non vengono cancellati automaticamente
* PEC_EML_STORE_FOLDER: [String] specifica la cartella dove salvare i file EML nel caso sia attiva l'opzione di archiviazione (valore di default ./WEB-INF/eml). ATTENZIONE: vengono salvati in questa posizione anche quando vengono automaticamente salvati per allegarli  alle email di notifica.
* PEC_FOLDER_IN: [String] nome della sotto-cartella di PEC_EML_STORE_FOLDER per la memorizzazione dei messaggi ricevuti (valore di default IN)
* PEC_FOLDER_OUT: [String] nome della sotto-cartella di PEC_EML_STORE_FOLDER per la memorizzazione dei messaggi inviati (valore di default OUT)

### Protocollo
* PEC_PROTOCOLLO_IMPL: [String|class] specifica la classe Java da utilizzare a run-time come implementazione del protocollo. Se non specificata i messaggi non vengono protocollati. (una classe protocollo base utilizzabile è it.attocchi.jpec.server.protocollo.impl.ProtocolloTest)

### Notifiche
Configurazioni Necessarie per Abilitare l'Invio delle Notifiche:
* PEC_ENABLE_NOTITY_SEND: [true/false] abilita invio delle notifiche (se non abilitato, anche se ci sono notifiche da inviare non vengono inviate.
* PEC_NOTIFICHE_SMTP_SERVER: [String] smtp server da utilizzare per le notifiche
* PEC_NOTIFICHE_SMTP_PORT: [int] porta server smtp
* PEC_NOTIFICHE_SMTP_USERNAME: [String] smtp username
* PEC_NOTIFICHE_SMTP_PASSWORD: [String] smtp password
* PEC_NOTIFICA_INVIO_DESTINATARI: [String] destinatari a cui inviare le notifiche (elenco separato da ,)
* PEC_NOTIFICHE_SENDER_EMAIL: [String] smtp password

Opzionali:
* PEC_NOTIFICHE_SMTP_SSL: indica se il server smtp necessita di connessione ssl
* PEC_NOTIFICHE_SMTP_SSLNOCHECK: indica se considerare il certificato SSL del server come attendibile (utile in caso di certificati self-signed)
* PEC_NOTIFICHE_SENDER_NAME: specifica il nome visualizzato del mittente

La notifica di errore contiene in allegato il file .eml della pec che ha generato errore se PEC_ENABLE_EML_STORE, a tale scopo verifica le tre impostazioni:
* PEC_ENABLE_EML_STORE: true
* PEC_EML_STORE_FOLDER: cartella dove salvare il file .eml
* PEC_FOLDER_IN: nome della sotto-cartella dove salvare .eml errori in ingresso.
(il programma deve salvare .eml su file system per poter poi inviarlo in secondo momento come file allegato sulla notifica).

## Configurazione delle Regole
Eventi a cui e' possibile agganciare comportamento personalizzato:

| Evento | Descrizione |
| --- | --- |
|IMPORTA_MESSAGGIO|handle per la defizione di criteri per importazione dei messaggi|
|PROTOCOLLA_MESSAGGIO|handle per la  definizione di un comportamento per la protocollazione dei messaggi|
|AGGIORNA_STATO|handle per la definizione di regole per l'aggiornamento di (esempio: allega le ricevute nel documentale)|
|AGGIORNA_SEGNATURA|handle per la definizione di un comportamento quando in presenza di una segnatura, esempio risposta automatica|

| Evento | Descrizione |
| --- | --- |
|evento|hande a cui agganciare questa regola|
|nome|nome della regola|
|ordine|se sono configurate piu regole sullo stesso handle utile per determinare ordine di esecuzione|
|criterio|script groovy che viene valutato per verificare se la regola è applicabile, se ritorna true viene "eseguita"|
|azione|script groovy con il quale è possibile interagire con la regola, settando variabili ad esempio nell'istanza di classe|
|classe|specifica quale classe istanziare per l'applicazione di questa regola|
|note|note relative alla regola|

### Scripting via Groovy

| Variabile Contesto | Descrizione |
| --- | --- |
|azione|è la variabile che a runtime (di valutazione script goovy) contiene l'istanza della classe specificata nella regola nel campo "classe" (e quindi si possono usare tutti i suoi getter e setter)|
|helper|la variabile che a runtime (di valutazione script goovy) contiene l'istanza della classe Helper (it.attocchi.jpec.server.regole.RegolaPecHelper) che ha una serie di "metodi utili" pronti all'uso|

l'istanza "helper" di RegolaPecHelper contiene al suo interno due field con riferimento all'istanza di RegolaPec in .regola (la regola che ha creato l'istanza di questo Helper) e di Message in .messaggioEmail (il messaggio email che sta per essere valuato)

### Estensione con Plugin Personalizzati

it.attocchi.jpec.server.protocollo.AbstractAzione