package com.wso2.carbon.identity.msf4j.bridge.hello.world;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.msf4j.Microservice;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component(
        name = "com.wso2.carbon.identity.msf4j.bridge.hello.world.HelloService",
        service = Microservice.class,
        immediate = true
)
@Path("/hello")
public class HelloService implements Microservice {

    private Map<String, User> nameToUserMap = new HashMap<>();

    @GET
    @Path("/testing")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUser() {
        return "HI";
    }

    @GET
    @Path("/user/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("name") String name) {
        User result = nameToUserMap.get(name);
        if (result != null) {
            return result;
        }
        return null;
    }


    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String helloPost(User user) {
        nameToUserMap.put(user.getName(), user);
        return user.getName();
    }

    @Activate
    protected void activate(BundleContext bundleContext) {
        // Nothing to do
    }

    @Deactivate
    protected void deactivate(BundleContext bundleContext) {
        // Nothing to do
    }

    @Override
    public String toString() {
        return "HelloWorld-OSGi-bundle";
    }
}
