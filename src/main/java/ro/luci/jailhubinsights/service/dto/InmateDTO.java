package ro.luci.jailhubinsights.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link ro.luci.jailhubinsights.domain.Inmate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InmateDTO implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private LocalDate dateOfIncarceration;

    private LocalDate dateOfExpectedRelease;

    private PrisonDTO prison;

    private AreaDTO assignedCell;

    private Set<ActivityDTO> activities = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfIncarceration() {
        return dateOfIncarceration;
    }

    public void setDateOfIncarceration(LocalDate dateOfIncarceration) {
        this.dateOfIncarceration = dateOfIncarceration;
    }

    public LocalDate getDateOfExpectedRelease() {
        return dateOfExpectedRelease;
    }

    public void setDateOfExpectedRelease(LocalDate dateOfExpectedRelease) {
        this.dateOfExpectedRelease = dateOfExpectedRelease;
    }

    public PrisonDTO getPrison() {
        return prison;
    }

    public void setPrison(PrisonDTO prison) {
        this.prison = prison;
    }

    public AreaDTO getAssignedCell() {
        return assignedCell;
    }

    public void setAssignedCell(AreaDTO assignedCell) {
        this.assignedCell = assignedCell;
    }

    public Set<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(Set<ActivityDTO> activities) {
        this.activities = activities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InmateDTO)) {
            return false;
        }

        InmateDTO inmateDTO = (InmateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inmateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InmateDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", dateOfIncarceration='" + getDateOfIncarceration() + "'" +
            ", dateOfExpectedRelease='" + getDateOfExpectedRelease() + "'" +
            ", prison=" + getPrison() +
            ", assignedCell=" + getAssignedCell() +
            ", activities=" + getActivities() +
            "}";
    }
}
