package com.statistics.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;


public class JettyEmbeddedAppServer {
	
	
	
	public void initialize() throws Exception {
		
        ServletHolder restfulServiceServletHolder = new ServletHolder(new HttpServletDispatcher());
        restfulServiceServletHolder.setInitParameter("javax.ws.rs.Application", ApplicationResources.class.getName());
        restfulServiceServletHolder.setInitParameter("resteasy.use.builtin.providers", "true");
				 
		ServletContextHandler servletCtxHandler = new ServletContextHandler();
		servletCtxHandler.addServlet(restfulServiceServletHolder, "/");

		Server server = new Server(9696);
		server.setHandler(servletCtxHandler);
		server.start();
		server.join();
		
	}

}
