package it.attocchi.jpec.server.entities.filters;

import it.attocchi.jpa2.JPAEntityFilter;
import it.attocchi.jpec.server.bl.RegolaPecEventoEnum;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.jpec.server.entities.RegolaPec_;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class RegolaPecFilter extends JPAEntityFilter<RegolaPec> {

	private RegolaPecEventoEnum evento;

	public RegolaPecEventoEnum getEvento() {
		return evento;
	}

	public void setEvento(RegolaPecEventoEnum evento) {
		this.evento = evento;
	}

	@Override
	public void buildWhere(EntityManagerFactory emf, List<Predicate> predicateList, CriteriaQuery<RegolaPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<RegolaPec> root) {
		if (evento != null) {
			predicateList.add(criteriaBuilder.equal(root.get(RegolaPec_.evento), evento.name()));
		}
	}

	@Override
	public void buildSort(List<Order> orderList, CriteriaQuery<RegolaPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<RegolaPec> root) {
		orderList.add(criteriaBuilder.asc(root.get(RegolaPec_.evento)));
		orderList.add(criteriaBuilder.asc(root.get(RegolaPec_.ordine)));
	}

}
