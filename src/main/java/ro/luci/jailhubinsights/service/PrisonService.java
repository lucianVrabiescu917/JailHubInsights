package ro.luci.jailhubinsights.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.repository.PrisonRepository;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.mapper.PrisonMapper;

/**
 * Service Implementation for managing {@link Prison}.
 */
@Service
@Transactional
public class PrisonService {

    private final Logger log = LoggerFactory.getLogger(PrisonService.class);

    private final PrisonRepository prisonRepository;

    private final PrisonMapper prisonMapper;

    public PrisonService(PrisonRepository prisonRepository, PrisonMapper prisonMapper) {
        this.prisonRepository = prisonRepository;
        this.prisonMapper = prisonMapper;
    }

    /**
     * Save a prison.
     *
     * @param prisonDTO the entity to save.
     * @return the persisted entity.
     */
    public PrisonDTO save(PrisonDTO prisonDTO) {
        log.debug("Request to save Prison : {}", prisonDTO);
        Prison prison = prisonMapper.toEntity(prisonDTO);
        prison = prisonRepository.save(prison);
        return prisonMapper.toDto(prison);
    }

    /**
     * Update a prison.
     *
     * @param prisonDTO the entity to save.
     * @return the persisted entity.
     */
    public PrisonDTO update(PrisonDTO prisonDTO) {
        log.debug("Request to update Prison : {}", prisonDTO);
        Prison prison = prisonMapper.toEntity(prisonDTO);
        prison = prisonRepository.save(prison);
        return prisonMapper.toDto(prison);
    }

    /**
     * Partially update a prison.
     *
     * @param prisonDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PrisonDTO> partialUpdate(PrisonDTO prisonDTO) {
        log.debug("Request to partially update Prison : {}", prisonDTO);

        return prisonRepository
            .findById(prisonDTO.getId())
            .map(existingPrison -> {
                prisonMapper.partialUpdate(existingPrison, prisonDTO);

                return existingPrison;
            })
            .map(prisonRepository::save)
            .map(prisonMapper::toDto);
    }

    /**
     * Get all the prisons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PrisonDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Prisons");
        return prisonRepository.findAll(pageable).map(prisonMapper::toDto);
    }

    /**
     * Get one prison by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PrisonDTO> findOne(Long id) {
        log.debug("Request to get Prison : {}", id);
        return prisonRepository.findById(id).map(prisonMapper::toDto);
    }

    /**
     * Delete the prison by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Prison : {}", id);
        prisonRepository.deleteById(id);
    }
}
