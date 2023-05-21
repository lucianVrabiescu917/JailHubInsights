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
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.repository.PrisonRepository;
import ro.luci.jailhubinsights.service.criteria.PrisonCriteria;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.mapper.PrisonMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Prison} entities in the database.
 * The main input is a {@link PrisonCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrisonDTO} or a {@link Page} of {@link PrisonDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrisonQueryService extends QueryService<Prison> {

    private final Logger log = LoggerFactory.getLogger(PrisonQueryService.class);

    private final PrisonRepository prisonRepository;

    private final PrisonMapper prisonMapper;

    public PrisonQueryService(PrisonRepository prisonRepository, PrisonMapper prisonMapper) {
        this.prisonRepository = prisonRepository;
        this.prisonMapper = prisonMapper;
    }

    /**
     * Return a {@link List} of {@link PrisonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrisonDTO> findByCriteria(PrisonCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Prison> specification = createSpecification(criteria);
        return prisonMapper.toDto(prisonRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PrisonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrisonDTO> findByCriteria(PrisonCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Prison> specification = createSpecification(criteria);
        return prisonRepository.findAll(specification, page).map(prisonMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrisonCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Prison> specification = createSpecification(criteria);
        return prisonRepository.count(specification);
    }

    /**
     * Function to convert {@link PrisonCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Prison> createSpecification(PrisonCriteria criteria) {
        Specification<Prison> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Prison_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Prison_.name));
            }
            if (criteria.getLocation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLocation(), Prison_.location));
            }
            if (criteria.getInmateId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getInmateId(), root -> root.join(Prison_.inmates, JoinType.LEFT).get(Inmate_.id))
                    );
            }
            if (criteria.getAreaId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAreaId(), root -> root.join(Prison_.areas, JoinType.LEFT).get(Area_.id))
                    );
            }
            if (criteria.getActivityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getActivityId(), root -> root.join(Prison_.activities, JoinType.LEFT).get(Activity_.id))
                    );
            }
            if (criteria.getStaffId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStaffId(), root -> root.join(Prison_.staff, JoinType.LEFT).get(Staff_.id))
                    );
            }
        }
        return specification;
    }
}
