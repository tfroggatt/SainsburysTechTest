package com.sainsburys.test.products;

/**
 * This class will hold a Food Product using the same values scrapped from the web page.
 * This object is defined as food if there is nutritional information that has been scrapped,
 * alongside the information found for a Product. 
 */
public class FoodProduct extends Product {

    private String calories;

    public FoodProduct(String name, String description, String price, String calories) {
        super(name, description, price);
        this.calories = calories;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

}
