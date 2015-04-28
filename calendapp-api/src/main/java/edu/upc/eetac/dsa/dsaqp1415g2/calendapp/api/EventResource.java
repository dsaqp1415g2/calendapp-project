package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.Event;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.EventCollection;

@Path("/events")
public class EventResource {
	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private String GET_EVENTS_GROUP_QUERY = "select * from events where dateInitial > ifnull(?, now()) and groupid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_GROUP_QUERY_FROM_LAST = "select * from events where dateInitial < ifnull(?, now()) and groupid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_GROUP_BY_NAME = "select * from events where name LIKE ? and dateInitial > ifnull(?, now()) and groupid = ? order by dateInitial asc limit ?";
	private String GET_EVENTS_GROUP_BY_NAME_FROM_LAST = "select * from events where name LIKE ? and dateInitial < ifnull(?, now()) and groupid = ? order by dateInitial asc limit ?";
	private String GET_EVENT_BY_ID_QUERY = "select * from events where eventid = ?";
	private String INSERT_EVENT_QUERY = "insert into events (userid, groupid, name, dateInitial, dateFinish) values (?,?,?,?,?)";

	@GET
	@Path("/group/{groupid}")
	@Produces(MediaType.CALENDAPP_API_EVENT_COLLECTION)
	public EventCollection getEvents(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after,
			@QueryParam("name") String name,
			@PathParam("groupid") String groupid) {

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
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(INSERT_EVENT_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, event.getUserid());
			stmt.setInt(2, event.getGroupid());
			stmt.setString(3, event.getName());
			stmt.setTimestamp(4, new Timestamp(event.getDateInitial()));
			stmt.setTimestamp(5, new Timestamp(event.getDateFinish()));
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
		return event;
	}
}
