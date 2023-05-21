package ro.luci.jailhubinsights.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.domain.*; // for static metamodels
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.repository.AreaRepository;
import ro.luci.jailhubinsights.service.criteria.AreaCriteria;
import ro.luci.jailhubinsights.service.dto.AreaDTO;
import ro.luci.jailhubinsights.service.mapper.AreaMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Area} entities in the database.
 * The main input is a {@link AreaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AreaDTO} or a {@link Page} of {@link AreaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AreaQueryService extends QueryService<Area> {

    private final Logger log = LoggerFactory.getLogger(AreaQueryService.class);

    private final AreaRepository areaRepository;

    private final AreaMapper areaMapper;

    public AreaQueryService(AreaRepository areaRepository, AreaMapper areaMapper) {
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    /**
     * Return a {@link List} of {@link AreaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AreaDTO> findByCriteria(AreaCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Area> specification = createSpecification(criteria);
        return areaMapper.toDto(areaRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AreaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AreaDTO> findByCriteria(AreaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Area> specification = createSpecification(criteria);
        return areaRepository.findAll(specification, page).map(areaMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AreaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Area> specification = createSpecification(criteria);
        return areaRepository.count(specification);
    }

    /**
     * Function to convert {@link AreaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Area> createSpecification(AreaCriteria criteria) {
        Specification<Area> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Area_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Area_.name));
            }
            if (criteria.getAreaSize() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAreaSize(), Area_.areaSize));
            }
            if (criteria.getAreaType() != null) {
                specification = specification.and(buildSpecification(criteria.getAreaType(), Area_.areaType));
            }
            if (criteria.getPrisonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPrisonId(), root -> root.join(Area_.prison, JoinType.LEFT).get(Prison_.id))
                    );
            }
            if (criteria.getAssignedStaffAreasId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAssignedStaffAreasId(),
                            root -> root.join(Area_.assignedStaffAreas, JoinType.LEFT).get(Staff_.id)
                        )
                    );
            }
            if (criteria.getComposedOfAreasId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getComposedOfAreasId(),
                            root -> root.join(Area_.composedOfAreas, JoinType.LEFT).get(Area_.id)
                        )
                    );
            }
            if (criteria.getInmateId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getInmateId(), root -> root.join(Area_.inmates, JoinType.LEFT).get(Inmate_.id))
                    );
            }
            if (criteria.getComposingAreasId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getComposingAreasId(),
                            root -> root.join(Area_.composingAreas, JoinType.LEFT).get(Area_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
