package it.attocchi.jpec.server.entities.filters;

import it.attocchi.jpa2.JPAEntityFilter;
import it.attocchi.jpa2.entities.EntityMarks_;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.MessaggioPec.Folder;
import it.attocchi.jpec.server.entities.MessaggioPec_;
import it.attocchi.utils.ListUtils;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

public class MessaggioPecFilter extends JPAEntityFilter<MessaggioPec> {

	private long idUtenteCreazione;

	public long getIdUtenteCreazione() {
		return idUtenteCreazione;
	}

	public void setIdUtenteCreazione(long idUtenteCreazione) {
		this.idUtenteCreazione = idUtenteCreazione;
	}

	public MessaggioPecFilter setIdUtenteCreazioneL(int idUtenteCreazione) {
		setIdUtenteCreazione(idUtenteCreazione);
		return this;
	}

	private boolean escludiConsegnati;
	private boolean conStatoDaAggiornare;
	private boolean conMessageID;
	private boolean soloRicevuteConRiferimento;

	public boolean isEscludiConsegnati() {
		return escludiConsegnati;
	}

	public void setEscludiConsegnati(boolean escludiConsegnati) {
		this.escludiConsegnati = escludiConsegnati;
	}

	public boolean isConStatoDaAggiornare() {
		return conStatoDaAggiornare;
	}

	public void setConStatoDaAggiornare(boolean conStatoDaAggiornare) {
		this.conStatoDaAggiornare = conStatoDaAggiornare;
	}

	public boolean isConMessageID() {
		return conMessageID;
	}

	public void setConMessageID(boolean conMessageID) {
		this.conMessageID = conMessageID;
	}

	public boolean isSoloRicevuteConRiferimento() {
		return soloRicevuteConRiferimento;
	}

	public void setSoloRicevuteConRiferimento(boolean soloRicevuteConRiferimento) {
		this.soloRicevuteConRiferimento = soloRicevuteConRiferimento;
	}

	private String oggetto;
	private Date dataRicezione;
	private String messageID;
	private String mailbox;

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public Date getDataRicezione() {
		return dataRicezione;
	}

	public void setDataRicezione(Date dataRicezione) {
		this.dataRicezione = dataRicezione;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getMailbox() {
		return mailbox;
	}

	public void setMailbox(String mailbox) {
		this.mailbox = mailbox;
	}

	private Folder folder;

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	private boolean soloNonProcessati;
	private boolean soloNonInviati;

	public boolean isSoloNonProcessati() {
		return soloNonProcessati;
	}

	public void setSoloNonProcessati(boolean soloNonProcessati) {
		this.soloNonProcessati = soloNonProcessati;
	}

	public boolean isSoloNonInviati() {
		return soloNonInviati;
	}

	public void setSoloNonInviati(boolean soloNonInviati) {
		this.soloNonInviati = soloNonInviati;
	}

	private Date dataInvioOriginale;

	public Date getDataInvioOriginale() {
		return dataInvioOriginale;
	}

	public void setDataInvioOriginale(Date dataInvioOriginale) {
		this.dataInvioOriginale = dataInvioOriginale;
	}

	private boolean mostraArchiviati;

	public boolean isMostraArchiviati() {
		return mostraArchiviati;
	}

	public void setMostraArchiviati(boolean mostraArchiviati) {
		this.mostraArchiviati = mostraArchiviati;
	}

	List<String> excludeSubjects;

	public List<String> getExcludeSubjects() {
		return excludeSubjects;
	}

	public void setExcludeSubjects(List<String> excludeSubjects) {
		this.excludeSubjects = excludeSubjects;
	}

	private boolean excludeProtocolled;

	public boolean isExcludeProtocolled() {
		return excludeProtocolled;
	}

	public void setExcludeProtocolled(boolean excludeProtocolled) {
		this.excludeProtocolled = excludeProtocolled;
	}

	@Override
	public void buildWhere(EntityManagerFactory emf, List<Predicate> predicateList, CriteriaQuery<MessaggioPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<MessaggioPec> root) {
		// TODO Auto-generated method stub

		if (idUtenteCreazione > 0) {
			predicateList.add(criteriaBuilder.equal(root.get(MessaggioPec_.entityMarks).get(EntityMarks_.utenteCreazioneId), idUtenteCreazione));
		}

		// root.<String> get("numero")

		if (escludiConsegnati) {
			predicateList.add(criteriaBuilder.isFalse(root.get(MessaggioPec_.consegnato)));
		}

		if (conStatoDaAggiornare) {
			Predicate p1 = criteriaBuilder.isFalse(root.get(MessaggioPec_.accettato));
			Predicate p2 = criteriaBuilder.isFalse(root.get(MessaggioPec_.consegnato));
			Predicate p3 = criteriaBuilder.isFalse(root.get(MessaggioPec_.anomalia));
			predicateList.add(criteriaBuilder.or(p1, p2, p3));
		}
		if (conMessageID) {
			Predicate p1 = criteriaBuilder.isNotNull(root.get(MessaggioPec_.messageID));
			Predicate p2 = criteriaBuilder.notEqual(root.get(MessaggioPec_.messageID), "");
			predicateList.add(criteriaBuilder.and(p1, p2));
		}
		if (soloRicevuteConRiferimento) {
			Predicate p1 = criteriaBuilder.isNotNull(root.get(MessaggioPec_.xRicevuta));
			Predicate p2 = criteriaBuilder.notEqual(root.get(MessaggioPec_.xRicevuta), "");
			Predicate p3 = criteriaBuilder.isNotNull(root.get(MessaggioPec_.xRiferimentoMessageID));
			Predicate p4 = criteriaBuilder.notEqual(root.get(MessaggioPec_.xRiferimentoMessageID), "");			
			predicateList.add(criteriaBuilder.and(p1, p2));
		}		

		if (StringUtils.isNotEmpty(oggetto)) {
			predicateList.add(criteriaBuilder.equal(root.get(MessaggioPec_.oggetto), oggetto));
		}

		if (StringUtils.isNotEmpty(semeRicerca)) {
			Predicate p1 = criteriaBuilder.like(root.get(MessaggioPec_.oggetto), getSemeRicercaForLike());
			Predicate p2 = criteriaBuilder.like(root.get(MessaggioPec_.messaggio), getSemeRicercaForLike());

			Predicate p3 = criteriaBuilder.like(root.get(MessaggioPec_.destinatari), getSemeRicercaForLike());
			Predicate p4 = criteriaBuilder.like(root.get(MessaggioPec_.emailMittente), getSemeRicercaForLike());
			Predicate p5 = criteriaBuilder.like(root.get(MessaggioPec_.nomeMittente), getSemeRicercaForLike());
			Predicate p6 = criteriaBuilder.like(root.get(MessaggioPec_.usernameMittente), getSemeRicercaForLike());

			Predicate p7 = criteriaBuilder.like(root.get(MessaggioPec_.postacertBody), getSemeRicercaForLike());

			predicateList.add(criteriaBuilder.or(p1, p2, p3, p4, p5, p6, p7));
		}

		if (StringUtils.isNotEmpty(messageID)) {
			predicateList.add(criteriaBuilder.equal(root.get(MessaggioPec_.messageID), messageID));
		}

		if (dataRicezione != null) {
			predicateList.add(criteriaBuilder.equal(root.get(MessaggioPec_.dataRicezione), dataRicezione));
		}

		if (dataInvioOriginale != null) {
			predicateList.add(criteriaBuilder.equal(root.get(MessaggioPec_.dataInvioOriginale), dataInvioOriginale));
		}

		if (soloNonProcessati) {
			predicateList.add(criteriaBuilder.isFalse(root.get(MessaggioPec_.processato)));
		}

		if (soloNonInviati) {
			predicateList.add(criteriaBuilder.isFalse(root.get(MessaggioPec_.inviato)));
		}

		if (ListUtils.isNotEmpty(excludeSubjects)) {
			for (String s : excludeSubjects) {
				predicateList.add(criteriaBuilder.notLike(root.get(MessaggioPec_.oggetto), getForLike(s)));
			}
		}

		if (isExcludeProtocolled()) {
			predicateList.add(criteriaBuilder.isNull(root.get(MessaggioPec_.protocollo)));
		}

		if (!mostraArchiviati) {
			predicateList.add(criteriaBuilder.isFalse(root.get(MessaggioPec_.archiviato)));
		}

		if (folder != null) {
			predicateList.add(criteriaBuilder.equal(root.get(MessaggioPec_.folder), folder.name()));
		}

		if (StringUtils.isNoneBlank(mailbox)) {
			predicateList.add(criteriaBuilder.equal(root.get(MessaggioPec_.mailbox), mailbox));
		}

		if (!includiEliminati) {
			predicateList.add(criteriaBuilder.isNull(root.get(MessaggioPec_.entityMarks).get(EntityMarks_.dataCancellazione)));
		}
	}

	@Override
	public void buildSort(List<Order> orderList, CriteriaQuery<MessaggioPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<MessaggioPec> root) {

		// criteriaQuery.orderBy(criteriaBuilder.asc(root.get(MessaggioPec_.username)));
		// criteriaQuery.orderBy(criteriaBuilder.desc("entityMarks.dataCreazione"));
		orderList.add(criteriaBuilder.desc(root.get(MessaggioPec_.entityMarks).get(EntityMarks_.dataCreazione)));

		// Person.address is an embedded attribute

		// Join<Messaggio, EntityMarks> entityMarks = root.join("entityMarks");
		// // Fetch<Messaggio, EntityMarks> entityMarks2 =
		// root.fetch("entityMarks");
		// criteriaQuery.orderBy(criteriaBuilder.desc(entityMarks.get("dataCreazione")));

	}

}
