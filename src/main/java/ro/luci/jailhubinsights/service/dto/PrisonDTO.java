package ro.luci.jailhubinsights.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;

/**
 * A DTO for the {@link ro.luci.jailhubinsights.domain.Prison} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PrisonDTO implements Serializable {

    private Long id;

    private String name;

    private String location;

    private String image;

    private Double cellRatio;

    private Double cellBlockRatio;

    private Double laborRatio;

    private Double recreationRatio;

    private Double classRatio;

    private Double diningRatio;

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

    public Double getCellRatio() {
        return cellRatio;
    }

    public void setCellRatio(Double cellRatio) {
        this.cellRatio = cellRatio;
    }

    public Double getCellBlockRatio() {
        return cellBlockRatio;
    }

    public void setCellBlockRatio(Double cellBlockRatio) {
        this.cellBlockRatio = cellBlockRatio;
    }

    public Double getLaborRatio() {
        return laborRatio;
    }

    public void setLaborRatio(Double laborRatio) {
        this.laborRatio = laborRatio;
    }

    public Double getRecreationRatio() {
        return recreationRatio;
    }

    public void setRecreationRatio(Double recreationRatio) {
        this.recreationRatio = recreationRatio;
    }

    public Double getClassRatio() {
        return classRatio;
    }

    public void setClassRatio(Double classRatio) {
        this.classRatio = classRatio;
    }

    public Double getDiningRatio() {
        return diningRatio;
    }

    public void setDiningRatio(Double diningRatio) {
        this.diningRatio = diningRatio;
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
