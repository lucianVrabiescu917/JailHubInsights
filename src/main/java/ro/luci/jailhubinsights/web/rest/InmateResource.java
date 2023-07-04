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

    @GetMapping("/inmates/hint")
    public ResponseEntity<List<InmateDTO>> getInmatesWithHint(
        InmateCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        String hint
    ) {
        log.debug("REST request to get Inmates by criteria: {} with hint: {}", criteria, hint);
        Page<InmateDTO> page = inmateQueryService.findByCriteriaWithHint(criteria, pageable, hint);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/inmates/count")
    public ResponseEntity<Long> countInmates(InmateCriteria criteria) {
        log.debug("REST request to count Inmates by criteria: {}", criteria);
        return ResponseEntity.ok().body(inmateQueryService.countByCriteria(criteria));
    }

    @GetMapping("/inmates/{id}")
    public ResponseEntity<InmateDTO> getInmate(@PathVariable Long id) {
        log.debug("REST request to get Inmate : {}", id);
        Optional<InmateDTO> inmateDTO = inmateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inmateDTO);
    }

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
