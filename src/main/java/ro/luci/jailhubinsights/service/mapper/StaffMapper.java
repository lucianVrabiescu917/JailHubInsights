package ro.luci.jailhubinsights.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.service.dto.ActivityDTO;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.dto.StaffDTO;

/**
 * Mapper for the entity {@link Staff} and its DTO {@link StaffDTO}.
 */
@Mapper(componentModel = "spring")
public interface StaffMapper extends EntityMapper<StaffDTO, Staff> {
    @Mapping(target = "prison", source = "prison", qualifiedByName = "prison")
    @Mapping(target = "activities", source = "activities", qualifiedByName = "activitiesSet")
    StaffDTO toDto(Staff s);

    @Mapping(target = "removeActivity", ignore = true)
    Staff toEntity(StaffDTO staffDTO);

    @Named("prison")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PrisonDTO toDtoPrison(Prison prison);

    @Named("activities")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ActivityDTO toDtoInmate(Activity activity);

    @Named("activitiesSet")
    default Set<ActivityDTO> toDtoActivitySet(Set<Activity> activities) {
        return activities.stream().map(this::toDtoInmate).collect(Collectors.toSet());
    }
}
