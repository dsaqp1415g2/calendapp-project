package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.List;


import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.GroupResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;

public class Group {
	@InjectLinks({
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "groups", title = "Latest Groups", type = MediaType.CALENDAPP_API_GROUP_COLLECTION),
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "self", title = "Group", type = MediaType.CALENDAPP_API_GROUP, method = "getGroup", bindings = @Binding(name = "groupid", value = "${instance.groupid}")),
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "users-of-group", title ="Users of this group", type = MediaType.CALENDAPP_API_USER_COLLECTION, method = "getUsers", bindings = {@Binding(name = "groupid", value ="${instance.groupid}"), @Binding(name = "action", value = "accepted")}),	
		@InjectLink(value="/events/group/{groupid}", style = Style.ABSOLUTE, rel = "events-of-group", title = "Events of this group", type = MediaType.CALENDAPP_API_EVENT_COLLECTION, bindings = @Binding(name = "groupid", value = "${instance.groupid}"))
	})
	private List<Link> links;
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	private int groupid;
	private String name;
	private String admin;
	private String description;
	private long lastModified;
	private long creationTimestamp;
	private boolean shared;

	public int getGroupid() {
		return groupid;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
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

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}
}
