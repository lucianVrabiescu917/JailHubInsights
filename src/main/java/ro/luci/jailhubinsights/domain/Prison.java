package ro.luci.jailhubinsights.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Prison.
 */
@Entity
@Table(name = "prison")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Prison implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "prison")
    @JsonIgnoreProperties(value = { "prison", "assignedCell", "activities" }, allowSetters = true)
    private Set<Inmate> inmates = new HashSet<>();

    @OneToMany(mappedBy = "prison")
    @JsonIgnoreProperties(value = { "prison", "assignedStaffAreas", "composedOfAreas", "inmates", "composingAreas" }, allowSetters = true)
    private Set<Area> areas = new HashSet<>();

    @OneToMany(mappedBy = "prison")
    @JsonIgnoreProperties(value = { "prison", "inmates", "staff" }, allowSetters = true)
    private Set<Activity> activities = new HashSet<>();

    @OneToMany(mappedBy = "prison")
    @JsonIgnoreProperties(value = { "prison", "activities", "assignedAreas" }, allowSetters = true)
    private Set<Staff> staff = new HashSet<>();

    @OneToMany(mappedBy = "prison")
    @JsonIgnoreProperties(value = { "prison", "activities", "assignedAreas" }, allowSetters = true)
    private Set<User> users = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public Prison id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Prison name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public Prison location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<Inmate> getInmates() {
        return this.inmates;
    }

    public void setInmates(Set<Inmate> inmates) {
        if (this.inmates != null) {
            this.inmates.forEach(i -> i.setPrison(null));
        }
        if (inmates != null) {
            inmates.forEach(i -> i.setPrison(this));
        }
        this.inmates = inmates;
    }

    public Prison inmates(Set<Inmate> inmates) {
        this.setInmates(inmates);
        return this;
    }

    public Prison addInmate(Inmate inmate) {
        this.inmates.add(inmate);
        inmate.setPrison(this);
        return this;
    }

    public Prison removeInmate(Inmate inmate) {
        this.inmates.remove(inmate);
        inmate.setPrison(null);
        return this;
    }

    public Set<Area> getAreas() {
        return this.areas;
    }

    public void setAreas(Set<Area> areas) {
        if (this.areas != null) {
            this.areas.forEach(i -> i.setPrison(null));
        }
        if (areas != null) {
            areas.forEach(i -> i.setPrison(this));
        }
        this.areas = areas;
    }

    public Prison areas(Set<Area> areas) {
        this.setAreas(areas);
        return this;
    }

    public Prison addArea(Area area) {
        this.areas.add(area);
        area.setPrison(this);
        return this;
    }

    public Prison removeArea(Area area) {
        this.areas.remove(area);
        area.setPrison(null);
        return this;
    }

    public Set<Activity> getActivities() {
        return this.activities;
    }

    public void setActivities(Set<Activity> activities) {
        if (this.activities != null) {
            this.activities.forEach(i -> i.setPrison(null));
        }
        if (activities != null) {
            activities.forEach(i -> i.setPrison(this));
        }
        this.activities = activities;
    }

    public Prison activities(Set<Activity> activities) {
        this.setActivities(activities);
        return this;
    }

    public Prison addActivity(Activity activity) {
        this.activities.add(activity);
        activity.setPrison(this);
        return this;
    }

    public Prison removeActivity(Activity activity) {
        this.activities.remove(activity);
        activity.setPrison(null);
        return this;
    }

    public Set<Staff> getStaff() {
        return this.staff;
    }

    public void setStaff(Set<Staff> staff) {
        if (this.staff != null) {
            this.staff.forEach(i -> i.setPrison(null));
        }
        if (staff != null) {
            staff.forEach(i -> i.setPrison(this));
        }
        this.staff = staff;
    }

    public Prison staff(Set<Staff> staff) {
        this.setStaff(staff);
        return this;
    }

    public Prison addStaff(Staff staff) {
        this.staff.add(staff);
        staff.setPrison(this);
        return this;
    }

    public Prison removeStaff(Staff staff) {
        this.staff.remove(staff);
        staff.setPrison(null);
        return this;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Prison)) {
            return false;
        }
        return id != null && id.equals(((Prison) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Prison{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", location='" + getLocation() + "'" +
            "}";
    }
}
