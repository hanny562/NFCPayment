package com.example.hanny.nfcpayment.helper;

/**
 * Created by Hanny on 12/11/2017.
 */

public class SequenceNumber {
    private static int _currentNumber = 000000;

    public static String GetNextNumber() {
        _currentNumber++;
        return "NPPG" + Integer.toString(_currentNumber);
    }

}
