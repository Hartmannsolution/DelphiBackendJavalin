package dk.cphbusiness.persistence.model;

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Purpose of this class is to represent an Answer entity.
 * Author: Thomas Hartmann
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQuery(name = "Answer.deleteAll", query = "DELETE FROM Answer")
public class Answer implements IIdProvider<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "is_positive", nullable = false)
    private boolean isPositive;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime time;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassName className;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rating> ratings = new HashSet<>();

    @PrePersist
    private void prePersist() {
        if (time == null) {
            time = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
