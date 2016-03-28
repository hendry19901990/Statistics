package com.statistics.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyEmbeddedAppServer {
	
	
	
	public void initialize() throws Exception {

		Server server = new Server(9696);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setDescriptor("./views/WEB-INF/web.xml");
		webAppContext.setResourceBase("./views");
		webAppContext.setWelcomeFiles(new String[]{ "default.html" });
		server.setHandler(webAppContext);
		
		ServletHolder restfulServiceServletHolder = new ServletHolder(new HttpServletDispatcher());
	    restfulServiceServletHolder.setInitParameter("javax.ws.rs.Application", ApplicationResources.class.getName());
	    restfulServiceServletHolder.setInitParameter("resteasy.use.builtin.providers", "true");
	    webAppContext.addServlet(restfulServiceServletHolder, "/statistics/*");
		
		server.start();
		server.join();
		
	}

}
