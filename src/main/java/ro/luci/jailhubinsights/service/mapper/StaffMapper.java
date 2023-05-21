package ro.luci.jailhubinsights.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.service.dto.ActivityDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.dto.StaffDTO;

/**
 * Mapper for the entity {@link Staff} and its DTO {@link StaffDTO}.
 */
@Mapper(componentModel = "spring")
public interface StaffMapper extends EntityMapper<StaffDTO, Staff> {
    @Mapping(target = "prison", source = "prison", qualifiedByName = "prisonId")
    @Mapping(target = "activities", source = "activities", qualifiedByName = "activityIdSet")
    StaffDTO toDto(Staff s);

    @Mapping(target = "removeActivity", ignore = true)
    Staff toEntity(StaffDTO staffDTO);

    @Named("prisonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PrisonDTO toDtoPrisonId(Prison prison);

    @Named("activityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ActivityDTO toDtoActivityId(Activity activity);

    @Named("activityIdSet")
    default Set<ActivityDTO> toDtoActivityIdSet(Set<Activity> activity) {
        return activity.stream().map(this::toDtoActivityId).collect(Collectors.toSet());
    }
}
