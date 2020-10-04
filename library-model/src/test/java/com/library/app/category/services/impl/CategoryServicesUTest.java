package com.library.app.category.services.impl;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.validation.Validation;
import javax.validation.Validator;

import com.library.app.category.exception.CategoryNotFoundException;
import org.junit.Before;
import org.junit.Test;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;

public class CategoryServicesUTest {
	private CategoryServices categoryServices;
	private Validator validator;
	private CategoryRepository categoryRepository;

	@Before
	public void initTestCase() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();

		categoryRepository = mock(CategoryRepository.class);

		categoryServices = new CategoryServicesImpl();
		((CategoryServicesImpl) categoryServices).validator = validator;
		((CategoryServicesImpl) categoryServices).categoryRepository = categoryRepository;
	}

	@Test
	public void addCategoryWithNullName() {
		addCategoryWithInvalidName(null);
	}

	@Test
	public void addCategoryWithShortName() {
		addCategoryWithInvalidName("A");
	}

	@Test
	public void addCategoryWithLongName() {
		addCategoryWithInvalidName("This is a long name that will cause an exception to be thrown");
	}

	@Test(expected = CategoryExistentException.class)
	public void addCategoryWithExistentName() {
		final Category java = java();
		when(categoryRepository.alreadyExists(java)).thenReturn(true);

		categoryServices.add(java);
	}

	@Test
	public void addValidCategory() {
		final Category java = java();
		when(categoryRepository.alreadyExists(java)).thenReturn(false);
		when(categoryRepository.add(java)).thenReturn(categoryWithId(java, 1L));

		final Category categoryAdded = categoryServices.add(java);
		assertThat(categoryAdded.getId(), is(equalTo(1L)));
	}

	@Test
	public void updateWithNullName() {
		updateCategoryWithInvalidName(null);
	}

	@Test
	public void updateWithShortName() {
		updateCategoryWithInvalidName("A");
	}

	@Test
	public void updateWithLongName() {
		updateCategoryWithInvalidName("This is a long name that will cause an exception to be thrown");
	}

	@Test(expected = CategoryExistentException.class)
	public void updateCategoryWithExistentName() {
		final Category java = categoryWithId(java(), 1L);
		when(categoryRepository.alreadyExists(java)).thenReturn(true);

		categoryServices.update(java);
	}

	@Test(expected = CategoryNotFoundException.class)
	public void updateCategoryNotFound() {
		final Category java = categoryWithId(java(), 1L);
		when(categoryRepository.alreadyExists(java)).thenReturn(false);
		when(categoryRepository.existsById(1L)).thenReturn(false);

		categoryServices.update(java);
	}

	@Test
	public void updateValidCategory() {
		final Category java = categoryWithId(java(), 1L);
		when(categoryRepository.alreadyExists(java)).thenReturn(false);
		when(categoryRepository.existsById(1L)).thenReturn(true);

		categoryServices.update(java);

		verify(categoryRepository).update(java);
	}

	private void addCategoryWithInvalidName(final String name) {
		try {
			categoryServices.add(new Category(name));
			fail("An error should have been throw");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
		}
	}

	private void updateCategoryWithInvalidName(final String name) {
		try {
			categoryServices.update(new Category(name));
			fail("An error should have been thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
		}
	}
}
