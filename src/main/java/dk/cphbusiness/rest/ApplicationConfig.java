package dk.cphbusiness.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.cphbusiness.exceptions.ApiException;
import dk.cphbusiness.security.ISecurityController;
import dk.cphbusiness.security.SecurityController;
import dk.cphbusiness.utils.Utils;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import static io.javalin.apibuilder.ApiBuilder.path;

import dk.cphbusiness.security.SecurityRoutes.Role;
import io.javalin.json.JavalinJackson;
import io.javalin.json.JsonMapper;

import java.util.Set;

/**
 * Purpose: To configure the Javalin server
 * Author: Thomas Hartmann
 */
public class ApplicationConfig {
    private ObjectMapper jsonMapper = new ObjectMapper();
    private static ApplicationConfig appConfig;
    private static JavalinConfig javalinConfig;
    private static Javalin app;
    private static ISecurityController securityController = SecurityController.getInstance();

    private ApplicationConfig() {
    }

    public static ApplicationConfig getInstance() {
        if (appConfig == null) {
            appConfig = new ApplicationConfig();
        }
        return appConfig;
    }

    public ApplicationConfig initiateServer() {
        app = Javalin.create(config -> {
            javalinConfig = config;
            // Set the Jackson ObjectMapper to represent DateTime with String
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.writer().withDefaultPrettyPrinter();
            }));
            config.bundledPlugins.enableDevLogging(); // enables extensive development logging in terminal
            config.staticFiles.add("/public"); // enables serving of static files from the public folder in the classpath. PROs: easy to use, CONs: you have to restart the server every time you change a file
            config.http.defaultContentType = "application/json"; // default content type for requests
            config.router.contextPath = "/api"; // base path for all routes
            config.bundledPlugins.enableRouteOverview("/routes", Role.ANYONE); // html overview of all registered routes at /routes for api documentation: https://javalin.io/news/2019/08/11/javalin-3.4.1-released.html
        });
        return appConfig;
    }

    public ApplicationConfig setRoute(EndpointGroup route) {
        javalinConfig.router.apiBuilder(() -> {
            path("/", route);
        });
        return appConfig;
    }


    public ApplicationConfig setCORS() {
        app.before(ctx -> {
            setCorsHeaders(ctx);
        });
        app.options("/*", ctx -> { // Burde nok ikke være nødvendig?
            setCorsHeaders(ctx);
        });
        return appConfig;
    }

    private static void setCorsHeaders(Context ctx) {
//        String requestOrigin = ctx.header("Origin"); // necessary because of the credentials: true when using cookies, then the * is not allowed
//        if(requestOrigin == null){
//            requestOrigin = "*";
//        }
//        Set<String> allowedOrigins = Set.of("http://localhost:5175"
//                , "http://localhost:5174"
//                , "http://localhost:5173"
//                , "https://tripapp.hartmanndemo.dk"
//        );
//        if (allowedOrigins.contains(requestOrigin)) {
//            ctx.header("Access-Control-Allow-Origin", requestOrigin);
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Username");
            ctx.header("Access-Control-Allow-Credentials", "true"); // necessary for cookies
//        }
    }

    // Adding below methods to ApplicationConfig, means that EVERY ROUTE will be checked for security roles. So open routes must have a role of ANYONE
    public ApplicationConfig checkSecurityRoles() {
        app.beforeMatched(securityController.authenticate()); // check if there is a valid token in the header
        app.beforeMatched(securityController.authorize()); // check if the user has the required role
        return appConfig;
    }

    public ApplicationConfig startServer(int port) {
        app.start(port);

        return appConfig;
    }

    public ApplicationConfig stopServer() {
        app.stop();
        return appConfig;
    }

    public ApplicationConfig setApiExceptionHandling() {
        // Might be overruled by the setErrorHandling method
        app.exception(ApiException.class, (e, ctx) -> {
            int statusCode = e.getStatusCode();
            ObjectNode on = jsonMapper
                    .createObjectNode()
                    .put("status", statusCode)
                    .put("msg", e.getMessage());
            ctx.json(on);
            ctx.status(statusCode);
        });
        app.exception(RuntimeException.class, (e, ctx) -> {
            int statusCode = 500;
            ObjectNode on = jsonMapper
                    .createObjectNode()
                    .put("status", statusCode)
                    .put("msg", e.getMessage());
            ctx.status(statusCode);
            ctx.json(on);
        });
        return appConfig;
    }

    public ApplicationConfig setErrorHandling() {
        // To use this one, just set ctx.status(404) in the controller and add a ctx.attribute("msg", "Your message") to the ctx
        // Look at the PersonController: delete() method for an example
        // Might be better to just use the setApiExceptionHandling method above
        app.error(404, ctx -> {
            String message = ctx.attribute("msg");
            ObjectNode on = jsonMapper
                    .createObjectNode()
                    .put("msg", message);
            ctx.json(on);
        });
        return appConfig;
    }

    public ApplicationConfig setGeneralExceptionHandling() {
        ObjectNode on = jsonMapper.createObjectNode();
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.json(on.put("msg",e.getMessage()));
        });
        return appConfig;
    }

    //    public ApplicationConfig setStaticFiles(String dirPath){
//        app.updateConfig(config -> {
//            config.staticFiles.add("/public", Location.EXTERNAL);
//        });
//        return appConfig;
//    }
    public ApplicationConfig beforeFilter() {
        app.before(ctx -> {
            String pathInfo = ctx.req().getPathInfo();
            ctx.req().getHeaderNames().asIterator().forEachRemaining(el -> System.out.println(el));
        });
        return appConfig;
    }
}
