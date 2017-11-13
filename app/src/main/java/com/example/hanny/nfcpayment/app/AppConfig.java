package com.example.hanny.nfcpayment.app;

/**
 * Created by Hanny on 28/10/2017.
 */

public class AppConfig {
    private static String DOMAIN = "http://192.168.0.108";
    public static String URL_LOGIN = DOMAIN + "/login.php";
    public static String URL_REGISTER = DOMAIN + "/register.php";
    public static String URL_POPULATECART = DOMAIN + "/populatecartbyemail.php?email=";
    public static String URL_GETITEM = DOMAIN + "/item.php?item_id=";
    public static String URL_ADDITEMINTOCART = DOMAIN + "/addItemCart.php";
    public static String URL_POPULATEHISTORY = DOMAIN + "/populatehistory.php?email=";
    public static String URL_DELETEITEMCART = DOMAIN +"/delete_item.php?email=";
    public static String URL_AFTERPAYMENTHISTORY = DOMAIN + "/payment_history.php";
    public static String URL_DROPITEM = DOMAIN + "/drop_item.php?item_id=";
}
