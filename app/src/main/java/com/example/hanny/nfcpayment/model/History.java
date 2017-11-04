package com.example.hanny.nfcpayment.model;

/**
 * Created by Hanny on 2/11/2017.
 */

public class History {
    private String hBillId;
    private String hDate;
    private double hTotalPrice;

    public History(){
        super();
    }

    public History(String hBillId, String hDate, double hTotalPrice) {
        this.hBillId = hBillId;
        this.hDate = hDate;
        this.hTotalPrice = hTotalPrice;
    }

    public String gethBillId() {
        return hBillId;
    }

    public void sethBillId(String hBillId) {
        this.hBillId = hBillId;
    }

    public String gethDate() {
        return hDate;
    }

    public void sethDate(String hDate) {
        this.hDate = hDate;
    }

    public double gethTotalPrice() {
        return hTotalPrice;
    }

    public void sethTotalPrice(double hTotalPrice) {
        this.hTotalPrice = hTotalPrice;
    }
}
