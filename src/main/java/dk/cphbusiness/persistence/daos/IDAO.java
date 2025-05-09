package dk.cphbusiness.persistence.daos;

import dk.cphbusiness.dtos.AnswerDTO;
import dk.cphbusiness.dtos.ClassDTO;
import dk.cphbusiness.dtos.EvaluatorDTO;
import dk.cphbusiness.dtos.RatingDTO;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;

/**
 * Purpose: This is an interface for making a DAO (Data Access Object) that can be used to perform CRUD operations on any entity.
 * Author: Thomas Hartmann
 */
public interface IDAO {
    Set<AnswerDTO> getAllAnswers();
    Set<RatingDTO> getAllRatings(Long answerId);
    AnswerDTO create(AnswerDTO dto);
    ClassDTO create(ClassDTO dto);
    ClassDTO update(ClassDTO dto) throws EntityNotFoundException;
    AnswerDTO update(Long id, AnswerDTO dto) throws EntityNotFoundException; // For adding a comment to the answer
    RatingDTO create(RatingDTO dto);
    EvaluatorDTO createEvaluator(EvaluatorDTO dto);
}