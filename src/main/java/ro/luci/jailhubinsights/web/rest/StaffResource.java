package ro.luci.jailhubinsights.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ro.luci.jailhubinsights.repository.StaffRepository;
import ro.luci.jailhubinsights.service.StaffQueryService;
import ro.luci.jailhubinsights.service.StaffService;
import ro.luci.jailhubinsights.service.criteria.InmateCriteria;
import ro.luci.jailhubinsights.service.criteria.StaffCriteria;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.service.dto.StaffDTO;
import ro.luci.jailhubinsights.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ro.luci.jailhubinsights.domain.Staff}.
 */
@RestController
@RequestMapping("/api")
public class StaffResource {

    private final Logger log = LoggerFactory.getLogger(StaffResource.class);

    private static final String ENTITY_NAME = "staff";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StaffService staffService;

    private final StaffRepository staffRepository;

    private final StaffQueryService staffQueryService;

    public StaffResource(StaffService staffService, StaffRepository staffRepository, StaffQueryService staffQueryService) {
        this.staffService = staffService;
        this.staffRepository = staffRepository;
        this.staffQueryService = staffQueryService;
    }

    /**
     * {@code POST  /staff} : Create a new staff.
     *
     * @param staffDTO the staffDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new staffDTO, or with status {@code 400 (Bad Request)} if the staff has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/staff")
    public ResponseEntity<StaffDTO> createStaff(@RequestBody StaffDTO staffDTO) throws URISyntaxException {
        log.debug("REST request to save Staff : {}", staffDTO);
        if (staffDTO.getId() != null) {
            throw new BadRequestAlertException("A new staff cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StaffDTO result = staffService.save(staffDTO);
        return ResponseEntity
            .created(new URI("/api/staff/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /staff/:id} : Updates an existing staff.
     *
     * @param id the id of the staffDTO to save.
     * @param staffDTO the staffDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staffDTO,
     * or with status {@code 400 (Bad Request)} if the staffDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the staffDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/staff/{id}")
    public ResponseEntity<StaffDTO> updateStaff(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StaffDTO staffDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Staff : {}, {}", id, staffDTO);
        if (staffDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, staffDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!staffRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StaffDTO result = staffService.update(staffDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, staffDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /staff/:id} : Partial updates given fields of an existing staff, field will ignore if it is null
     *
     * @param id the id of the staffDTO to save.
     * @param staffDTO the staffDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staffDTO,
     * or with status {@code 400 (Bad Request)} if the staffDTO is not valid,
     * or with status {@code 404 (Not Found)} if the staffDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the staffDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/staff/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StaffDTO> partialUpdateStaff(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StaffDTO staffDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Staff partially : {}, {}", id, staffDTO);
        if (staffDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, staffDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!staffRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StaffDTO> result = staffService.partialUpdate(staffDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, staffDTO.getId().toString())
        );
    }

    @GetMapping("/staff")
    public ResponseEntity<List<StaffDTO>> getAllStaff(
        StaffCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Staff by criteria: {}", criteria);
        Page<StaffDTO> page = staffQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/staff/all")
    public ResponseEntity<List<StaffDTO>> getAllStaffNoPagination(
        StaffCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Staff by criteria: {}", criteria);
        List<StaffDTO> list = staffQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/staff/hint")
    public ResponseEntity<List<StaffDTO>> getStaffWithHint(
        StaffCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        String hint
    ) {
        log.debug("REST request to get Staff by criteria: {}", criteria);
        Page<StaffDTO> page = staffQueryService.findByCriteriaWithHint(criteria, pageable, hint);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /staff/count} : count all the staff.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/staff/count")
    public ResponseEntity<Long> countStaff(StaffCriteria criteria) {
        log.debug("REST request to count Staff by criteria: {}", criteria);
        return ResponseEntity.ok().body(staffQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /staff/:id} : get the "id" staff.
     *
     * @param id the id of the staffDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the staffDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/staff/{id}")
    public ResponseEntity<StaffDTO> getStaff(@PathVariable Long id) {
        log.debug("REST request to get Staff : {}", id);
        Optional<StaffDTO> staffDTO = staffService.findOne(id);
        return ResponseUtil.wrapOrNotFound(staffDTO);
    }

    /**
     * {@code DELETE  /staff/:id} : delete the "id" staff.
     *
     * @param id the id of the staffDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/staff/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        log.debug("REST request to delete Staff : {}", id);
        staffService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
