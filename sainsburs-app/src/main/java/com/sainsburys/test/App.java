package com.sainsburys.test;

import com.sainsburys.test.scrapper.WebScraper;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	String urlString = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
    	WebScraper.scrapeWebPage(urlString);
        System.out.println( "Hello World!" );
    }
}
