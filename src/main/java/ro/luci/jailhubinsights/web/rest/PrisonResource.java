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
import ro.luci.jailhubinsights.repository.PrisonRepository;
import ro.luci.jailhubinsights.service.PrisonQueryService;
import ro.luci.jailhubinsights.service.PrisonService;
import ro.luci.jailhubinsights.service.criteria.PrisonCriteria;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ro.luci.jailhubinsights.domain.Prison}.
 */
@RestController
@RequestMapping("/api")
public class PrisonResource {

    private final Logger log = LoggerFactory.getLogger(PrisonResource.class);

    private static final String ENTITY_NAME = "prison";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrisonService prisonService;

    private final PrisonRepository prisonRepository;

    private final PrisonQueryService prisonQueryService;

    public PrisonResource(PrisonService prisonService, PrisonRepository prisonRepository, PrisonQueryService prisonQueryService) {
        this.prisonService = prisonService;
        this.prisonRepository = prisonRepository;
        this.prisonQueryService = prisonQueryService;
    }

    /**
     * {@code POST  /prisons} : Create a new prison.
     *
     * @param prisonDTO the prisonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prisonDTO, or with status {@code 400 (Bad Request)} if the prison has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/prisons")
    public ResponseEntity<PrisonDTO> createPrison(@RequestBody PrisonDTO prisonDTO) throws URISyntaxException {
        log.debug("REST request to save Prison : {}", prisonDTO);
        if (prisonDTO.getId() != null) {
            throw new BadRequestAlertException("A new prison cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PrisonDTO result = prisonService.save(prisonDTO);
        return ResponseEntity
            .created(new URI("/api/prisons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /prisons/:id} : Updates an existing prison.
     *
     * @param id the id of the prisonDTO to save.
     * @param prisonDTO the prisonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prisonDTO,
     * or with status {@code 400 (Bad Request)} if the prisonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prisonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/prisons/{id}")
    public ResponseEntity<PrisonDTO> updatePrison(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PrisonDTO prisonDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Prison : {}, {}", id, prisonDTO);
        if (prisonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, prisonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!prisonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PrisonDTO result = prisonService.update(prisonDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, prisonDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /prisons/:id} : Partial updates given fields of an existing prison, field will ignore if it is null
     *
     * @param id the id of the prisonDTO to save.
     * @param prisonDTO the prisonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prisonDTO,
     * or with status {@code 400 (Bad Request)} if the prisonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the prisonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the prisonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/prisons/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PrisonDTO> partialUpdatePrison(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PrisonDTO prisonDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Prison partially : {}, {}", id, prisonDTO);
        if (prisonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, prisonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!prisonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PrisonDTO> result = prisonService.partialUpdate(prisonDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, prisonDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /prisons} : get all the prisons.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prisons in body.
     */
    @GetMapping("/prisons")
    public ResponseEntity<List<PrisonDTO>> getAllPrisons(
        PrisonCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Prisons by criteria: {}", criteria);
        Page<PrisonDTO> page = prisonQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /prisons/count} : count all the prisons.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/prisons/count")
    public ResponseEntity<Long> countPrisons(PrisonCriteria criteria) {
        log.debug("REST request to count Prisons by criteria: {}", criteria);
        return ResponseEntity.ok().body(prisonQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /prisons/:id} : get the "id" prison.
     *
     * @param id the id of the prisonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prisonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/prisons/{id}")
    public ResponseEntity<PrisonDTO> getPrison(@PathVariable Long id) {
        log.debug("REST request to get Prison : {}", id);
        Optional<PrisonDTO> prisonDTO = prisonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(prisonDTO);
    }

    /**
     * {@code DELETE  /prisons/:id} : delete the "id" prison.
     *
     * @param id the id of the prisonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/prisons/{id}")
    public ResponseEntity<Void> deletePrison(@PathVariable Long id) {
        log.debug("REST request to delete Prison : {}", id);
        prisonService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
