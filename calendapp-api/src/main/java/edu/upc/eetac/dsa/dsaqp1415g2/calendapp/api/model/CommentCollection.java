package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CommentResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;

public class CommentCollection {
	@InjectLinks({
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "create-comment", title = "Create comment", type = MediaType.CALENDAPP_API_COMMENT),
		@InjectLink(value = "/comments/{eventid}", style = Style.ABSOLUTE, rel = "comments", title = "Latest comments", type = MediaType.CALENDAPP_API_COMMENT_COLLECTION, bindings = @Binding(name = "eventid", value = "${instance.comments.get(0).getEventid()}") )
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
	private List<Comment> comments;
	public CommentCollection() {
		super();
		comments = new ArrayList<>();
	}

	public void addComment(Comment comment) {
		comments.add(comment);
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

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}
