package com.wso2.carbon.identity.msf4j.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Just for testing.
 */
public class EmbeddedTomcatServer {

    private Tomcat tomcat = new Tomcat();

    public void start() {
        try {
            File baseDir = new File("tomcat");
            tomcat.setBaseDir(baseDir.getAbsolutePath());

            tomcat.setPort(8090);
            Context appContext = tomcat.addContext("/test", new File(".").getAbsolutePath());
            Wrapper wrapper = tomcat.addServlet(appContext, "hello", new Msf4jBridgeServlet());
            wrapper.setAsyncSupported(true);
            appContext.addServletMappingDecoded("/*", "hello");
            wrapper.addInitParameter("ServiceClasses", HelloService.class.getName());
            tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            tomcat.stop();
            tomcat.destroy();
            FileUtils.deleteDirectory(new File("work"));
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        EmbeddedTomcatServer embeddedTomcatServer = new EmbeddedTomcatServer();
        embeddedTomcatServer.start();

        Thread.sleep(200000);
        embeddedTomcatServer.stop();
    }
}
