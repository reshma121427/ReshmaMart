package com.reshmamart.runner;

import java.io.File;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class EmbeddedTomcatServer {

    public static void main(String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp";
        Tomcat tomcat = new Tomcat();

        // Determine port
        int port = 8085; // Default to 8085 to avoid port 8080 conflict
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            port = Integer.parseInt(envPort);
        } else {
            // Check config.properties
            try (InputStream in = EmbeddedTomcatServer.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (in != null) {
                    Properties props = new Properties();
                    props.load(in);
                    String propPort = props.getProperty("server.port");
                    if (propPort != null && !propPort.isEmpty()) {
                        port = Integer.parseInt(propPort);
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }

        // Verify port availability, increment if in use
        while (!isPortAvailable(port)) {
            System.out.println(">>> Port " + port + " is in use, trying " + (port + 1) + "...");
            port++;
        }

        tomcat.setPort(port);
        tomcat.getConnector(); // Initialize default HTTP connector

        File webappDir = new File(webappDirLocation);
        if (!webappDir.exists() || !new File(webappDir, "WEB-INF").exists()) {
            File subDir = new File("reshmamart/" + webappDirLocation);
            if (subDir.exists() && new File(subDir, "WEB-INF").exists()) {
                webappDir = subDir;
            } else {
                File assembledDir = new File("target/reshmamart");
                if (assembledDir.exists() && new File(assembledDir, "WEB-INF").exists()) {
                    webappDir = assembledDir;
                }
            }
        }

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDir.getAbsolutePath());
        System.out.println(">>> Configuring reshmamart webapp with basedir: " + webappDir.getAbsolutePath());

        // Ensure webapp class loader delegates to application classloader for Tomcat embed classes
        ctx.setParentClassLoader(EmbeddedTomcatServer.class.getClassLoader());

        // Declare class resource set for WEB-INF/classes
        File additionWebInfClasses = new File("target/classes");
        if (!additionWebInfClasses.exists()) {
            additionWebInfClasses = new File("reshmamart/target/classes");
        }

        WebResourceRoot resources = new StandardRoot(ctx);
        if (additionWebInfClasses.exists()) {
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
        }
        ctx.setResources(resources);

        tomcat.start();
        System.out.println("==================================================================");
        System.out.println("  ReshmaMart Server is running!");
        System.out.println("  Access URL: http://localhost:" + port + "/");
        System.out.println("  Health API: http://localhost:" + port + "/api/v1/health");
        System.out.println("==================================================================");
        tomcat.getServer().await();
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
