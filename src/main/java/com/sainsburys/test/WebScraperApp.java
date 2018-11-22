package com.sainsburys.test;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import com.sainsburys.test.products.Product;
import com.sainsburys.test.scraper.Scraper;
import com.sainsburys.test.transform.TransformToJson;

/**
 * This is the entry point for the Web Scraper application. It can take in urls via command line arguments, or use a
 * dummy url if non are given.
 */
public class WebScraperApp {

    private String webPageUrl = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            WebScraperApp givenUrlApp = new WebScraperApp(args[0]);
            givenUrlApp.scrapeUrl();
        } else {
            WebScraperApp defaultUrlApp = new WebScraperApp();
            defaultUrlApp.scrapeUrl();
        }

    }

    /**
     * Default constructor to create an instance of the application with a default URL.
     */
    public WebScraperApp() {
    }

    /**
     * Constructor to create an instance of the application with the URL for the webpage to scrap given as the first
     * command line argument.
     * 
     * @param webPageUrl
     *            The new URL to scrape
     */
    public WebScraperApp(String webPageUrl) {
        this.webPageUrl = webPageUrl;
    }

    /**
     * Will take the URL provide, create a scraper and pull out the relevant information before calling a transformer to
     * transform the found content to JSON which is printed to the console.
     * 
     * @param url
     *            The URL of the web page to scrape
     * @throws IOException
     */
    public void scrapeUrl() throws IOException {
        Scraper scraper = new Scraper(webPageUrl);
        List<Product> products = scraper.scrapeWebPage();

        TransformToJson transformer = new TransformToJson();
        JSONObject json = transformer.createJsonForProducts(products);
        System.out.println(json);
    }

}
