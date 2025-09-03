// co.com.crediya.model.application.exception.LoanDomainValidator.java
package co.com.crediya.model.application.exeption;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.LoanType;


public class LoanDomainValidator {

    public static void validate(Application app, LoanType type) {
        if (app == null) throw new IllegalArgumentException("application: Application is required");

        if (app.getDocument() == null || app.getDocument().isBlank())
            throw new IllegalArgumentException("document: Document is required");

        if (app.getAmount() == null || app.getAmount() <= 0L)
            throw new IllegalArgumentException("amount: Amount must be greater than 0");

        if (app.getTerm() == null || app.getTerm() < 1 || app.getTerm() > 120)
            throw new IllegalArgumentException("term: Term must be between 1 and 120 months");

        if (type == null)
            throw new IllegalArgumentException("loanTypeId: Loan type not found");

        if (type.getMinAmount() == null || type.getMaxAmount() == null)
            throw new IllegalArgumentException("loanType: Loan type limits not configured");

        if (app.getAmount().compareTo(type.getMinAmount()) < 0 ||
                app.getAmount().compareTo(type.getMaxAmount()) > 0)
            throw new IllegalArgumentException("amount: Amount must be between loan type min and max");
    }
}

