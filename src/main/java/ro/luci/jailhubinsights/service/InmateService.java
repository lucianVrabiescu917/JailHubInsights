package ro.luci.jailhubinsights.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.User;
import ro.luci.jailhubinsights.repository.InmateRepository;
import ro.luci.jailhubinsights.repository.UserRepository;
import ro.luci.jailhubinsights.security.SecurityUtils;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.mapper.InmateMapper;

/**
 * Service Implementation for managing {@link Inmate}.
 */
@Service
@Transactional
public class InmateService {

    private final Logger log = LoggerFactory.getLogger(InmateService.class);

    private final InmateRepository inmateRepository;

    private final InmateMapper inmateMapper;

    private final UserRepository userRepository;

    public InmateService(InmateRepository inmateRepository, InmateMapper inmateMapper, UserRepository userRepository) {
        this.inmateRepository = inmateRepository;
        this.inmateMapper = inmateMapper;
        this.userRepository = userRepository;
    }

    public Inmate updateAndReturnEntityWithCurrentPrison(InmateDTO inmateDTO) {
        Inmate inmate = inmateMapper.toEntity(inmateDTO);
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        if (currentUser != null) {
            inmate.setPrison(currentUser.getPrison());
        } else {
            inmate.setPrison(null);
        }
        return inmate;
    }

    /**
     * Save a inmate.
     *
     * @param inmateDTO the entity to save.
     * @return the persisted entity.
     */
    public InmateDTO save(InmateDTO inmateDTO) {
        log.debug("Request to save Inmate : {}", inmateDTO);
        Inmate inmate = this.updateAndReturnEntityWithCurrentPrison(inmateDTO);
        inmate = inmateRepository.save(inmate);
        return inmateMapper.toDto(inmate);
    }

    /**
     * Update a inmate.
     *
     * @param inmateDTO the entity to save.
     * @return the persisted entity.
     */
    public InmateDTO update(InmateDTO inmateDTO) {
        log.debug("Request to update Inmate : {}", inmateDTO);
        Inmate inmate = this.updateAndReturnEntityWithCurrentPrison(inmateDTO);
        inmate = inmateRepository.save(inmate);
        return inmateMapper.toDto(inmate);
    }

    /**
     * Partially update a inmate.
     *
     * @param inmateDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InmateDTO> partialUpdate(InmateDTO inmateDTO) {
        log.debug("Request to partially update Inmate : {}", inmateDTO);

        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }

        if (currentUser != null && currentUser.getPrison() != null) {
            Prison prison = currentUser.getPrison();
            inmateDTO.setPrison(new PrisonDTO(prison.getId(), prison.getName(), prison.getLocation(), prison.getImage()));
        } else {
            inmateDTO.setPrison(null);
        }

        return inmateRepository
            .findById(inmateDTO.getId())
            .map(existingInmate -> {
                inmateMapper.partialUpdate(existingInmate, inmateDTO);

                return existingInmate;
            })
            .map(inmateRepository::save)
            .map(inmateMapper::toDto);
    }

    /**
     * Get all the inmates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InmateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Inmates");
        return inmateRepository.findAll(pageable).map(inmateMapper::toDto);
    }

    /**
     * Get all the inmates with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InmateDTO> findAllWithEagerRelationships(Pageable pageable) {
        return inmateRepository.findAllWithEagerRelationships(pageable).map(inmateMapper::toDto);
    }

    /**
     * Get one inmate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InmateDTO> findOne(Long id) {
        log.debug("Request to get Inmate : {}", id);
        return inmateRepository.findOneWithEagerRelationships(id).map(inmateMapper::toDto);
    }

    /**
     * Delete the inmate by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Inmate : {}", id);
        inmateRepository.deleteById(id);
    }
}
