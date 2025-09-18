package co.com.crediya.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationListItemResponse {
    private Double amount;
    private Integer term;
    private String email;
    private String firstName;
    private String loanType;
    private Double interestRate;
    private String applicationStatus;
    private Double baseSalary;
}
