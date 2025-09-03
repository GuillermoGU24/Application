package co.com.crediya.model.application;


import co.com.crediya.model.application.exeption.LoanDomainValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanDomainValidatorTest {

    @Test
    void shouldThrowWhenApplicationIsNull() {
        LoanType type = LoanType.builder()
                .minAmount(1000.0)
                .maxAmount(5000.0)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> LoanDomainValidator.validate(null, type));

        assertEquals("application: Application is required", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDocumentIsNullOrBlank() {
        Application app = Application.builder()
                .document(null)
                .amount(2000.0)
                .term(12)
                .build();

        LoanType type = LoanType.builder()
                .minAmount(1000.0)
                .maxAmount(5000.0)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> LoanDomainValidator.validate(app, type));

        assertEquals("document: Document is required", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAmountIsNullOrInvalid() {
        Application app = Application.builder()
                .document("12345")
                .amount(0.0)
                .term(12)
                .build();

        LoanType type = LoanType.builder()
                .minAmount(1000.0)
                .maxAmount(5000.0)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> LoanDomainValidator.validate(app, type));

        assertEquals("amount: Amount must be greater than 0", ex.getMessage());
    }

    @Test
    void shouldThrowWhenTermIsInvalid() {
        Application app = Application.builder()
                .document("12345")
                .amount(2000.0)
                .term(0)
                .build();

        LoanType type = LoanType.builder()
                .minAmount(1000.0)
                .maxAmount(5000.0)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> LoanDomainValidator.validate(app, type));

        assertEquals("term: Term must be between 1 and 120 months", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLoanTypeIsNull() {
        Application app = Application.builder()
                .document("12345")
                .amount(2000.0)
                .term(12)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> LoanDomainValidator.validate(app, null));

        assertEquals("loanTypeId: Loan type not found", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLoanTypeLimitsNotConfigured() {
        Application app = Application.builder()
                .document("12345")
                .amount(2000.0)
                .term(12)
                .build();

        LoanType type = LoanType.builder()
                .minAmount(null)
                .maxAmount(null)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> LoanDomainValidator.validate(app, type));

        assertEquals("loanType: Loan type limits not configured", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAmountOutOfRange() {
        Application app = Application.builder()
                .document("12345")
                .amount(9000.0)
                .term(12)
                .build();

        LoanType type = LoanType.builder()
                .minAmount(1000.0)
                .maxAmount(5000.0)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> LoanDomainValidator.validate(app, type));

        assertEquals("amount: Amount must be between loan type min and max", ex.getMessage());
    }

    @Test
    void shouldNotThrowWhenApplicationIsValid() {
        Application app = Application.builder()
                .document("12345")
                .amount(3000.0)
                .term(24)
                .build();

        LoanType type = LoanType.builder()
                .minAmount(1000.0)
                .maxAmount(5000.0)
                .build();

        assertDoesNotThrow(() -> LoanDomainValidator.validate(app, type));
    }
}
