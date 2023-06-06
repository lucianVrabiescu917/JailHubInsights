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
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.repository.StaffRepository;
import ro.luci.jailhubinsights.repository.UserRepository;
import ro.luci.jailhubinsights.security.SecurityUtils;
import ro.luci.jailhubinsights.service.criteria.InmateCriteria;
import ro.luci.jailhubinsights.service.criteria.StaffCriteria;
import ro.luci.jailhubinsights.service.dto.StaffDTO;
import ro.luci.jailhubinsights.service.mapper.StaffMapper;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link Staff} entities in the database.
 * The main input is a {@link StaffCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StaffDTO} or a {@link Page} of {@link StaffDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StaffQueryService extends QueryService<Staff> {

    private final Logger log = LoggerFactory.getLogger(StaffQueryService.class);

    private final StaffRepository staffRepository;

    private final StaffMapper staffMapper;

    private final UserRepository userRepository;

    public StaffQueryService(StaffRepository staffRepository, StaffMapper staffMapper, UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
        this.userRepository = userRepository;
    }

    /**
     * Return a {@link List} of {@link StaffDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StaffDTO> findByCriteria(StaffCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffMapper.toDto(staffRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StaffDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StaffDTO> findByCriteria(StaffCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        final Specification<Staff> specification = createSpecification(criteria, currentUser);
        return staffRepository.findAll(specification, page).map(staffMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StaffCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffRepository.count(specification);
    }

    protected Specification<Staff> createSpecification(StaffCriteria criteria) {
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        return this.createSpecification(criteria, currentUser);
    }

    /**
     * Function to convert {@link StaffCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Staff> createSpecification(StaffCriteria criteria, User currentUser) {
        Specification<Staff> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Staff_.id));
            }
            if (criteria.getStaffType() != null) {
                specification = specification.and(buildSpecification(criteria.getStaffType(), Staff_.staffType));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Staff_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Staff_.lastName));
            }
            if (currentUser != null && currentUser.getPrison() != null) {
                LongFilter longFilter = new LongFilter();
                longFilter.setEquals(currentUser.getPrison().getId());
                specification =
                    specification.and(buildSpecification(longFilter, root -> root.join(Staff_.prison, JoinType.LEFT).get(Prison_.id)));
            }
            if (criteria.getActivityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getActivityId(), root -> root.join(Staff_.activities, JoinType.LEFT).get(Activity_.id))
                    );
            }
            if (criteria.getAssignedAreasId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAssignedAreasId(),
                            root -> root.join(Staff_.assignedAreas, JoinType.LEFT).get(Area_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
