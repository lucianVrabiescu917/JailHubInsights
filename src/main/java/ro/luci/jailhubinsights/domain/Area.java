package ro.luci.jailhubinsights.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import ro.luci.jailhubinsights.domain.enumeration.AreaType;

/**
 * A Area.
 */
@Entity
@Table(name = "area")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "area_size")
    private Double areaSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "area_type")
    private AreaType areaType;

    @ManyToOne
    @JsonIgnoreProperties(value = { "inmates", "areas", "activities", "staff" }, allowSetters = true)
    private Prison prison;

    @ManyToMany
    @JoinTable(
        name = "rel_area__assigned_staff_areas",
        joinColumns = @JoinColumn(name = "area_id"),
        inverseJoinColumns = @JoinColumn(name = "assigned_staff_areas_id")
    )
    @JsonIgnoreProperties(value = { "prison", "activities", "assignedAreas" }, allowSetters = true)
    private Set<Staff> assignedStaffAreas = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_area__composed_of_areas",
        joinColumns = @JoinColumn(name = "area_id"),
        inverseJoinColumns = @JoinColumn(name = "composed_of_areas_id")
    )
    @JsonIgnoreProperties(value = { "prison", "assignedStaffAreas", "composedOfAreas", "inmates", "composingAreas" }, allowSetters = true)
    private Set<Area> composedOfAreas = new HashSet<>();

    @OneToMany(mappedBy = "assignedCell")
    @JsonIgnoreProperties(value = { "prison", "assignedCell", "activities" }, allowSetters = true)
    private Set<Inmate> inmates = new HashSet<>();

    @ManyToMany(mappedBy = "composedOfAreas")
    @JsonIgnoreProperties(value = { "prison", "assignedStaffAreas", "composedOfAreas", "inmates", "composingAreas" }, allowSetters = true)
    private Set<Area> composingAreas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Area id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Area name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAreaSize() {
        return this.areaSize;
    }

    public Area areaSize(Double areaSize) {
        this.setAreaSize(areaSize);
        return this;
    }

    public void setAreaSize(Double areaSize) {
        this.areaSize = areaSize;
    }

    public AreaType getAreaType() {
        return this.areaType;
    }

    public Area areaType(AreaType areaType) {
        this.setAreaType(areaType);
        return this;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    public Prison getPrison() {
        return this.prison;
    }

    public void setPrison(Prison prison) {
        this.prison = prison;
    }

    public Area prison(Prison prison) {
        this.setPrison(prison);
        return this;
    }

    public Set<Staff> getAssignedStaffAreas() {
        return this.assignedStaffAreas;
    }

    public void setAssignedStaffAreas(Set<Staff> staff) {
        this.assignedStaffAreas = staff;
    }

    public Area assignedStaffAreas(Set<Staff> staff) {
        this.setAssignedStaffAreas(staff);
        return this;
    }

    public Area addAssignedStaffAreas(Staff staff) {
        this.assignedStaffAreas.add(staff);
        staff.getAssignedAreas().add(this);
        return this;
    }

    public Area removeAssignedStaffAreas(Staff staff) {
        this.assignedStaffAreas.remove(staff);
        staff.getAssignedAreas().remove(this);
        return this;
    }

    public Set<Area> getComposedOfAreas() {
        return this.composedOfAreas;
    }

    public void setComposedOfAreas(Set<Area> areas) {
        this.composedOfAreas = areas;
    }

    public Area composedOfAreas(Set<Area> areas) {
        this.setComposedOfAreas(areas);
        return this;
    }

    public Area addComposedOfAreas(Area area) {
        this.composedOfAreas.add(area);
        area.getComposingAreas().add(this);
        return this;
    }

    public Area removeComposedOfAreas(Area area) {
        this.composedOfAreas.remove(area);
        area.getComposingAreas().remove(this);
        return this;
    }

    public Set<Inmate> getInmates() {
        return this.inmates;
    }

    public void setInmates(Set<Inmate> inmates) {
        if (this.inmates != null) {
            this.inmates.forEach(i -> i.setAssignedCell(null));
        }
        if (inmates != null) {
            inmates.forEach(i -> i.setAssignedCell(this));
        }
        this.inmates = inmates;
    }

    public Area inmates(Set<Inmate> inmates) {
        this.setInmates(inmates);
        return this;
    }

    public Area addInmate(Inmate inmate) {
        this.inmates.add(inmate);
        inmate.setAssignedCell(this);
        return this;
    }

    public Area removeInmate(Inmate inmate) {
        this.inmates.remove(inmate);
        inmate.setAssignedCell(null);
        return this;
    }

    public Set<Area> getComposingAreas() {
        return this.composingAreas;
    }

    public void setComposingAreas(Set<Area> areas) {
        if (this.composingAreas != null) {
            this.composingAreas.forEach(i -> i.removeComposedOfAreas(this));
        }
        if (areas != null) {
            areas.forEach(i -> i.addComposedOfAreas(this));
        }
        this.composingAreas = areas;
    }

    public Area composingAreas(Set<Area> areas) {
        this.setComposingAreas(areas);
        return this;
    }

    public Area addComposingAreas(Area area) {
        this.composingAreas.add(area);
        area.getComposedOfAreas().add(this);
        return this;
    }

    public Area removeComposingAreas(Area area) {
        this.composingAreas.remove(area);
        area.getComposedOfAreas().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Area)) {
            return false;
        }
        return id != null && id.equals(((Area) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Area{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", areaSize=" + getAreaSize() +
            ", areaType='" + getAreaType() + "'" +
            "}";
    }
}
