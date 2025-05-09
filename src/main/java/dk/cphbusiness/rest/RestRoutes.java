package dk.cphbusiness.rest;

import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.rest.controllers.Controller;
import dk.cphbusiness.security.SecurityRoutes;
import dk.cphbusiness.security.SecurityRoutes.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Purpose: To demonstrate the use of unprotected routes and protected ones
 *
 * Author: Thomas Hartmann
 */
public class RestRoutes {
    private static final Controller controller = new Controller(HibernateConfig.getEntityManagerFactory());
    public static EndpointGroup getRoutes() {
        // get all the routes from controller: getAllAnswers, getAllRatings, createAnswer, createClass, addCommentToAnswer, createRating, createEvaluator
        return () -> {
            path("", () -> {
                path("/answers", () -> {
                    get(controller::getAllAnswers, Role.USER);
                    post(controller::createAnswer, Role.USER);
                });
                path("/ratings", () -> {
                    get("{answerId}", controller::getAllRatings, Role.USER);
                    post("{answerId}", controller::createRating, Role.USER);
                });
                path("/classes", () -> {
                    post(controller::createClass, Role.USER);
                });
            });
        };
    }
}