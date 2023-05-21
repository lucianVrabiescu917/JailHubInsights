package ro.luci.jailhubinsights.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import ro.luci.jailhubinsights.domain.enumeration.StaffType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ro.luci.jailhubinsights.domain.Staff} entity. This class is used
 * in {@link ro.luci.jailhubinsights.web.rest.StaffResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /staff?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StaffCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StaffType
     */
    public static class StaffTypeFilter extends Filter<StaffType> {

        public StaffTypeFilter() {}

        public StaffTypeFilter(StaffTypeFilter filter) {
            super(filter);
        }

        @Override
        public StaffTypeFilter copy() {
            return new StaffTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StaffTypeFilter staffType;

    private StringFilter firstName;

    private StringFilter lastName;

    private LongFilter prisonId;

    private LongFilter activityId;

    private LongFilter assignedAreasId;

    private Boolean distinct;

    public StaffCriteria() {}

    public StaffCriteria(StaffCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.staffType = other.staffType == null ? null : other.staffType.copy();
        this.firstName = other.firstName == null ? null : other.firstName.copy();
        this.lastName = other.lastName == null ? null : other.lastName.copy();
        this.prisonId = other.prisonId == null ? null : other.prisonId.copy();
        this.activityId = other.activityId == null ? null : other.activityId.copy();
        this.assignedAreasId = other.assignedAreasId == null ? null : other.assignedAreasId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StaffCriteria copy() {
        return new StaffCriteria(this);
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

    public StaffTypeFilter getStaffType() {
        return staffType;
    }

    public StaffTypeFilter staffType() {
        if (staffType == null) {
            staffType = new StaffTypeFilter();
        }
        return staffType;
    }

    public void setStaffType(StaffTypeFilter staffType) {
        this.staffType = staffType;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public StringFilter firstName() {
        if (firstName == null) {
            firstName = new StringFilter();
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public StringFilter lastName() {
        if (lastName == null) {
            lastName = new StringFilter();
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public LongFilter getPrisonId() {
        return prisonId;
    }

    public LongFilter prisonId() {
        if (prisonId == null) {
            prisonId = new LongFilter();
        }
        return prisonId;
    }

    public void setPrisonId(LongFilter prisonId) {
        this.prisonId = prisonId;
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

    public LongFilter getAssignedAreasId() {
        return assignedAreasId;
    }

    public LongFilter assignedAreasId() {
        if (assignedAreasId == null) {
            assignedAreasId = new LongFilter();
        }
        return assignedAreasId;
    }

    public void setAssignedAreasId(LongFilter assignedAreasId) {
        this.assignedAreasId = assignedAreasId;
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
        final StaffCriteria that = (StaffCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(staffType, that.staffType) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(prisonId, that.prisonId) &&
            Objects.equals(activityId, that.activityId) &&
            Objects.equals(assignedAreasId, that.assignedAreasId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, staffType, firstName, lastName, prisonId, activityId, assignedAreasId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StaffCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (staffType != null ? "staffType=" + staffType + ", " : "") +
            (firstName != null ? "firstName=" + firstName + ", " : "") +
            (lastName != null ? "lastName=" + lastName + ", " : "") +
            (prisonId != null ? "prisonId=" + prisonId + ", " : "") +
            (activityId != null ? "activityId=" + activityId + ", " : "") +
            (assignedAreasId != null ? "assignedAreasId=" + assignedAreasId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
