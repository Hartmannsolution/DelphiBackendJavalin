package dk.cphbusiness.persistence.daos;

import dk.cphbusiness.dtos.AnswerDTO;
import dk.cphbusiness.dtos.ClassDTO;
import dk.cphbusiness.dtos.EvaluatorDTO;
import dk.cphbusiness.dtos.RatingDTO;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.model.*;
import dk.cphbusiness.utils.IIdProvider;
import dk.cphbusiness.utils.Populator;
import jakarta.persistence.*;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * author: Thomas Hartmann
 */
public class DAO implements IDAO{

    Logger logger = Logger.getLogger(DAO.class.getName());
    private final EntityManagerFactory emf;
    public DAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Set<RatingDTO> getAllRatings(Long answerId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<RatingDTO> query = em.createQuery("SELECT new dk.cphbusiness.dtos.RatingDTO(r) FROM  Rating r WHERE r.answer.id = :answerId ", RatingDTO.class);
            query.setParameter("answerId", answerId);
            return query.getResultStream().collect(Collectors.toSet());
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error getting all) answers", e);
            throw new RuntimeException("Error getting all answers: " + e.getMessage());
        }
    }

    @Override
    public AnswerDTO create(AnswerDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            ClassName className = em.createQuery("SELECT c FROM ClassName c WHERE c.name = :name", ClassName.class)
                    .setParameter("name", dto.getClassName())
                    .getSingleResult();
            Answer answer = dto.toEntity(className);
            em.persist(answer);
            em.getTransaction().commit();
            return new AnswerDTO(answer);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error creating answer", e);
            throw new RuntimeException("Error creating answer: " + e.getMessage());
        }
    }

    @Override
    public ClassDTO create(ClassDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            TypedQuery<Evaluator> query = em.createQuery("SELECT e FROM Evaluator e WHERE e.user.username = :username", Evaluator.class);
            query.setParameter("username", dto.getFacilitator());
            Evaluator evaluator = query.getSingleResult();
            System.out.println("Evaluator: " + evaluator);
            ClassName className = dto.toEntity(evaluator);
            className.setId(null);
            em.persist(className);
            em.getTransaction().commit();
            return new ClassDTO(className);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error creating class", e);
            throw new RuntimeException("Error creating class: " + e.getMessage());
        }
    }

    @Override
    public ClassDTO updateClass(ClassDTO dto) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            ClassName className = em.find(ClassName.class, dto.getId());
            if (className == null) {
                throw new EntityNotFoundException("Class not found with id: " + dto.getId());
            }
            TypedQuery<Evaluator> query = em.createQuery("SELECT e FROM Evaluator e WHERE e.user.username = :username", Evaluator.class);
            query.setParameter("username", dto.getFacilitator());
            Evaluator evaluator = query.getSingleResult();
            em.getTransaction().begin();
//            ClassName updateClass = dto.toEntity(evaluator);
//            em.merge(updateClass);
            className.setName(dto.getName());
            className. setFacilitator(evaluator);
            className.setNumberOfStudents(dto.getNumberOfStudents());
            em.getTransaction().commit();
            return new ClassDTO(className);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error updating class", e);
            throw new RuntimeException("Error updating class: " + e.getMessage());
        }
    }

    @Override
    public ClassDTO getClassByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ClassName> query = em.createQuery("SELECT c FROM ClassName c WHERE c.name = :name", ClassName.class);
            query.setParameter("name", name);
            ClassName className = query.getSingleResult();
            if (className == null) {
                throw new EntityNotFoundException("Class not found with name: " + name);
            }
            return new ClassDTO(className);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error getting class", e);
            throw new RuntimeException("Error getting class: no class with name " + name);
        }
    }

    public static void main(String[] args) {
        // Test create class
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        DAO dao = new DAO(emf);
        Map<String, IIdProvider<String>> users = new Populator().createUsersAndRoles(emf);
        EvaluatorDTO evaluator = new EvaluatorDTO(((User) users.get("user")).getUsername());
        evaluator = dao.createEvaluator(evaluator);
        AnswerDTO answerDTO = AnswerDTO.builder()
                .text("Test Answer")
                .isPositive(true)
                .className("TestClass")
                .comment("Test comment")
                .build();
        answerDTO = dao.create(answerDTO);
        System.out.println("Answer created: " + answerDTO.getText());
        RatingDTO ratingDTO = new RatingDTO(null, answerDTO.getId(), 1);
        dao.create(ratingDTO);
        System.out.println("Rating created: " + ratingDTO.getValue());
        answerDTO.setComment("sfsfsf sdfs fs dfs fsf sdf sdfs dfs dfsdf sdfs dfsdf sdf sdfsdf sdfs dfs dfsdf sdf sdf sdf sdf sdfsdfsdfsdfs sdfsdfsdf sdfsdfsdf sdfsdfsdfsdf sdfsdfsdf sdfsd sdfsd  sdfsdfsdfsdf sdfsdfsdf sdfsdfsdf sdfsdfsdf sdfsdf sdfsdf sdfsdf");
        dao.update(answerDTO.getId(), answerDTO);
    }

    public AnswerDTO getAnswer(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Answer answer = em.find(Answer.class, id);
            if (answer == null) {
                throw new EntityNotFoundException("Answer not found with id: " + id);
            }
            return new AnswerDTO(answer);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error getting answer", e);
            throw new RuntimeException("Error getting answer: " + e.getMessage());
        }
    }

    @Override
    public AnswerDTO update(Long id, AnswerDTO dto) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            Answer found = em.find(Answer.class, id);
            if (found == null) {
                throw new EntityNotFoundException("Answer not found with id: " + id);
            }
            em.getTransaction().begin();
            found.setComment(dto.getComment());
            em.getTransaction().commit();
            return new AnswerDTO(found);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error updating answer", e);
            throw new RuntimeException("Error updating answer: " + e.getMessage());
        }
    }

    @Override
    public Set<AnswerDTO> getAllAnswers(String className) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<AnswerDTO> query = em.createQuery("SELECT new dk.cphbusiness.dtos.AnswerDTO(a) FROM  Answer a WHERE a.className.name = :className", AnswerDTO.class);
            query.setParameter("className", className);
            return query.getResultStream().collect(Collectors.toSet());
        } catch (Exception e) {
           logger.log(java.util.logging.Level.SEVERE, "Error getting all) answers", e);
            throw new RuntimeException("Error getting all answers: " + e.getMessage());
        }
    }

    @Override
    public RatingDTO create(RatingDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            Answer answer = em.find(Answer.class, dto.getAnswerId());
            em.getTransaction().begin();
            Rating rating = dto.toEntity(answer);
            em.persist(rating);
            em.getTransaction().commit();
            return new RatingDTO(rating);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error creating rating", e);
            throw new RuntimeException("Error creating trip: " + e.getMessage());
        }
    }

    @Override
    public EvaluatorDTO createEvaluator(EvaluatorDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            User user = em.find(User.class, dto.getUsername());
            if (user == null) {
                em.persist(new User(dto.getUsername(), dto.getUsername()));
            }
            Evaluator evaluator = new Evaluator(dto.getUsername(), user);
            em.persist(evaluator);
            em.getTransaction().commit();
            return new EvaluatorDTO(evaluator);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error creating evaluator", e);
            throw new RuntimeException("Error creating evaluator: " + e.getMessage());
        }
    }
}
