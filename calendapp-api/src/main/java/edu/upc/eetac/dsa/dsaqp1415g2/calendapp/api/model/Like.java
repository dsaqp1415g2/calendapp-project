package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

public class Like {
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
