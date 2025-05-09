package dk.cphbusiness.persistence.daos;


import dk.cphbusiness.dtos.AnswerDTO;
import dk.cphbusiness.dtos.ClassDTO;
import dk.cphbusiness.dtos.EvaluatorDTO;
import dk.cphbusiness.dtos.RatingDTO;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.model.Answer;
import dk.cphbusiness.persistence.model.ClassName;
import dk.cphbusiness.persistence.model.Rating;
import dk.cphbusiness.persistence.model.User;
import dk.cphbusiness.utils.IIdProvider;
import dk.cphbusiness.utils.Populator;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
class DAOTest {

    private DAO dao;
    Map<String, IIdProvider<String>> users;
    EvaluatorDTO evaluator;
    ClassDTO classDTO;
    AnswerDTO answerDTO;
    RatingDTO ratingDTO;

    @BeforeAll
    void setup() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = new DAO(emf);
        Populator populator = new Populator();
        users = populator.createUsersAndRoles(emf);
        evaluator = new EvaluatorDTO(((User)users.get("user")).getUsername());
        evaluator = dao.createEvaluator(evaluator);
        classDTO = new ClassDTO("TestClass", null, 10, evaluator.getUsername());
        classDTO = dao.create(classDTO);
        answerDTO = AnswerDTO.builder()
                .text("Test Answer")
                .isPositive(true)
                .className("TestClass")
                .comment("Test comment")
                .build();
        answerDTO = dao.create(answerDTO);
        ratingDTO = new RatingDTO(null, answerDTO.getId(), Rating.Value.AGREE);
        dao.create(ratingDTO);
    }

    @Test
    void testCreateAndGetAnswer() {
        EvaluatorDTO evaluatorDTO = new EvaluatorDTO();
        ClassDTO classDTO = new ClassDTO("TestClass", null, 10, evaluatorDTO.getUsername());

        AnswerDTO dto = AnswerDTO.builder()
                .text("Test Answer")
                .isPositive(true)
                .className("TestClass")
                .comment("Test comment")
                .build();

        ClassDTO createdClass = dao.create(classDTO);
        AnswerDTO created = dao.create(dto);

        assertNotNull(created.getId());
        assertEquals("Test Answer", created.getText());

        Set<AnswerDTO> all = dao.getAllAnswers();
        assertTrue(all.stream().anyMatch(a -> a.getId().equals(created.getId())));
    }

//    @Test
//    void testCreateClass() {
//        ClassDTO dto = new ClassDTO(null, "MyClass", 10);
//        ClassDTO created = dao.create(dto);
//
//        assertNotNull(created.getId());
//        assertEquals("MyClass", created.getName());
//    }
//
//    @Test
//    void testCreateAndGetRating() {
//        AnswerDTO answerDTO = dao.create(AnswerDTO.builder()
//                .text("Rating Answer")
//                .isPositive(true)
//                .time(LocalDateTime.now())
//                .className("RClass")
//                .comment("Comment")
//                .build());
//
//        RatingDTO ratingDTO = new RatingDTO(null, answerDTO.getId(), Rating.Value.AGREE);
//        RatingDTO created = dao.create(ratingDTO);
//
//        assertNotNull(created.getId());
//        assertEquals(Rating.Value.AGREE, created.getValue());
//
//        Set<RatingDTO> ratings = dao.getAllRatings(answerDTO.getId());
//        assertEquals(1, ratings.size());
//    }
//
//    @Test
//    void testUpdateAnswer() {
//        AnswerDTO original = dao.create(AnswerDTO.builder()
//                .text("ToUpdate")
//                .isPositive(false)
//                .time(LocalDateTime.now())
//                .className("UpdateClass")
//                .comment("Old comment")
//                .build());
//
//        original.setComment("Updated comment");
//        AnswerDTO updated = dao.update(original.getId(), original);
//
//        assertEquals("Updated comment", updated.getComment());
//    }
}
