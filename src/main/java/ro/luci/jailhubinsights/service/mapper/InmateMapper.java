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
    @Mapping(target = "prison", source = "prison", qualifiedByName = "prison")
    @Mapping(target = "assignedCell", source = "assignedCell", qualifiedByName = "area")
    @Mapping(target = "activities", source = "activities", qualifiedByName = "activitySet")
    InmateDTO toDto(Inmate s);

    @Mapping(target = "removeActivity", ignore = true)
    Inmate toEntity(InmateDTO inmateDTO);

    @Named("prison")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PrisonDTO toDtoPrison(Prison prison);

    @Named("area")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AreaDTO toDtoArea(Area area);

    @Named("activity")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ActivityDTO toDtoActivity(Activity activity);

    @Named("activitySet")
    default Set<ActivityDTO> toDtoActivityIdSet(Set<Activity> activity) {
        return activity.stream().map(this::toDtoActivity).collect(Collectors.toSet());
    }
}
