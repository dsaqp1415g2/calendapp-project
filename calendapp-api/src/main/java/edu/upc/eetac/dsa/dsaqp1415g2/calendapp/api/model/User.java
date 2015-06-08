package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.List;


import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.EventResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.GroupResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.UserResource;

public class User {
	
	@InjectLinks({
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "self", title = "Get user", type = MediaType.CALENDAPP_API_USER, method = "getUser", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "my-groups", title = "My Groups", type = MediaType.CALENDAPP_API_GROUP_COLLECTION, method = "getGroupsOfUser", bindings = @Binding(name = "userid", value ="${instance.userid}")),
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel ="my-groups-admin", title = "My groups admin", type = MediaType.CALENDAPP_API_GROUP_COLLECTION, method = "getGroupsOfAdmin", bindings = @Binding(name = "userid", value ="${instance.userid}")),
		@InjectLink(value = "/events/user/{userid}", style = Style.ABSOLUTE, rel = "my-events", title = "My events", type = MediaType.CALENDAPP_API_EVENT_COLLECTION, bindings = @Binding(name = "userid", value ="${instance.userid}")),
		@InjectLink(value = "/events/now/{userid}", style = Style.ABSOLUTE, rel = "my-events-now", title = "My events Now", type = MediaType.CALENDAPP_API_EVENT_COLLECTION, bindings = @Binding(name = "userid", value = "${instance.userid}"))
	})
	
	private List<Link> links;
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	private int userid;
	private String username;
	private String userpass;
	private String name;
	private int age;
	private String email;
	private boolean loginSuccessful;

	public boolean isLoginSuccessful() {
		return loginSuccessful;
	}

	public void setLoginSuccessful(boolean loginSuccessful) {
		this.loginSuccessful = loginSuccessful;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpass() {
		return userpass;
	}

	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
