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

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.Event;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.EventCollection;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.Group;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.User;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.UserCollection;

@Path("/events")
public class EventResource {
	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private String GET_EVENTS_GROUP_QUERY = "select * from events where dateInitial > ifnull(?, now()) and groupid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_GROUP_QUERY_FROM_LAST = "select * from events where dateInitial < ifnull(?, now()) and groupid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_GROUP_BY_NAME = "select * from events where name LIKE ? and dateInitial > ifnull(?, now()) and groupid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_GROUP_BY_NAME_FROM_LAST = "select * from events where name LIKE ? and dateInitial < ifnull(?, now()) and groupid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_USER_QUERY = "select * from events where dateInitial > ifnull(?, now()) and userid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_USER_QUERY_FROM_LAST = "select * from events where dateInitial < ifnull(?, now()) and userid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_USER_BY_NAME = "select * from events where name LIKE ? and dateInitial > ifnull(?, now()) and userid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_USER_BY_NAME_FROM_LAST = "select * from events where name LIKE ? and dateInitial < ifnull(?, now()) and userid = ? order by dateInitial asc limit ?";
	private String GET_EVENT_BY_ID_QUERY = "select * from events where eventid = ?";
	private String GET_EVENTS_NOW_USER_QUERY = "select * from events where dateInitial < now() and now() < dateFinish and userid = ? order by dateInitial asc";
	private String GET_EVENTS_NOW_GROUP_QUERY = "select e.* from events e, group_users g where e.dateInitial < now() and now() < e.dateFinish and e.groupid = g.groupid and g.userid = ? order by dateInitial asc";
	private String INSERT_EVENT_GROUP_QUERY = "insert into events (groupid, name, dateInitial, dateFinish) values (?,?,?,?)";
	private String INSERT_EVENT_USER_QUERY = "insert into events (userid, name, dateInitial, dateFinish) values (?,?,?,?)";
	private String UPDATE_EVENT_QUERY = "update events set name = ifnull(?, name), dateInitial = ifnull(?, dateInitial), dateFinish = ifnull(?, dateFinish) where eventid = ?";
	private String DELETE_EVENT_QUERY = "delete from events where eventid = ?";
	private String GET_USERS_STATE_QUERY = "select u.* from users u, state s where s.eventid = ? and s.state = ? and u.userid = s.userid";
	private String UPDATE_STATE_QUERY = "update state set state = ifnull(?, state) where userid = ? and eventid = ?";

	@GET
	@Path("/group/{groupid}")
	@Produces(MediaType.CALENDAPP_API_EVENT_COLLECTION)
	public EventCollection getEventsGroup(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after,
			@QueryParam("name") String name,
			@PathParam("groupid") String groupid) {
		// validateUserOfGroup(groupid);
		EventCollection events = new EventCollection();

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
					stmt = conn
							.prepareStatement(GET_EVENTS_GROUP_BY_NAME_FROM_LAST);
					stmt.setString(1, "%" + name + "%");
					stmt.setTimestamp(2, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_EVENTS_GROUP_BY_NAME);
					stmt.setString(1, "%" + name + "%");
					if (after > 0) {
						stmt.setTimestamp(2, new Timestamp(after));
					} else
						stmt.setTimestamp(2, null);
				}
				stmt.setInt(3, Integer.valueOf(groupid));
				length = (length <= 0) ? 5 : length;
				stmt.setInt(4, length);
			} else {
				if (before > 0) {
					stmt = conn
							.prepareStatement(GET_EVENTS_GROUP_QUERY_FROM_LAST);
					stmt.setTimestamp(1, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_EVENTS_GROUP_QUERY);
					if (after > 0)
						stmt.setTimestamp(1, new Timestamp(after));
					else
						stmt.setTimestamp(1, null);
				}
				stmt.setInt(2, Integer.valueOf(groupid));
				length = (length <= 0) ? 5 : length;
				stmt.setInt(3, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long lastTimestamp = 0;
			while (rs.next()) {
				Event event = new Event();
				event.setEventid(rs.getInt("eventid"));
				event.setGroupid(rs.getInt("groupid"));
				event.setName(rs.getString("name"));
				event.setDateInitial(rs.getTimestamp("dateInitial").getTime());
				event.setDateFinish(rs.getTimestamp("dateFinish").getTime());
				event.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				lastTimestamp = rs.getTimestamp("dateInitial").getTime();

				if (first) {
					first = false;
					events.setFirstTimestamp(event.getDateInitial());
				}
				events.addEvent(event);
			}
			events.setLastTimestamp(lastTimestamp);

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

		return events;
	}

	private String ASK_USER_OF_GROUPID = "select g.* from group_users g, users u where g.userid = u.userid and u.username = ? and g.groupid = ? and g.state = 'accepted'";

	private void validateUserOfGroup(String groupid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(ASK_USER_OF_GROUPID);
			stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setInt(2, Integer.valueOf(groupid));
			ResultSet rs = stmt.executeQuery();
			if (!rs.next())
				throw new ForbiddenException(
						"You are not allowed to make this. ");
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

	private String VALIDATE_USER = "select * from users where userid = ?";

	private void validateUser(String userid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(VALIDATE_USER);
			stmt.setInt(1, Integer.valueOf(userid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String username = rs.getString("username");
				if (!security.getUserPrincipal().getName().equals(username))
					throw new ForbiddenException("You aren't userid = "
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
	}

	@GET
	@Path("/user/{userid}")
	@Produces(MediaType.CALENDAPP_API_EVENT_COLLECTION)
	public EventCollection getEventsUser(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after,
			@QueryParam("name") String name, @PathParam("userid") String userid) {
		// validateUser(userid);
		EventCollection events = new EventCollection();

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
					stmt = conn
							.prepareStatement(GET_EVENTS_USER_BY_NAME_FROM_LAST);
					stmt.setString(1, "%" + name + "%");
					stmt.setTimestamp(2, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_EVENTS_USER_BY_NAME);
					stmt.setString(1, "%" + name + "%");
					if (after > 0) {
						stmt.setTimestamp(2, new Timestamp(after));
					} else
						stmt.setTimestamp(2, null);
				}
				stmt.setInt(3, Integer.valueOf(userid));
				length = (length <= 0) ? 5 : length;
				stmt.setInt(4, length);
			} else {
				if (before > 0) {
					stmt = conn
							.prepareStatement(GET_EVENTS_USER_QUERY_FROM_LAST);
					stmt.setTimestamp(1, new Timestamp(before));
				} else {
					stmt = conn.prepareStatement(GET_EVENTS_USER_QUERY);
					if (after > 0)
						stmt.setTimestamp(1, new Timestamp(after));
					else
						stmt.setTimestamp(1, null);
				}
				stmt.setInt(2, Integer.valueOf(userid));
				length = (length <= 0) ? 5 : length;
				stmt.setInt(3, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long lastTimestamp = 0;
			if (rs.wasNull()) {
				throw new NotFoundException(
						"No events for the user with userid =" + userid);
			} else {
				while (rs.next()) {
					Event event = new Event();
					event.setEventid(rs.getInt("eventid"));
					event.setUserid(rs.getInt("userid"));
					event.setName(rs.getString("name"));
					event.setDateInitial(rs.getTimestamp("dateInitial")
							.getTime());
					event.setDateFinish(rs.getTimestamp("dateFinish").getTime());
					event.setLastModified(rs.getTimestamp("last_modified")
							.getTime());
					lastTimestamp = rs.getTimestamp("dateInitial").getTime();

					if (first) {
						first = false;
						events.setFirstTimestamp(event.getDateInitial());
					}
					events.addEvent(event);
				}
				events.setLastTimestamp(lastTimestamp);
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
		return events;
	}

	@GET
	@Path("/now/{userid}")
	public EventCollection getEventsNow(@PathParam("userid") String userid) {
		validateUser(userid);
		EventCollection events = new EventCollection();
		events = getEventsNowUser(events, userid);
		events = getEventsNowGroup(events, userid);
		return events;
	}

	private EventCollection getEventsNowUser(EventCollection events,
			String userid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_EVENTS_NOW_USER_QUERY);
			stmt.setInt(1, Integer.valueOf(userid));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Event event = new Event();
				event.setEventid(rs.getInt("eventid"));
				event.setUserid(rs.getInt("userid"));
				event.setName(rs.getString("name"));
				event.setDateInitial(rs.getTimestamp("dateInitial").getTime());
				event.setDateFinish(rs.getTimestamp("dateFinish").getTime());
				event.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				events.addEvent(event);
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
		return events;
	}

	private EventCollection getEventsNowGroup(EventCollection events,
			String userid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_EVENTS_NOW_GROUP_QUERY);
			stmt.setInt(1, Integer.valueOf(userid));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Event event = new Event();
				event.setEventid(rs.getInt("eventid"));
				event.setGroupid(rs.getInt("groupid"));
				event.setName(rs.getString("name"));
				event.setDateInitial(rs.getTimestamp("dateInitial").getTime());
				event.setDateFinish(rs.getTimestamp("dateFinish").getTime());
				event.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				events.addEvent(event);
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
		return events;
	}

	@GET
	@Path("/{eventid}")
	@Produces(MediaType.CALENDAPP_API_EVENT)
	public Response getEvent(@PathParam("eventid") String eventid,
			@Context Request request) {
		CacheControl cc = new CacheControl();

		Event event = getEventFromDataBase(eventid);

		EntityTag eTag = new EntityTag(Long.toString(event.getLastModified()));

		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}

		rb = Response.ok(event).cacheControl(cc).tag(eTag);

		return rb.build();

	}

	private Event getEventFromDataBase(String eventid) {
		Event event = new Event();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_EVENT_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(eventid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				event.setEventid(rs.getInt("eventid"));
				event.setUserid(rs.getInt("userid"));
				event.setGroupid(rs.getInt("groupid"));
				event.setName(rs.getString("name"));
				event.setDateInitial(rs.getTimestamp("dateInitial").getTime());
				event.setDateFinish(rs.getTimestamp("dateFinish").getTime());
				event.setLastModified(rs.getTimestamp("last_modified")
						.getTime());

			} else {
				throw new NotFoundException("There's no group with eventid = "
						+ eventid);
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
		return event;
	}

	@POST
	@Consumes(MediaType.CALENDAPP_API_EVENT)
	@Produces(MediaType.CALENDAPP_API_EVENT)
	public Event createEvent(Event event) {
		validateEvent(event);
		// validateModifyEvent(event.getGroupid());
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		boolean group = false;
		try {
			if (event.getGroupid() != 0 && event.getUserid() == 0) {
				stmt = conn.prepareStatement(INSERT_EVENT_GROUP_QUERY,
						Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, event.getGroupid());
				group = true;
			} else if (event.getGroupid() == 0 && event.getUserid() != 0) {
				stmt = conn.prepareStatement(INSERT_EVENT_USER_QUERY,
						Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, event.getUserid());
			}
			stmt.setString(2, event.getName());
			stmt.setTimestamp(3, new Timestamp(event.getDateInitial()));
			stmt.setTimestamp(4, new Timestamp(event.getDateFinish()));
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int eventid = rs.getInt(1);
				event = getEventFromDataBase(Integer.toString(eventid));
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
		if (group)
			createEvento(event.getEventid());
		return event;
	}

	private void validateEvent(Event event) {
		if (event.getGroupid() == 0 && event.getUserid() == 0) {
			throw new BadRequestException("This event who is?");
		}
		if (event.getName() == null)
			throw new BadRequestException("The name can't be null");
		if (event.getName().length() > 100)
			throw new BadRequestException(
					"The name can't be greater than 100 characters");

	}

	private String VALIDATE_MODIFY_EVENT = "select * from groups where groupid = ?";

	private void validateModifyEvent(int groupid) {
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
			stmt = conn.prepareStatement(VALIDATE_MODIFY_EVENT);
			stmt.setInt(1, groupid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				group.setShared(rs.getBoolean("shared"));
				group.setAdmin(rs.getString("admin"));
				if (group.isShared()) {
					validateUserOfGroup(Integer.toString(groupid));
				} else {
					if (!security.getUserPrincipal().getName()
							.equals(group.getAdmin()))
						throw new ForbiddenException(
								"You are not the admin of group with groupid = "
										+ groupid);
				}
			} else
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

	private String CREATE_EVENT_STATE_PENDING_QUERY = "insert into state values (?, ?, 'pending')";

	private void createEvento(int eventid) {
		UserCollection users;
		users = getUsersOfEventId(eventid);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			for (int i = 0; i < users.getUsers().size(); i++) {
				stmt = null;
				stmt = conn.prepareStatement(CREATE_EVENT_STATE_PENDING_QUERY,
						Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, users.getUsers().get(i).getUserid());
				stmt.setInt(2, eventid);
				stmt.executeUpdate();
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
	}

	private String GET_USERS_OF_EVENT = "select g.userid from group_users g, events e where e.eventid = ? and e.groupid = g.groupid";

	private UserCollection getUsersOfEventId(int eventid) {
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
			stmt = conn.prepareStatement(GET_USERS_OF_EVENT);
			stmt.setInt(1, eventid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = new User();
				user.setUserid(rs.getInt("userid"));
				users.addUser(user);
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
		return users;
	}

	@PUT
	@Path("/{eventid}")
	@Consumes(MediaType.CALENDAPP_API_EVENT)
	@Produces(MediaType.CALENDAPP_API_EVENT)
	public Event updateEvent(@PathParam("eventid") String eventid, Event event) {
		validateEvent(event);
		// validateModifyEvent(event.getGroupid());

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_EVENT_QUERY);
			stmt.setString(1, event.getName());
			stmt.setTimestamp(2, new Timestamp(event.getDateInitial()));
			stmt.setTimestamp(3, new Timestamp(event.getDateFinish()));
			stmt.setInt(4, Integer.valueOf(eventid));
			int rows = stmt.executeUpdate();
			if (rows == 1) {
				event = getEventFromDataBase(eventid);
			} else {
				throw new NotFoundException("There's no event with eventid = "
						+ eventid);
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
		return event;
	}

	@DELETE
	@Path("/{eventid}")
	public void deleteEvent(@PathParam("eventid") String eventid) {
		Event event = getEventFromDataBase(eventid);
		// validateModifyEvent(event.getGroupid());
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(DELETE_EVENT_QUERY);
			stmt.setInt(1, Integer.valueOf(eventid));

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no event with eventid = "
						+ eventid);

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
	@Path("/state/{eventid}/{state}")
	@Produces(MediaType.CALENDAPP_API_USER_COLLECTION)
	public UserCollection getUsersState(@PathParam("eventid") String eventid,
			@PathParam("state") String state) {
		Event event = getEventFromDataBase(eventid);
		// validateUserOfGroup(Integer.toString(event.getGroupid()));
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
			stmt = conn.prepareStatement(GET_USERS_STATE_QUERY);
			stmt.setInt(1, Integer.valueOf(eventid));
			if (state.equals("join") || state.equals("pending")
					|| state.equals("decline")) {
				stmt.setString(2, state);
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

	@PUT
	@Path("/state/{eventid}/{userid}/{state}")
	public void updateState(@PathParam("eventid") String eventid,
			@PathParam("userid") String userid, @PathParam("state") String state) {
		Event event = getEventFromDataBase(eventid);
		// validateUserOfGroup(Integer.toString(event.getGroupid()));
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_STATE_QUERY);
			if (state.equals("join") || state.equals("pending")
					|| state.equals("decline")) {
				stmt.setString(1, state);
				stmt.setInt(2, Integer.valueOf(userid));
				stmt.setInt(3, Integer.valueOf(eventid));
				int rows = stmt.executeUpdate();
				if (rows != 1)
					throw new NotFoundException(
							"There's no state with userid = " + userid
									+ " and eventid = " + eventid);
			} else
				throw new NotFoundException("Esta mal el estado");
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
