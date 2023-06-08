package ro.luci.jailhubinsights.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import ro.luci.jailhubinsights.domain.enumeration.StaffType;

/**
 * A Staff.
 */
@Entity
@Table(name = "staff")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type")
    private StaffType staffType;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "image")
    private String image;

    @ManyToOne
    @JsonIgnoreProperties(value = { "inmates", "areas", "activities", "staff" }, allowSetters = true)
    private Prison prison;

    @ManyToMany(mappedBy = "staff")
    @JsonIgnoreProperties(value = { "prison", "inmates", "staff" }, allowSetters = true)
    private Set<Activity> activities = new HashSet<>();

    @ManyToMany(mappedBy = "assignedStaffAreas")
    @JsonIgnoreProperties(value = { "prison", "assignedStaffAreas", "composedOfAreas", "inmates", "composingAreas" }, allowSetters = true)
    private Set<Area> assignedAreas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Staff id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StaffType getStaffType() {
        return this.staffType;
    }

    public Staff staffType(StaffType staffType) {
        this.setStaffType(staffType);
        return this;
    }

    public void setStaffType(StaffType staffType) {
        this.staffType = staffType;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Staff firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Staff lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Prison getPrison() {
        return this.prison;
    }

    public void setPrison(Prison prison) {
        this.prison = prison;
    }

    public Staff prison(Prison prison) {
        this.setPrison(prison);
        return this;
    }

    public Set<Activity> getActivities() {
        return this.activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    public Staff activities(Set<Activity> activities) {
        this.setActivities(activities);
        return this;
    }

    public Staff addActivity(Activity activity) {
        this.activities.add(activity);
        activity.getStaff().add(this);
        return this;
    }

    public Staff removeActivity(Activity activity) {
        this.activities.remove(activity);
        activity.getStaff().remove(this);
        return this;
    }

    public Set<Area> getAssignedAreas() {
        return this.assignedAreas;
    }

    public void setAssignedAreas(Set<Area> areas) {
        if (this.assignedAreas != null) {
            this.assignedAreas.forEach(i -> i.removeAssignedStaffAreas(this));
        }
        if (areas != null) {
            areas.forEach(i -> i.addAssignedStaffAreas(this));
        }
        this.assignedAreas = areas;
    }

    public Staff assignedAreas(Set<Area> areas) {
        this.setAssignedAreas(areas);
        return this;
    }

    public Staff addAssignedAreas(Area area) {
        this.assignedAreas.add(area);
        area.getAssignedStaffAreas().add(this);
        return this;
    }

    public Staff removeAssignedAreas(Area area) {
        this.assignedAreas.remove(area);
        area.getAssignedStaffAreas().remove(this);
        return this;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Staff)) {
            return false;
        }
        return id != null && id.equals(((Staff) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Staff{" +
            "id=" + getId() +
            ", staffType='" + getStaffType() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            "}";
    }
}
