package dk.cphbusiness.persistence.model;

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Purpose of this class is to represent a Class entity.
 * Author: Thomas Hartmann
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQuery(name = "ClassName.deleteAll", query = "DELETE FROM ClassName")
public class ClassName implements IIdProvider<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "number_of_students", nullable = false)
    private int numberOfStudents;

    @OneToMany(mappedBy = "className", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "facilitator_id", nullable = false)
    private Evaluator facilitator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassName that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}



