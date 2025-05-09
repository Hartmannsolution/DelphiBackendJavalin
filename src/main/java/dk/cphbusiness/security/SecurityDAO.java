package dk.cphbusiness.security;

import dk.bugelhartmann.UserDTO;
import dk.cphbusiness.dtos.EvaluatorDTO;
import dk.cphbusiness.exceptions.ApiException;
import dk.cphbusiness.persistence.daos.DAO;
import dk.cphbusiness.persistence.model.Role;
import dk.cphbusiness.persistence.model.User;
import dk.cphbusiness.exceptions.ValidationException;
import jakarta.persistence.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.stream.Collectors;


/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityDAO implements ISecurityDAO {

    private static ISecurityDAO instance;
    private static EntityManagerFactory emf;

    // For generating secure random refresh tokens
    private static final SecureRandom secureRandom = new SecureRandom(); // Thread-safe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); // URL-safe encoding

    public SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Purpose: Used in SecurityController login method to verify user
     * @param username
     * @param password
     * @return
     * @throws ValidationException
     */
    @Override
    public UserDTO getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = getEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
            user.getRoles().size(); // force roles to be fetched from db
            if (!user.verifyPassword(password))
                throw new ValidationException("Wrong password");
            return new UserDTO(user.getUsername(), user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
        }
    }

    /**
     * Purpose: Used in SecurityController to renew JWT token by verifying user from refresh token.
     * @param username
     * @param Token (refresh token)
     * @return
     * @throws ValidationException
     */
    @Override
    public UserDTO getTokenVerifiedUser(String username, String Token) throws ValidationException {
        try (EntityManager em = getEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
            user.getRoles().size(); // force roles to be fetched from db

//            if (!user.verifyRefreshToken(Token))
//                throw new ValidationException("Could not verify token: " + Token);
            return new UserDTO(user.getUsername(), user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
        }
    }

    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = getEntityManager()) {
            User userEntity = em.find(User.class, username);
            if (userEntity != null)
                throw new EntityExistsException("User with username: " + username + " already exists");
            userEntity = new User(username, password);
            em.getTransaction().begin();
            Role userRole = em.find(Role.class, "user");
            if (userRole == null)
                userRole = new Role("user");
            em.persist(userRole);
            userEntity.addRole(userRole);
            em.persist(userEntity);
            em.getTransaction().commit();
            // Create an evaluator when creating a user
            new DAO(emf).createEvaluator(new EvaluatorDTO(userEntity.getUsername()));
            return userEntity;
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(400, e.getMessage());
        }
    }

//    @Override
//    public String addRefreshToken(String username) {
//        try (EntityManager em = getEntityManager()) {
//            User user = em.find(User.class, username);
//            if (user == null)
//                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
//            // check if user already has a valid refresh token
//            if (user.getValidRefreshToken() != null)
//                return user.getValidRefreshToken().getId();
//            // create a refresh token
//            byte[] randomBytes = new byte[32];
//            secureRandom.nextBytes(randomBytes);
//            String refreshToken = base64Encoder.encodeToString(randomBytes);
//            em.getTransaction().begin();
//            RefreshToken newToken = user.addRefreshToken(refreshToken);
//            em.persist(newToken);
//            em.getTransaction().commit();
//            return refreshToken;
//        }
//    }

//    @Override
//    public void invalidateRefreshToken(String username, String token) throws ValidationException {
//        try (EntityManager em = getEntityManager()) {
//            User user = em.find(User.class, username);
//            if (user == null)
//                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
//            if (!user.verifyRefreshToken(token))
//                throw new ValidationException("Could not verify token: " + token);
//            em.getTransaction().begin();
//            user.invalidateRefreshToken(token);
//            em.getTransaction().commit();
//        }
//    }

    @Override
    public User addRoleToUser(String username, String role) {
        try(EntityManager em = emf.createEntityManager()){
            User foundUser = em.find(User.class, username);
            Role foundRole = em.find(Role.class, role);
            em.getTransaction().begin();
            foundUser.addRole(foundRole);
            em.getTransaction().commit();
            return foundUser;
        }
    }

    @Override
    public User removeRoleFromUser(String username, String role) {
        try(EntityManager em = emf.createEntityManager()){
            User foundUser = em.find(User.class, username);
            Role foundRole = em.find(Role.class, role);
            em.getTransaction().begin();
            foundUser.removeRole(role);
            em.getTransaction().commit();
            return foundUser;
        }
    }
}
