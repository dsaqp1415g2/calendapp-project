package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
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

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.Group;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.GroupCollection;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.User;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.UserCollection;

@Path("/groups")
public class GroupResource {

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	private String GET_GROUPS_QUERY_FROM_LAST = "select * from groups where creation_timestamp > ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_GROUPS_QUERY = "select * from groups where creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_GROUP_BY_ID = "select * from groups where groupid = ?";
	private String GET_GROUPS_BY_NAME = "select * from groups where name LIKE ? and creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_GROUPS_BY_NAME_FROM_LAST = "select * from groups where name LIKE ? and creation_timestamp > ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String UPDATE_GROUP_QUERY = "update groups set name=ifnull(?, name), admin = ifnull(?, admin), description = ifnull(?, description), shared = ifnull(?, shared) where groupid = ?";
	private String INSERT_GROUP_QUERY = "insert into groups (name, admin, description, shared) values (?,?,?,?)";
	private String DELETE_GROUP_QUERY = "delete from groups where groupid = ?";
	private String GET_USERS_QUERY = "select u.userid, u.username, u.name, u.age, u.email from group_users g, users u where g.groupid = ? and u.userid = g.userid and g.state = ?";
	private String INSERT_USER_IN_GROUP_QUERY = "update group_users set userid = ifnull(?, userid), state = ifnull(?, state) where groupid = ?";
	private String DELETE_USER_OF_GROUP_QUERY = "delete from group_user where userid = ? and groupid = ?";
	private String GET_GROUPS_OF_USERID_QUERY = "select g.* from group_users gu, groups g where gu.userid = ? and gu.groupid = g.groupid";
	private String GET_GROUPS_ADMIN_USERID_QUERY = "select g.* from users u, groups g where u.userid = ? and u.username = g.admin";

	@GET
	@Produces(MediaType.CALENDAPP_API_GROUP_COLLECTION)
	public GroupCollection getGroups(@QueryParam("length") int length,
			@QueryParam("name") String name, @QueryParam("before") long before,
			@QueryParam("after") long after) {
		GroupCollection groups = new GroupCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			if (name != null) {
				if (before > 0) {
					stmt = conn.prepareStatement(GET_GROUPS_BY_NAME_FROM_LAST);
					stmt.setString(1, "%" + name + "%");
					stmt.setTimestamp(2, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_GROUPS_BY_NAME);
					stmt.setString(1, "%" + name + "%");
					if (after > 0) {
						stmt.setTimestamp(2, new Timestamp(after));
					} else
						stmt.setTimestamp(2, null);
				}
				length = (length <= 0) ? 5 : length;
				stmt.setInt(3, length);
			} else {
				if (before > 0) {
					stmt = conn.prepareStatement(GET_GROUPS_QUERY_FROM_LAST);
					stmt.setTimestamp(1, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_GROUPS_QUERY);
					if (after > 0)
						stmt.setTimestamp(1, new Timestamp(after));
					else
						stmt.setTimestamp(1, null);
				}
				length = (length <= 0) ? 5 : length;
				stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Group group = new Group();
				group.setGroupid(rs.getInt("groupid"));
				group.setName(rs.getString("name"));
				group.setAdmin(rs.getString("admin"));
				group.setDescription(rs.getString("description"));
				group.setShared(rs.getBoolean("shared"));
				group.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				group.setCreationTimestamp(rs
						.getTimestamp("creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				if (first) {
					first = false;
					groups.setNewestTimestamp(group.getCreationTimestamp());
				}
				groups.addGroup(group);
			}
			groups.setOldestTimestamp(oldestTimestamp);
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
		return groups;
	}

	@GET
	@Path("/{groupid}")
	@Produces(MediaType.CALENDAPP_API_GROUP)
	public Response getGroup(@PathParam("groupid") String groupid,
			@Context Request request) {
		CacheControl cc = new CacheControl();

		Group group = getGroupFromDataBase(groupid);

		EntityTag eTag = new EntityTag(Long.toString(group.getLastModified()));

		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		rb = Response.ok(group).cacheControl(cc).tag(eTag);

		return rb.build();

	}

	private Group getGroupFromDataBase(String groupid) {
		Group group = new Group();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(GET_GROUP_BY_ID);
			stmt.setInt(1, Integer.valueOf(groupid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				group.setGroupid(rs.getInt("groupid"));
				group.setName(rs.getString("name"));
				group.setAdmin(rs.getString("admin"));
				group.setDescription(rs.getString("description"));
				group.setShared(rs.getBoolean("shared"));
				group.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				group.setCreationTimestamp(rs
						.getTimestamp("creation_timestamp").getTime());
			} else {
				throw new NotFoundException("There's no group with groupid = "
						+ groupid);
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

		return group;
	}

	@POST
	@Consumes(MediaType.CALENDAPP_API_GROUP)
	@Produces(MediaType.CALENDAPP_API_GROUP)
	public Group createGroup(Group group) {
		validateGroup(group);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_GROUP_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, group.getName());
			stmt.setString(2, group.getAdmin());
			stmt.setString(3, group.getDescription());
			stmt.setBoolean(4, group.isShared());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int groupid = rs.getInt(1);
				group = getGroupFromDataBase(Integer.toString(groupid));
			} else {

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
		return group;

	}

	@PUT
	@Path("/{groupid}")
	@Consumes(MediaType.CALENDAPP_API_GROUP)
	@Produces(MediaType.CALENDAPP_API_GROUP)
	public Group updateGroup(@PathParam("groupid") String groupid, Group group) {
		validateGroup(group);
		validateAdmin(groupid);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_GROUP_QUERY);
			stmt.setString(1, group.getName());
			stmt.setString(2, group.getAdmin());
			stmt.setString(3, group.getDescription());
			stmt.setBoolean(4, group.isShared());
			stmt.setInt(5, Integer.valueOf(groupid));
			int rows = stmt.executeUpdate();
			if (rows == 1) {
				group = getGroupFromDataBase(groupid);
			} else {
				throw new NotFoundException("There's no group with groupid="
						+ groupid);
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
		return group;

	}

	private void validateGroup(Group group) {
		if (group.getName() == null)
			throw new BadRequestException("Name cannot be null.");
		if (group.getAdmin() == null)
			throw new BadRequestException("Admin cannot be null.");
		if (group.getDescription() == null)
			throw new BadRequestException("Description cannot be null.");
	}

	@DELETE
	@Path("/{groupid}")
	public void deleteGroup(@PathParam("groupid") String groupid) {
		validateAdmin(groupid);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_GROUP_QUERY);
			stmt.setInt(1, Integer.valueOf(groupid));

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no group with groupid = "
						+ groupid);

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

	private void validateAdmin(String groupid) {
		Group group = getGroupFromDataBase(groupid);
		String admin = group.getAdmin();
		if (!security.getUserPrincipal().getName().equals(admin))
			throw new ForbiddenException("You are not allowd to modify this group.");
	}

	@GET
	@Path("/{groupid}/{action}")
	@Produces(MediaType.CALENDAPP_API_USER_COLLECTION)
	public UserCollection getUsers(@PathParam("groupid") String groupid,
			@PathParam("action") String action) {
		UserCollection users = new UserCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			if (action.equals("accepted") || action.equals("pending")) {
				stmt = conn.prepareStatement(GET_USERS_QUERY);
				stmt.setInt(1, Integer.valueOf(groupid));
				stmt.setString(2, action);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					User user = new User();
					user.setUserid(rs.getInt("userid"));
					user.setUsername(rs.getString("username"));
					user.setName(rs.getString("name"));
					user.setAge(rs.getInt("age"));
					user.setEmail(rs.getString("email"));
					users.addUser(user);
				}
			} else
				throw new NotFoundException("URL incorrect");
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
		return users;
	}

	@POST
	@Path("/{groupid}/{action}")
	@Consumes(MediaType.CALENDAPP_API_USER)
	@Produces(MediaType.CALENDAPP_API_USER)
	public User createUserInGroup(@PathParam("groupid") String groupid,
			@PathParam("action") String action, User user) {
		validateAdmin(groupid);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_USER_IN_GROUP_QUERY);
			stmt.setInt(1, user.getUserid());
			stmt.setString(2, action);
			stmt.setInt(3, Integer.valueOf(groupid));
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int gid = rs.getInt(2);

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
		return user;
	}

	@DELETE
	@Path("/{groupid}/{userid}")
	public void deleteUserOfGroup(@PathParam("groupid") String groupid,
			@PathParam("userid") String userid) {
		validateAdmin(groupid);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(DELETE_USER_OF_GROUP_QUERY);
			stmt.setInt(1, Integer.valueOf(userid));
			stmt.setInt(2, Integer.valueOf(groupid));
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no relation userid = "
						+ userid + " with groupid = " + groupid);
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
	@Path("/user/{userid}")
	@Produces(MediaType.CALENDAPP_API_GROUP_COLLECTION)
	public GroupCollection getGroupsOfUser(@PathParam("userid") String userid) {
		GroupCollection groups = new GroupCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_GROUPS_OF_USERID_QUERY);
			stmt.setInt(1, Integer.valueOf(userid));
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Group group = new Group();
				group.setGroupid(rs.getInt("groupid"));
				group.setName(rs.getString("name"));
				group.setAdmin(rs.getString("admin"));
				group.setDescription(rs.getString("description"));
				group.setShared(rs.getBoolean("shared"));
				group.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				group.setCreationTimestamp(rs
						.getTimestamp("creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				if (first) {
					first = false;
					groups.setNewestTimestamp(group.getCreationTimestamp());
				}
				groups.addGroup(group);
			}
			groups.setOldestTimestamp(oldestTimestamp);

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
		return groups;
	}

	@GET
	@Path("/admin/{userid}")
	@Produces(MediaType.CALENDAPP_API_GROUP_COLLECTION)
	public GroupCollection getGroupsOfAdmin(@PathParam("userid") String userid) {
		GroupCollection groups = new GroupCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_GROUPS_ADMIN_USERID_QUERY);
			stmt.setInt(1, Integer.valueOf(userid));
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Group group = new Group();
				group.setGroupid(rs.getInt("groupid"));
				group.setName(rs.getString("name"));
				group.setAdmin(rs.getString("admin"));
				group.setDescription(rs.getString("description"));
				group.setShared(rs.getBoolean("shared"));
				group.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				group.setCreationTimestamp(rs
						.getTimestamp("creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				if (first) {
					first = false;
					groups.setNewestTimestamp(group.getCreationTimestamp());
				}
				groups.addGroup(group);
			}
			groups.setOldestTimestamp(oldestTimestamp);

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
		return groups;
	}

}
