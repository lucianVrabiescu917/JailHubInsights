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
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.domain.User;
import ro.luci.jailhubinsights.repository.StaffRepository;
import ro.luci.jailhubinsights.repository.UserRepository;
import ro.luci.jailhubinsights.security.SecurityUtils;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.dto.StaffDTO;
import ro.luci.jailhubinsights.service.mapper.StaffMapper;

/**
 * Service Implementation for managing {@link Staff}.
 */
@Service
@Transactional
public class StaffService {

    private final Logger log = LoggerFactory.getLogger(StaffService.class);

    private final StaffRepository staffRepository;

    private final StaffMapper staffMapper;

    private final UserRepository userRepository;

    public StaffService(StaffRepository staffRepository, StaffMapper staffMapper, UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
        this.userRepository = userRepository;
    }

    public Staff updateAndReturnEntityWithCurrentPrison(StaffDTO staffDTO) {
        Staff staff = staffMapper.toEntity(staffDTO);
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        if (currentUser != null) {
            staff.setPrison(currentUser.getPrison());
        } else {
            staff.setPrison(null);
        }
        return staff;
    }

    /**
     * Save a staff.
     *
     * @param staffDTO the entity to save.
     * @return the persisted entity.
     */
    public StaffDTO save(StaffDTO staffDTO) {
        log.debug("Request to save Staff : {}", staffDTO);
        Staff staff = this.updateAndReturnEntityWithCurrentPrison(staffDTO);
        staff = staffRepository.save(staff);
        return staffMapper.toDto(staff);
    }

    /**
     * Update a staff.
     *
     * @param staffDTO the entity to save.
     * @return the persisted entity.
     */
    public StaffDTO update(StaffDTO staffDTO) {
        log.debug("Request to update Staff : {}", staffDTO);
        Staff staff = this.updateAndReturnEntityWithCurrentPrison(staffDTO);
        staff = staffRepository.save(staff);
        return staffMapper.toDto(staff);
    }

    /**
     * Partially update a staff.
     *
     * @param staffDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StaffDTO> partialUpdate(StaffDTO staffDTO) {
        log.debug("Request to partially update Staff : {}", staffDTO);

        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }

        if (currentUser != null && currentUser.getPrison() != null) {
            Prison prison = currentUser.getPrison();
            staffDTO.setPrison(new PrisonDTO(prison.getId(), prison.getName(), prison.getLocation(), prison.getImage()));
        } else {
            staffDTO.setPrison(null);
        }

        return staffRepository
            .findById(staffDTO.getId())
            .map(existingStaff -> {
                staffMapper.partialUpdate(existingStaff, staffDTO);

                return existingStaff;
            })
            .map(staffRepository::save)
            .map(staffMapper::toDto);
    }

    /**
     * Get all the staff.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StaffDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Staff");
        return staffRepository.findAll(pageable).map(staffMapper::toDto);
    }

    /**
     * Get all the staff with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StaffDTO> findAllWithEagerRelationships(Pageable pageable) {
        return staffRepository.findAllWithEagerRelationships(pageable).map(staffMapper::toDto);
    }

    /**
     * Get one staff by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StaffDTO> findOne(Long id) {
        log.debug("Request to get Staff : {}", id);
        return staffRepository.findOneWithEagerRelationships(id).map(staffMapper::toDto);
    }

    /**
     * Delete the staff by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Staff : {}", id);
        staffRepository.deleteById(id);
    }
}
