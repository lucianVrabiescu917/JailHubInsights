package ro.luci.jailhubinsights.service.dto;

import java.io.Serializable;
import java.util.Objects;
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
