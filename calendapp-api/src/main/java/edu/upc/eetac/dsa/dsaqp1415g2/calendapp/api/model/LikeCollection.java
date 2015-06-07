package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CommentResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;

public class LikeCollection {
	private List<Like> likes;
	private int count;

	public LikeCollection() {
		super();
		likes = new ArrayList<>();
	}

	public void addLike(Like like) {
		likes.add(like);
	}

	public List<Like> getLikes() {
		return likes;
	}

	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
