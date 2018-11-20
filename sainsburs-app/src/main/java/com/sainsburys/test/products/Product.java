package com.sainsburys.test.products;

import java.text.DecimalFormat;

/**
 * This class will create an object to hold Product information that has been scrapped from the
 * web page.
 */
public class Product {

	//Name of the product
    private String name;

    //Description of the product (first line if multiple)
    private String description;

    //The price per unit of the product
    private double price;

    //The VAT based off of the unit price
    private double vat;

    public Product(String name, String description, String price) {
        this.name = name;
        this.description = description;
        this.price = Double.parseDouble(price);
        calculateVat();
    }

    /**
     * Calculates the VAT on the product based off the price per unit found from the web page.
     */
    private void calculateVat() {
        DecimalFormat df = new DecimalFormat("##.00");
        vat = Double.parseDouble(df.format(price * 0.2));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVat() {
        return vat;
    }

}
