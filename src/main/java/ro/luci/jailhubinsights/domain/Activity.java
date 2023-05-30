package ro.luci.jailhubinsights.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import ro.luci.jailhubinsights.domain.enumeration.ActivityType;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ActivityType type;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnoreProperties(value = { "inmates", "areas", "activities", "staff" }, allowSetters = true)
    private Prison prison;

    @ManyToMany
    @JoinTable(
        name = "rel_inmate__activity",
        joinColumns = @JoinColumn(name = "inmate_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    @JsonIgnoreProperties(value = { "prison", "assignedCell", "activities" }, allowSetters = true)
    private Set<Inmate> inmates = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_staff__activity",
        joinColumns = @JoinColumn(name = "staff_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    @JsonIgnoreProperties(value = { "prison", "activities", "assignedAreas" }, allowSetters = true)
    private Set<Staff> staff = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Activity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityType getType() {
        return this.type;
    }

    public Activity type(ActivityType type) {
        this.setType(type);
        return this;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public Activity title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Activity description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Prison getPrison() {
        return this.prison;
    }

    public void setPrison(Prison prison) {
        this.prison = prison;
    }

    public Activity prison(Prison prison) {
        this.setPrison(prison);
        return this;
    }

    public Set<Inmate> getInmates() {
        return this.inmates;
    }

    public void setInmates(Set<Inmate> inmates) {
        if (this.inmates != null) {
            this.inmates.forEach(i -> i.removeActivity(this));
        }
        if (inmates != null) {
            inmates.forEach(i -> i.addActivity(this));
        }
        this.inmates = inmates;
    }

    public Activity inmates(Set<Inmate> inmates) {
        this.setInmates(inmates);
        return this;
    }

    public Activity addInmate(Inmate inmate) {
        this.inmates.add(inmate);
        inmate.getActivities().add(this);
        return this;
    }

    public Activity removeInmate(Inmate inmate) {
        this.inmates.remove(inmate);
        inmate.getActivities().remove(this);
        return this;
    }

    public Set<Staff> getStaff() {
        return this.staff;
    }

    public void setStaff(Set<Staff> staff) {
        if (this.staff != null) {
            this.staff.forEach(i -> i.removeActivity(this));
        }
        if (staff != null) {
            staff.forEach(i -> i.addActivity(this));
        }
        this.staff = staff;
    }

    public Activity staff(Set<Staff> staff) {
        this.setStaff(staff);
        return this;
    }

    public Activity addStaff(Staff staff) {
        this.staff.add(staff);
        staff.getActivities().add(this);
        return this;
    }

    public Activity removeStaff(Staff staff) {
        this.staff.remove(staff);
        staff.getActivities().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Activity)) {
            return false;
        }
        return id != null && id.equals(((Activity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
