package ro.luci.jailhubinsights.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ro.luci.jailhubinsights.repository.AreaRepository;
import ro.luci.jailhubinsights.service.AreaQueryService;
import ro.luci.jailhubinsights.service.AreaService;
import ro.luci.jailhubinsights.service.criteria.AreaCriteria;
import ro.luci.jailhubinsights.service.dto.AreaDTO;
import ro.luci.jailhubinsights.service.dto.StaffDTO;
import ro.luci.jailhubinsights.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ro.luci.jailhubinsights.domain.Area}.
 */
@RestController
@RequestMapping("/api")
public class AreaResource {

    private final Logger log = LoggerFactory.getLogger(AreaResource.class);

    private static final String ENTITY_NAME = "area";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AreaService areaService;

    private final AreaRepository areaRepository;

    private final AreaQueryService areaQueryService;

    public AreaResource(AreaService areaService, AreaRepository areaRepository, AreaQueryService areaQueryService) {
        this.areaService = areaService;
        this.areaRepository = areaRepository;
        this.areaQueryService = areaQueryService;
    }

    @PostMapping("/areas")
    public ResponseEntity<AreaDTO> createArea(@RequestBody AreaDTO areaDTO) throws URISyntaxException {
        log.debug("REST request to save Area : {}", areaDTO);
        if (areaDTO.getId() != null) {
            throw new BadRequestAlertException("A new area cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AreaDTO result = areaService.save(areaDTO);
        return ResponseEntity
            .created(new URI("/api/areas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/areas/{id}")
    public ResponseEntity<AreaDTO> updateArea(@PathVariable(value = "id", required = false) final Long id, @RequestBody AreaDTO areaDTO)
        throws URISyntaxException {
        log.debug("REST request to update Area : {}, {}", id, areaDTO);
        if (areaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, areaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!areaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AreaDTO result = areaService.update(areaDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, areaDTO.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/areas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AreaDTO> partialUpdateArea(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AreaDTO areaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Area partially : {}, {}", id, areaDTO);
        if (areaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, areaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!areaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AreaDTO> result = areaService.partialUpdate(areaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, areaDTO.getId().toString())
        );
    }

    @GetMapping("/areas")
    public ResponseEntity<List<AreaDTO>> getAllAreas(
        AreaCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Areas by criteria: {}", criteria);
        Page<AreaDTO> page = areaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/areas/hint")
    public ResponseEntity<List<AreaDTO>> getAreasWithHint(
        AreaCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        String hint
    ) {
        log.debug("REST request to get Areas by criteria: {}", criteria);
        Page<AreaDTO> page = areaQueryService.findByCriteriaWithHint(criteria, pageable, hint);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/areas/count")
    public ResponseEntity<Long> countAreas(AreaCriteria criteria) {
        log.debug("REST request to count Areas by criteria: {}", criteria);
        return ResponseEntity.ok().body(areaQueryService.countByCriteria(criteria));
    }

    @GetMapping("/areas/{id}")
    public ResponseEntity<AreaDTO> getArea(@PathVariable Long id) {
        log.debug("REST request to get Area : {}", id);
        Optional<AreaDTO> areaDTO = areaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(areaDTO);
    }

    @GetMapping("/areas/{id}/staffIds")
    public ResponseEntity<List<Long>> getAllDistinctStaffIds(@PathVariable Long id) {
        log.debug("REST request to get Area : {}", id);
        List<Long> staffIds = areaService.getAllDistinctStaff(id);
        return ResponseEntity.ok().body(staffIds);
    }

    @GetMapping("/areas/{id}/inmatesIds")
    public ResponseEntity<List<Long>> getAllDistinctInmatesIds(@PathVariable Long id) {
        log.debug("REST request to get Area : {}", id);
        List<Long> staffIds = areaService.getAllDistinctInmates(id);
        return ResponseEntity.ok().body(staffIds);
    }

    @DeleteMapping("/areas/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        log.debug("REST request to delete Area : {}", id);
        areaService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
