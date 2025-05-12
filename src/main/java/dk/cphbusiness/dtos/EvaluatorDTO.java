package dk.cphbusiness.dtos;

import dk.bugelhartmann.UserDTO;
import dk.cphbusiness.persistence.model.Evaluator;
import dk.cphbusiness.persistence.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluatorDTO {

    private String username;
    private List<ClassDTO> classes;

    public EvaluatorDTO(Evaluator facilitator) {
        this.username = facilitator.getUser().getUsername();
        this.classes = facilitator.getClasses()
                .stream()
                .map(className -> new ClassDTO(-1, className.getName(), null, className.getNumberOfStudents(), username))
                .collect(Collectors.toList());
    }

    public EvaluatorDTO(String username) {
        this.username = username;
    }
}