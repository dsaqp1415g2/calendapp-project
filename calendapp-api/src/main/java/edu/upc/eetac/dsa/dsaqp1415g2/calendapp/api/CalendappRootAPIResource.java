package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model.CalendappRootAPI;

@Path("/")
public class CalendappRootAPIResource {
	@GET
	public CalendappRootAPI getRootAPI() {
		CalendappRootAPI api = new CalendappRootAPI();
		return api;
	}
}
