package dk.cphbusiness.rest.controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Purpose: This interface is used to determine which methods a controller must implement
 * Author: Thomas Hartmann
 */
public interface IController {
    void getAllAnswers(Context ctx);
    void getAllRatings(Context ctx);
    void createAnswer(Context ctx);
    void createClass(Context ctx);
    void editClass(Context ctx);
    void getClassName(Context ctx);
    void addCommentToAnswer(Context ctx);
    void createRating(Context ctx);
//    void createEvaluator(Context ctx); // will be created at register

}
