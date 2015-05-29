package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jordi on 29/05/2015.
 */
public class CalendappRootAPI {

    private Map<String, Link> links;

    public CalendappRootAPI() {
        links = new HashMap<String, Link>();
    }

    public Map<String, Link> getLinks() {
        return links;
    }

}