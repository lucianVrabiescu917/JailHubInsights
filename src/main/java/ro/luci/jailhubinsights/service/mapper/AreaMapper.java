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
    @Mapping(target = "prison", source = "prison", qualifiedByName = "prisonId")
    @Mapping(target = "assignedStaffAreas", source = "assignedStaffAreas", qualifiedByName = "staffIdSet")
    @Mapping(target = "composedOfAreas", source = "composedOfAreas", qualifiedByName = "areaIdSet")
    AreaDTO toDto(Area s);

    @Mapping(target = "removeAssignedStaffAreas", ignore = true)
    @Mapping(target = "removeComposedOfAreas", ignore = true)
    Area toEntity(AreaDTO areaDTO);

    @Named("prisonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PrisonDTO toDtoPrisonId(Prison prison);

    @Named("staffId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StaffDTO toDtoStaffId(Staff staff);

    @Named("staffIdSet")
    default Set<StaffDTO> toDtoStaffIdSet(Set<Staff> staff) {
        return staff.stream().map(this::toDtoStaffId).collect(Collectors.toSet());
    }

    @Named("areaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AreaDTO toDtoAreaId(Area area);

    @Named("areaIdSet")
    default Set<AreaDTO> toDtoAreaIdSet(Set<Area> area) {
        return area.stream().map(this::toDtoAreaId).collect(Collectors.toSet());
    }
}
