package dk.cphbusiness.dtos;

import dk.cphbusiness.persistence.model.Rating;
import dk.cphbusiness.persistence.model.Answer;
import dk.cphbusiness.utils.IIdProvider;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO implements IIdProvider<Long> {
    private Long id;
    private Long answerId;
    private int value;

    public RatingDTO(Rating rating) {
        this.id = rating.getId();
        this.answerId = rating.getAnswer().getId();
        this.value = rating.getValue().getNumericValue();
    }

    public Rating toEntity(Answer answer) {
        return Rating.builder()
                .id(id)
                .answer(answer)
                .value(value==0? Rating.Value.NEUTRAL :
                        value > 0? Rating.Value.AGREE :
                                Rating.Value.DISAGREE)
                .build();
    }
}
