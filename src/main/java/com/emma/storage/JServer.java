package com.emma.storage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JServer {
    private final static Logger logger = Logger.getLogger(JServer.class
            .getName());

    private MonitorConfiguration monitorConfiguration;

    /**
     * Constructor for the heartbeat monitor.
     * 
     * 
     * @param monitorConfiguration
     *            configurations for the monitor.
     */
    public JServer(MonitorConfiguration monitorConfiguration) {
        this.monitorConfiguration = monitorConfiguration;
    }

    /**
     * The method how to start heartbeat monitor.
     * 
     */
    public void start() {
        Server server = new Server(monitorConfiguration.getPort());
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(monitorConfiguration.getWelcomeFiles());
        resourceHandler.setResourceBase(monitorConfiguration.getResourceBase());

        ServletContextHandler contextHandler = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        contextHandler.setContextPath(monitorConfiguration.getContextPath());
        contextHandler.setResourceBase(monitorConfiguration.getResourceBase());
        server.setHandler(contextHandler);
        ServletHolder holder = new ServletHolder();
        holder.setInitOrder(0);
        holder.setInitParameter("resourceBase", "/servlet");
        holder.setInitParameter("pathInfoOnly", "true");
        holder.setInitParameter("unavailableThreshold", ""
                + monitorConfiguration.getUnavailableThreshold());
        holder.setDisplayName("kineticadminservlet");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, contextHandler,
                new DefaultHandler() });
        server.setHandler(handlers);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * A default heartbeat monitor instance boot-strap method.
     * 
     */
    public static void main(String[] args) throws IOException {
        MonitorConfiguration config = new MonitorConfiguration();
        JServer server = new JServer(config);
        server.start();
    }
}
