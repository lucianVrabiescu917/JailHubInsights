package ro.luci.jailhubinsights.service.mapper;

import org.mapstruct.*;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;

/**
 * Mapper for the entity {@link Prison} and its DTO {@link PrisonDTO}.
 */
@Mapper(componentModel = "spring")
public interface PrisonMapper extends EntityMapper<PrisonDTO, Prison> {}
