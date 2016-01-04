package it.attocchi.jpec.server.entities.filters;

import it.attocchi.jpa2.JPAEntityFilter;
import it.attocchi.jpec.server.entities.AllegatoPec;
import it.attocchi.jpec.server.entities.AllegatoPec_;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class AllegatoPecFilter extends JPAEntityFilter<AllegatoPec> {
	private long idMessaggio;

	public long getIdMessaggio() {
		return idMessaggio;
	}

	public void setIdMessaggio(long idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	@Override
	public void buildWhere(EntityManagerFactory emf, List<Predicate> predicateList, CriteriaQuery<AllegatoPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<AllegatoPec> root) throws Exception {

		if (idMessaggio > 0) {
			predicateList.add(criteriaBuilder.equal(root.get(AllegatoPec_.idMessaggio), idMessaggio));
		}

	}

	@Override
	public void buildSort(List<Order> orderList, CriteriaQuery<AllegatoPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<AllegatoPec> root) throws Exception {
		orderList.add(criteriaBuilder.asc(root.get(AllegatoPec_.fileName)));
	}

}
