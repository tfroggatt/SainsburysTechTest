package com.sainsburys.test;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import com.sainsburys.test.products.Product;
import com.sainsburys.test.scraper.WebScraper;
import com.sainsburys.test.transform.TransformToJson;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException {
        String urlString = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
        WebScraper scraper = new WebScraper(urlString);

        List<Product> products = scraper.scrapeWebPage();
        TransformToJson transformer = new TransformToJson();

        JSONObject json = transformer.createJsonForProducts(products);
        System.out.println(json);
        System.out.println("Hello World!");
    }
}
