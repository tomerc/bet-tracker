package com.bettracker;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;

import java.net.URL;

/**
 * @author Tomer Cohen
 */
public class Driver {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        //Manually set the selector (needed explicitly here before any logger kicks in)
        // create the logger only after artifactory.home is set
        Server server = null;
        try {
            URL configUrl = Driver.class.getResource("/jetty.xml");
            XmlConfiguration xmlConfiguration = new XmlConfiguration(configUrl);
            WebAppContext appContext = new WebAppContext();
            appContext.setServer(server);
            appContext.getSessionHandler().getSessionManager().setSessionIdPathParameterName("none");
            server = new Server();
            xmlConfiguration.configure(server);
            server.start();
        } catch (Exception e) {
            System.err.println("Could not start the Jetty server: " + e);
            if (server != null) {
                try {
                    server.stop();
                } catch (Exception e1) {
                    System.err.println("Unable to stop the jetty server: " + e1);
                }
            }
        }
    }
}
