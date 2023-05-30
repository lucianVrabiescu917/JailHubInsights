package ro.luci.jailhubinsights.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.domain.enumeration.ActivityType;

/**
 * A DTO for the {@link ro.luci.jailhubinsights.domain.Activity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ActivityDTO implements Serializable {

    private Long id;

    private ActivityType type;

    private String title;

    private String description;

    private PrisonDTO prison;

    private Set<InmateDTO> inmates;

    private Set<StaffDTO> staff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PrisonDTO getPrison() {
        return prison;
    }

    public void setPrison(PrisonDTO prison) {
        this.prison = prison;
    }

    public Set<InmateDTO> getInmates() {
        return inmates;
    }

    public void setInmates(Set<InmateDTO> inmates) {
        this.inmates = inmates;
    }

    public Set<StaffDTO> getStaff() {
        return staff;
    }

    public void setStaff(Set<StaffDTO> staff) {
        this.staff = staff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivityDTO)) {
            return false;
        }

        ActivityDTO activityDTO = (ActivityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, activityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActivityDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", prison=" + getPrison() +
            "}";
    }
}
