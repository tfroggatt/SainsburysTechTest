package com.sainsburys.test;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import com.sainsburys.test.products.Product;
import com.sainsburys.test.scraper.Scraper;
import com.sainsburys.test.transform.TransformToJson;

/**
 * This is the entry point for the Web Scraper application. It can take in urls via 
 * command line arguments, or use a dummy url if non are given.
 */
public class WebScraperApp {
	
    public static void main(String[] args) throws IOException {
    	if(args.length > 0) {
    		for (String url : args) {
    			scrapeUrl(url);
    		}
    	} else {
    		scrapeUrl(getUrlString());
    	}
    	
    }
    
    /**
     * Will take the URL provide, create a scraper and pull out the relevant information before calling
     * a transformer to transform the found content to JSON which is printed to the console.
     * 
     * @param url The URL of the web page to scrape
     * @throws IOException
     */
    public static void scrapeUrl(String url) throws IOException {
        Scraper scraper = new Scraper(url);
        List<Product> products = scraper.scrapeWebPage();
        
        TransformToJson transformer = new TransformToJson();
        JSONObject json = transformer.createJsonForProducts(products);
        System.out.println(json);
    }
    
    /**
     * This returns the URL to use if non were provided through the command line.
     * 
     * @return The URL as a string
     */
    public static String getUrlString() {
    	return "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
    }
}
