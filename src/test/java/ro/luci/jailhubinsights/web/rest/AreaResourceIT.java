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
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.domain.enumeration.AreaType;
import ro.luci.jailhubinsights.repository.AreaRepository;
import ro.luci.jailhubinsights.service.AreaService;
import ro.luci.jailhubinsights.service.criteria.AreaCriteria;
import ro.luci.jailhubinsights.service.dto.AreaDTO;
import ro.luci.jailhubinsights.service.mapper.AreaMapper;

/**
 * Integration tests for the {@link AreaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AreaResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_AREA_SIZE = 1D;
    private static final Double UPDATED_AREA_SIZE = 2D;
    private static final Double SMALLER_AREA_SIZE = 1D - 1D;

    private static final AreaType DEFAULT_AREA_TYPE = AreaType.CELL;
    private static final AreaType UPDATED_AREA_TYPE = AreaType.CELL_BLOCK;

    private static final String ENTITY_API_URL = "/api/areas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AreaRepository areaRepository;

    @Mock
    private AreaRepository areaRepositoryMock;

    @Autowired
    private AreaMapper areaMapper;

    @Mock
    private AreaService areaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAreaMockMvc;

    private Area area;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Area createEntity(EntityManager em) {
        Area area = new Area().name(DEFAULT_NAME).areaSize(DEFAULT_AREA_SIZE).areaType(DEFAULT_AREA_TYPE);
        return area;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Area createUpdatedEntity(EntityManager em) {
        Area area = new Area().name(UPDATED_NAME).areaSize(UPDATED_AREA_SIZE).areaType(UPDATED_AREA_TYPE);
        return area;
    }

    @BeforeEach
    public void initTest() {
        area = createEntity(em);
    }

    @Test
    @Transactional
    void createArea() throws Exception {
        int databaseSizeBeforeCreate = areaRepository.findAll().size();
        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);
        restAreaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(areaDTO)))
            .andExpect(status().isCreated());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeCreate + 1);
        Area testArea = areaList.get(areaList.size() - 1);
        assertThat(testArea.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArea.getAreaSize()).isEqualTo(DEFAULT_AREA_SIZE);
        assertThat(testArea.getAreaType()).isEqualTo(DEFAULT_AREA_TYPE);
    }

    @Test
    @Transactional
    void createAreaWithExistingId() throws Exception {
        // Create the Area with an existing ID
        area.setId(1L);
        AreaDTO areaDTO = areaMapper.toDto(area);

        int databaseSizeBeforeCreate = areaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAreaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(areaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAreas() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList
        restAreaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(area.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].areaSize").value(hasItem(DEFAULT_AREA_SIZE.doubleValue())))
            .andExpect(jsonPath("$.[*].areaType").value(hasItem(DEFAULT_AREA_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAreasWithEagerRelationshipsIsEnabled() throws Exception {
        when(areaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAreaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(areaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAreasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(areaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAreaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(areaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getArea() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get the area
        restAreaMockMvc
            .perform(get(ENTITY_API_URL_ID, area.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(area.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.areaSize").value(DEFAULT_AREA_SIZE.doubleValue()))
            .andExpect(jsonPath("$.areaType").value(DEFAULT_AREA_TYPE.toString()));
    }

    @Test
    @Transactional
    void getAreasByIdFiltering() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        Long id = area.getId();

        defaultAreaShouldBeFound("id.equals=" + id);
        defaultAreaShouldNotBeFound("id.notEquals=" + id);

        defaultAreaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAreaShouldNotBeFound("id.greaterThan=" + id);

        defaultAreaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAreaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAreasByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where name equals to DEFAULT_NAME
        defaultAreaShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the areaList where name equals to UPDATED_NAME
        defaultAreaShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAreasByNameIsInShouldWork() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where name in DEFAULT_NAME or UPDATED_NAME
        defaultAreaShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the areaList where name equals to UPDATED_NAME
        defaultAreaShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAreasByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where name is not null
        defaultAreaShouldBeFound("name.specified=true");

        // Get all the areaList where name is null
        defaultAreaShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllAreasByNameContainsSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where name contains DEFAULT_NAME
        defaultAreaShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the areaList where name contains UPDATED_NAME
        defaultAreaShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAreasByNameNotContainsSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where name does not contain DEFAULT_NAME
        defaultAreaShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the areaList where name does not contain UPDATED_NAME
        defaultAreaShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAreasByAreaSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaSize equals to DEFAULT_AREA_SIZE
        defaultAreaShouldBeFound("areaSize.equals=" + DEFAULT_AREA_SIZE);

        // Get all the areaList where areaSize equals to UPDATED_AREA_SIZE
        defaultAreaShouldNotBeFound("areaSize.equals=" + UPDATED_AREA_SIZE);
    }

    @Test
    @Transactional
    void getAllAreasByAreaSizeIsInShouldWork() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaSize in DEFAULT_AREA_SIZE or UPDATED_AREA_SIZE
        defaultAreaShouldBeFound("areaSize.in=" + DEFAULT_AREA_SIZE + "," + UPDATED_AREA_SIZE);

        // Get all the areaList where areaSize equals to UPDATED_AREA_SIZE
        defaultAreaShouldNotBeFound("areaSize.in=" + UPDATED_AREA_SIZE);
    }

    @Test
    @Transactional
    void getAllAreasByAreaSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaSize is not null
        defaultAreaShouldBeFound("areaSize.specified=true");

        // Get all the areaList where areaSize is null
        defaultAreaShouldNotBeFound("areaSize.specified=false");
    }

    @Test
    @Transactional
    void getAllAreasByAreaSizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaSize is greater than or equal to DEFAULT_AREA_SIZE
        defaultAreaShouldBeFound("areaSize.greaterThanOrEqual=" + DEFAULT_AREA_SIZE);

        // Get all the areaList where areaSize is greater than or equal to UPDATED_AREA_SIZE
        defaultAreaShouldNotBeFound("areaSize.greaterThanOrEqual=" + UPDATED_AREA_SIZE);
    }

    @Test
    @Transactional
    void getAllAreasByAreaSizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaSize is less than or equal to DEFAULT_AREA_SIZE
        defaultAreaShouldBeFound("areaSize.lessThanOrEqual=" + DEFAULT_AREA_SIZE);

        // Get all the areaList where areaSize is less than or equal to SMALLER_AREA_SIZE
        defaultAreaShouldNotBeFound("areaSize.lessThanOrEqual=" + SMALLER_AREA_SIZE);
    }

    @Test
    @Transactional
    void getAllAreasByAreaSizeIsLessThanSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaSize is less than DEFAULT_AREA_SIZE
        defaultAreaShouldNotBeFound("areaSize.lessThan=" + DEFAULT_AREA_SIZE);

        // Get all the areaList where areaSize is less than UPDATED_AREA_SIZE
        defaultAreaShouldBeFound("areaSize.lessThan=" + UPDATED_AREA_SIZE);
    }

    @Test
    @Transactional
    void getAllAreasByAreaSizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaSize is greater than DEFAULT_AREA_SIZE
        defaultAreaShouldNotBeFound("areaSize.greaterThan=" + DEFAULT_AREA_SIZE);

        // Get all the areaList where areaSize is greater than SMALLER_AREA_SIZE
        defaultAreaShouldBeFound("areaSize.greaterThan=" + SMALLER_AREA_SIZE);
    }

    @Test
    @Transactional
    void getAllAreasByAreaTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaType equals to DEFAULT_AREA_TYPE
        defaultAreaShouldBeFound("areaType.equals=" + DEFAULT_AREA_TYPE);

        // Get all the areaList where areaType equals to UPDATED_AREA_TYPE
        defaultAreaShouldNotBeFound("areaType.equals=" + UPDATED_AREA_TYPE);
    }

    @Test
    @Transactional
    void getAllAreasByAreaTypeIsInShouldWork() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaType in DEFAULT_AREA_TYPE or UPDATED_AREA_TYPE
        defaultAreaShouldBeFound("areaType.in=" + DEFAULT_AREA_TYPE + "," + UPDATED_AREA_TYPE);

        // Get all the areaList where areaType equals to UPDATED_AREA_TYPE
        defaultAreaShouldNotBeFound("areaType.in=" + UPDATED_AREA_TYPE);
    }

    @Test
    @Transactional
    void getAllAreasByAreaTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        // Get all the areaList where areaType is not null
        defaultAreaShouldBeFound("areaType.specified=true");

        // Get all the areaList where areaType is null
        defaultAreaShouldNotBeFound("areaType.specified=false");
    }

    @Test
    @Transactional
    void getAllAreasByPrisonIsEqualToSomething() throws Exception {
        Prison prison;
        if (TestUtil.findAll(em, Prison.class).isEmpty()) {
            areaRepository.saveAndFlush(area);
            prison = PrisonResourceIT.createEntity(em);
        } else {
            prison = TestUtil.findAll(em, Prison.class).get(0);
        }
        em.persist(prison);
        em.flush();
        area.setPrison(prison);
        areaRepository.saveAndFlush(area);
        Long prisonId = prison.getId();

        // Get all the areaList where prison equals to prisonId
        defaultAreaShouldBeFound("prisonId.equals=" + prisonId);

        // Get all the areaList where prison equals to (prisonId + 1)
        defaultAreaShouldNotBeFound("prisonId.equals=" + (prisonId + 1));
    }

    @Test
    @Transactional
    void getAllAreasByAssignedStaffAreasIsEqualToSomething() throws Exception {
        Staff assignedStaffAreas;
        if (TestUtil.findAll(em, Staff.class).isEmpty()) {
            areaRepository.saveAndFlush(area);
            assignedStaffAreas = StaffResourceIT.createEntity(em);
        } else {
            assignedStaffAreas = TestUtil.findAll(em, Staff.class).get(0);
        }
        em.persist(assignedStaffAreas);
        em.flush();
        area.addAssignedStaffAreas(assignedStaffAreas);
        areaRepository.saveAndFlush(area);
        Long assignedStaffAreasId = assignedStaffAreas.getId();

        // Get all the areaList where assignedStaffAreas equals to assignedStaffAreasId
        defaultAreaShouldBeFound("assignedStaffAreasId.equals=" + assignedStaffAreasId);

        // Get all the areaList where assignedStaffAreas equals to (assignedStaffAreasId + 1)
        defaultAreaShouldNotBeFound("assignedStaffAreasId.equals=" + (assignedStaffAreasId + 1));
    }

    @Test
    @Transactional
    void getAllAreasByComposedOfAreasIsEqualToSomething() throws Exception {
        Area composedOfAreas;
        if (TestUtil.findAll(em, Area.class).isEmpty()) {
            areaRepository.saveAndFlush(area);
            composedOfAreas = AreaResourceIT.createEntity(em);
        } else {
            composedOfAreas = TestUtil.findAll(em, Area.class).get(0);
        }
        em.persist(composedOfAreas);
        em.flush();
        area.addComposedOfAreas(composedOfAreas);
        areaRepository.saveAndFlush(area);
        Long composedOfAreasId = composedOfAreas.getId();

        // Get all the areaList where composedOfAreas equals to composedOfAreasId
        defaultAreaShouldBeFound("composedOfAreasId.equals=" + composedOfAreasId);

        // Get all the areaList where composedOfAreas equals to (composedOfAreasId + 1)
        defaultAreaShouldNotBeFound("composedOfAreasId.equals=" + (composedOfAreasId + 1));
    }

    @Test
    @Transactional
    void getAllAreasByInmateIsEqualToSomething() throws Exception {
        Inmate inmate;
        if (TestUtil.findAll(em, Inmate.class).isEmpty()) {
            areaRepository.saveAndFlush(area);
            inmate = InmateResourceIT.createEntity(em);
        } else {
            inmate = TestUtil.findAll(em, Inmate.class).get(0);
        }
        em.persist(inmate);
        em.flush();
        area.addInmate(inmate);
        areaRepository.saveAndFlush(area);
        Long inmateId = inmate.getId();

        // Get all the areaList where inmate equals to inmateId
        defaultAreaShouldBeFound("inmateId.equals=" + inmateId);

        // Get all the areaList where inmate equals to (inmateId + 1)
        defaultAreaShouldNotBeFound("inmateId.equals=" + (inmateId + 1));
    }

    @Test
    @Transactional
    void getAllAreasByComposingAreasIsEqualToSomething() throws Exception {
        Area composingAreas;
        if (TestUtil.findAll(em, Area.class).isEmpty()) {
            areaRepository.saveAndFlush(area);
            composingAreas = AreaResourceIT.createEntity(em);
        } else {
            composingAreas = TestUtil.findAll(em, Area.class).get(0);
        }
        em.persist(composingAreas);
        em.flush();
        area.addComposingAreas(composingAreas);
        areaRepository.saveAndFlush(area);
        Long composingAreasId = composingAreas.getId();

        // Get all the areaList where composingAreas equals to composingAreasId
        defaultAreaShouldBeFound("composingAreasId.equals=" + composingAreasId);

        // Get all the areaList where composingAreas equals to (composingAreasId + 1)
        defaultAreaShouldNotBeFound("composingAreasId.equals=" + (composingAreasId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAreaShouldBeFound(String filter) throws Exception {
        restAreaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(area.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].areaSize").value(hasItem(DEFAULT_AREA_SIZE.doubleValue())))
            .andExpect(jsonPath("$.[*].areaType").value(hasItem(DEFAULT_AREA_TYPE.toString())));

        // Check, that the count call also returns 1
        restAreaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAreaShouldNotBeFound(String filter) throws Exception {
        restAreaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAreaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingArea() throws Exception {
        // Get the area
        restAreaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingArea() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        int databaseSizeBeforeUpdate = areaRepository.findAll().size();

        // Update the area
        Area updatedArea = areaRepository.findById(area.getId()).get();
        // Disconnect from session so that the updates on updatedArea are not directly saved in db
        em.detach(updatedArea);
        updatedArea.name(UPDATED_NAME).areaSize(UPDATED_AREA_SIZE).areaType(UPDATED_AREA_TYPE);
        AreaDTO areaDTO = areaMapper.toDto(updatedArea);

        restAreaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, areaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(areaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
        Area testArea = areaList.get(areaList.size() - 1);
        assertThat(testArea.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArea.getAreaSize()).isEqualTo(UPDATED_AREA_SIZE);
        assertThat(testArea.getAreaType()).isEqualTo(UPDATED_AREA_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAreaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, areaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(areaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAreaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(areaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAreaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(areaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAreaWithPatch() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        int databaseSizeBeforeUpdate = areaRepository.findAll().size();

        // Update the area using partial update
        Area partialUpdatedArea = new Area();
        partialUpdatedArea.setId(area.getId());

        partialUpdatedArea.name(UPDATED_NAME).areaSize(UPDATED_AREA_SIZE);

        restAreaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArea.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArea))
            )
            .andExpect(status().isOk());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
        Area testArea = areaList.get(areaList.size() - 1);
        assertThat(testArea.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArea.getAreaSize()).isEqualTo(UPDATED_AREA_SIZE);
        assertThat(testArea.getAreaType()).isEqualTo(DEFAULT_AREA_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateAreaWithPatch() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        int databaseSizeBeforeUpdate = areaRepository.findAll().size();

        // Update the area using partial update
        Area partialUpdatedArea = new Area();
        partialUpdatedArea.setId(area.getId());

        partialUpdatedArea.name(UPDATED_NAME).areaSize(UPDATED_AREA_SIZE).areaType(UPDATED_AREA_TYPE);

        restAreaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArea.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArea))
            )
            .andExpect(status().isOk());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
        Area testArea = areaList.get(areaList.size() - 1);
        assertThat(testArea.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArea.getAreaSize()).isEqualTo(UPDATED_AREA_SIZE);
        assertThat(testArea.getAreaType()).isEqualTo(UPDATED_AREA_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAreaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, areaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(areaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAreaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(areaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAreaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(areaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteArea() throws Exception {
        // Initialize the database
        areaRepository.saveAndFlush(area);

        int databaseSizeBeforeDelete = areaRepository.findAll().size();

        // Delete the area
        restAreaMockMvc
            .perform(delete(ENTITY_API_URL_ID, area.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Area> areaList = areaRepository.findAll();
        assertThat(areaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
