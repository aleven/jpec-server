update jpec.pec06_regole set pec06_evento = 'IMPORTA_MESSAGGIO' where pec06_evento = 'IMPORTA';
update jpec.pec06_regole set pec06_evento = 'PROTOCOLLA_MESSAGGIO' where pec06_evento = 'PROTOCOLLA';

delete from jpec.pec03_config where pec03_nome = 'PEC_PROTOCOLLO_IMPL';

-- alter table jpec.pec06_regole drop column pec06_classe;
alter table jpec.pec06_regole add column pec06_classe longtext;