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
    Set<AnswerDTO> getAllAnswers(String className);
    Set<RatingDTO> getAllRatings(Long answerId);
    AnswerDTO create(AnswerDTO dto);
    ClassDTO create(ClassDTO dto);
    AnswerDTO update(Long id, AnswerDTO dto) throws EntityNotFoundException; // For adding a comment to the answer
    ClassDTO updateClass(ClassDTO classDTO) throws EntityNotFoundException;
    ClassDTO getClassByName(String name);


    RatingDTO create(RatingDTO dto);
    EvaluatorDTO createEvaluator(EvaluatorDTO dto);
}