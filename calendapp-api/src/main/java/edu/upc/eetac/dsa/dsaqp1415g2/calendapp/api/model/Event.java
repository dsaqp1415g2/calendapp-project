package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

public class Event {
	private int eventid;
	private int userid;
	private int groupid;
	private String name;
	private long dateInitial;
	private long dateFinish;
	private long lastModified;

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}
