package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappRootAPIResource;

public class CalendappRootAPI {
	@InjectLinks({
			@InjectLink(resource = CalendappRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Calendapp Root API")})
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
