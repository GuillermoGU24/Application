package co.com.crediya.model.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    private Long applicationId;
    private Double amount;
    private Integer term;
    private String document;
    private Long stateId;
    private Long loanTypeId;
}
