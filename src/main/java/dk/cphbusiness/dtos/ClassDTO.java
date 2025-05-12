package dk.cphbusiness.dtos;

import dk.cphbusiness.persistence.model.ClassName;
import dk.cphbusiness.persistence.model.Evaluator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * Author: Thomas Hartmann
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassDTO {

    long id;
    private String name;
    private List<AnswerDTO> answers;
    private int numberOfStudents;
    private String facilitator;

    public ClassDTO(ClassName className) {
        if (className.getId() != 0) {
            this.id = className.getId();
        } else {
            this.id = -1;
        }
        this.name = className.getName();
        this.answers = className.getAnswers() != null? className.getAnswers().stream().map(AnswerDTO::new).collect(Collectors.toList()): new ArrayList<>();
        this.numberOfStudents = className.getNumberOfStudents();
        this.facilitator = className.getFacilitator().getUser().getUsername();
    }
    public ClassName toEntity(Evaluator evaluator) {
        return ClassName.builder()
                // if id is -1, it means it's a new entity
                .id(id == 0 ? null : id)
                .name(name)
                .answers(null)
                .numberOfStudents(numberOfStudents)
                .facilitator(evaluator)
                .build();
    }
}