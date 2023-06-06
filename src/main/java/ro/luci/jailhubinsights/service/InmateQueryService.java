package ro.luci.jailhubinsights.service;

import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.domain.*; // for static metamodels
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.repository.InmateRepository;
import ro.luci.jailhubinsights.repository.UserRepository;
import ro.luci.jailhubinsights.security.SecurityUtils;
import ro.luci.jailhubinsights.service.criteria.InmateCriteria;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.service.mapper.InmateMapper;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link Inmate} entities in the database.
 * The main input is a {@link InmateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InmateDTO} or a {@link Page} of {@link InmateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InmateQueryService extends QueryService<Inmate> {

    private final Logger log = LoggerFactory.getLogger(InmateQueryService.class);

    private final InmateRepository inmateRepository;

    private final InmateMapper inmateMapper;
    private final UserRepository userRepository;

    public InmateQueryService(InmateRepository inmateRepository, InmateMapper inmateMapper, UserRepository userRepository) {
        this.inmateRepository = inmateRepository;
        this.inmateMapper = inmateMapper;
        this.userRepository = userRepository;
    }

    /**
     * Return a {@link List} of {@link InmateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InmateDTO> findByCriteria(InmateCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Inmate> specification = createSpecification(criteria);
        return inmateMapper.toDto(inmateRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link InmateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InmateDTO> findByCriteria(InmateCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Inmate> specification = createSpecification(criteria);
        return inmateRepository.findAll(specification, page).map(inmateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InmateCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Inmate> specification = createSpecification(criteria);
        return inmateRepository.count(specification);
    }

    protected Specification<Inmate> createSpecification(InmateCriteria criteria) {
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        return this.createSpecification(criteria, currentUser);
    }

    protected Specification<Inmate> createSpecification(InmateCriteria criteria, User currentUser) {
        Specification<Inmate> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Inmate_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Inmate_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Inmate_.lastName));
            }
            if (criteria.getDateOfBirth() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateOfBirth(), Inmate_.dateOfBirth));
            }
            if (criteria.getDateOfIncarceration() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateOfIncarceration(), Inmate_.dateOfIncarceration));
            }
            if (criteria.getDateOfExpectedRelease() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getDateOfExpectedRelease(), Inmate_.dateOfExpectedRelease));
            }
            if (currentUser != null && currentUser.getPrison() != null) {
                LongFilter longFilter = new LongFilter();
                longFilter.setEquals(currentUser.getPrison().getId());
                specification =
                    specification.and(buildSpecification(longFilter, root -> root.join(Inmate_.prison, JoinType.LEFT).get(Prison_.id)));
            }
            if (criteria.getAssignedCellId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAssignedCellId(),
                            root -> root.join(Inmate_.assignedCell, JoinType.LEFT).get(Area_.id)
                        )
                    );
            }
            if (criteria.getActivityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getActivityId(), root -> root.join(Inmate_.activities, JoinType.LEFT).get(Activity_.id))
                    );
            }
        }
        return specification;
    }
}
