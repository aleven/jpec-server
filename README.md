# jpec-server
una web-app java per la gestione e smistamento della casella di posta elettronica certificata

## Specifiche PEC
Regole tecniche del servizio di trasmissione di documenti informatici mediante posta el ettronica certificata: 
http://www.agid.gov.it/sites/default/files/leggi_decreti_direttive/pec_regole_tecniche_dm_2-nov-2005.pdf

## Riepilogo Funzionalità (principali)
- monitor ricezione nuova PEC
- notifiche in ricezione/invio nuovi messaggi
- notifica in caso di eventi, errori o anomalie
- condivisione casella PEC nel gruppo di lavoro
- verifica stato PEC inviata
- analisi ricevute PEC per calcolo stato messaggi inviati ed associazione ricevute
- regole per filtri ed azioni scriptabili via groovy ()
- possibilità di estensione, personalizzazione comportamento regole con aggiunta di plugin (es: Protocollo personalizzato)
- api rest per utilizzo da applicativi aziendali (invio PEC, stato messaggi e ricezione)
- supporto estrazione segnatura.xml
- archiviazione dati e messaggi in database open
- supporto ad archiviazione messaggi .eml in file system (opzionale)
- supporto a mailbox PEC multiple

## Requisiti e Specifiche Tecniche
- database (postgresql o mysql)
- servlet container with servlet spec >= 3.0
- java 7
