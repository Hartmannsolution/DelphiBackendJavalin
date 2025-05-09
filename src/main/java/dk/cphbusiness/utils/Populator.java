package dk.cphbusiness.utils;

import dk.cphbusiness.dtos.AnswerDTO;
import dk.cphbusiness.dtos.RatingDTO;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.daos.IDAO;
import dk.cphbusiness.persistence.daos.DAO;
import dk.cphbusiness.persistence.model.*;
import dk.cphbusiness.security.ISecurityController;
import dk.cphbusiness.security.ISecurityDAO;
import dk.cphbusiness.security.SecurityController;
import dk.cphbusiness.security.SecurityDAO;
import dk.cphbusiness.persistence.model.Role;
import dk.cphbusiness.persistence.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Purpose: To populate the database with users and roles
 * Author: Thomas Hartmann
 */
public class Populator {
    // method to create users and roles before each test
    public Map<String, IIdProvider<String>> createUsersAndRoles(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM Rating r").executeUpdate();
            em.createQuery("DELETE FROM Answer a").executeUpdate();
            em.createQuery("DELETE FROM ClassName c").executeUpdate();
            em.createQuery("DELETE FROM Evaluator e").executeUpdate();
            em.createQuery("DELETE FROM User u").executeUpdate();
            em.createQuery("DELETE FROM Role r").executeUpdate();
            User user = new User("user", "user123");
            User admin = new User("admin", "admin123");
            User superUser = new User("super", "super123");
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user.addRole(userRole);
            admin.addRole(adminRole);
            superUser.addRole(userRole);
            superUser.addRole(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(superUser);
            em.persist(userRole);
            em.persist(adminRole);
            em.getTransaction().commit();
            return Map.of("user", user, "admin", admin, "superUser", superUser, "userRole", new Role("user"), "adminRole", new Role("admin"));
        }
    }
//
//    public Map<String, IIdProvider<Long>> createTripsAndGuides(EntityManagerFactory emf) {
//        Map<String, IIdProvider<Long>> tripsAndGuides = new HashMap<>();
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            em.createQuery("DELETE FROM Rating t").executeUpdate();
//            em.createQuery("DELETE FROM Answer g").executeUpdate();
//            // reset the sequence
//            em.createNativeQuery("ALTER SEQUENCE trip_id_seq RESTART WITH 1").executeUpdate();
//            em.createNativeQuery("ALTER SEQUENCE guide_id_seq RESTART WITH 1").executeUpdate();
//            em.getTransaction().commit();
//        }
//        IDAO<RatingDTO> tripDAO;
//        IDAO<AnswerDTO> guideDAO;
//        ITripGuideDAO tripGuideDAO;
//        ISecurityDAO securityDAO;
//        ISecurityController securityController;
//
//        AnswerDTO guide1, guide2, guide3;
//        RatingDTO trip1, trip2, trip3, trip4, trip5, trip6, trip7, trip8, trip9, trip10;
//        User user, admin, superUser = null;
//        emf = HibernateConfig.getEntityManagerFactory();
//        securityDAO = new SecurityDAO(emf);
//        securityController = SecurityController.getInstance();
//        tripDAO = DAO.getTripDAO(emf);
//        guideDAO = GuideDAO.getGuideDAO(emf);
//        tripGuideDAO = DAO.getTripGuideDAO(emf);
//        LocalDateTime base = LocalDateTime.now();
//        trip1 = new RatingDTO( base.plusDays(14), base.plusDays(15), 12.34, 56.78, "Beach Holiday", 150.00, 1L, Rating.TripCategory.BEACH, null);
//        trip2 = new RatingDTO( base.plusDays(30), base.plusDays(31), 13.45, 57.89, "City Tour", 200.00, 1L, Rating.TripCategory.CITY, null);
//        trip3 = new RatingDTO( base.plusDays(50), base.plusDays(52), 14.56, 58.90, "Forest Adventure", 180.00, 2L, Rating.TripCategory.FOREST, null);
//        trip4 = new RatingDTO( base.plusDays(40), base.plusDays(54), 14.96, 58.95, "Forest Climbing", 280.00, 2L, Rating.TripCategory.FOREST, null);
//        trip5 = new RatingDTO( base.plusDays(60), base.plusDays(62), 14.94, 58.75, "Forest Dwelling", 780.00, 2L, Rating.TripCategory.FOREST, null);
//        trip6 = new RatingDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Lake swimming", 130.00, 2L, Rating.TripCategory.LAKE, null);
//        trip7 = new RatingDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Shark fishing", 510.00, 2L, Rating.TripCategory.SEA, null);
//        trip8 = new RatingDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Cross Country Skiing", 444.00, 2L, Rating.TripCategory.SNOW, null);
//        trip9 = new RatingDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Snow boarding", 440.00, 2L, Rating.TripCategory.SNOW, null);
//        trip10 = new RatingDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Living under a bridge", 10.00, 2L, Rating.TripCategory.CITY, null);
//
//        guide1 = new AnswerDTO("Andreas", "Turkey", "andreas@mail.com", "33293922", 10);
//        guide2 = new AnswerDTO("Jane", "Doe", "jdo@mail.com", "12345678", 11);
//        guide3 = new AnswerDTO("Brian", "Swan", "jsw@mail.com", "44993434", 12);
//        try {
//            trip1 = tripDAO.create(trip1);
//            trip2 = tripDAO.create(trip2);
//            trip3 = tripDAO.create(trip3);
//            trip4 = tripDAO.create(trip4);
//            trip5 = tripDAO.create(trip5);
//            trip6 = tripDAO.create(trip6);
//            trip7 = tripDAO.create(trip7);
//            trip8 = tripDAO.create(trip8);
//            trip9 = tripDAO.create(trip9);
//            trip10 = tripDAO.create(trip10);
//
//            guide1 = guideDAO.create(guide1);
//            guide2 = guideDAO.create(guide2);
//            guide3 = guideDAO.create(guide3);
//
//            trip1 = tripGuideDAO.addGuideToTrip(trip1.getId(), guide1.getId());
//            trip2 = tripGuideDAO.addGuideToTrip(trip2.getId(), guide1.getId());
//            trip3 = tripGuideDAO.addGuideToTrip(trip3.getId(), guide2.getId());
//            trip4 = tripGuideDAO.addGuideToTrip(trip4.getId(), guide2.getId());
//            trip5 = tripGuideDAO.addGuideToTrip(trip5.getId(), guide2.getId());
//            trip6 = tripGuideDAO.addGuideToTrip(trip6.getId(), guide3.getId());
//            trip7 = tripGuideDAO.addGuideToTrip(trip7.getId(), guide3.getId());
//            trip8 = tripGuideDAO.addGuideToTrip(trip8.getId(), guide3.getId());
//            trip9 = tripGuideDAO.addGuideToTrip(trip9.getId(), guide1.getId());
//            trip10 = tripGuideDAO.addGuideToTrip(trip10.getId(), guide1.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        tripsAndGuides.put("trip1", trip1);
//        tripsAndGuides.put("trip2", trip2);
//        tripsAndGuides.put("trip3", trip3);
//        tripsAndGuides.put("trip4", trip4);
//        tripsAndGuides.put("trip5", trip5);
//        tripsAndGuides.put("trip6", trip6);
//        tripsAndGuides.put("trip7", trip7);
//        tripsAndGuides.put("trip8", trip8);
//        tripsAndGuides.put("trip9", trip9);
//        tripsAndGuides.put("trip10", trip10);
//        tripsAndGuides.put("guide1", guide1);
//        tripsAndGuides.put("guide2", guide2);
//        tripsAndGuides.put("guide3", guide3);
//        return tripsAndGuides;
////        return Map.of("trip1", trip1, "trip2", trip2, "trip3", trip3, "guide1", guide1, "guide2", guide2, "guide3", guide3);
//    }
//
//    public static void main(String[] args) {
//        Populator populator = new Populator();
//        Map<String, IIdProvider<String>> populatedUsers = populator.createUsersAndRoles(HibernateConfig.getEntityManagerFactory());
//        Map<String, IIdProvider<Long>> populatedTrips = populator.createTripsAndGuides(HibernateConfig.getEntityManagerFactory());
//    }
}

