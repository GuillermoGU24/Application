package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer term;

    @NotBlank(message = "Document is required")
    private String document;

    @NotNull(message = "Loan type is required")
    private Long loanTypeId;
}
