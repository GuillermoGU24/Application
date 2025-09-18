package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.ApplicationListItemResponse;
import co.com.crediya.api.dto.ApplicationRequest;
import co.com.crediya.api.dto.ApplicationResponse;
import co.com.crediya.model.application.Application;

import co.com.crediya.model.application.ApplicationForReview;
import co.com.crediya.model.application.LoanType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    default Application toDomain(ApplicationRequest request) {
        Application app = new Application();
        app.setAmount(request.getAmount());
        app.setTerm(request.getTerm());
        app.setDocument(request.getDocument());

        LoanType loanType = new LoanType();
        loanType.setLoanTypeId(request.getLoanTypeId());
        app.setLoanType(loanType);

        return app;
    }

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "loanType", ignore = true)
    ApplicationResponse toResponse(Application application);

    ApplicationListItemResponse toListItemResponse(ApplicationForReview applicationForReview);
}
