package ro.luci.jailhubinsights.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import ro.luci.jailhubinsights.domain.enumeration.ActivityType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ro.luci.jailhubinsights.domain.Activity} entity. This class is used
 * in {@link ro.luci.jailhubinsights.web.rest.ActivityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /activities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ActivityCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ActivityType
     */
    public static class ActivityTypeFilter extends Filter<ActivityType> {

        public ActivityTypeFilter() {}

        public ActivityTypeFilter(ActivityTypeFilter filter) {
            super(filter);
        }

        @Override
        public ActivityTypeFilter copy() {
            return new ActivityTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ActivityTypeFilter type;

    private StringFilter title;

    private StringFilter description;

    private LongFilter prisonId;

    private LongFilter inmateId;

    private LongFilter staffId;

    private Boolean distinct;

    public ActivityCriteria() {}

    public ActivityCriteria(ActivityCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.prisonId = other.prisonId == null ? null : other.prisonId.copy();
        this.inmateId = other.inmateId == null ? null : other.inmateId.copy();
        this.staffId = other.staffId == null ? null : other.staffId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ActivityCriteria copy() {
        return new ActivityCriteria(this);
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

    public ActivityTypeFilter getType() {
        return type;
    }

    public ActivityTypeFilter type() {
        if (type == null) {
            type = new ActivityTypeFilter();
        }
        return type;
    }

    public void setType(ActivityTypeFilter type) {
        this.type = type;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
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
        final ActivityCriteria that = (ActivityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(prisonId, that.prisonId) &&
            Objects.equals(inmateId, that.inmateId) &&
            Objects.equals(staffId, that.staffId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, title, description, prisonId, inmateId, staffId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActivityCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (prisonId != null ? "prisonId=" + prisonId + ", " : "") +
            (inmateId != null ? "inmateId=" + inmateId + ", " : "") +
            (staffId != null ? "staffId=" + staffId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
