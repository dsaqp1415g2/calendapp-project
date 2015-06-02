package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jordi on 29/05/2015.
 */
public class SimpleLinkHeaderParser {
    private final static Pattern pattern = Pattern
            .compile("\\s*<(.*)>\\s*(.*)");

    public final static Link parseLink(String linkHeader)
            throws Exception {
        Link link = new Link();
        Matcher m = pattern.matcher(linkHeader);
        if (!m.matches()) {
            throw new Exception("illegal Link header field value:"
                    + linkHeader);
        }

        try {
            link.setTarget(URLDecoder.decode(m.group(1), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
        String pars[] = m.group(2).split(";");
        for (String s : pars) {
            String parameter = s.trim();
            if (parameter.length() == 0)
                continue;
            String[] nvp = parameter.split("=");
            link.getParameters().put(nvp[0],
                    nvp[1].substring(1, nvp[1].length() - 1));
        }

        return link;
    }
}