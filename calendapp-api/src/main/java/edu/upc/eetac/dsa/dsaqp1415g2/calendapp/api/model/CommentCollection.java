package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.ArrayList;
import java.util.List;

public class CommentCollection {
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
