package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.EventResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;

public class EventCollection {
	
	@InjectLinks({
		@InjectLink(resource = EventResource.class, style = Style.ABSOLUTE, rel = "create-event", title = "Create event", type = MediaType.CALENDAPP_API_EVENT),
	//	@InjectLink(value = "/events/group/{groupid}?before={before}", style = Style.ABSOLUTE, rel = "previous", title = "Previous events", type = MediaType.CALENDAPP_API_EVENT_COLLECTION, bindings = {@Binding(name = "before", value = "${instance.lastTimestamp}" ), @Binding(name = "groupid", value = "${instance.events.get(0).getGroupid()")})
//		@InjectLink(value = "/events/group/{groupid}?after={after}", style = Style.ABSOLUTE, rel = "current", title = "Newest events", type = MediaType.CALENDAPP_API_EVENT_COLLECTION, bindings = @Binding(name = "after", value = "${instance.firstTimestamp}" ))
	})
	
	private List<Link> links;
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	private long firstTimestamp;
	private long lastTimestamp;
	private List<Event> events;

	public EventCollection() {
		super();
		events = new ArrayList<>();
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public long getFirstTimestamp() {
		return firstTimestamp;
	}

	public void setFirstTimestamp(long firstTimestamp) {
		this.firstTimestamp = firstTimestamp;
	}

	public long getLastTimestamp() {
		return lastTimestamp;
	}

	public void setLastTimestamp(long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

}
