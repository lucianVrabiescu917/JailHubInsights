package ro.luci.jailhubinsights.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.enumeration.AreaType;

/**
 * A DTO for the {@link ro.luci.jailhubinsights.domain.Area} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AreaDTO implements Serializable {

    private Long id;

    private String name;

    private Double areaSize;

    private AreaType areaType;

    private PrisonDTO prison;

    private Set<StaffDTO> assignedStaffAreas = new HashSet<>();

    private Set<InmateDTO> inmates = new HashSet<>();

    private Set<AreaDTO> composedOfAreas = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(Double areaSize) {
        this.areaSize = areaSize;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    public PrisonDTO getPrison() {
        return prison;
    }

    public void setPrison(PrisonDTO prison) {
        this.prison = prison;
    }

    public Set<StaffDTO> getAssignedStaffAreas() {
        return assignedStaffAreas;
    }

    public void setAssignedStaffAreas(Set<StaffDTO> assignedStaffAreas) {
        this.assignedStaffAreas = assignedStaffAreas;
    }

    public Set<AreaDTO> getComposedOfAreas() {
        return composedOfAreas;
    }

    public void setComposedOfAreas(Set<AreaDTO> composedOfAreas) {
        this.composedOfAreas = composedOfAreas;
    }

    public Set<InmateDTO> getInmates() {
        return inmates;
    }

    public void setInmates(Set<InmateDTO> inmates) {
        this.inmates = inmates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AreaDTO)) {
            return false;
        }

        AreaDTO areaDTO = (AreaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, areaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AreaDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", areaSize=" + getAreaSize() +
            ", areaType='" + getAreaType() + "'" +
            ", prison=" + getPrison() +
            ", assignedStaffAreas=" + getAssignedStaffAreas() +
            ", composedOfAreas=" + getComposedOfAreas() +
            "}";
    }
}
