package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.Comment;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.CommentCollection;

@Path("/comments")
public class CommentResource {
	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private String GET_COMMENTS_QUERY = "select * from comments where creation_timestamp < ifnull(?, now()) and eventid = ? order by creation_timestamp desc limit ?";
	private String GET_COMMENTS_QUERY_FROM_LAST = "select * from comments where creation_timestamp > ifnull(?, now())  and eventid = ? order by creation_timestamp desc limit ?";
	private String GET_COMMENT_BY_ID_QUERY = "select * from comments where commentid = ?";

	@GET
	@Path("/{eventid}")
	@Produces(MediaType.CALENDAPP_API_COMMENT_COLLECTION)
	public CommentCollection getComments(@QueryParam("length") int length,
			@PathParam("eventid") String eventid,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		CommentCollection comments = new CommentCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			if (before > 0) {
				stmt = conn.prepareStatement(GET_COMMENTS_QUERY_FROM_LAST);
				stmt.setTimestamp(1, new Timestamp(before));
			} else {
				stmt = conn.prepareStatement(GET_COMMENTS_QUERY);
				if (after > 0)
					stmt.setTimestamp(1, new Timestamp(after));
				else
					stmt.setTimestamp(1, null);
			}
			stmt.setInt(2, Integer.valueOf(eventid));
			length = (length <= 0) ? 5 : length;
			stmt.setInt(2, length);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Comment comment = new Comment();
				comment.setCommentid(rs.getInt("commentid"));
				comment.setUsername(rs.getString("username"));
				comment.setEventid(rs.getInt("eventid"));
				comment.setContent(rs.getString("content"));
				comment.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				comment.setCreationTimestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				if (first) {
					first = false;
					comments.setNewestTimestamp(comment.getCreationTimestamp());
				}
				comments.addComment(comment);
			}
			comments.setOldestTimestamp(oldestTimestamp);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return comments;
	}

	@GET
	@Path("/{commentid}")
	@Produces(MediaType.CALENDAPP_API_COMMENT)
	public Response getComment(@PathParam("commentid") String commentid,
			@Context Request request) {
		CacheControl cc = new CacheControl();

		Comment comment = getCommentFromDataBase(commentid);

		EntityTag eTag = new EntityTag(Long.toString(comment.getLastModified()));

		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		rb = Response.ok(comment).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	private Comment getCommentFromDataBase(String commentid) {
		Comment comment = new Comment();
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement stmt = null;
		 try{
			 stmt = conn.prepareStatement(GET_COMMENT_BY_ID_QUERY);
			 stmt.setInt(1, Integer.valueOf(commentid));
			 ResultSet rs = stmt.executeQuery();
			 if(rs.next()) {
				 comment.setCommentid(rs.getInt("commentid"));
					comment.setUsername(rs.getString("username"));
					comment.setEventid(rs.getInt("eventid"));
					comment.setContent(rs.getString("content"));
					comment.setLastModified(rs.getTimestamp("last_modified")
							.getTime());
					comment.setCreationTimestamp(rs.getTimestamp(
							"creation_timestamp").getTime());
			 }else {
					throw new NotFoundException("There's no group with commentid = "
							+ commentid);
				}
		 }catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					conn.close();
				} catch (SQLException e) {
				}
			}
		 
		return comment;
	}
	
	@POST
	@Consumes(MediaType.CALENDAPP_API_COMMENT)
	@Produces(MediaType.CALENDAPP_API_COMMENT)
	public Comment createComment(Comment comment) {
		//validateComment
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		
		return comment;
	}
}
