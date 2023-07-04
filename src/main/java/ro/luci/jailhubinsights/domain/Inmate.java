package ro.luci.jailhubinsights.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Inmate.
 */
@Entity
@Table(name = "inmate")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Inmate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "image")
    private String image;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_incarceration")
    private LocalDate dateOfIncarceration;

    @Column(name = "date_of_expected_release")
    private LocalDate dateOfExpectedRelease;

    @ManyToOne
    @JsonIgnoreProperties(value = { "inmates", "areas", "activities", "staff" }, allowSetters = true)
    private Prison prison;

    @ManyToMany(mappedBy = "assignedStaffAreas")
    @JsonIgnoreProperties(value = { "prison", "assignedStaffAreas", "composedOfAreas", "inmates", "composingAreas" }, allowSetters = true)
    private Set<Area> assignedAreas = new HashSet<>();

    @ManyToMany(mappedBy = "inmates")
    @JsonIgnoreProperties(value = { "prison", "inmates", "staff" }, allowSetters = true)
    private Set<Activity> activities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Inmate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Inmate firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Inmate lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Inmate dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfIncarceration() {
        return this.dateOfIncarceration;
    }

    public Inmate dateOfIncarceration(LocalDate dateOfIncarceration) {
        this.setDateOfIncarceration(dateOfIncarceration);
        return this;
    }

    public void setDateOfIncarceration(LocalDate dateOfIncarceration) {
        this.dateOfIncarceration = dateOfIncarceration;
    }

    public LocalDate getDateOfExpectedRelease() {
        return this.dateOfExpectedRelease;
    }

    public Inmate dateOfExpectedRelease(LocalDate dateOfExpectedRelease) {
        this.setDateOfExpectedRelease(dateOfExpectedRelease);
        return this;
    }

    public void setDateOfExpectedRelease(LocalDate dateOfExpectedRelease) {
        this.dateOfExpectedRelease = dateOfExpectedRelease;
    }

    public Prison getPrison() {
        return this.prison;
    }

    public void setPrison(Prison prison) {
        this.prison = prison;
    }

    public Inmate prison(Prison prison) {
        this.setPrison(prison);
        return this;
    }

    public Set<Area> getAssignedAreas() {
        return assignedAreas;
    }

    public void setAssignedAreas(Set<Area> areas) {
        if (this.assignedAreas != null) {
            this.assignedAreas.forEach(i -> i.removeInmates(this));
        }
        if (areas != null) {
            areas.forEach(i -> i.addInmates(this));
        }
        this.assignedAreas = areas;
    }

    public Inmate assignedAreas(Set<Area> areas) {
        this.setAssignedAreas(areas);
        return this;
    }

    public Inmate addAssignedAreas(Area area) {
        this.assignedAreas.add(area);
        area.getInmates().add(this);
        return this;
    }

    public Inmate removeAssignedAreas(Area area) {
        this.assignedAreas.remove(area);
        area.getInmates().remove(this);
        return this;
    }

    public Set<Activity> getActivities() {
        return this.activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    public Inmate activities(Set<Activity> activities) {
        this.setActivities(activities);
        return this;
    }

    public Inmate addActivity(Activity activity) {
        this.activities.add(activity);
        activity.getInmates().add(this);
        return this;
    }

    public Inmate removeActivity(Activity activity) {
        this.activities.remove(activity);
        activity.getInmates().remove(this);
        return this;
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
        if (!(o instanceof Inmate)) {
            return false;
        }
        return id != null && id.equals(((Inmate) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inmate{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", dateOfIncarceration='" + getDateOfIncarceration() + "'" +
            ", dateOfExpectedRelease='" + getDateOfExpectedRelease() + "'" +
            "}";
    }
}
