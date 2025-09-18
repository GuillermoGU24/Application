package co.com.crediya.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationForReviewResponse {
    private Long applicationId;
    private Double amount;
    private Integer term;
    private String document;
    private String fullName;
    private String email;
    private String loanType;
    private Double interestRate;
    private String applicationStatus;
    private Long baseSalary;
    private Double monthlyApplicationAmount; 
}

