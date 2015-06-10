package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.digest.DigestUtils;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.User;

@Path("/users")
public class UserResource {

	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private String GET_USER_BY_USERNAME_QUERY = "select * from users where username = ?";
	private String INSERT_USER_QUERY = "insert into users values(NULL, ?, MD5(?), ?, ?, ?)";
	private String UPDATE_USER_QUERY = "update users set username = ifnull(?, username), userpass = ifnull(MD5(?), userpass), name = ifnull(?, name), age =ifnull(?, age), email = ifnull(?, email) where userid = ?";
	private String DELETE_USER_QUERY = "delete from users where userid = ?";

	@GET
	@Path("/{username}")
	@Produces(MediaType.CALENDAPP_API_USER)
	public User getUser(@PathParam("username") String username) {
		User user = new User();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUserid(rs.getInt("userid"));
				user.setUsername(rs.getString("username"));
				user.setUserpass(rs.getString("userpass"));
				user.setName(rs.getString("name"));
				user.setAge(rs.getInt("age"));
				user.setEmail(rs.getString("email"));
			} else
				throw new NotFoundException("There's no user with username = "
						+ username);
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

	@POST
	@Consumes(MediaType.CALENDAPP_API_USER)
	@Produces(MediaType.CALENDAPP_API_USER)
	public User createUser(User user) {
		validateUser(user);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmtGetUsername = null;
		PreparedStatement stmtInsertUser = null;
		try {
			stmtGetUsername = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
			stmtGetUsername.setString(1, user.getUsername());

			ResultSet rs = stmtGetUsername.executeQuery();
			if (rs.next())
				
			rs.close();

			conn.setAutoCommit(false);
			stmtInsertUser = conn.prepareStatement(INSERT_USER_QUERY);
			stmtInsertUser.setString(1, user.getUsername());
			stmtInsertUser.setString(2, user.getUserpass());
			stmtInsertUser.setString(3, user.getName());
			stmtInsertUser.setInt(4, user.getAge());
			stmtInsertUser.setString(5, user.getEmail());
			stmtInsertUser.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
				}
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmtGetUsername != null)
					stmtGetUsername.close();
				if (stmtInsertUser != null)
					stmtInsertUser.close();
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
			}
		}
		user.setUserpass(null);
		return user;
	}

	private void validateUser(User user) {
		if (user.getUsername() == null)
			throw new BadRequestException("username cannot be null.");
		if (user.getUserpass() == null)
			throw new BadRequestException("password cannot be null.");
		if (user.getName() == null)
			throw new BadRequestException("name cannot be null.");
		if (user.getAge() == 0)
			throw new BadRequestException("age cannot be null.");
		if (user.getEmail() == null)
			throw new BadRequestException("email cannot be null.");
	}

	@Path("/login")
	@POST
	@Produces(MediaType.CALENDAPP_API_USER)
	@Consumes(MediaType.CALENDAPP_API_USER)
	public User login(User user) {
		if (user.getUsername() == null || user.getUserpass() == null)
			throw new BadRequestException(
					"username and password cannot be null.");
		String pwdDigest = DigestUtils.md5Hex(user.getUserpass());
		String storedPwd = getUserFromDatabase(user.getUsername(), true)
				.getUserpass();
		
		user = getUserFromDatabase(user.getUsername(), false);
		user.setLoginSuccessful(pwdDigest.equals(storedPwd));
		user.setUserpass(null);
		
		return user;
	}

	private User getUserFromDatabase(String username, boolean password) {
		User user = new User();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				if (password)
					user.setUserpass(rs.getString("userpass"));
				user.setUserid(rs.getInt("userid"));
				user.setName(rs.getString("name"));
				user.setAge(rs.getInt("age"));
				user.setEmail(rs.getString("email"));
			} else
				throw new NotFoundException(username + " not found");
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

	@PUT
	@Path("/{userid}")
	@Consumes(MediaType.CALENDAPP_API_USER)
	@Produces(MediaType.CALENDAPP_API_USER)
	public User updateUser(@PathParam("userid") String userid, User user) {
		validateUser(user);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_USER_QUERY);
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getUserpass());
			stmt.setString(3, user.getName());
			stmt.setInt(4, user.getAge());
			stmt.setString(5, user.getEmail());
			stmt.setInt(6, Integer.valueOf(userid));
			int rows = stmt.executeUpdate();
			if (rows == 1) {
				user = getUserFromDatabase(user.getUsername(), true);
			} else {
				throw new NotFoundException("There's no user with userid = "
						+ userid);
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
	@Path("/{userid}")
	public void deleteUser(@PathParam("userid") String userid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(DELETE_USER_QUERY);
			stmt.setInt(1, Integer.valueOf(userid));

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no user with userid = "
						+ userid);
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
