package ro.luci.jailhubinsights.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.User;
import ro.luci.jailhubinsights.repository.ActivityRepository;
import ro.luci.jailhubinsights.repository.UserRepository;
import ro.luci.jailhubinsights.security.SecurityUtils;
import ro.luci.jailhubinsights.service.dto.ActivityDTO;
import ro.luci.jailhubinsights.service.dto.ActivityDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.mapper.ActivityMapper;

/**
 * Service Implementation for managing {@link Activity}.
 */
@Service
@Transactional
public class ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;

    private final ActivityMapper activityMapper;

    private final UserRepository userRepository;

    public ActivityService(ActivityRepository activityRepository, ActivityMapper activityMapper, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.userRepository = userRepository;
    }

    public Activity updateAndReturnEntityWithCurrentPrison(ActivityDTO activityDTO) {
        Activity activity = activityMapper.toEntity(activityDTO);
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        if (currentUser != null) {
            activity.setPrison(currentUser.getPrison());
        } else {
            activity.setPrison(null);
        }
        return activity;
    }

    /**
     * Save a activity.
     *
     * @param activityDTO the entity to save.
     * @return the persisted entity.
     */
    public ActivityDTO save(ActivityDTO activityDTO) {
        log.debug("Request to save Activity : {}", activityDTO);
        Activity activity = this.updateAndReturnEntityWithCurrentPrison(activityDTO);
        activity = activityRepository.save(activity);
        return activityMapper.toDto(activity);
    }

    /**
     * Update a activity.
     *
     * @param activityDTO the entity to save.
     * @return the persisted entity.
     */
    public ActivityDTO update(ActivityDTO activityDTO) {
        log.debug("Request to update Activity : {}", activityDTO);
        Activity activity = this.updateAndReturnEntityWithCurrentPrison(activityDTO);
        activity = activityRepository.save(activity);
        return activityMapper.toDto(activity);
    }

    /**
     * Partially update a activity.
     *
     * @param activityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ActivityDTO> partialUpdate(ActivityDTO activityDTO) {
        log.debug("Request to partially update Activity : {}", activityDTO);

        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }

        if (currentUser != null && currentUser.getPrison() != null) {
            Prison prison = currentUser.getPrison();
            activityDTO.setPrison(new PrisonDTO(prison.getId(), prison.getName(), prison.getLocation(), prison.getImage()));
        } else {
            activityDTO.setPrison(null);
        }

        return activityRepository
            .findById(activityDTO.getId())
            .map(existingActivity -> {
                activityMapper.partialUpdate(existingActivity, activityDTO);

                return existingActivity;
            })
            .map(activityRepository::save)
            .map(activityMapper::toDto);
    }

    /**
     * Get all the activities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ActivityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Activities");
        return activityRepository.findAll(pageable).map(activityMapper::toDto);
    }

    /**
     * Get one activity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ActivityDTO> findOne(Long id) {
        log.debug("Request to get Activity : {}", id);
        return activityRepository.findById(id).map(activityMapper::toDto);
    }

    /**
     * Delete the activity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.deleteById(id);
    }
}
