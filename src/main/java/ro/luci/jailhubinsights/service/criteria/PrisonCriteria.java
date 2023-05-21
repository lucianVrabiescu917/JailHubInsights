package ro.luci.jailhubinsights.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ro.luci.jailhubinsights.domain.Prison} entity. This class is used
 * in {@link ro.luci.jailhubinsights.web.rest.PrisonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /prisons?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PrisonCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter location;

    private LongFilter inmateId;

    private LongFilter areaId;

    private LongFilter activityId;

    private LongFilter staffId;

    private Boolean distinct;

    public PrisonCriteria() {}

    public PrisonCriteria(PrisonCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.location = other.location == null ? null : other.location.copy();
        this.inmateId = other.inmateId == null ? null : other.inmateId.copy();
        this.areaId = other.areaId == null ? null : other.areaId.copy();
        this.activityId = other.activityId == null ? null : other.activityId.copy();
        this.staffId = other.staffId == null ? null : other.staffId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PrisonCriteria copy() {
        return new PrisonCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getLocation() {
        return location;
    }

    public StringFilter location() {
        if (location == null) {
            location = new StringFilter();
        }
        return location;
    }

    public void setLocation(StringFilter location) {
        this.location = location;
    }

    public LongFilter getInmateId() {
        return inmateId;
    }

    public LongFilter inmateId() {
        if (inmateId == null) {
            inmateId = new LongFilter();
        }
        return inmateId;
    }

    public void setInmateId(LongFilter inmateId) {
        this.inmateId = inmateId;
    }

    public LongFilter getAreaId() {
        return areaId;
    }

    public LongFilter areaId() {
        if (areaId == null) {
            areaId = new LongFilter();
        }
        return areaId;
    }

    public void setAreaId(LongFilter areaId) {
        this.areaId = areaId;
    }

    public LongFilter getActivityId() {
        return activityId;
    }

    public LongFilter activityId() {
        if (activityId == null) {
            activityId = new LongFilter();
        }
        return activityId;
    }

    public void setActivityId(LongFilter activityId) {
        this.activityId = activityId;
    }

    public LongFilter getStaffId() {
        return staffId;
    }

    public LongFilter staffId() {
        if (staffId == null) {
            staffId = new LongFilter();
        }
        return staffId;
    }

    public void setStaffId(LongFilter staffId) {
        this.staffId = staffId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PrisonCriteria that = (PrisonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(location, that.location) &&
            Objects.equals(inmateId, that.inmateId) &&
            Objects.equals(areaId, that.areaId) &&
            Objects.equals(activityId, that.activityId) &&
            Objects.equals(staffId, that.staffId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, inmateId, areaId, activityId, staffId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PrisonCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (location != null ? "location=" + location + ", " : "") +
            (inmateId != null ? "inmateId=" + inmateId + ", " : "") +
            (areaId != null ? "areaId=" + areaId + ", " : "") +
            (activityId != null ? "activityId=" + activityId + ", " : "") +
            (staffId != null ? "staffId=" + staffId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
