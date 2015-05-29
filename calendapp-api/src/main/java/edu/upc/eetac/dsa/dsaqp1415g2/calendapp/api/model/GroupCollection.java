package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.GroupResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;

public class GroupCollection {
	@InjectLinks({
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "create-group", title = "Create group", type = MediaType.CALENDAPP_API_GROUP_COLLECTION),
		@InjectLink(value = "/groups?before={before}", style = Style.ABSOLUTE, rel = "previous", title = "Previous groups", type = MediaType.CALENDAPP_API_GROUP_COLLECTION, bindings = @Binding(name = "before", value = "${instance.oldestTimestamp}") ),
		@InjectLink(value = "/groups?after={after}", style = Style.ABSOLUTE, rel = "current", title = "Newest groups", type = MediaType.CALENDAPP_API_GROUP_COLLECTION, bindings = @Binding(name = "after", value = "${instance.newestTimestamp}"))
	})
	private List<Link> links;
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	
	private long newestTimestamp;
	private long oldestTimestamp;
	
	private List<Group> groups;

	public GroupCollection() {
		super();
		groups = new ArrayList<>();
	}

	public void addGroup(Group group) {
		groups.add(group);
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public long getNewestTimestamp() {
		return newestTimestamp;
	}

	public void setNewestTimestamp(long newestTimestamp) {
		this.newestTimestamp = newestTimestamp;
	}

	public long getOldestTimestamp() {
		return oldestTimestamp;
	}

	public void setOldestTimestamp(long oldestTimestamp) {
		this.oldestTimestamp = oldestTimestamp;
	}

}
