package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

/**
 * Created by Jordi on 29/05/2015.
 */
public class AppException extends Exception {
    public AppException() {
        super();
    }

    public AppException(String detailMessage) {
        super(detailMessage);
    }
}
