package ro.luci.jailhubinsights.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import ro.luci.jailhubinsights.domain.enumeration.AreaType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ro.luci.jailhubinsights.domain.Area} entity. This class is used
 * in {@link ro.luci.jailhubinsights.web.rest.AreaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /areas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AreaCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AreaType
     */
    public static class AreaTypeFilter extends Filter<AreaType> {

        public AreaTypeFilter() {}

        public AreaTypeFilter(AreaTypeFilter filter) {
            super(filter);
        }

        @Override
        public AreaTypeFilter copy() {
            return new AreaTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private DoubleFilter areaSize;

    private AreaTypeFilter areaType;

    private LongFilter prisonId;

    private LongFilter assignedStaffAreasId;

    private LongFilter composedOfAreasId;

    private LongFilter inmateId;

    private LongFilter composingAreasId;

    private Boolean distinct;

    public AreaCriteria() {}

    public AreaCriteria(AreaCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.areaSize = other.areaSize == null ? null : other.areaSize.copy();
        this.areaType = other.areaType == null ? null : other.areaType.copy();
        this.prisonId = other.prisonId == null ? null : other.prisonId.copy();
        this.assignedStaffAreasId = other.assignedStaffAreasId == null ? null : other.assignedStaffAreasId.copy();
        this.composedOfAreasId = other.composedOfAreasId == null ? null : other.composedOfAreasId.copy();
        this.inmateId = other.inmateId == null ? null : other.inmateId.copy();
        this.composingAreasId = other.composingAreasId == null ? null : other.composingAreasId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AreaCriteria copy() {
        return new AreaCriteria(this);
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

    public DoubleFilter getAreaSize() {
        return areaSize;
    }

    public DoubleFilter areaSize() {
        if (areaSize == null) {
            areaSize = new DoubleFilter();
        }
        return areaSize;
    }

    public void setAreaSize(DoubleFilter areaSize) {
        this.areaSize = areaSize;
    }

    public AreaTypeFilter getAreaType() {
        return areaType;
    }

    public AreaTypeFilter areaType() {
        if (areaType == null) {
            areaType = new AreaTypeFilter();
        }
        return areaType;
    }

    public void setAreaType(AreaTypeFilter areaType) {
        this.areaType = areaType;
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

    public LongFilter getAssignedStaffAreasId() {
        return assignedStaffAreasId;
    }

    public LongFilter assignedStaffAreasId() {
        if (assignedStaffAreasId == null) {
            assignedStaffAreasId = new LongFilter();
        }
        return assignedStaffAreasId;
    }

    public void setAssignedStaffAreasId(LongFilter assignedStaffAreasId) {
        this.assignedStaffAreasId = assignedStaffAreasId;
    }

    public LongFilter getComposedOfAreasId() {
        return composedOfAreasId;
    }

    public LongFilter composedOfAreasId() {
        if (composedOfAreasId == null) {
            composedOfAreasId = new LongFilter();
        }
        return composedOfAreasId;
    }

    public void setComposedOfAreasId(LongFilter composedOfAreasId) {
        this.composedOfAreasId = composedOfAreasId;
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

    public LongFilter getComposingAreasId() {
        return composingAreasId;
    }

    public LongFilter composingAreasId() {
        if (composingAreasId == null) {
            composingAreasId = new LongFilter();
        }
        return composingAreasId;
    }

    public void setComposingAreasId(LongFilter composingAreasId) {
        this.composingAreasId = composingAreasId;
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
        final AreaCriteria that = (AreaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(areaSize, that.areaSize) &&
            Objects.equals(areaType, that.areaType) &&
            Objects.equals(prisonId, that.prisonId) &&
            Objects.equals(assignedStaffAreasId, that.assignedStaffAreasId) &&
            Objects.equals(composedOfAreasId, that.composedOfAreasId) &&
            Objects.equals(inmateId, that.inmateId) &&
            Objects.equals(composingAreasId, that.composingAreasId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            areaSize,
            areaType,
            prisonId,
            assignedStaffAreasId,
            composedOfAreasId,
            inmateId,
            composingAreasId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AreaCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (areaSize != null ? "areaSize=" + areaSize + ", " : "") +
            (areaType != null ? "areaType=" + areaType + ", " : "") +
            (prisonId != null ? "prisonId=" + prisonId + ", " : "") +
            (assignedStaffAreasId != null ? "assignedStaffAreasId=" + assignedStaffAreasId + ", " : "") +
            (composedOfAreasId != null ? "composedOfAreasId=" + composedOfAreasId + ", " : "") +
            (inmateId != null ? "inmateId=" + inmateId + ", " : "") +
            (composingAreasId != null ? "composingAreasId=" + composingAreasId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
