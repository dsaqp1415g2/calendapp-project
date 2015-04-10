package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class CalendappApplication extends ResourceConfig {
	public CalendappApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
	}
}
