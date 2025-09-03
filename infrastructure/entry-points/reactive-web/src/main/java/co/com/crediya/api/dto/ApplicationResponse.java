package co.com.crediya.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationResponse {
    private Long applicationId;
    private Double amount;
    private Integer term;
    private String document;
    private String state;
    private String loanType;
}
