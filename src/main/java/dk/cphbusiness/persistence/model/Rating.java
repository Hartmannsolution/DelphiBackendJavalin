package dk.cphbusiness.persistence.model;

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a rating for an answer.
 * Author: Thomas Hartmann
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQuery(name = "Rating.deleteAll", query = "DELETE FROM Rating")
public class Rating implements IIdProvider<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Value value;

    @PrePersist
    private void prePersist() {
        if (time == null) {
            time = LocalDateTime.now();
        }
    }

    @Getter
    public enum Value {
        AGREE(1), NEUTRAL(0), DISAGREE(-1);

        private final int numericValue;

        Value(int numericValue) {
            this.numericValue = numericValue;
        }

        public static Value fromNumeric(int numericValue) {
            return switch (numericValue) {
                case 1 -> AGREE;
                case 0 -> NEUTRAL;
                case -1 -> DISAGREE;
                default -> throw new IllegalArgumentException("Unknown numeric value: " + numericValue);
            };
        }
    }
}
