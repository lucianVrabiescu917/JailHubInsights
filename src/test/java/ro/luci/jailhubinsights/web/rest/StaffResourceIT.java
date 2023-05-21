package ro.luci.jailhubinsights.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.IntegrationTest;
import ro.luci.jailhubinsights.domain.Activity;
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.domain.enumeration.StaffType;
import ro.luci.jailhubinsights.repository.StaffRepository;
import ro.luci.jailhubinsights.service.StaffService;
import ro.luci.jailhubinsights.service.criteria.StaffCriteria;
import ro.luci.jailhubinsights.service.dto.StaffDTO;
import ro.luci.jailhubinsights.service.mapper.StaffMapper;

/**
 * Integration tests for the {@link StaffResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StaffResourceIT {

    private static final StaffType DEFAULT_STAFF_TYPE = StaffType.DIRECTOR;
    private static final StaffType UPDATED_STAFF_TYPE = StaffType.GUARD;

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/staff";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StaffRepository staffRepository;

    @Mock
    private StaffRepository staffRepositoryMock;

    @Autowired
    private StaffMapper staffMapper;

    @Mock
    private StaffService staffServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStaffMockMvc;

    private Staff staff;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createEntity(EntityManager em) {
        Staff staff = new Staff().staffType(DEFAULT_STAFF_TYPE).firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME);
        return staff;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createUpdatedEntity(EntityManager em) {
        Staff staff = new Staff().staffType(UPDATED_STAFF_TYPE).firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
        return staff;
    }

    @BeforeEach
    public void initTest() {
        staff = createEntity(em);
    }

    @Test
    @Transactional
    void createStaff() throws Exception {
        int databaseSizeBeforeCreate = staffRepository.findAll().size();
        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);
        restStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(staffDTO)))
            .andExpect(status().isCreated());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeCreate + 1);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getStaffType()).isEqualTo(DEFAULT_STAFF_TYPE);
        assertThat(testStaff.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testStaff.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void createStaffWithExistingId() throws Exception {
        // Create the Staff with an existing ID
        staff.setId(1L);
        StaffDTO staffDTO = staffMapper.toDto(staff);

        int databaseSizeBeforeCreate = staffRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(staffDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStaff() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staff.getId().intValue())))
            .andExpect(jsonPath("$.[*].staffType").value(hasItem(DEFAULT_STAFF_TYPE.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStaffWithEagerRelationshipsIsEnabled() throws Exception {
        when(staffServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStaffMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(staffServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStaffWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(staffServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStaffMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(staffRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStaff() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get the staff
        restStaffMockMvc
            .perform(get(ENTITY_API_URL_ID, staff.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(staff.getId().intValue()))
            .andExpect(jsonPath("$.staffType").value(DEFAULT_STAFF_TYPE.toString()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME));
    }

    @Test
    @Transactional
    void getStaffByIdFiltering() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        Long id = staff.getId();

        defaultStaffShouldBeFound("id.equals=" + id);
        defaultStaffShouldNotBeFound("id.notEquals=" + id);

        defaultStaffShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStaffShouldNotBeFound("id.greaterThan=" + id);

        defaultStaffShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStaffShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStaffByStaffTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where staffType equals to DEFAULT_STAFF_TYPE
        defaultStaffShouldBeFound("staffType.equals=" + DEFAULT_STAFF_TYPE);

        // Get all the staffList where staffType equals to UPDATED_STAFF_TYPE
        defaultStaffShouldNotBeFound("staffType.equals=" + UPDATED_STAFF_TYPE);
    }

    @Test
    @Transactional
    void getAllStaffByStaffTypeIsInShouldWork() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where staffType in DEFAULT_STAFF_TYPE or UPDATED_STAFF_TYPE
        defaultStaffShouldBeFound("staffType.in=" + DEFAULT_STAFF_TYPE + "," + UPDATED_STAFF_TYPE);

        // Get all the staffList where staffType equals to UPDATED_STAFF_TYPE
        defaultStaffShouldNotBeFound("staffType.in=" + UPDATED_STAFF_TYPE);
    }

    @Test
    @Transactional
    void getAllStaffByStaffTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where staffType is not null
        defaultStaffShouldBeFound("staffType.specified=true");

        // Get all the staffList where staffType is null
        defaultStaffShouldNotBeFound("staffType.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where firstName equals to DEFAULT_FIRST_NAME
        defaultStaffShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the staffList where firstName equals to UPDATED_FIRST_NAME
        defaultStaffShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultStaffShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the staffList where firstName equals to UPDATED_FIRST_NAME
        defaultStaffShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where firstName is not null
        defaultStaffShouldBeFound("firstName.specified=true");

        // Get all the staffList where firstName is null
        defaultStaffShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where firstName contains DEFAULT_FIRST_NAME
        defaultStaffShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the staffList where firstName contains UPDATED_FIRST_NAME
        defaultStaffShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where firstName does not contain DEFAULT_FIRST_NAME
        defaultStaffShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the staffList where firstName does not contain UPDATED_FIRST_NAME
        defaultStaffShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where lastName equals to DEFAULT_LAST_NAME
        defaultStaffShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the staffList where lastName equals to UPDATED_LAST_NAME
        defaultStaffShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultStaffShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the staffList where lastName equals to UPDATED_LAST_NAME
        defaultStaffShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where lastName is not null
        defaultStaffShouldBeFound("lastName.specified=true");

        // Get all the staffList where lastName is null
        defaultStaffShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByLastNameContainsSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where lastName contains DEFAULT_LAST_NAME
        defaultStaffShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the staffList where lastName contains UPDATED_LAST_NAME
        defaultStaffShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        // Get all the staffList where lastName does not contain DEFAULT_LAST_NAME
        defaultStaffShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the staffList where lastName does not contain UPDATED_LAST_NAME
        defaultStaffShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByPrisonIsEqualToSomething() throws Exception {
        Prison prison;
        if (TestUtil.findAll(em, Prison.class).isEmpty()) {
            staffRepository.saveAndFlush(staff);
            prison = PrisonResourceIT.createEntity(em);
        } else {
            prison = TestUtil.findAll(em, Prison.class).get(0);
        }
        em.persist(prison);
        em.flush();
        staff.setPrison(prison);
        staffRepository.saveAndFlush(staff);
        Long prisonId = prison.getId();

        // Get all the staffList where prison equals to prisonId
        defaultStaffShouldBeFound("prisonId.equals=" + prisonId);

        // Get all the staffList where prison equals to (prisonId + 1)
        defaultStaffShouldNotBeFound("prisonId.equals=" + (prisonId + 1));
    }

    @Test
    @Transactional
    void getAllStaffByActivityIsEqualToSomething() throws Exception {
        Activity activity;
        if (TestUtil.findAll(em, Activity.class).isEmpty()) {
            staffRepository.saveAndFlush(staff);
            activity = ActivityResourceIT.createEntity(em);
        } else {
            activity = TestUtil.findAll(em, Activity.class).get(0);
        }
        em.persist(activity);
        em.flush();
        staff.addActivity(activity);
        staffRepository.saveAndFlush(staff);
        Long activityId = activity.getId();

        // Get all the staffList where activity equals to activityId
        defaultStaffShouldBeFound("activityId.equals=" + activityId);

        // Get all the staffList where activity equals to (activityId + 1)
        defaultStaffShouldNotBeFound("activityId.equals=" + (activityId + 1));
    }

    @Test
    @Transactional
    void getAllStaffByAssignedAreasIsEqualToSomething() throws Exception {
        Area assignedAreas;
        if (TestUtil.findAll(em, Area.class).isEmpty()) {
            staffRepository.saveAndFlush(staff);
            assignedAreas = AreaResourceIT.createEntity(em);
        } else {
            assignedAreas = TestUtil.findAll(em, Area.class).get(0);
        }
        em.persist(assignedAreas);
        em.flush();
        staff.addAssignedAreas(assignedAreas);
        staffRepository.saveAndFlush(staff);
        Long assignedAreasId = assignedAreas.getId();

        // Get all the staffList where assignedAreas equals to assignedAreasId
        defaultStaffShouldBeFound("assignedAreasId.equals=" + assignedAreasId);

        // Get all the staffList where assignedAreas equals to (assignedAreasId + 1)
        defaultStaffShouldNotBeFound("assignedAreasId.equals=" + (assignedAreasId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStaffShouldBeFound(String filter) throws Exception {
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staff.getId().intValue())))
            .andExpect(jsonPath("$.[*].staffType").value(hasItem(DEFAULT_STAFF_TYPE.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)));

        // Check, that the count call also returns 1
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStaffShouldNotBeFound(String filter) throws Exception {
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStaff() throws Exception {
        // Get the staff
        restStaffMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStaff() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        int databaseSizeBeforeUpdate = staffRepository.findAll().size();

        // Update the staff
        Staff updatedStaff = staffRepository.findById(staff.getId()).get();
        // Disconnect from session so that the updates on updatedStaff are not directly saved in db
        em.detach(updatedStaff);
        updatedStaff.staffType(UPDATED_STAFF_TYPE).firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
        StaffDTO staffDTO = staffMapper.toDto(updatedStaff);

        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getStaffType()).isEqualTo(UPDATED_STAFF_TYPE);
        assertThat(testStaff.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStaff.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void putNonExistingStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(staffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        int databaseSizeBeforeUpdate = staffRepository.findAll().size();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStaff))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getStaffType()).isEqualTo(DEFAULT_STAFF_TYPE);
        assertThat(testStaff.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStaff.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void fullUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        int databaseSizeBeforeUpdate = staffRepository.findAll().size();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff.staffType(UPDATED_STAFF_TYPE).firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStaff))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getStaffType()).isEqualTo(UPDATED_STAFF_TYPE);
        assertThat(testStaff.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStaff.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, staffDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(staffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStaff() throws Exception {
        // Initialize the database
        staffRepository.saveAndFlush(staff);

        int databaseSizeBeforeDelete = staffRepository.findAll().size();

        // Delete the staff
        restStaffMockMvc
            .perform(delete(ENTITY_API_URL_ID, staff.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Staff> staffList = staffRepository.findAll();
        assertThat(staffList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
