package dk.cphbusiness;

import dk.cphbusiness.rest.ApplicationConfig;
import dk.cphbusiness.rest.RestRoutes;
import dk.cphbusiness.security.SecurityRoutes;
import dk.cphbusiness.utils.Utils;
import io.javalin.json.JavalinJackson;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Main {
    public static void main(String[] args) {

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .checkSecurityRoles() // check for role when route is called
                .setRoute(SecurityRoutes.getSecurityRoutes())
                .setRoute(SecurityRoutes.getSecuredRoutes())
                .setRoute(RestRoutes.getRoutes())
                .startServer(7070)
                .setCORS()
                .setGeneralExceptionHandling()
                .setApiExceptionHandling();
    }
}