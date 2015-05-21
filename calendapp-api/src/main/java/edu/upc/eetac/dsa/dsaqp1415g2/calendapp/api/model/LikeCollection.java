package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.ArrayList;
import java.util.List;

public class LikeCollection {
	private List<Like> likes;
	private int count;
	public LikeCollection(){
		super();
		likes = new ArrayList<>();
	}
	
	public void addLike(Like like){
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
