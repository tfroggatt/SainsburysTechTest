package com.sainsburys.test.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sainsburys.test.products.FoodProduct;
import com.sainsburys.test.products.Product;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

/**
 * Unit test class for the {@link WebScraper} test.
 *
 */
public class WebScraperTest {

    @Mocked
    Document document;

    @Mocked
    Element productElement;

    private WebScraper scraper;

    @Before
    public void before() throws MalformedURLException {
        scraper = new WebScraper("http://www.google.com");
    }

    /**
     * Tests an {@link IOException} is caught and thrown when {@link Jsoup} is unable to get the web page DOM for the
     * {@link URL} given.
     */
    @Test(expected = IOException.class)
    public void testScrapeWebPage_ioExceptionGettingPage_ioExceptionCaughtAndThrown(@Mocked Connection connection)
            throws IOException {

        new Expectations(Jsoup.class) {
            {
                Jsoup.connect(Deencapsulation.getField(scraper, "webPageUrl").toString());
                result = connection;

                connection.get();
                result = new IOException("Error getting web page DOM");
            }
        };

        scraper.scrapeWebPage();
    }

    /**
     * Tests that when there are no elements with the class 'product', then no java product instances are created.
     */
    @Test
    public void testScrapeWebPage_noProductElements_zeroProductObjects(@Mocked Connection connection,
            @Mocked Product product, @Mocked FoodProduct foodProduct) throws IOException {

        new Expectations(Jsoup.class) {
            {
                Jsoup.connect(Deencapsulation.getField(scraper, "webPageUrl").toString());
                result = connection;

                connection.get();
                result = document;

                document.getElementsByClass("product");
                result = new Elements();
            }
        };

        List<Product> result = scraper.scrapeWebPage();
        Assert.assertThat("There should have been no products found via scraping", result.isEmpty(),
                CoreMatchers.is(true));

        new Verifications() {
            {
                new Product(anyString, anyString, anyString);
                times = 0;

                new FoodProduct(anyString, anyString, anyString, anyString);
                times = 0;
            }
        };

    }

    /**
     * Tests that no product objects are created if when trying to scrape the product elements found, no name is found
     * and therefore we don't know what to call the product.
     */
    @Test
    public void testScrapeWebPage_productElementsFoundNoName_zeroProductObjects(@Mocked Connection connection,
            @Mocked Element productElement, @Mocked Product product, @Mocked FoodProduct foodProduct)
            throws IOException {

        new Expectations(scraper, Jsoup.class) {
            {
                Jsoup.connect(Deencapsulation.getField(scraper, "webPageUrl").toString());
                result = connection;

                connection.get();
                result = document;

                document.getElementsByClass("product");
                result = new Elements(productElement);

                // Acts as a verification
                scraper.scrapeProductElement(productElement);
            }
        };

        List<Product> result = scraper.scrapeWebPage();
        Assert.assertThat("There should have been no products found via scraping", result.isEmpty(),
                CoreMatchers.is(true));

        new Verifications() {
            {
                new Product(anyString, anyString, anyString);
                times = 0;

                new FoodProduct(anyString, anyString, anyString, anyString);
                times = 0;
            }
        };
    }

    /**
     * Tests that if a name and a price are the only things found from the product element, a standard product is
     * created to hold the information.
     */
    @Test
    public void testScrapeWebPage_productElementsFoundNamePriceFound_productObjectCreated(@Mocked Connection connection,
            @Mocked Element productElement, @Mocked Product product, @Mocked FoodProduct foodProduct)
            throws IOException {

        Deencapsulation.setField(scraper, "name", "productName");
        Deencapsulation.setField(scraper, "price", "10.00");

        new Expectations(scraper, Jsoup.class) {
            {
                Jsoup.connect(Deencapsulation.getField(scraper, "webPageUrl").toString());
                result = connection;

                connection.get();
                result = document;

                document.getElementsByClass("product");
                result = new Elements(productElement);

                // Acts as a verification
                scraper.scrapeProductElement(productElement);
            }
        };

        List<Product> result = scraper.scrapeWebPage();
        Assert.assertThat("There should have been 1 product found via scraping", result.size(), CoreMatchers.is(1));
        Assert.assertThat("The instance of the product found should be Product", result.get(0),
                CoreMatchers.instanceOf(Product.class));

        new Verifications() {
            {
                new Product("productName", null, "10.00");

                new FoodProduct(anyString, anyString, anyString, anyString);
                times = 0;
            }
        };
    }

    /**
     * Tests that if a name and a price are the only things found from the product element, a standard product is
     * created to hold the information.
     */
    @Test
    public void testScrapeWebPage_productElementsFoundNamePriceCaloriesFound_productObjectCreated(
            @Mocked Connection connection, @Mocked Element productElement, @Mocked Product product,
            @Mocked FoodProduct foodProduct) throws IOException {

        Deencapsulation.setField(scraper, "name", "productName");
        Deencapsulation.setField(scraper, "price", "10.00");
        Deencapsulation.setField(scraper, "calories", "23");

        new Expectations(scraper, Jsoup.class) {
            {
                Jsoup.connect(Deencapsulation.getField(scraper, "webPageUrl").toString());
                result = connection;

                connection.get();
                result = document;

                document.getElementsByClass("product");
                result = new Elements(productElement);

                // Acts as a verification
                scraper.scrapeProductElement(productElement);
            }
        };

        List<Product> result = scraper.scrapeWebPage();
        Assert.assertThat("There should have been 1 product found via scraping", result.size(), CoreMatchers.is(1));
        Assert.assertThat("The instance of the product found should be FoodProduct", result.get(0),
                CoreMatchers.instanceOf(FoodProduct.class));

        new Verifications() {
            {
                new Product("productName", null, "10.00");
                times = 0;

                new FoodProduct("productName", null, "10.00", "23");
            }
        };
    }

    /**
     * Tests that when no errors are thrown, the attributes required are scraped from the web page url, and the products
     * further information page and stored on the scraper instance.
     */
    @Test
    public void testScrapeProductElement_allAttributesScrapedAndStored(@Mocked Connection connection)
            throws IOException {

        new Expectations(scraper, Jsoup.class) {
            {
                scraper.setNameAndGetForwardLink(productElement);
                result = "/images";

                scraper.getPrice(productElement);
                result = "10.00";

                Jsoup.connect("http://www.google.com/images");
                result = connection;

                connection.get();
                result = document;

                scraper.getDescription(document);
                result = "description";

                scraper.getCalories(document);
                result = "45";

            }
        };

        scraper.scrapeProductElement(productElement);

        Assert.assertThat("The price of the Product is incorrect", Deencapsulation.getField(scraper, "price"),
                CoreMatchers.is("10.00"));
        Assert.assertThat("The description of the Product is incorrect",
                Deencapsulation.getField(scraper, "description"), CoreMatchers.is("description"));
        Assert.assertThat("The number of calories of the Product is incorrect",
                Deencapsulation.getField(scraper, "calories"), CoreMatchers.is("45"));
    }

    /**
     * Tests that an {@link IOException} is caught and thrown if the link found for the products further information
     * page creates a malformed URL when put relatively against the starting URL.
     */
    @Test(expected = IOException.class)
    public void testScrapeProductElement_malformedExceptionCreatingNewUrl_ioExceptionThrown(
            @Mocked Connection connection) throws IOException {

        new Expectations(scraper, URL.class) {
            {
                scraper.setNameAndGetForwardLink(productElement);
                result = "/images";

                scraper.getPrice(productElement);
                result = "10.00";

                new URL(Deencapsulation.getField(scraper, "webPageUrl"), "/images");
                result = new MalformedURLException("Malformed URL");

            }
        };

        scraper.scrapeProductElement(productElement);
    }

    /**
     * Tests an {@link IOException} is caught and thrown when {@link Jsoup} is unable to get the web page DOM for the
     * {@link URL} given.
     */
    @Test(expected = IOException.class)
    public void testScrapeProductElement_ioExceptionGettingConnection_ioExceptionThrown(@Mocked Connection connection)
            throws IOException {

        new Expectations(scraper, Jsoup.class) {
            {
                scraper.setNameAndGetForwardLink(productElement);
                result = "/images";

                scraper.getPrice(productElement);
                result = "10.00";

                Jsoup.connect("http://www.google.com/images");
                result = connection;

                connection.get();
                result = new IOException("Exception getting web page");

            }
        };

        scraper.scrapeProductElement(productElement);
    }

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

        String result = scraper.setNameAndGetForwardLink(productElement);
        Assert.assertThat("Null should be returned as no elements found", result, CoreMatchers.nullValue());
    }

    /**
     * Tests that the link is returned from the first href element found within the element is passed in, and the name
     * is set.
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

        String result = scraper.setNameAndGetForwardLink(productElement);
        Assert.assertThat("The link returned from the href element was incorrect", result,
                CoreMatchers.is("relative_link"));
        Assert.assertThat("The name set in the class is inocrrect", Deencapsulation.getField(scraper, "name"),
                CoreMatchers.is("product_name"));
    }

    /**
     * Tests that null is returned is there are no
     * <p>
     * elements with pricePerUnit on them found in the element passed in.
     */
    @Test
    public void testGetPrice_noPricePerUnitElements_null() {
        new Expectations() {
            {
                productElement.select("p.pricePerUnit");
                result = new Elements();
            }
        };

        String result = scraper.getPrice(productElement);
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

        String result = scraper.getPrice(productElement);
        Assert.assertThat("The price returned from the pricePerUnit element was incorrect", result,
                CoreMatchers.is("9.00"));
    }

    /**
     * Test the correct price is returned from the pricePerUnit element if one is found on the element passed in. Only
     * numbers should be returned regardless of any other characters.
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

        String result = scraper.getPrice(productElement);
        Assert.assertThat("The price returned from the pricePerUnit element was incorrect", result,
                CoreMatchers.is("9.00"));
    }

    /**
     * Tests null is returned if no elements contain the word 'Description' on the {@link Document} passed in.
     */
    @Test
    public void testGetDescription_noElementsWithDescriptionText_null() {

        new Expectations() {
            {
                document.select("h3:contains(Description)");
                result = new Elements();
            }
        };

        String result = scraper.getDescription(document);
        Assert.assertThat("Null should be returned as no elements found containing the word 'Description'", result,
                CoreMatchers.nullValue());
    }

    /**
     * Tests that null is returned if there is an element with text 'Description' but it has no sibling elements to
     * represent the actual description.
     */
    @Test
    public void testGetDescription_elementsWithDescriptionTextNoSiblings_null(@Mocked Element descElement) {

        new Expectations() {
            {
                document.select("h3:contains(Description)");
                result = new Elements(descElement);

                descElement.siblingElements();
                result = new Elements();
            }
        };

        String result = scraper.getDescription(document);
        Assert.assertThat("Null should be returned as no elements found containing the word 'Description'", result,
                CoreMatchers.nullValue());
    }

    /**
     * Tests that the correct description text is returned if there is an element with text 'Description' that has
     * sibling elements to represent the actual description.
     */
    @Test
    public void testGetDescription_elementsWithDescriptionTextSiblingElement_description(@Mocked Element descElement,
            @Mocked Element siblingElemnt) {

        new Expectations() {
            {
                document.select("h3:contains(Description)");
                result = new Elements(descElement);

                descElement.siblingElements();
                result = new Elements(siblingElemnt);

                siblingElemnt.text();
                result = "description";
            }
        };

        String result = scraper.getDescription(document);
        Assert.assertThat("The description text returned is incorrect", result, CoreMatchers.is("description"));
    }

    /**
     * Tests that null is returned when there is no element on the page with the class 'nutrionalTable'.
     */
    @Test
    public void testGetCalories_noNutritionalTable_null() {

        new Expectations() {
            {
                document.getElementsByClass("nutritionTable");
                result = new Elements();
            }
        };

        String result = scraper.getCalories(document);
        Assert.assertThat("Null should be returned as no elements found with the class 'nutirionTable'", result,
                CoreMatchers.nullValue());
    }

    /**
     * Tests that null is returned when there an element on the page with the class 'nutrionalTable' but it does not
     * contain a row with the text 'Energy' or 'Energy kcal'.
     */
    @Test
    public void testGetCalories_nutritionalTableNoEnergyRow_null(@Mocked Element nutritionTable,
            @Mocked Element tableRow) {

        new Expectations() {
            {
                document.getElementsByClass("nutritionTable");
                result = new Elements(nutritionTable);

                nutritionTable.select("th");
                result = new Elements(tableRow);

                tableRow.text();
                result = "randomText";
            }
        };

        String result = scraper.getCalories(document);
        Assert.assertThat(
                "Null should be returned as the element found with the class 'nutirionTable' does not have a th element with 'Energy' or 'Energy kcal'",
                result, CoreMatchers.nullValue());
    }

    /**
     * Tests that null is returned when there an element on the page with the class 'nutrionalTable' that has a th row
     * with the text 'Energy', but its parent has no sibling elements.
     */
    @Test
    public void testGetCalories_nutritionalTableEnergyRowNoSiblingElements_null(@Mocked Element nutritionTable,
            @Mocked Element tableRow, @Mocked Element parentElement) {

        new Expectations() {
            {
                document.getElementsByClass("nutritionTable");
                result = new Elements(nutritionTable);

                nutritionTable.select("th");
                result = new Elements(tableRow);

                tableRow.text();
                result = "Energy";

                tableRow.parent();
                result = parentElement;

                parentElement.siblingElements();
                result = new Elements();
            }
        };

        String result = scraper.getCalories(document);
        Assert.assertThat(
                "Null should be returned as the element found with the class 'nutirionTable' has a th element with 'Energy' or 'Energy kcal' but has no sibling elements",
                result, CoreMatchers.nullValue());
    }

    /**
     * Tests that the number value of calories is returned when there an element on the page with the class
     * 'nutrionalTable' that contains a row with the text 'Energy' and has a sibling node containing the number value.
     */
    @Test
    public void testGetCalories_nutritionalTableEnergyRowSiblingElementsNumberCalories_calories(
            @Mocked Element nutritionTable, @Mocked Element tableRow, @Mocked Element parentElement,
            @Mocked Element calorieElement) {

        new Expectations() {
            {
                document.getElementsByClass("nutritionTable");
                result = new Elements(nutritionTable);

                nutritionTable.select("th");
                result = new Elements(tableRow);

                tableRow.text();
                result = "Energy";

                tableRow.parent();
                result = parentElement;

                parentElement.siblingElements();
                result = new Elements(calorieElement);

                calorieElement.text();
                result = "55";
            }
        };

        String result = scraper.getCalories(document);
        Assert.assertThat("The number of calories returned is incorrect", result, CoreMatchers.is("55"));
    }

    /**
     * Tests that the number value of calories is returned when there an element on the page with the class
     * 'nutrionalTable' that contains a row with the text 'Energy' and has a sibling node containing the value with
     * extra characters.
     */
    @Test
    public void testGetCalories_nutritionalTableEnergyRowSiblingElementsStringCalories_caloriesStrippedToNumbersOnly(
            @Mocked Element nutritionTable, @Mocked Element tableRow, @Mocked Element parentElement,
            @Mocked Element calorieElement) {

        new Expectations() {
            {
                document.getElementsByClass("nutritionTable");
                result = new Elements(nutritionTable);

                nutritionTable.select("th");
                result = new Elements(tableRow);

                tableRow.text();
                result = "Energy";

                tableRow.parent();
                result = parentElement;

                parentElement.siblingElements();
                result = new Elements(calorieElement);

                calorieElement.text();
                result = "55kcal";
            }
        };

        String result = scraper.getCalories(document);
        Assert.assertThat("The number of calories returned is incorrect", result, CoreMatchers.is("55"));
    }

    /**
     * Tests that null is returned when there an element on the page with the class 'nutrionalTable'has a th row that
     * contains the text 'Energy kcal' but has no sibling elements.
     */
    @Test
    public void testGetCalories_nutritionalTableEnergyKcalRowNoSiblingElements_null(
            @Mocked Element nutritionTable, @Mocked Element tableRow) {

        new Expectations() {
            {
                document.getElementsByClass("nutritionTable");
                result = new Elements(nutritionTable);

                nutritionTable.select("th");
                result = new Elements(tableRow);

                tableRow.text();
                result = "Energy kcal";

                tableRow.siblingElements();
                result = new Elements();
            }
        };

        String result = scraper.getCalories(document);
        Assert.assertThat(
                "Null should be returned as the element found with the class 'nutirionTable' has a th element with 'Energy' or 'Energy kcal' but has no sibling elements",
                result, CoreMatchers.nullValue());
    }

    /**
     * Tests that the number value of calories is returned when there an element on the page with the class
     * 'nutrionalTable' that contains a row with the text 'Energy kcal' and has a sibling node containing the number
     * value.
     */
    @Test
    public void testGetCalories_nutritionalTableEnergyKcalRowSiblingElementsNumberCalories_calories(
            @Mocked Element nutritionTable, @Mocked Element tableRow, @Mocked Element calorieElement) {

        new Expectations() {
            {
                document.getElementsByClass("nutritionTable");
                result = new Elements(nutritionTable);

                nutritionTable.select("th");
                result = new Elements(tableRow);

                tableRow.text();
                result = "Energy kcal";

                tableRow.siblingElements();
                result = new Elements(calorieElement);

                calorieElement.text();
                result = "55";
            }
        };

        String result = scraper.getCalories(document);
        Assert.assertThat("The number of calories returned is incorrect", result, CoreMatchers.is("55"));
    }

    /**
     * Tests that the number value of calories is returned when there an element on the page with the class
     * 'nutrionalTable' that contains a row with the text 'Energy kcal' and has a sibling node containing the value with
     * extra characters.
     */
    @Test
    public void testGetCalories_nutritionalTableEnergyKcalRowSiblingElementsStringCalories_caloriesStrippedToNumbersOnly(
            @Mocked Element nutritionTable, @Mocked Element tableRow, @Mocked Element calorieElement) {

        new Expectations() {
            {
                document.getElementsByClass("nutritionTable");
                result = new Elements(nutritionTable);

                nutritionTable.select("th");
                result = new Elements(tableRow);

                tableRow.text();
                result = "Energy kcal";

                tableRow.siblingElements();
                result = new Elements(calorieElement);

                calorieElement.text();
                result = "55kcal";
            }
        };

        String result = scraper.getCalories(document);
        Assert.assertThat("The number of calories returned is incorrect", result, CoreMatchers.is("55"));
    }

}
