package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.SolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.model.application.Application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    @Mapping(target = "idSolicitud", ignore = true)
    @Mapping(target = "idEstado", ignore = true)
        // se asigna en usecase
    Application toDomain(SolicitudRequest request);

    SolicitudResponse toResponse(Application solicitud);
}
