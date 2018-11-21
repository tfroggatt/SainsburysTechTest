package com.sainsburys.test.products;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link Product} class.
 */
public class ProductTest {
	
	/**
	 * Tests that a product is created with the correct fields set given the input, and the correct VAT is calculated and stored.
	 */
	@Test
	public void productCreationTest() {
		Product product = new Product("product_name", "product_description", "10.00");
		Assert.assertThat("The name of the product is incorrect", product.getName(), CoreMatchers.is("product_name"));
		Assert.assertThat("The description of the product is incorrect", product.getDescription(), CoreMatchers.is("product_description"));
		Assert.assertThat("The price of the product is incorrect", product.getPrice(), CoreMatchers.is(10.00));
		Assert.assertThat("The VAT on the product is incorrect", product.getVat(), CoreMatchers.is(2.00));
	}

}
