package com.sainsburys.test;

import java.io.IOException;

import com.sainsburys.test.scraper.WebScraper;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException {
        String urlString = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
        WebScraper scraper = new WebScraper(urlString);
        scraper.scrapeWebPage();
        System.out.println("Hello World!");
    }
}
