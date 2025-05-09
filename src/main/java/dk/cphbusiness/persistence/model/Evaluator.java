package dk.cphbusiness.persistence.model;

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Purpose of this class is to represent a Evaluator entity.
 * Author: Thomas Hartmann
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQuery(name = "Facilitator.deleteAll", query = "DELETE FROM Evaluator")
public class Evaluator implements IIdProvider<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "facilitator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassName> classes = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "username", nullable = false)
    private User user;

    public Evaluator(String name, User user) {
        this.name = name;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evaluator that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}



