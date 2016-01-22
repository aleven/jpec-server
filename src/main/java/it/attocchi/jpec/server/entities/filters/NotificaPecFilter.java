package it.attocchi.jpec.server.entities.filters;

import it.attocchi.jpa2.JPAEntityFilter;
import it.attocchi.jpa2.entities.EntityMarks_;
import it.attocchi.jpec.server.entities.NotificaPec;
import it.attocchi.jpec.server.entities.NotificaPec_;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

public class NotificaPecFilter extends JPAEntityFilter<NotificaPec> {

	private boolean daInviare;
	private String oggetto;
	private String protocollo;
	private String destinatari;

	public boolean isDaInviare() {
		return daInviare;
	}

	public void setDaInviare(boolean daInviare) {
		this.daInviare = daInviare;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getProtocollo() {
		return protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public String getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}

	private String tipo;
	private long idMessaggioPadre;

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public long getIdMessaggioPadre() {
		return idMessaggioPadre;
	}

	public void setIdMessaggioPadre(long idMessaggioPadre) {
		this.idMessaggioPadre = idMessaggioPadre;
	}

	@Override
	public void buildWhere(EntityManagerFactory emf, List<Predicate> predicateList, CriteriaQuery<NotificaPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<NotificaPec> root) {

		if (daInviare) {
			predicateList.add(criteriaBuilder.equal(root.get(NotificaPec_.inviato), !daInviare));
		}

		if (StringUtils.isNotBlank(oggetto)) {
			predicateList.add(criteriaBuilder.equal(root.get(NotificaPec_.oggetto), oggetto));
		}

		if (StringUtils.isNotBlank(protocollo)) {
			predicateList.add(criteriaBuilder.equal(root.get(NotificaPec_.protocollo), protocollo));
		}

		if (StringUtils.isNotBlank(destinatari)) {
			predicateList.add(criteriaBuilder.equal(root.get(NotificaPec_.destinatari), destinatari));
		}

		if (idMessaggioPadre > 0) {
			predicateList.add(criteriaBuilder.equal(root.get(NotificaPec_.idMessaggioPadre), idMessaggioPadre));
		}

		if (StringUtils.isNotBlank(tipo)) {
			predicateList.add(criteriaBuilder.equal(root.get(NotificaPec_.tipo), tipo));
		}

	}

	@Override
	public void buildSort(List<Order> orderList, CriteriaQuery<NotificaPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<NotificaPec> root) {

		// criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NotificaPec_.username)));
		// criteriaQuery.orderBy(criteriaBuilder.desc("entityMarks.dataCreazione"));
		orderList.add(criteriaBuilder.desc(root.get(NotificaPec_.entityMarks).get(EntityMarks_.dataCreazione)));

		// Person.address is an embedded attribute

		// Join<Notifica, EntityMarks> entityMarks = root.join("entityMarks");
		// // Fetch<Notifica, EntityMarks> entityMarks2 =
		// root.fetch("entityMarks");
		// criteriaQuery.orderBy(criteriaBuilder.desc(entityMarks.get("dataCreazione")));

	}

}
