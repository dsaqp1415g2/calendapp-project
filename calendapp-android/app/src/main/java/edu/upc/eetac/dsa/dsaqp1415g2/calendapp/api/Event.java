package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by angel on 8/06/15.
 */
public class Event {
    private int eventid;
    private int userid;
    private int groupid;
    private String name;
    private long dateInitial;
    private long dateFinish;
    private long lastModified;
    private Map<String, Link> links = new HashMap<String, Link>();
    private String eTag;

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateInitial() {
        return dateInitial;
    }

    public void setDateInitial(long dateInitial) {
        this.dateInitial = dateInitial;
    }

    public long getDateFinish() {
        return dateFinish;
    }

    public void setDateFinish(long dateFinish) {
        this.dateFinish = dateFinish;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
