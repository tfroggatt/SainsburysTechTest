package com.sainsburys.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.sainsburys.test.products.Product;
import com.sainsburys.test.scraper.Scraper;

import mockit.Expectations;
import mockit.Mocked;

public class WebScraperAppTest {
	
	/**
	 * Tests that when no command line argument urls are given, the scrape method is
	 * called on the default url.
	 */
	@Test
	public void testMain_noArgs_defaultUrlUsed() throws IOException {
		
		new Expectations(WebScraperApp.class) {
			{
				//Acts as a verification
				WebScraperApp.scrapeUrl(WebScraperApp.getUrlString());
			}
		};
		
		WebScraperApp.main(new String[0]);
	}
	
	/**
	 * Tests that when command line argument url are given, the scrape method is called 
	 * on the given url.
	 */
	@Test
	public void testMain_urlArgsGiven_givenUrlUsed() throws IOException {
		
		String[] urls = new String[1];
		urls[0] = "http://www.google.com";
		
		new Expectations(WebScraperApp.class) {
			{
				//Acts as a verification
				WebScraperApp.scrapeUrl("http://www.google.com");
			}
		};
		
		WebScraperApp.main(urls);
	}
	
	/**
	 * Tests that the code to scrape the URL given and then the transformation to turn the results to JSON
	 * are called when run.
	 */
	@Test
	public void testScrapeUrl_noErrorsThrown(@Mocked Scraper scraper) throws IOException {
		
		List<Product> products = new ArrayList<>();
		products.add(new Product("name", "description", "10.00"));
		
		new Expectations() {
			{
				new Scraper("http://www.google.com");
				result = scraper;
				
				scraper.scrapeWebPage();
				result = products;
			}
		};
		
		WebScraperApp.scrapeUrl("http://www.google.com");
		
	}
	
	/**
	 * Tests that the URL string returned to be scraped is the default one.
	 */
	@Test
	public void testGetUrlString_defaultString() {
		Assert.assertThat("The URL returned was not expected", WebScraperApp.getUrlString(), 
			CoreMatchers.is("https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html"));
	}
}
