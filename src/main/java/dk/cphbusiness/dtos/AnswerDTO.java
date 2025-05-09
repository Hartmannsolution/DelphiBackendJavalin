package dk.cphbusiness.dtos;

/**
 * Purpose:
 *
 * Author: Thomas Hartmann
 */
import dk.cphbusiness.persistence.model.Answer;
import dk.cphbusiness.persistence.model.ClassName;
import dk.cphbusiness.utils.IIdProvider;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerDTO implements IIdProvider<Long> {
    private Long id;
    private String text;
    private String className;
    private String comment;
    private boolean isPositive;
    private List<RatingDTO> ratings;
    // constructor with Answer entity
    public AnswerDTO(Answer answer){
        this.id = answer.getId();
        this.text = answer.getText();
        this.className = answer.getClassName().getName();
        this.comment = answer.getComment();
        this.isPositive = answer.isPositive();
        this.ratings = answer.getRatings() != null? answer.getRatings().stream().map(RatingDTO::new).toList(): new ArrayList<>();
    }
    public Answer toEntity(ClassName className){
        return Answer.builder()
                .id(id)
                .text(text)
                .className(className)
                .comment(comment)
                .isPositive(isPositive)
                .build();
    }
}

