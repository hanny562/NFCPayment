package com.example.hanny.nfcpayment.model;

/**
 * Created by Hanny on 1/11/2017.
 */

public class Item {
    private String itemName;
    private String itemId;
    private String itemPrice;
    private int itemQuantity;
    private double itemtotalPrice;

    public Item(double itemtotalPrice) {
        this.itemtotalPrice = itemtotalPrice;
    }

    public Item() {

    }

    public Item(String itemName,String itemId, String itemPrice, int itemQuantity) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
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

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public double getItemtotalPrice() {

        return itemtotalPrice;
    }

    public void setItemtotalPrice(double itemtotalPrice) {
        this.itemtotalPrice = itemtotalPrice;
    }
}
