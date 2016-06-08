
ALTER TABLE jpec.pec01_messaggi ADD COLUMN pec01_segnatura_xml longtext;

-- cambia il filtro delle ricevute soloRicevuteConRiferimento quindi cercare quante sono:
-- xRicevuta  ma senza xRiferimentoMessageID
select pec01_x_ricevuta, pec01_x_riferimento_message_id from pec01_messaggi;
select * from pec01_messaggi WHERE pec01_x_ricevuta <> '' and (pec01_x_riferimento_message_id is null or pec01_x_riferimento_message_id = '');