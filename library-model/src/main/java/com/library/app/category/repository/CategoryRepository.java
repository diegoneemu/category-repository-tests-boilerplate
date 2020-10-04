package com.library.app.category.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.library.app.category.model.Category;

public class CategoryRepository {

	EntityManager em;

	public Category add(final Category category) {
		em.persist(category);

		return category;
	}

	public Category findById(final Long id) {
		if (id == null) {
			return null;
		}

		return em.find(Category.class, id);
	}

	public void update(final Category category) {
		// TODO Auto-generated method stub
		em.merge(category);
	}

	@SuppressWarnings("unchecked")
	final List<Category> findAll(final String orderField) {
		return em.createQuery("SELECT e FROM Category e ORDER BY e." + orderField).getResultList();
	}

	public boolean alreadyExists(final Category category) {
		final StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT 1 FROM Category e WHERE e.name = :name");

		if (category.getId() != null) {
			jpql.append(" AND e.id != :id");
		}

		final Query query = em.createQuery(jpql.toString());
		query.setParameter("name", category.getName());

		if (category.getId() != null) {
			query.setParameter("id", category.getId());
		}

		return query.setMaxResults(1).getResultList().size() > 0;
	}

	public boolean existsById(final Long id) {
		return em.createQuery("SELECT 1 from Category e WHERE e.id = :id")
				.setParameter("id", id)
				.setMaxResults(1)
				.getResultList().size() > 0;
	}
}
