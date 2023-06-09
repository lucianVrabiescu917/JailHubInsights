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
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.repository.ActivityRepository;
import ro.luci.jailhubinsights.repository.UserRepository;
import ro.luci.jailhubinsights.security.SecurityUtils;
import ro.luci.jailhubinsights.service.criteria.ActivityCriteria;
import ro.luci.jailhubinsights.service.dto.ActivityDTO;
import ro.luci.jailhubinsights.service.mapper.ActivityMapper;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link Activity} entities in the database.
 * The main input is a {@link ActivityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ActivityDTO} or a {@link Page} of {@link ActivityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ActivityQueryService extends QueryService<Activity> {

    private final Logger log = LoggerFactory.getLogger(ActivityQueryService.class);

    private final ActivityRepository activityRepository;

    private final ActivityMapper activityMapper;

    private final UserRepository userRepository;

    public ActivityQueryService(ActivityRepository activityRepository, ActivityMapper activityMapper, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.userRepository = userRepository;
    }

    /**
     * Return a {@link List} of {@link ActivityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ActivityDTO> findByCriteria(ActivityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Activity> specification = createSpecification(criteria);
        return activityMapper.toDto(activityRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ActivityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ActivityDTO> findByCriteria(ActivityCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Activity> specification = createSpecification(criteria);
        return activityRepository.findAll(specification, page).map(activityMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ActivityDTO> findByCriteriaWithHint(ActivityCriteria criteria, Pageable page, String hint) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Activity> specification = createSpecification(criteria, hint);
        return activityRepository.findAll(specification, page).map(activityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ActivityCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Activity> specification = createSpecification(criteria);
        return activityRepository.count(specification);
    }

    protected Specification<Activity> createSpecification(ActivityCriteria criteria) {
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        return this.createSpecification(criteria, currentUser, null);
    }

    protected Specification<Activity> createSpecification(ActivityCriteria criteria, String hint) {
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        return this.createSpecification(criteria, currentUser, hint);
    }

    /**
     * Function to convert {@link ActivityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Activity> createSpecification(ActivityCriteria criteria, User currentUser, String hint) {
        Specification<Activity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Activity_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Activity_.type));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Activity_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Activity_.description));
            }
            if (hint != null && !hint.equals("")) {
                specification =
                    specification.and((root, query, criteriaBuilder) -> {
                        return criteriaBuilder.like(root.get(Activity_.title), "%" + hint + "%");
                    });
            }
            if (currentUser != null && currentUser.getPrison() != null) {
                LongFilter longFilter = new LongFilter();
                longFilter.setEquals(currentUser.getPrison().getId());
                specification =
                    specification.and(buildSpecification(longFilter, root -> root.join(Activity_.prison, JoinType.LEFT).get(Prison_.id)));
            }
            if (criteria.getInmateId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getInmateId(), root -> root.join(Activity_.inmates, JoinType.LEFT).get(Inmate_.id))
                    );
            }
            if (criteria.getStaffId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStaffId(), root -> root.join(Activity_.staff, JoinType.LEFT).get(Staff_.id))
                    );
            }
        }
        return specification;
    }
}
