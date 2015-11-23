package it.attocchi.jpec.server.entities.filters;

import it.attocchi.jpa2.JPAEntityFilter;
import it.attocchi.jpec.server.entities.ProtocolloPec;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

public class ProtocolloFilter extends JPAEntityFilter<ProtocolloPec> {

	private String serie;
	private String anno;

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getAnno() {
		return anno;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}

	@Override
	public void buildWhere(EntityManagerFactory emf, List<Predicate> predicateList, CriteriaQuery<ProtocolloPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<ProtocolloPec> root) {

		if (StringUtils.isNotEmpty(anno)) {
			// predicateList.add(criteriaBuilder.equal(root.get("entityMarks").get("idUtenteCreazione"),
			// idUtenteCreazione));
			predicateList.add(criteriaBuilder.equal(root.get("anno"), anno));
		}

		if (StringUtils.isNotEmpty(serie)) {
			predicateList.add(criteriaBuilder.equal(root.get("serie"), serie));
		}
	}

	@Override
	public void buildSort(List<Order> orderList, CriteriaQuery<ProtocolloPec> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<ProtocolloPec> root) {
		orderList.add(criteriaBuilder.desc(root.get("anno")));
	}

}
