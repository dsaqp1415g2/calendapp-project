package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CommentResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;
public class Like {
	@InjectLinks({
		@InjectLink(value = "/comments/likes/{commentid}", style = Style.ABSOLUTE, rel = "self edit", title = "Like", type = MediaType.CALENDAPP_API_LIKE, bindings = @Binding(name = "commentid", value = "${instance.commentid}")),
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "delete-like", title = "Delete Like", method = "deleteLike", bindings = @Binding(name = "likeid", value ="${instance.likeid}"))
	})
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	private int likeid;
	private int commentid;
	private String username;
	private boolean likeComment;
	private boolean dislikeComment;

	public int getLikeid() {
		return likeid;
	}

	public void setLikeid(int likeid) {
		this.likeid = likeid;
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

	public boolean isLikeComment() {
		return likeComment;
	}

	public void setLikeComment(boolean likeComment) {
		this.likeComment = likeComment;
	}

	public boolean isDislikeComment() {
		return dislikeComment;
	}

	public void setDislikeComment(boolean dislikeComment) {
		this.dislikeComment = dislikeComment;
	}

}
