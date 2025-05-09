package dk.cphbusiness.security;
import dk.bugelhartmann.UserDTO;
import dk.cphbusiness.persistence.model.User;
import dk.cphbusiness.exceptions.ValidationException;

/**
 * Purpose: To handle security with the database
 * Author: Thomas Hartmann
 */
public interface ISecurityDAO {
    UserDTO getVerifiedUser(String username, String password) throws ValidationException;
    UserDTO getTokenVerifiedUser(String username, String refreshToken) throws ValidationException;
    User createUser(String username, String password);
//    String addRefreshToken(String username); // if user allready has a valid token, return that
//    void invalidateRefreshToken(String username, String refreshToken) throws ValidationException;

    User addRoleToUser(String username, String role);
    User removeRoleFromUser(String username, String role);
}
