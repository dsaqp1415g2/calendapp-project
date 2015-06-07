package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappRootAPIResource;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.UserResource;

public class CalendappRootAPI {
	@InjectLinks({
		@InjectLink(resource = CalendappRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Calendapp Root API"),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "login", title = "Check login", type = MediaType.CALENDAPP_API_USER, method = "login"),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "create-user", title = "Create user", type = MediaType.CALENDAPP_API_USER, method = "createUser"),
	})
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
