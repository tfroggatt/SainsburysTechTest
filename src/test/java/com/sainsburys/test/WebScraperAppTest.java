package com.sainsburys.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sainsburys.test.products.Product;
import com.sainsburys.test.scraper.Scraper;

import mockit.Expectations;
import mockit.Mocked;

public class WebScraperAppTest {

    /**
     * Tests that when no command line argument urls are given, the scrape method is called on the default url.
     */
    @Test
    public void testMain_noArgs_defaultUrlUsed(@Mocked Scraper scraper) throws IOException {

        new Expectations() {
            {
                new Scraper(
                        "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html");
                result = scraper;

                scraper.scrapeWebPage();
                result = new ArrayList<>();
            }
        };

        WebScraperApp.main(new String[0]);
    }

    /**
     * Tests that when command line argument url are given, the scrape method is called on the given url.
     */
    @Test
    public void testMain_urlArgsGiven_givenUrlUsed(@Mocked Scraper scraper) throws IOException {

        String[] urls = new String[1];
        urls[0] = "http://www.google.com";

        new Expectations() {
            {
                new Scraper("http://www.google.com");
                result = scraper;

                scraper.scrapeWebPage();
                result = new ArrayList<>();
            }
        };

        WebScraperApp.main(urls);
    }

    /**
     * Tests that the code to scrape the URL given and then the transformation to turn the results to JSON are called
     * when run.
     */
    @Test
    public void testScrapeUrl_noErrorsThrown(@Mocked Scraper scraper) throws IOException {

        List<Product> products = new ArrayList<>();
        products.add(new Product("name", "description", "10.00"));

        WebScraperApp giveUrlApp = new WebScraperApp("http://www.google.com");

        new Expectations() {
            {
                new Scraper("http://www.google.com");
                result = scraper;

                scraper.scrapeWebPage();
                result = products;
            }
        };

        giveUrlApp.scrapeUrl();

    }

}
