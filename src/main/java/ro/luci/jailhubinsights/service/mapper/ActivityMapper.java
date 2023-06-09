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
 * Mapper for the entity {@link Activity} and its DTO {@link ActivityDTO}.
 */
@Mapper(componentModel = "spring")
public interface ActivityMapper extends EntityMapper<ActivityDTO, Activity> {
    @Mapping(target = "prison", source = "prison", qualifiedByName = "prisonDto")
    @Mapping(target = "staff", source = "staff", qualifiedByName = "staffSet")
    @Mapping(target = "inmates", source = "inmates", qualifiedByName = "inmatesSet")
    ActivityDTO toDto(Activity s);

    @Named("prisonDto")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PrisonDTO toDtoPrisonId(Prison prison);

    @Named("staff")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    StaffDTO toDtoStaff(Staff staff);

    @Named("staffSet")
    default Set<StaffDTO> toDtoStaffSet(Set<Staff> staff) {
        return staff.stream().map(this::toDtoStaff).collect(Collectors.toSet());
    }

    @Named("inmates")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    InmateDTO toDtoInmate(Inmate staff);

    @Named("inmatesSet")
    default Set<InmateDTO> toDtoInmateSet(Set<Inmate> inmates) {
        return inmates.stream().map(this::toDtoInmate).collect(Collectors.toSet());
    }
}
