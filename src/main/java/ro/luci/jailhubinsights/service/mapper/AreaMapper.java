package ro.luci.jailhubinsights.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.service.dto.AreaDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.dto.StaffDTO;

/**
 * Mapper for the entity {@link Area} and its DTO {@link AreaDTO}.
 */
@Mapper(componentModel = "spring")
public interface AreaMapper extends EntityMapper<AreaDTO, Area> {
    @Mapping(target = "prison", source = "prison", qualifiedByName = "prison")
    @Mapping(target = "assignedStaffAreas", source = "assignedStaffAreas", qualifiedByName = "staffSet")
    @Mapping(target = "composedOfAreas", source = "composedOfAreas", qualifiedByName = "areaSet")
    AreaDTO toDto(Area s);

    @Mapping(target = "removeAssignedStaffAreas", ignore = true)
    @Mapping(target = "removeComposedOfAreas", ignore = true)
    Area toEntity(AreaDTO areaDTO);

    @Named("prison")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PrisonDTO toDtoPrison(Prison prison);

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

    @Named("area")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AreaDTO toDtoArea(Area area);

    @Named("areaSet")
    default Set<AreaDTO> toDtoAreaSet(Set<Area> area) {
        return area.stream().map(this::toDtoArea).collect(Collectors.toSet());
    }
}
