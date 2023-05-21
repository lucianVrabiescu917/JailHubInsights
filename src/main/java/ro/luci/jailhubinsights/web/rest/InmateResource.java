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
import ro.luci.jailhubinsights.repository.InmateRepository;
import ro.luci.jailhubinsights.service.InmateQueryService;
import ro.luci.jailhubinsights.service.InmateService;
import ro.luci.jailhubinsights.service.criteria.InmateCriteria;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ro.luci.jailhubinsights.domain.Inmate}.
 */
@RestController
@RequestMapping("/api")
public class InmateResource {

    private final Logger log = LoggerFactory.getLogger(InmateResource.class);

    private static final String ENTITY_NAME = "inmate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InmateService inmateService;

    private final InmateRepository inmateRepository;

    private final InmateQueryService inmateQueryService;

    public InmateResource(InmateService inmateService, InmateRepository inmateRepository, InmateQueryService inmateQueryService) {
        this.inmateService = inmateService;
        this.inmateRepository = inmateRepository;
        this.inmateQueryService = inmateQueryService;
    }

    /**
     * {@code POST  /inmates} : Create a new inmate.
     *
     * @param inmateDTO the inmateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inmateDTO, or with status {@code 400 (Bad Request)} if the inmate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/inmates")
    public ResponseEntity<InmateDTO> createInmate(@RequestBody InmateDTO inmateDTO) throws URISyntaxException {
        log.debug("REST request to save Inmate : {}", inmateDTO);
        if (inmateDTO.getId() != null) {
            throw new BadRequestAlertException("A new inmate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InmateDTO result = inmateService.save(inmateDTO);
        return ResponseEntity
            .created(new URI("/api/inmates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /inmates/:id} : Updates an existing inmate.
     *
     * @param id the id of the inmateDTO to save.
     * @param inmateDTO the inmateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inmateDTO,
     * or with status {@code 400 (Bad Request)} if the inmateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inmateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/inmates/{id}")
    public ResponseEntity<InmateDTO> updateInmate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody InmateDTO inmateDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Inmate : {}, {}", id, inmateDTO);
        if (inmateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inmateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inmateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        InmateDTO result = inmateService.update(inmateDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inmateDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /inmates/:id} : Partial updates given fields of an existing inmate, field will ignore if it is null
     *
     * @param id the id of the inmateDTO to save.
     * @param inmateDTO the inmateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inmateDTO,
     * or with status {@code 400 (Bad Request)} if the inmateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the inmateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the inmateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/inmates/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InmateDTO> partialUpdateInmate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody InmateDTO inmateDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Inmate partially : {}, {}", id, inmateDTO);
        if (inmateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inmateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inmateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InmateDTO> result = inmateService.partialUpdate(inmateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inmateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /inmates} : get all the inmates.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of inmates in body.
     */
    @GetMapping("/inmates")
    public ResponseEntity<List<InmateDTO>> getAllInmates(
        InmateCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Inmates by criteria: {}", criteria);
        Page<InmateDTO> page = inmateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /inmates/count} : count all the inmates.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/inmates/count")
    public ResponseEntity<Long> countInmates(InmateCriteria criteria) {
        log.debug("REST request to count Inmates by criteria: {}", criteria);
        return ResponseEntity.ok().body(inmateQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /inmates/:id} : get the "id" inmate.
     *
     * @param id the id of the inmateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inmateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/inmates/{id}")
    public ResponseEntity<InmateDTO> getInmate(@PathVariable Long id) {
        log.debug("REST request to get Inmate : {}", id);
        Optional<InmateDTO> inmateDTO = inmateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inmateDTO);
    }

    /**
     * {@code DELETE  /inmates/:id} : delete the "id" inmate.
     *
     * @param id the id of the inmateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/inmates/{id}")
    public ResponseEntity<Void> deleteInmate(@PathVariable Long id) {
        log.debug("REST request to delete Inmate : {}", id);
        inmateService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
