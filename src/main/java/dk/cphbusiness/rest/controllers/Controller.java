package dk.cphbusiness.rest.controllers;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */

//import dk.cphbusiness.persistence.daos.ITripGuideDAO;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.dtos.AnswerDTO;
import dk.cphbusiness.dtos.ClassDTO;
import dk.cphbusiness.dtos.RatingDTO;
import dk.cphbusiness.exceptions.ApiException;
import dk.cphbusiness.persistence.daos.DAO;
import dk.cphbusiness.persistence.daos.IDAO;
import dk.cphbusiness.utils.Utils;
import io.javalin.http.Context;
import io.javalin.validation.BodyValidator;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Purpose: REST controller for RatingDTO.
 * Author: Thomas Hartmann
 */
public class Controller implements IController {

    private static IDAO dao;
    private static ObjectMapper objectMapper = Utils.getObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);


    public Controller(EntityManagerFactory emf) {
        dao = new DAO(emf);
    }


    @Override
    public void getAllAnswers(Context ctx) {
        String className = ctx.pathParam("className");
        ctx.status(200).json(dao.getAllAnswers(className));
    }

    @Override
    public void getAllRatings(Context ctx) {
        // get all ratings based on answerId from path param
        Long answerId = Long.valueOf(ctx.pathParam("answerId"));
        Set<RatingDTO> ratings = dao.getAllRatings(answerId);
        if (ratings.isEmpty()) {
            ctx.status(404).json(objectMapper.createObjectNode().put("message", "No such answer"));
        } else {
            ctx.status(200).json(ratings);
        }
    }

    @Override
    public void createAnswer(Context ctx) {
        // create answer based on body
        try{
        BodyValidator<AnswerDTO> bodyValidator = ctx.bodyValidator(AnswerDTO.class);
        AnswerDTO answerDTO = bodyValidator
                .check(dto -> dto.getText() != null && !dto.getText().isEmpty(), "Text is required")
                .check(dto -> dto.getClassName() != null && !dto.getClassName().isEmpty(), "Class name is required")
                .get();
//            AnswerDTO answerDTO = ctx.bodyAsClass(AnswerDTO.class);
        answerDTO = dao.create(answerDTO);
        ctx.status(201).json(answerDTO);
        } catch(Exception ex){
            logger.error("Error creating class"+ ex);
            ex.printStackTrace();
            throw new ApiException(400, "Error creating class: " + ex.getMessage());
        }
    }

    @Override
    public void createClass(Context ctx){
            // create class based on body
            BodyValidator<ClassDTO> bodyValidator = ctx.bodyValidator(ClassDTO.class);
            ClassDTO classDTO = bodyValidator
                    .check(dto -> dto.getName() != null && !dto.getName().isEmpty(), "Class name is required")
                    .check(dto -> dto.getFacilitator() != null && !dto.getFacilitator().isEmpty(), "Facilitator is required")
                    .get();

        classDTO = dao.create(classDTO);
        ctx.status(201).json(classDTO);
    }

    @Override
    public void editClass(Context ctx) {
        BodyValidator<ClassDTO> bodyValidator = ctx.bodyValidator(ClassDTO.class);
        ClassDTO classDTO = bodyValidator
                .check(dto -> dto.getName() != null && !dto.getName().isEmpty(), "Class name is required")
//                .check(dto -> dto.getFacilitator() != null && !dto.getFacilitator().isEmpty(), "Facilitator is required")
                .get();

        classDTO.setId(Long.parseLong(ctx.pathParam("classId")));
        classDTO = dao.updateClass(classDTO);
        ctx.status(201).json(classDTO);

    }

    @Override
    public void getClassName(Context ctx) {
        String className = ctx.pathParam("className");
        System.out.println("Class name: " + className);
        try {
            ClassDTO classDTO = dao.getClassByName(className);
            ctx.status(200).json(classDTO);
        } catch (RuntimeException ex) {
            logger.error("Error getting class by name: " + ex.getMessage());
            throw new ApiException(404, "Error getting class by name: " + ex.getMessage());
        }
    }

    @Override
    public void addCommentsToAnswers(Context ctx) {
       // get Set of AnswerDTOs from context
        try {
            Set<AnswerDTO> answers = objectMapper.readValue(ctx.body(), new TypeReference<Set<AnswerDTO>>() {});

            // add comments to answers
            answers.forEach(answerDTO -> {
                if (answerDTO.getComment() != null) {
                    dao.updateAnswer(answerDTO.getId(), answerDTO);
                }
            });
            ctx.status(200).json(answers);
        } catch (IOException ex) {
            logger.error("Error adding comments to answers: {}", ex.getMessage());
            throw new ApiException(400, "Error reading the answers");
        }
    }

    private RatingDTO createRating(RatingDTO ratingDTO) {
        // create rating based on body
        AnswerDTO answerDTO = dao.getAnswer(ratingDTO.getAnswerId());
        if (answerDTO == null) {
            throw new EntityNotFoundException("No such answer");
        }
        ratingDTO = dao.create(ratingDTO);
        return ratingDTO;
    }

    @Override
    public void createRatings(Context ctx) {
        // get all ratings from body as List<RatingDTO>
        try {
            List<RatingDTO> ratingDTOs = objectMapper.readValue(ctx.body(), new TypeReference<List<RatingDTO>>() {});
            ratingDTOs.forEach(ratingDTO -> {
                RatingDTO rating = createRating(ratingDTO);
            });
            ctx.status(201);
        } catch (IOException ex){
            logger.error("Error creating ratings: {}", ex.getMessage());
            throw new ApiException(400, "Error reading the ratings");
        }
    }
}

