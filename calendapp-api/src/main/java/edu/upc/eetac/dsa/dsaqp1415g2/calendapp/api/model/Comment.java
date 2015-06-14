package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CommentResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;

public class Comment {
	@InjectLinks({
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "self", title = "Comment", type = MediaType.CALENDAPP_API_COMMENT, method = "getComment" ,bindings = @Binding(name = "commentid", value = "${instance.commentid}")),
		@InjectLink(value = "/comments/{eventid}", style = Style.ABSOLUTE, rel = "comments", title = "Latest comments", type = MediaType.CALENDAPP_API_COMMENT_COLLECTION, bindings = @Binding(name = "eventid", value = "${instance.eventid}")),
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "get-likes", title = "Likes", type = MediaType.CALENDAPP_API_LIKE_COLLECTION, method = "getLikes", bindings = {@Binding(name = "commentid", value ="${instance.commentid}"), @Binding(name = "like", value = "likes")}),
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "get-dislikes", title = "Dislikes", type = MediaType.CALENDAPP_API_LIKE_COLLECTION, method = "getLikes", bindings = {@Binding(name = "commentid", value ="${instance.commentid}"), @Binding(name = "like", value = "dislikes")}),
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "create-like", title = "Create Like", type = MediaType.CALENDAPP_API_LIKE, method = "createLike", bindings = @Binding(name = "commentid", value ="${instance.commentid}")) 
	})
	private List<Link> links;
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	private int commentid;
	private String username;
	private int eventid;
	private String content;
	private int likes;
	private int dislikes;
	private long lastModified;
	private long creationTimestamp;

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

	public int getCommentid() {
		return commentid;
	}

	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}

	public int getEventid() {
		return eventid;
	}

	public void setEventid(int eventid) {
		this.eventid = eventid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


}
