package ro.luci.jailhubinsights.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.IntegrationTest;
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.repository.PrisonRepository;
import ro.luci.jailhubinsights.service.criteria.PrisonCriteria;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.mapper.PrisonMapper;

/**
 * Integration tests for the {@link PrisonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PrisonResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/prisons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PrisonRepository prisonRepository;

    @Autowired
    private PrisonMapper prisonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrisonMockMvc;

    private Prison prison;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prison createEntity(EntityManager em) {
        Prison prison = new Prison().name(DEFAULT_NAME).location(DEFAULT_LOCATION);
        return prison;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prison createUpdatedEntity(EntityManager em) {
        Prison prison = new Prison().name(UPDATED_NAME).location(UPDATED_LOCATION);
        return prison;
    }

    @BeforeEach
    public void initTest() {
        prison = createEntity(em);
    }

    @Test
    @Transactional
    void createPrison() throws Exception {
        int databaseSizeBeforeCreate = prisonRepository.findAll().size();
        // Create the Prison
        PrisonDTO prisonDTO = prisonMapper.toDto(prison);
        restPrisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prisonDTO)))
            .andExpect(status().isCreated());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeCreate + 1);
        Prison testPrison = prisonList.get(prisonList.size() - 1);
        assertThat(testPrison.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPrison.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    @Transactional
    void createPrisonWithExistingId() throws Exception {
        // Create the Prison with an existing ID
        prison.setId(1L);
        PrisonDTO prisonDTO = prisonMapper.toDto(prison);

        int databaseSizeBeforeCreate = prisonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prisonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPrisons() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList
        restPrisonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prison.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)));
    }

    @Test
    @Transactional
    void getPrison() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get the prison
        restPrisonMockMvc
            .perform(get(ENTITY_API_URL_ID, prison.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prison.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION));
    }

    @Test
    @Transactional
    void getPrisonsByIdFiltering() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        Long id = prison.getId();

        defaultPrisonShouldBeFound("id.equals=" + id);
        defaultPrisonShouldNotBeFound("id.notEquals=" + id);

        defaultPrisonShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrisonShouldNotBeFound("id.greaterThan=" + id);

        defaultPrisonShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrisonShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPrisonsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where name equals to DEFAULT_NAME
        defaultPrisonShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the prisonList where name equals to UPDATED_NAME
        defaultPrisonShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrisonsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPrisonShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the prisonList where name equals to UPDATED_NAME
        defaultPrisonShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrisonsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where name is not null
        defaultPrisonShouldBeFound("name.specified=true");

        // Get all the prisonList where name is null
        defaultPrisonShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllPrisonsByNameContainsSomething() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where name contains DEFAULT_NAME
        defaultPrisonShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the prisonList where name contains UPDATED_NAME
        defaultPrisonShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrisonsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where name does not contain DEFAULT_NAME
        defaultPrisonShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the prisonList where name does not contain UPDATED_NAME
        defaultPrisonShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrisonsByLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where location equals to DEFAULT_LOCATION
        defaultPrisonShouldBeFound("location.equals=" + DEFAULT_LOCATION);

        // Get all the prisonList where location equals to UPDATED_LOCATION
        defaultPrisonShouldNotBeFound("location.equals=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllPrisonsByLocationIsInShouldWork() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where location in DEFAULT_LOCATION or UPDATED_LOCATION
        defaultPrisonShouldBeFound("location.in=" + DEFAULT_LOCATION + "," + UPDATED_LOCATION);

        // Get all the prisonList where location equals to UPDATED_LOCATION
        defaultPrisonShouldNotBeFound("location.in=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllPrisonsByLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where location is not null
        defaultPrisonShouldBeFound("location.specified=true");

        // Get all the prisonList where location is null
        defaultPrisonShouldNotBeFound("location.specified=false");
    }

    @Test
    @Transactional
    void getAllPrisonsByLocationContainsSomething() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where location contains DEFAULT_LOCATION
        defaultPrisonShouldBeFound("location.contains=" + DEFAULT_LOCATION);

        // Get all the prisonList where location contains UPDATED_LOCATION
        defaultPrisonShouldNotBeFound("location.contains=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllPrisonsByLocationNotContainsSomething() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        // Get all the prisonList where location does not contain DEFAULT_LOCATION
        defaultPrisonShouldNotBeFound("location.doesNotContain=" + DEFAULT_LOCATION);

        // Get all the prisonList where location does not contain UPDATED_LOCATION
        defaultPrisonShouldBeFound("location.doesNotContain=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllPrisonsByInmateIsEqualToSomething() throws Exception {
        Inmate inmate;
        if (TestUtil.findAll(em, Inmate.class).isEmpty()) {
            prisonRepository.saveAndFlush(prison);
            inmate = InmateResourceIT.createEntity(em);
        } else {
            inmate = TestUtil.findAll(em, Inmate.class).get(0);
        }
        em.persist(inmate);
        em.flush();
        prison.addInmate(inmate);
        prisonRepository.saveAndFlush(prison);
        Long inmateId = inmate.getId();

        // Get all the prisonList where inmate equals to inmateId
        defaultPrisonShouldBeFound("inmateId.equals=" + inmateId);

        // Get all the prisonList where inmate equals to (inmateId + 1)
        defaultPrisonShouldNotBeFound("inmateId.equals=" + (inmateId + 1));
    }

    @Test
    @Transactional
    void getAllPrisonsByAreaIsEqualToSomething() throws Exception {
        Area area;
        if (TestUtil.findAll(em, Area.class).isEmpty()) {
            prisonRepository.saveAndFlush(prison);
            area = AreaResourceIT.createEntity(em);
        } else {
            area = TestUtil.findAll(em, Area.class).get(0);
        }
        em.persist(area);
        em.flush();
        prison.addArea(area);
        prisonRepository.saveAndFlush(prison);
        Long areaId = area.getId();

        // Get all the prisonList where area equals to areaId
        defaultPrisonShouldBeFound("areaId.equals=" + areaId);

        // Get all the prisonList where area equals to (areaId + 1)
        defaultPrisonShouldNotBeFound("areaId.equals=" + (areaId + 1));
    }

    @Test
    @Transactional
    void getAllPrisonsByActivityIsEqualToSomething() throws Exception {
        Activity activity;
        if (TestUtil.findAll(em, Activity.class).isEmpty()) {
            prisonRepository.saveAndFlush(prison);
            activity = ActivityResourceIT.createEntity(em);
        } else {
            activity = TestUtil.findAll(em, Activity.class).get(0);
        }
        em.persist(activity);
        em.flush();
        prison.addActivity(activity);
        prisonRepository.saveAndFlush(prison);
        Long activityId = activity.getId();

        // Get all the prisonList where activity equals to activityId
        defaultPrisonShouldBeFound("activityId.equals=" + activityId);

        // Get all the prisonList where activity equals to (activityId + 1)
        defaultPrisonShouldNotBeFound("activityId.equals=" + (activityId + 1));
    }

    @Test
    @Transactional
    void getAllPrisonsByStaffIsEqualToSomething() throws Exception {
        Staff staff;
        if (TestUtil.findAll(em, Staff.class).isEmpty()) {
            prisonRepository.saveAndFlush(prison);
            staff = StaffResourceIT.createEntity(em);
        } else {
            staff = TestUtil.findAll(em, Staff.class).get(0);
        }
        em.persist(staff);
        em.flush();
        prison.addStaff(staff);
        prisonRepository.saveAndFlush(prison);
        Long staffId = staff.getId();

        // Get all the prisonList where staff equals to staffId
        defaultPrisonShouldBeFound("staffId.equals=" + staffId);

        // Get all the prisonList where staff equals to (staffId + 1)
        defaultPrisonShouldNotBeFound("staffId.equals=" + (staffId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrisonShouldBeFound(String filter) throws Exception {
        restPrisonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prison.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)));

        // Check, that the count call also returns 1
        restPrisonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrisonShouldNotBeFound(String filter) throws Exception {
        restPrisonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrisonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPrison() throws Exception {
        // Get the prison
        restPrisonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPrison() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();

        // Update the prison
        Prison updatedPrison = prisonRepository.findById(prison.getId()).get();
        // Disconnect from session so that the updates on updatedPrison are not directly saved in db
        em.detach(updatedPrison);
        updatedPrison.name(UPDATED_NAME).location(UPDATED_LOCATION);
        PrisonDTO prisonDTO = prisonMapper.toDto(updatedPrison);

        restPrisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, prisonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(prisonDTO))
            )
            .andExpect(status().isOk());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
        Prison testPrison = prisonList.get(prisonList.size() - 1);
        assertThat(testPrison.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPrison.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void putNonExistingPrison() throws Exception {
        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();
        prison.setId(count.incrementAndGet());

        // Create the Prison
        PrisonDTO prisonDTO = prisonMapper.toDto(prison);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, prisonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(prisonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPrison() throws Exception {
        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();
        prison.setId(count.incrementAndGet());

        // Create the Prison
        PrisonDTO prisonDTO = prisonMapper.toDto(prison);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(prisonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPrison() throws Exception {
        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();
        prison.setId(count.incrementAndGet());

        // Create the Prison
        PrisonDTO prisonDTO = prisonMapper.toDto(prison);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrisonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prisonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePrisonWithPatch() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();

        // Update the prison using partial update
        Prison partialUpdatedPrison = new Prison();
        partialUpdatedPrison.setId(prison.getId());

        partialUpdatedPrison.name(UPDATED_NAME).location(UPDATED_LOCATION);

        restPrisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrison.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrison))
            )
            .andExpect(status().isOk());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
        Prison testPrison = prisonList.get(prisonList.size() - 1);
        assertThat(testPrison.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPrison.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void fullUpdatePrisonWithPatch() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();

        // Update the prison using partial update
        Prison partialUpdatedPrison = new Prison();
        partialUpdatedPrison.setId(prison.getId());

        partialUpdatedPrison.name(UPDATED_NAME).location(UPDATED_LOCATION);

        restPrisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrison.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrison))
            )
            .andExpect(status().isOk());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
        Prison testPrison = prisonList.get(prisonList.size() - 1);
        assertThat(testPrison.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPrison.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void patchNonExistingPrison() throws Exception {
        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();
        prison.setId(count.incrementAndGet());

        // Create the Prison
        PrisonDTO prisonDTO = prisonMapper.toDto(prison);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, prisonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(prisonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPrison() throws Exception {
        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();
        prison.setId(count.incrementAndGet());

        // Create the Prison
        PrisonDTO prisonDTO = prisonMapper.toDto(prison);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(prisonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPrison() throws Exception {
        int databaseSizeBeforeUpdate = prisonRepository.findAll().size();
        prison.setId(count.incrementAndGet());

        // Create the Prison
        PrisonDTO prisonDTO = prisonMapper.toDto(prison);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrisonMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(prisonDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prison in the database
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePrison() throws Exception {
        // Initialize the database
        prisonRepository.saveAndFlush(prison);

        int databaseSizeBeforeDelete = prisonRepository.findAll().size();

        // Delete the prison
        restPrisonMockMvc
            .perform(delete(ENTITY_API_URL_ID, prison.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Prison> prisonList = prisonRepository.findAll();
        assertThat(prisonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
