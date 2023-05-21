package ro.luci.jailhubinsights.service.mapper;

import org.mapstruct.*;
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.service.dto.ActivityDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;

/**
 * Mapper for the entity {@link Activity} and its DTO {@link ActivityDTO}.
 */
@Mapper(componentModel = "spring")
public interface ActivityMapper extends EntityMapper<ActivityDTO, Activity> {
    @Mapping(target = "prison", source = "prison", qualifiedByName = "prisonId")
    ActivityDTO toDto(Activity s);

    @Named("prisonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PrisonDTO toDtoPrisonId(Prison prison);
}
