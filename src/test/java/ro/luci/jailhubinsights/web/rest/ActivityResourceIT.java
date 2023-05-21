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
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.domain.enumeration.ActivityType;
import ro.luci.jailhubinsights.repository.ActivityRepository;
import ro.luci.jailhubinsights.service.criteria.ActivityCriteria;
import ro.luci.jailhubinsights.service.dto.ActivityDTO;
import ro.luci.jailhubinsights.service.mapper.ActivityMapper;

/**
 * Integration tests for the {@link ActivityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActivityResourceIT {

    private static final ActivityType DEFAULT_TYPE = ActivityType.LABOR;
    private static final ActivityType UPDATED_TYPE = ActivityType.RECREATION;

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/activities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActivityMockMvc;

    private Activity activity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createEntity(EntityManager em) {
        Activity activity = new Activity().type(DEFAULT_TYPE).title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
        return activity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createUpdatedEntity(EntityManager em) {
        Activity activity = new Activity().type(UPDATED_TYPE).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        return activity;
    }

    @BeforeEach
    public void initTest() {
        activity = createEntity(em);
    }

    @Test
    @Transactional
    void createActivity() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();
        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);
        restActivityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate + 1);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testActivity.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testActivity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createActivityWithExistingId() throws Exception {
        // Create the Activity with an existing ID
        activity.setId(1L);
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllActivities() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get the activity
        restActivityMockMvc
            .perform(get(ENTITY_API_URL_ID, activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(activity.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getActivitiesByIdFiltering() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        Long id = activity.getId();

        defaultActivityShouldBeFound("id.equals=" + id);
        defaultActivityShouldNotBeFound("id.notEquals=" + id);

        defaultActivityShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultActivityShouldNotBeFound("id.greaterThan=" + id);

        defaultActivityShouldBeFound("id.lessThanOrEqual=" + id);
        defaultActivityShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllActivitiesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type equals to DEFAULT_TYPE
        defaultActivityShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultActivityShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type is not null
        defaultActivityShouldBeFound("type.specified=true");

        // Get all the activityList where type is null
        defaultActivityShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title equals to DEFAULT_TITLE
        defaultActivityShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the activityList where title equals to UPDATED_TITLE
        defaultActivityShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultActivityShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the activityList where title equals to UPDATED_TITLE
        defaultActivityShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title is not null
        defaultActivityShouldBeFound("title.specified=true");

        // Get all the activityList where title is null
        defaultActivityShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title contains DEFAULT_TITLE
        defaultActivityShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the activityList where title contains UPDATED_TITLE
        defaultActivityShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title does not contain DEFAULT_TITLE
        defaultActivityShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the activityList where title does not contain UPDATED_TITLE
        defaultActivityShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllActivitiesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where description equals to DEFAULT_DESCRIPTION
        defaultActivityShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the activityList where description equals to UPDATED_DESCRIPTION
        defaultActivityShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActivitiesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultActivityShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the activityList where description equals to UPDATED_DESCRIPTION
        defaultActivityShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActivitiesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where description is not null
        defaultActivityShouldBeFound("description.specified=true");

        // Get all the activityList where description is null
        defaultActivityShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where description contains DEFAULT_DESCRIPTION
        defaultActivityShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the activityList where description contains UPDATED_DESCRIPTION
        defaultActivityShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActivitiesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where description does not contain DEFAULT_DESCRIPTION
        defaultActivityShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the activityList where description does not contain UPDATED_DESCRIPTION
        defaultActivityShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActivitiesByPrisonIsEqualToSomething() throws Exception {
        Prison prison;
        if (TestUtil.findAll(em, Prison.class).isEmpty()) {
            activityRepository.saveAndFlush(activity);
            prison = PrisonResourceIT.createEntity(em);
        } else {
            prison = TestUtil.findAll(em, Prison.class).get(0);
        }
        em.persist(prison);
        em.flush();
        activity.setPrison(prison);
        activityRepository.saveAndFlush(activity);
        Long prisonId = prison.getId();

        // Get all the activityList where prison equals to prisonId
        defaultActivityShouldBeFound("prisonId.equals=" + prisonId);

        // Get all the activityList where prison equals to (prisonId + 1)
        defaultActivityShouldNotBeFound("prisonId.equals=" + (prisonId + 1));
    }

    @Test
    @Transactional
    void getAllActivitiesByInmateIsEqualToSomething() throws Exception {
        Inmate inmate;
        if (TestUtil.findAll(em, Inmate.class).isEmpty()) {
            activityRepository.saveAndFlush(activity);
            inmate = InmateResourceIT.createEntity(em);
        } else {
            inmate = TestUtil.findAll(em, Inmate.class).get(0);
        }
        em.persist(inmate);
        em.flush();
        activity.addInmate(inmate);
        activityRepository.saveAndFlush(activity);
        Long inmateId = inmate.getId();

        // Get all the activityList where inmate equals to inmateId
        defaultActivityShouldBeFound("inmateId.equals=" + inmateId);

        // Get all the activityList where inmate equals to (inmateId + 1)
        defaultActivityShouldNotBeFound("inmateId.equals=" + (inmateId + 1));
    }

    @Test
    @Transactional
    void getAllActivitiesByStaffIsEqualToSomething() throws Exception {
        Staff staff;
        if (TestUtil.findAll(em, Staff.class).isEmpty()) {
            activityRepository.saveAndFlush(activity);
            staff = StaffResourceIT.createEntity(em);
        } else {
            staff = TestUtil.findAll(em, Staff.class).get(0);
        }
        em.persist(staff);
        em.flush();
        activity.addStaff(staff);
        activityRepository.saveAndFlush(activity);
        Long staffId = staff.getId();

        // Get all the activityList where staff equals to staffId
        defaultActivityShouldBeFound("staffId.equals=" + staffId);

        // Get all the activityList where staff equals to (staffId + 1)
        defaultActivityShouldNotBeFound("staffId.equals=" + (staffId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultActivityShouldBeFound(String filter) throws Exception {
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultActivityShouldNotBeFound(String filter) throws Exception {
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingActivity() throws Exception {
        // Get the activity
        restActivityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity
        Activity updatedActivity = activityRepository.findById(activity.getId()).get();
        // Disconnect from session so that the updates on updatedActivity are not directly saved in db
        em.detach(updatedActivity);
        updatedActivity.type(UPDATED_TYPE).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        ActivityDTO activityDTO = activityMapper.toDto(updatedActivity);

        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, activityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testActivity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, activityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateActivityWithPatch() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity using partial update
        Activity partialUpdatedActivity = new Activity();
        partialUpdatedActivity.setId(activity.getId());

        partialUpdatedActivity.type(UPDATED_TYPE);

        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActivity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActivity))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testActivity.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testActivity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateActivityWithPatch() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity using partial update
        Activity partialUpdatedActivity = new Activity();
        partialUpdatedActivity.setId(activity.getId());

        partialUpdatedActivity.type(UPDATED_TYPE).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActivity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActivity))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testActivity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, activityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeDelete = activityRepository.findAll().size();

        // Delete the activity
        restActivityMockMvc
            .perform(delete(ENTITY_API_URL_ID, activity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
