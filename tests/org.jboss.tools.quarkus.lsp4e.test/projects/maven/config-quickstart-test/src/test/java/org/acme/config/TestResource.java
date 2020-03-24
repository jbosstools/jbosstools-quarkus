package org.acme.config;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
     
@Path("/greeting-test") 
public class TestResource {
  
    @ConfigProperty(name = "greeting.message.test")
    String message;
   
    @ConfigProperty(name = "greeting.suffix.test" , 
                    defaultValue="!")
    String suffix;

    @ConfigProperty(name = "greeting.name.test")
    Optional<String> name;


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return message + " " + name.orElse("world") + suffix;
    }
}
