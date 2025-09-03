package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.ApplicationRequest;
import co.com.crediya.api.dto.ApplicationResponse;
import co.com.crediya.model.application.Application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "applicationId", ignore = true) // before: idSolicitud
    @Mapping(target = "stateId", ignore = true)
    Application toDomain(ApplicationRequest request);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "loanType", ignore = true)
    ApplicationResponse toResponse(Application application);
}
