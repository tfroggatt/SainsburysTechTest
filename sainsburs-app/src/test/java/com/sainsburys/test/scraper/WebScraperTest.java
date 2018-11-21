package com.sainsburys.test.scraper;

import org.hamcrest.CoreMatchers;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;

public class WebScraperTest {

    @Mocked
    Document document;

    @Mocked
    Element productElement;

    private WebScraper scraper;

    @Before
    public void before() {
        scraper = new WebScraper("url");
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
