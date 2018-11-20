package com.sainsburys.test.scrapper;

import javax.xml.parsers.ParserConfigurationException;

import org.hamcrest.CoreMatchers;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;

public class WebScrapperTest {
	
	@Mocked
	Element productElement;
	
	/**
	 * Tests that null is returned is there are no href elements found in the element passed in.
	 */
	@Test
	public void testSetNameAndGetForwardLink_noHrefElements_null() {
		
		new Expectations() {
			{
				productElement.select("a[href]");
				result = new Elements();
			}
		};
		
		String result = WebScraper.setNameAndGetForwardLink(productElement);
		Assert.assertThat("Null should be returned as no elements found", result, CoreMatchers.nullValue());
	}
	
	/**
	 * Tests that the link is returned from the first href element found within the element is passed in, and the name is set.
	 */
	@Test
	public void testSetNameAndGetForwardLink_hrefElements_link(@Mocked Element hrefElement) {
		
		new Expectations() {
			{
				productElement.select("a[href]");
				result = new Elements(hrefElement);
				
				hrefElement.text();
				result = "product_name";
				
				hrefElement.attr("href");
				result = "relative_link";
			}
		};
		
		String result = WebScraper.setNameAndGetForwardLink(productElement);
		Assert.assertThat("The link returned from the href element was incorrect", result, CoreMatchers.is("relative_link"));
		Assert.assertThat("The name set in the class is inocrrect", Deencapsulation.getField(WebScraper.class, "name"), CoreMatchers.is("product_name"));
	}
	
	/**
	 * Tests that null is returned is there are no <p> elements with pricePerUnit on them found in the element passed in.
	 */
	@Test
	public void testGetPrice_noPricePerUnitElements_null() {
		new Expectations() {
			{
				productElement.select("p.pricePerUnit");
				result = new Elements();
			}
		};
		
		String result = WebScraper.getPrice(productElement);
		Assert.assertThat("Null should be returned as no elements found", result, CoreMatchers.nullValue());
	}
	
	/**
	 * Test the correct price is returned from the pricePerUnit element if one is found on the element passed in.
	 */
	@Test
	public void testGetPrice_pricePerUnitElement_price(@Mocked Element priceElement) {
		
		new Expectations() {
			{
				productElement.select("p.pricePerUnit");
				result = new Elements(priceElement);
				
				priceElement.text();
				result = "9.00";
			}
		};
		
		String result = WebScraper.getPrice(productElement);
		Assert.assertThat("The price returned from the pricePerUnit element was incorrect", result, CoreMatchers.is("9.00"));
	}
	
	/**
	 * Test the correct price is returned from the pricePerUnit element if one is found on the element passed in. Only numbers should be returned
	 * regardless of any other characters.
	 */
	@Test
	public void testGetPrice_pricePerUnitElement_priceStrippedToValue(@Mocked Element priceElement) {
		
		new Expectations() {
			{
				productElement.select("p.pricePerUnit");
				result = new Elements(priceElement);
				
				priceElement.text();
				result = "$%9.00";
			}
		};
		
		String result = WebScraper.getPrice(productElement);
		Assert.assertThat("The price returned from the pricePerUnit element was incorrect", result, CoreMatchers.is("9.00"));
	}

}
