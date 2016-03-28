package com.statistics.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.GzipHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.Handler;

public class JettyEmbeddedAppServer {
	
	
	
	public void initialize() throws Exception {
		
        ServletHolder restfulServiceServletHolder = new ServletHolder(new HttpServletDispatcher());
        restfulServiceServletHolder.setInitParameter("javax.ws.rs.Application", ApplicationResources.class.getName());
        restfulServiceServletHolder.setInitParameter("resteasy.use.builtin.providers", "true");
				 
		ServletContextHandler servletCtxHandler = new ServletContextHandler();
		servletCtxHandler.addServlet(restfulServiceServletHolder, "/");

		Server server = new Server(9696);
		server.setHandler(servletCtxHandler);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "default.html" });
        resource_handler.setResourceBase("./views");
 
        GzipHandler gzip = new GzipHandler();
        server.setHandler(gzip);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
        gzip.setHandler(handlers);
		
		
		server.start();
		server.join();
		
	}

}
