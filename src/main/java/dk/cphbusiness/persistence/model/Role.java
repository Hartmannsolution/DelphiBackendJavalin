package dk.cphbusiness.persistence.model;

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Purpose: To handle security in the API
 *  Author: Thomas Hartmann
 */
@Entity
@Table(name = "roles")
@NamedQueries(@NamedQuery(name = "Role.deleteAllRows", query = "DELETE from Role"))
public class Role implements Serializable, IIdProvider<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "name", length = 20)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role() {}

    public Role(String roleName) {
        this.name = roleName;
    }

    public String getRoleName() {
        return name;
    }
    public Set<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleName='" + name + '\'' +
                '}';
    }
    public String getId() {
        return name;
    }
}
