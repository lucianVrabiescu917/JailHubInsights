package ro.luci.jailhubinsights.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ro.luci.jailhubinsights.domain.Inmate} entity. This class is used
 * in {@link ro.luci.jailhubinsights.web.rest.InmateResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /inmates?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InmateCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter firstName;

    private StringFilter lastName;

    private LocalDateFilter dateOfBirth;

    private LocalDateFilter dateOfIncarceration;

    private LocalDateFilter dateOfExpectedRelease;

    private LongFilter prisonId;

    private LongFilter activityId;

    private LongFilter assignedAreasId;

    private Boolean distinct;

    public InmateCriteria() {}

    public InmateCriteria(InmateCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.firstName = other.firstName == null ? null : other.firstName.copy();
        this.lastName = other.lastName == null ? null : other.lastName.copy();
        this.dateOfBirth = other.dateOfBirth == null ? null : other.dateOfBirth.copy();
        this.dateOfIncarceration = other.dateOfIncarceration == null ? null : other.dateOfIncarceration.copy();
        this.dateOfExpectedRelease = other.dateOfExpectedRelease == null ? null : other.dateOfExpectedRelease.copy();
        this.prisonId = other.prisonId == null ? null : other.prisonId.copy();
        this.assignedAreasId = other.assignedAreasId == null ? null : other.assignedAreasId.copy();
        this.activityId = other.activityId == null ? null : other.activityId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public InmateCriteria copy() {
        return new InmateCriteria(this);
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

    public LocalDateFilter getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDateFilter dateOfBirth() {
        if (dateOfBirth == null) {
            dateOfBirth = new LocalDateFilter();
        }
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateFilter dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDateFilter getDateOfIncarceration() {
        return dateOfIncarceration;
    }

    public LocalDateFilter dateOfIncarceration() {
        if (dateOfIncarceration == null) {
            dateOfIncarceration = new LocalDateFilter();
        }
        return dateOfIncarceration;
    }

    public void setDateOfIncarceration(LocalDateFilter dateOfIncarceration) {
        this.dateOfIncarceration = dateOfIncarceration;
    }

    public LocalDateFilter getDateOfExpectedRelease() {
        return dateOfExpectedRelease;
    }

    public LocalDateFilter dateOfExpectedRelease() {
        if (dateOfExpectedRelease == null) {
            dateOfExpectedRelease = new LocalDateFilter();
        }
        return dateOfExpectedRelease;
    }

    public void setDateOfExpectedRelease(LocalDateFilter dateOfExpectedRelease) {
        this.dateOfExpectedRelease = dateOfExpectedRelease;
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
        final InmateCriteria that = (InmateCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(dateOfBirth, that.dateOfBirth) &&
            Objects.equals(dateOfIncarceration, that.dateOfIncarceration) &&
            Objects.equals(dateOfExpectedRelease, that.dateOfExpectedRelease) &&
            Objects.equals(prisonId, that.prisonId) &&
            Objects.equals(assignedAreasId, that.assignedAreasId) &&
            Objects.equals(activityId, that.activityId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            firstName,
            lastName,
            dateOfBirth,
            dateOfIncarceration,
            dateOfExpectedRelease,
            prisonId,
            assignedAreasId,
            activityId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InmateCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (firstName != null ? "firstName=" + firstName + ", " : "") +
            (lastName != null ? "lastName=" + lastName + ", " : "") +
            (dateOfBirth != null ? "dateOfBirth=" + dateOfBirth + ", " : "") +
            (dateOfIncarceration != null ? "dateOfIncarceration=" + dateOfIncarceration + ", " : "") +
            (dateOfExpectedRelease != null ? "dateOfExpectedRelease=" + dateOfExpectedRelease + ", " : "") +
            (prisonId != null ? "prisonId=" + prisonId + ", " : "") +
            (assignedAreasId != null ? "assignedAreasId=" + assignedAreasId + ", " : "") +
            (activityId != null ? "activityId=" + activityId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
