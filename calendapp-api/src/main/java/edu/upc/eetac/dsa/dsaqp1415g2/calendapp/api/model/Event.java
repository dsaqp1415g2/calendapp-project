package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.EventResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;

public class Event {
	@InjectLinks({
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "events", title = "Latest event", type = MediaType.CALENDAPP_API_EVENT_COLLECTION),
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "self-edit", title = "Event", type = MediaType.CALENDAPP_API_EVENT, method = "getEvent", bindings = @Binding(name = "eventid", value = "${instance.eventid}")),
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "who-is-comming", title = "Users comming", type = MediaType.CALENDAPP_API_USER_COLLECTION, method = "getUsersState", bindings = {@Binding(name = "eventid", value = "${instance.eventid}"), @Binding(name = "state", value = "accepted")}),
		@InjectLink(value = "/comments/{eventid}", style = Style.ABSOLUTE, rel = "comments", title = "Comments", type = MediaType.CALENDAPP_API_COMMENT_COLLECTION, bindings = @Binding(name = "name", value = "${instance.eventid}"))
	})
	private List<Link> links;
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
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
