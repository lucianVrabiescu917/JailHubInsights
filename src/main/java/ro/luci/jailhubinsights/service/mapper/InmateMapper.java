package ro.luci.jailhubinsights.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.service.dto.ActivityDTO;
import ro.luci.jailhubinsights.service.dto.AreaDTO;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;

/**
 * Mapper for the entity {@link Inmate} and its DTO {@link InmateDTO}.
 */
@Mapper(componentModel = "spring")
public interface InmateMapper extends EntityMapper<InmateDTO, Inmate> {
    @Mapping(target = "prison", source = "prison", qualifiedByName = "prisonId")
    @Mapping(target = "assignedCell", source = "assignedCell", qualifiedByName = "areaId")
    @Mapping(target = "activities", source = "activities", qualifiedByName = "activityIdSet")
    InmateDTO toDto(Inmate s);

    @Mapping(target = "removeActivity", ignore = true)
    Inmate toEntity(InmateDTO inmateDTO);

    @Named("prisonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PrisonDTO toDtoPrisonId(Prison prison);

    @Named("areaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AreaDTO toDtoAreaId(Area area);

    @Named("activityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ActivityDTO toDtoActivityId(Activity activity);

    @Named("activityIdSet")
    default Set<ActivityDTO> toDtoActivityIdSet(Set<Activity> activity) {
        return activity.stream().map(this::toDtoActivityId).collect(Collectors.toSet());
    }
}
