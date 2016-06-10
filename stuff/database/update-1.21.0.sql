
ALTER TABLE jpec.pec01_messaggi ADD COLUMN pec01_segnatura_xml longtext;

-- cambia il filtro delle ricevute soloRicevuteConRiferimento quindi cercare quante sono:
-- xRicevuta  ma senza xRiferimentoMessageID
select pec01_x_ricevuta, pec01_x_riferimento_message_id from pec01_messaggi;
select * from pec01_messaggi WHERE pec01_x_ricevuta <> '' and (pec01_x_riferimento_message_id is null or pec01_x_riferimento_message_id = '');

-- nuova configurazione regola per segnatura confermaricezione
INSERT INTO jpec.pec06_regole
(pec06_azione, pec06_classe, pec06_criterio, pec06_dt_creazione, pec06_evento, pec06_nome, pec06_note, pec06_ordine)
VALUES
(NULL, 'it.attocchi.jpec.server.protocollo.impl.InviaSegnaturaConferma', '', null, 'AGGIORNA_SEGNATURA', 'Processa Segnatura', 'processa i messaggi ricevuti e crea le risposte segnature', NULL);
