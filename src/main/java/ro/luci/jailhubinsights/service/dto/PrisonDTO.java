package ro.luci.jailhubinsights.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ro.luci.jailhubinsights.domain.Prison} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PrisonDTO implements Serializable {

    private Long id;

    private String name;

    private String location;

    private String image;

    public PrisonDTO(Long id, String name, String location, String image) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.image = image;
    }

    public PrisonDTO() {}

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrisonDTO)) {
            return false;
        }

        PrisonDTO prisonDTO = (PrisonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, prisonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrisonDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", location='" + getLocation() + "'" +
            "}";
    }
}
