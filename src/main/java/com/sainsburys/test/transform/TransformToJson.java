package com.sainsburys.test.transform;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sainsburys.test.products.FoodProduct;
import com.sainsburys.test.products.Product;

public class TransformToJson {

    private double totalPrice;

    private double totalVat;

    /**
     * Given a list of products scraped from a webpage, this will turn those products into JSON with the resulting
     * pricing of all products.
     * 
     * @param products
     *            The list of products scraped
     * @return The {@link JSONObject} holding the information
     */
    public JSONObject createJsonForProducts(List<Product> products) {
        List<JSONObject> jsonProductObjects = new ArrayList<>();
        JSONObject result = new JSONObject();

        // Creates a separate JSONObject for each product found and adds it to a list of JSONObjects to be turned into a
        // JSONArray
        for (Product product : products) {
            JSONObject productJson = createProductJson(product);
            if (null != productJson) {
                jsonProductObjects.add(productJson);
            }
        }

        // Creates a JSONObject to hold the total pricing
        JSONObject totals = createTotalJson();

        JSONArray productsJson;

        if (jsonProductObjects.isEmpty()) {
            productsJson = new JSONArray();
        } else {
            productsJson = new JSONArray(jsonProductObjects);
        }

        // Accumulates all the necessary JSON information into a single JSONObject
        try {
            result.put("result", productsJson);
            result.put("total", totals);
        } catch (JSONException e) {
            System.out.println(
                    "Caught a JSON Exception whilst creating the result to send back with the following exception"
                            + e.getLocalizedMessage());
            result = new JSONObject();
        }

        return result;
    }

    /**
     * Creates the {@link JSONObject} that will have the information of a given product object.
     * 
     * @param product
     *            The Java product object found through scraping
     * @return The JSON representation of the object
     */
    protected JSONObject createProductJson(Product product) {
        JSONObject jsonProduct = new JSONObject();
        try {
            jsonProduct.put("title", product.getName());
            if (product instanceof FoodProduct) {
                jsonProduct.put("kcal_per_100g", ((FoodProduct) product).getCalories());
            }
            jsonProduct.put("unit_price", String.format("%.2f", product.getPrice()));
            jsonProduct.put("description", product.getDescription());

            // If everything is added to the JSONObject successfully add the prices to the running totals
            totalPrice = totalPrice + product.getPrice();
            totalVat = totalVat + product.getVat();
        } catch (JSONException e) {
            System.out.println("Caught a JSON Exception whilst creating " + product.getName()
                    + " and has therefore been ommited from the results");
            return null;
        }

        return jsonProduct;
    }

    /**
     * Creates the {@link JSONObject} that holds the final pricing figures for the products successfully turned to JSON.
     * 
     * @return The {@link JSONObject} holding the total price information
     */
    protected JSONObject createTotalJson() {
        JSONObject totals = new JSONObject();

        try {
            totals.put("gross", String.format("%.2f", totalPrice));
            totals.put("vat", String.format("%.2f", totalVat));
        } catch (JSONException e) {
            System.out.println(
                    "Caught a JSON Exception whilst creating the totals and a blank JSONObject has been returned");
            return new JSONObject();
        }

        return totals;
    }

}
