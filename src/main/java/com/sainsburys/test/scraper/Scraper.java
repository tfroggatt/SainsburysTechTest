package com.sainsburys.test.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sainsburys.test.products.FoodProduct;
import com.sainsburys.test.products.Product;

/**
 * This class will perform the main scraping of a given web page to gather the necessary information.
 *
 */
public class Scraper {

    private URL webPageUrl;

    private String name;

    private String description;

    private String price;

    private String calories;

    public Scraper(String url) throws MalformedURLException {
        try {
            this.webPageUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new MalformedURLException("The URL provided is malformed, Exitting program");
        }
    }

    /**
     * This method will take in a web page in the form of a URL and scrape the underlying html in order to pull out the
     * key information for each of the products on the page.
     * 
     * @param webPageUrl
     *            The URL of the page to be scrapped.
     * @throws IOException
     */
    public List<Product> scrapeWebPage() throws IOException {

        List<Product> productsFound = new ArrayList<>();

        try {
            Document page;

            // Creates a DOM object of the webpage for the given URL
            page = Jsoup.connect(webPageUrl.toString()).get();

            // Finds all elements in the DOM that have the 'product' class associated to them
            Elements products = page.getElementsByClass("product");

            for (Element product : products) {

                scrapeProductElement(product);

                // Creates a product instance for the values that have just been scrapped
                if (StringUtils.isNotBlank(name)) {
                    if (StringUtils.isEmpty(calories)) {
                        productsFound.add(new Product(name, description, price));
                    } else {
                        productsFound.add(new FoodProduct(name, description, price, calories));
                    }
                }

                // Sets the fields back to null for the next product found
                name = null;
                description = null;
                price = null;
                calories = null;

            }
        } catch (IOException e) {
            throw new IOException(e.getLocalizedMessage());
        }

        return productsFound;

    }

    /**
     * For each product element found, we need to scrape the relevant information, in this case being the name,
     * description, price and number of calories (if applicable).
     * 
     * @param productElement
     *            The html element of the product
     * @param url
     *            The url of the page it was retrieved from
     * @throws IOException
     */
    protected void scrapeProductElement(Element productElement) throws IOException {
        String link = setNameAndGetForwardLink(productElement);
        price = getPrice(productElement);

        Document itemPage;
        try {
            // Creates a new url using the relative link retrieved from the name link
            URL itemPageUrl = new URL(webPageUrl, link);

            // Creates a new DOM Document of the products further information page in order to get the
            // additional information that wasn't present on the main page
            itemPage = Jsoup.connect(itemPageUrl.toString()).get();
            description = getDescription(itemPage);
            calories = getCalories(itemPage);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

    /**
     * Parses a html element representing a product to pull out the name and the forwarding link to the products further
     * details page.
     * 
     * @param productElement
     *            The html element from the DOM of a product
     * @return The relative URl path to the further product information page
     */
    protected String setNameAndGetForwardLink(Element productElement) {
        Elements linksToDetails = productElement.select("a[href]");
        if (!linksToDetails.isEmpty()) {
            name = linksToDetails.get(0).text();
            return linksToDetails.get(0).attr("href");
        }
        return null;
    }

    /**
     * Gets the price per unit of the product by scraping it from the html DOM object.
     * 
     * @param productElement
     *            The html DOM object for the product
     * @return The price per unit of the product
     */
    protected String getPrice(Element productElement) {
        Element pricing = productElement.select("p.pricePerUnit").first();
        if (null != pricing) {
            return pricing.text().replaceAll("[^0-9.]", "");
        }
        return null;
    }

    /**
     * Gets the description field for the product by scraping the relevant description field from the products further
     * information page. If there are multiple lines to the description, only the first line is returned.
     * 
     * @param itemPage
     *            The html DOM of the products further product details page
     * @return The description of the product
     */
    protected String getDescription(Document itemPage) {

        Element descElem = itemPage.select("h3:contains(Description)").first();
        if (null != descElem && !descElem.siblingElements().isEmpty()) {
            return descElem.nextElementSibling().text();
        }

        return null;
    }

    /**
     * Scrapes the amount of calories (in kcal) from the nutritional information (if it exists) off of the products
     * further information page.
     * 
     * @param itemPage
     *            The html DOM of the products further product details page
     * @return The amount of calories for the product as plain numbers, or null if not present.
     */
    protected String getCalories(Document itemPage) {

        Elements nutritionTable = itemPage.getElementsByClass("nutritionTable");

        if (!nutritionTable.isEmpty()) {
            for (Element row : nutritionTable.first().select("th")) {
                if (row.text().equals("Energy") && !row.parent().siblingElements().isEmpty()) {
                    return row.parent().nextElementSibling().child(0).text().replaceAll("[^0-9]", "");
                } else if (row.text().equals("Energy kcal") && !row.siblingElements().isEmpty()) {
                    return row.nextElementSibling().text().replaceAll("[^0-9]", "");
                }
            }
        }
        return null;
    }

}
