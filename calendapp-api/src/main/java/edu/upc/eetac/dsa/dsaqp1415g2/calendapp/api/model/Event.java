package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

public class Event {
	private int eventid;
	private int userid;
	private int groupid;
	private long dateInitial;
	private long dateFinish;

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
