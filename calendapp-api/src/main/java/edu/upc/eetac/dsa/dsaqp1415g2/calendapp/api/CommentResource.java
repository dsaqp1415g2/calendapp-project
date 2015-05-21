package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.Like;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.LikeCollection;

@Path("/comments")
public class CommentResource {
	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private String GET_COMMENTS_QUERY = "select * from comments where creation_timestamp < ifnull(?, now()) and eventid = ? order by creation_timestamp desc limit ?";
	private String GET_COMMENTS_QUERY_FROM_LAST = "select * from comments where creation_timestamp > ifnull(?, now())  and eventid = ? order by creation_timestamp desc limit ?";
	private String GET_COMMENT_BY_ID_QUERY = "select * from comments where commentid = ?";
	private String INSERT_COMMENT_QUERY = "insert into comments (username, eventid, content) values (?, ?, ?)";
	private String UPDATE_COMMENT_QUERY = "update comments set content = ifnull(?, content) where commentid = ?";
	private String DELETE_COMMENT_QUERY = "delete from comments where commentid = ?";
	private String GET_LIKES_OF_COMMENT_QUERY = "select * from likes where commentid = ? and likeComment = true";
	private String GET_DISLIKES_OF_COMMENT_QUERY = "select * from likes where commentid = ? and dislikeComment = true";
	private String INSERT_LIKE_QUERY = "insert into likes (commentid, username, likeComment, dislikeComment) values(?,?,?,?)";
	private String UPDATE_LIKE_QUERY = "update likes set likeComment = ifnull(?, likeComment), dislikeComment = ifnull(?, dislikeComment) where likeid = ?";
	private String DELETE_LIKE_QUERY = "delete from likes where likeid = ?";

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
			stmt.setInt(3, length);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Comment comment = new Comment();
				comment.setCommentid(rs.getInt("commentid"));
				comment.setUsername(rs.getString("username"));
				comment.setEventid(rs.getInt("eventid"));
				comment.setContent(rs.getString("content"));
				comment.setLikes(rs.getInt("likes"));
				comment.setDislikes(rs.getInt("dislikes"));
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
		try {
			stmt = conn.prepareStatement(GET_COMMENT_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(commentid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				comment.setCommentid(rs.getInt("commentid"));
				comment.setUsername(rs.getString("username"));
				comment.setEventid(rs.getInt("eventid"));
				comment.setContent(rs.getString("content"));
				comment.setLikes(rs.getInt("likes"));
				comment.setDislikes(rs.getInt("dislikes"));
				comment.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				comment.setCreationTimestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
			} else {
				throw new NotFoundException(
						"There's no comment with commentid = " + commentid);
			}
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

		return comment;
	}

	@POST
	@Consumes(MediaType.CALENDAPP_API_COMMENT)
	@Produces(MediaType.CALENDAPP_API_COMMENT)
	public Comment createComment(Comment comment) {
		// validateComment
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_COMMENT_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, comment.getUsername());
			stmt.setInt(2, comment.getEventid());
			stmt.setString(3, comment.getContent());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int commentid = rs.getInt(1);

				comment = getCommentFromDataBase(Integer.toString(commentid));
			}
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
		return comment;
	}

	@PUT
	@Path("/{commentid}")
	@Consumes(MediaType.CALENDAPP_API_COMMENT)
	@Produces(MediaType.CALENDAPP_API_COMMENT)
	public Comment updateComment(@PathParam("commentid") String commentid,
			Comment comment) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_COMMENT_QUERY);
			stmt.setString(1, comment.getContent());
			stmt.setInt(2, Integer.valueOf(commentid));
			int rows = stmt.executeUpdate();
			if (rows == 1) {
				comment = getCommentFromDataBase(commentid);
			} else {
				throw new NotFoundException(
						"There's no comment with commentid = " + commentid);
			}
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
		return comment;
	}

	@DELETE
	@Path("/{commentid}")
	public void deleteComment(@PathParam("commentid") String commentid) {
		// validateUser
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_COMMENT_QUERY);
			stmt.setInt(1, Integer.valueOf(commentid));
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException(
						"There's no comment with commentid = " + commentid);
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
	}

	@GET
	@Path("/likes/{commentid}/{like}")
	@Produces(MediaType.CALENDAPP_API_LIKE_COLLECTION)
	public LikeCollection getLikes(@PathParam("commentid") String commentid,
			@PathParam("like") String likeQuery) {
		LikeCollection likes = new LikeCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			if (likeQuery.equals("likes"))
				stmt = conn.prepareStatement(GET_LIKES_OF_COMMENT_QUERY);
			else if (likeQuery.equals("dislikes"))
				stmt = conn.prepareStatement(GET_DISLIKES_OF_COMMENT_QUERY);
			else
				throw new NotFoundException("Bad url");
			stmt.setInt(1, Integer.valueOf(commentid));
			ResultSet rs = stmt.executeQuery();
			int count = 0;
			while (rs.next()) {
				Like like = new Like();
				like.setLikeid(rs.getInt("likeid"));
				like.setCommentid(rs.getInt("commentid"));
				like.setUsername(rs.getString("username"));
				like.setLikeComment(rs.getBoolean("likeComment"));
				like.setDislikeComment(rs.getBoolean("dislikeComment"));
				likes.addLike(like);
				count++;
			}
			likes.setCount(count);
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
		return likes;
	}

	@POST
	@Path("/likes/{commentid}")
	@Consumes(MediaType.CALENDAPP_API_LIKE)
	public void createLike(@PathParam("commentid") String commentid, Like like) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_LIKE_QUERY);
			stmt.setInt(1, like.getCommentid());
			stmt.setString(2, like.getUsername());
			stmt.setBoolean(3, like.isLikeComment());
			stmt.setBoolean(4, like.isDislikeComment());
			stmt.executeUpdate();

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
	}

	@PUT
	@Path("/likes/{commentid}")
	@Consumes(MediaType.CALENDAPP_API_LIKE)
	public void updateLike(@PathParam("commentid") String commentid, Like like) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_LIKE_QUERY);
			stmt.setBoolean(1, like.isLikeComment());
			stmt.setBoolean(2, like.isDislikeComment());
			stmt.setInt(3, like.getLikeid());
			int rows = stmt.executeUpdate();
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
	}

	@DELETE
	@Path("/likes/{likeid}")
	public void deleteLike(@PathParam("likeid") String likeid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(DELETE_LIKE_QUERY);
			stmt.setInt(1, Integer.valueOf(likeid));
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no like with likeid = "
						+ likeid);
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
	}
}
