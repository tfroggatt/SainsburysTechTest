package com.sainsburys.test.products;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link FoodProduct} class.
 */
public class FoodProductTest {
	
	/**
	 * Tests that a {@link FoodProduct} is created with the correct fields set given the input, and the correct VAT is calculated and stored.
	 */
	@Test
	public void foodProductCreationTest() {
		FoodProduct product = new FoodProduct("product_name", "product_description", "10.00", "55");
		Assert.assertThat("The name of the product is incorrect", product.getName(), CoreMatchers.is("product_name"));
		Assert.assertThat("The description of the product is incorrect", product.getDescription(), CoreMatchers.is("product_description"));
		Assert.assertThat("The number of calories on the product is incorrect", product.getCalories(), CoreMatchers.is("55"));
		Assert.assertThat("The price of the product is incorrect", product.getPrice(), CoreMatchers.is(10.00));
		Assert.assertThat("The VAT on the product is incorrect", product.getVat(), CoreMatchers.is(2.00));
	}

}
