// co.com.crediya.model.application.exception.LoanDomainValidator.java
package co.com.crediya.model.application.exeption;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.LoanType;


public class LoanDomainValidator {

    public static void validate(Application app, LoanType type) {
        if (app == null) throw new IllegalArgumentException("application: Application is required");

        if (app.getDocumento() == null || app.getDocumento().isBlank())
            throw new IllegalArgumentException("documento: Document is required");

        if (app.getMonto() == null || app.getMonto() <= 0L)
            throw new IllegalArgumentException("monto: Amount must be greater than 0");

        if (app.getPlazo() == null || app.getPlazo() < 1 || app.getPlazo() > 120)
            throw new IllegalArgumentException("plazo: Term must be between 1 and 120 months");

        if (type == null)
            throw new IllegalArgumentException("idTipoPrestamo: Loan type not found");

        if (type.getMontoMinimo() == null || type.getMontoMaximo() == null)
            throw new IllegalArgumentException("tipo: Loan type limits not configured");

        if (app.getMonto().compareTo(type.getMontoMinimo()) < 0 ||
                app.getMonto().compareTo(type.getMontoMaximo()) > 0)
            throw new IllegalArgumentException("monto: Amount must be between loan type min and max");
    }
}
