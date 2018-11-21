package com.sainsburys.test.transform;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.sainsburys.test.products.FoodProduct;
import com.sainsburys.test.products.Product;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;

/**
 * Unit tests for the {@link TransformToJson} class.
 *
 */
public class TransformToJsonTest {

    private List<Product> products;

    private Product product;

    private FoodProduct foodProduct;

    private TransformToJson transformer;

    @Before
    public void before() {
        transformer = new TransformToJson();

        product = new Product("Product1", "Description1", "10.00");
        foodProduct = new FoodProduct("FoodProduct1", "FoodDesc1", "5.00", "42");

        products = new ArrayList<>();
        products.add(product);
        products.add(foodProduct);
    }

    /**
     * Verifies that correct JSON with the fields expected is created for the given input.
     */
    @Test
    public void testCreateJsonForProducts_correctJsonCreated() throws JSONException {
        JSONObject result = transformer.createJsonForProducts(products);
        JSONAssert.assertEquals("The JSONObject returned was not what was expected.",
                "{result:[{title:\"Product1\", unit_price:\"10.00\", description:\"Description1\"},{title:\"FoodProduct1\", kcal_per_100g:\"42\", unit_price:\"5.00\", description:\"FoodDesc1\"}], total:{gross:\"15.00\", vat:\"3.00\"}}",
                result, JSONCompareMode.LENIENT);
    }

    /**
     * Verifies that correct JSON with the fields expected is created for the given input.
     */
    @Test
    public void testCreateJsonForProducts_noProducts_correctJsonCreated() throws JSONException {
        JSONObject result = transformer.createJsonForProducts(new ArrayList<>());
        JSONAssert.assertEquals("The JSONObject returned was not what was expected.",
                "{result:[], total:{gross:\"0.00\", vat:\"0.00\"}}", result, JSONCompareMode.LENIENT);
    }

    /**
     * Tests that if an {@link JSONException} is thrown putting the result together, a blank {@link JSONObject} is
     * returned.
     */
    @Test
    public void testCreateJsonForProducts_jsonExceptionCreatingResult_emptyJsonResult(@Mocked JSONObject resultObj)
            throws JSONException {

        new Expectations(transformer) {
            {
                new JSONObject();
                result = resultObj;

                transformer.createTotalJson();
                result = resultObj;

                resultObj.put("result", any);
                result = new JSONException("ERROR");
            }
        };

        JSONObject result = transformer.createJsonForProducts(new ArrayList<>());
        JSONAssert.assertEquals("The JSONObject returned was not what was expected.", "{}", result,
                JSONCompareMode.LENIENT);
    }

    /**
     * Tests the correct JSON is returned for a given {@link Product} and the correct totals are set in the rolling
     * totals.
     */
    @Test
    public void testCreateProductJson_product_jsonWithProductInfo() throws JSONException {

        JSONObject result = transformer.createProductJson(product);

        JSONAssert.assertEquals("The JSONObject returned was not what was expected.",
                "{title:\"Product1\", unit_price:\"10.00\", description:\"Description1\"}",
                result, JSONCompareMode.LENIENT);

        Assert.assertThat("The totalPrice is incorrect", Deencapsulation.getField(transformer, "totalPrice"),
                CoreMatchers.is(10.00));
        Assert.assertThat("The totalPrice is incorrect", Deencapsulation.getField(transformer, "totalVat"),
                CoreMatchers.is(2.00));
    }

    /**
     * Tests that when a {@link JSONException} is thrown creating a {@link Product} {@link JSONObject}, a message is
     * logged to the terminal and null is returned.
     */
    @Test
    public void testCreateProductJson_productJsonException_null(@Mocked JSONObject jsonObj) throws JSONException {

        new Expectations() {
            {
                new JSONObject();
                result = jsonObj;

                jsonObj.put(anyString, any);
                result = new JSONException("JSON Excpetion");
            }
        };

        JSONObject result = transformer.createProductJson(product);
        Assert.assertThat("The object returned should have been null", result, CoreMatchers.nullValue());

    }

    /**
     * Tests the correct JSON is returned for a given {@link FoodProduct} and the correct totals are set in the rolling
     * totals.
     */
    @Test
    public void testCreateProductJson_foodProduct_jsonWithProductInfo() throws JSONException {

        JSONObject result = transformer.createProductJson(foodProduct);

        JSONAssert.assertEquals("The JSONObject returned was not what was expected.",
                "{title:\"FoodProduct1\", kcal_per_100g:\"42\", unit_price:\"5.00\", description:\"FoodDesc1\"}",
                result, JSONCompareMode.LENIENT);

        Assert.assertThat("The totalPrice is incorrect", Deencapsulation.getField(transformer, "totalPrice"),
                CoreMatchers.is(5.00));
        Assert.assertThat("The totalPrice is incorrect", Deencapsulation.getField(transformer, "totalVat"),
                CoreMatchers.is(1.00));
    }

    /**
     * Tests that when a {@link JSONException} is thrown creating a {@link FoodProduct} {@link JSONObject}, a message is
     * logged to the terminal and null is returned.
     */
    @Test
    public void testCreateProductJson_foodProductJsonException_null(@Mocked JSONObject jsonObj) throws JSONException {

        new Expectations() {
            {
                new JSONObject();
                result = jsonObj;

                jsonObj.put(anyString, any);
                result = new JSONException("JSON Excpetion");
            }
        };

        JSONObject result = transformer.createProductJson(foodProduct);
        Assert.assertThat("The object returned should have been null", result, CoreMatchers.nullValue());

    }

    /**
     * Tests that when a {@link JSONException} is thrown creating the total pricing {@link JSONObject}, a message is
     * logged to the terminal and a blank {@link JSONObject} is returned.
     */
    @Test
    public void testCreateTotalJson_jsonExceptionThrown_exceptionCaughtNewObjectReturned(@Mocked JSONObject jsonObj)
            throws JSONException {

        new Expectations() {
            {
                new JSONObject();
                result = jsonObj;

                jsonObj.put(anyString, any);
                result = new JSONException("JSON Excpetion");
            }
        };

        JSONObject result = transformer.createTotalJson();

        JSONAssert.assertEquals("The JSONObject returned was not what was expected", "{}", result,
                JSONCompareMode.LENIENT);

    }

    /**
     * Tests that the correct expected {@link JSONObject} is created for the total pricing with the values given.
     */
    @Test
    public void testCreateTotalJson() throws JSONException {
        Deencapsulation.setField(transformer, "totalPrice", 15.00);
        Deencapsulation.setField(transformer, "totalVat", 3.00);

        JSONObject result = transformer.createTotalJson();

        JSONAssert.assertEquals("The JSONObject returned was not what was expected", "{gross:\"15.00\", vat:\"3.00\"}",
                result, JSONCompareMode.LENIENT);
    }

}
