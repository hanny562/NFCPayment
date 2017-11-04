package com.example.hanny.nfcpayment.model;

/**
 * Created by Hanny on 1/11/2017.
 */

public class Item {
    private String itemName;
    private String itemId;
    private double itemPrice;
    private String itemQuantity;
    private double itemtotalPrice;
    private String itemAddedDate;

    public Item(double itemtotalPrice) {
        this.itemtotalPrice = itemtotalPrice;
    }

    public Item() {
        super();

    }

    public Item(String itemName,String itemId, double itemPrice, String itemQuantity, String itemAddedDate) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.itemAddedDate = itemAddedDate;
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public double getItemtotalPrice() {

        return itemtotalPrice;
    }

    public void setItemtotalPrice(double itemtotalPrice) {
        this.itemtotalPrice = itemtotalPrice;
    }

    public String getItemAddedDate() {
        return itemAddedDate;
    }

    public void setItemAddedDate(String itemAddedDate) {
        this.itemAddedDate = itemAddedDate;
    }
}
