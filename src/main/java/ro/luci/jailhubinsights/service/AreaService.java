package ro.luci.jailhubinsights.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.repository.AreaRepository;
import ro.luci.jailhubinsights.repository.InmateRepository;
import ro.luci.jailhubinsights.service.dto.AreaDTO;
import ro.luci.jailhubinsights.service.mapper.AreaMapper;

/**
 * Service Implementation for managing {@link Area}.
 */
@Service
@Transactional
public class AreaService {

    private final Logger log = LoggerFactory.getLogger(AreaService.class);

    private final AreaRepository areaRepository;

    private final InmateRepository inmateRepository;
    private final AreaMapper areaMapper;

    public AreaService(AreaRepository areaRepository, InmateRepository inmateRepository, AreaMapper areaMapper) {
        this.areaRepository = areaRepository;
        this.inmateRepository = inmateRepository;
        this.areaMapper = areaMapper;
    }

    /**
     * Save a area.
     *
     * @param areaDTO the entity to save.
     * @return the persisted entity.
     */
    public AreaDTO save(AreaDTO areaDTO) {
        log.debug("Request to save Area : {}", areaDTO);
        Area area = areaMapper.toEntity(areaDTO);
        area = areaRepository.save(area);
        return areaMapper.toDto(area);
    }

    /**
     * Update a area.
     *
     * @param areaDTO the entity to save.
     * @return the persisted entity.
     */
    public AreaDTO update(AreaDTO areaDTO) {
        log.debug("Request to update Area : {}", areaDTO);
        Area area = areaMapper.toEntity(areaDTO);
        area = areaRepository.save(area);
        return areaMapper.toDto(area);
    }

    /**
     * Partially update a area.
     *
     * @param areaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AreaDTO> partialUpdate(AreaDTO areaDTO) {
        log.debug("Request to partially update Area : {}", areaDTO);

        return areaRepository
            .findById(areaDTO.getId())
            .map(existingArea -> {
                areaMapper.partialUpdate(existingArea, areaDTO);

                return existingArea;
            })
            .map(areaRepository::save)
            .map(areaMapper::toDto);
    }

    /**
     * Get all the areas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AreaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Areas");
        return areaRepository.findAll(pageable).map(areaMapper::toDto);
    }

    /**
     * Get all the areas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AreaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return areaRepository.findAllWithEagerRelationships(pageable).map(areaMapper::toDto);
    }

    /**
     * Get one area by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AreaDTO> findOne(Long id) {
        log.debug("Request to get Area : {}", id);
        return areaRepository.findOneWithEagerRelationships(id).map(areaMapper::toDto);
    }

    /**
     * Delete the area by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Area : {}", id);
        areaRepository.deleteRelationsWithComposedOfByAreaId(id);
        areaRepository.deleteRelationsWithComposingByAreaId(id);
        areaRepository.deleteRelationsWithStaffByAreaId(id);
        inmateRepository.removeAreaFromInmateByAreaId(id);
        areaRepository.deleteById(id);
    }
}
