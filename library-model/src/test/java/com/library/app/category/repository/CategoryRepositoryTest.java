package com.library.app.category.repository;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.library.app.category.model.Category;
import com.library.app.commontests.utils.DBCommandTransactionalExecutor;

public class CategoryRepositoryTest {
	private EntityManager em;
	private EntityManagerFactory emf;
	private CategoryRepository categoryRepository;
	private DBCommandTransactionalExecutor dbCommandTransactionalExecutor;

	@Before
	public void initTestCase() {
		emf = Persistence.createEntityManagerFactory("libraryPU");
		em = emf.createEntityManager();

		categoryRepository = new CategoryRepository();
		categoryRepository.em = em;

		dbCommandTransactionalExecutor = new DBCommandTransactionalExecutor(em);
	}

	@After
	public void closeEntityManager() {
		em.close();
		emf.close();
	}

	@Test
	public void addCategoryAndFindIt() {
		final Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});

		assertThat(categoryAddedId, is(notNullValue()));

		final Category category = categoryRepository.findById(categoryAddedId);
		assertThat(category, is(notNullValue()));
		assertThat(category.getName(), is(equalTo(java().getName())));
	}

	@Test
	public void findCategoryByIdNotFound() {
		final Category category = categoryRepository.findById(999L);
		assertThat(category, is(nullValue()));
	}

	@Test
	public void findCategoryByIdWithNullId() {
		final Category category = categoryRepository.findById(null);
		assertThat(category, is(nullValue()));
	}

	@Test
	public void updateCategory() {
		final Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});

		final Category categoryAfterAdd = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));

		categoryAfterAdd.setName(cleanCode().getName());
		dbCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.update(categoryAfterAdd);
			return null;
		});

		final Category categoryAfterUpdate = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));
	}

	@Test
	public void findAllCategories() {
		dbCommandTransactionalExecutor.executeCommand(() -> {
			allCategories().forEach(categoryRepository::add);

			return null;
		});

		final List<Category> categories = categoryRepository.findAll("name");

		assertThat(categories.size(), is(equalTo(4)));
		assertThat(categories.get(0).getName(), is(equalTo(architecture().getName())));
		assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
		assertThat(categories.get(2).getName(), is(equalTo(java().getName())));
		assertThat(categories.get(3).getName(), is(equalTo(networks().getName())));
	}

	@Test
	public void alreadyExistsForAdd() {
		dbCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(java());
			return null;
		});

		assertThat(categoryRepository.alreadyExists(java()), is(equalTo(true)));
		assertThat(categoryRepository.alreadyExists(cleanCode()), is(equalTo(false)));
	}

	@Test
	public void alreadyExistsWithId() {
		final Category java = dbCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(cleanCode());
			return categoryRepository.add(java());
		});

		assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));

		java.setName(cleanCode().getName());
		assertThat(categoryRepository.alreadyExists(java), is(equalTo(true)));

		java.setName(networks().getName());
		assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));
	}

	@Test
	public void existsById() {
		final Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(cleanCode());
			return categoryRepository.add(java()).getId();
		});

		assertThat(categoryRepository.existsById(categoryAddedId), is(equalTo(true)));
		assertThat(categoryRepository.existsById(999L), is(equalTo(false)));
	}
}
