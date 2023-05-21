package ro.luci.jailhubinsights.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
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
import ro.luci.jailhubinsights.domain.Inmate;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.repository.InmateRepository;
import ro.luci.jailhubinsights.service.InmateService;
import ro.luci.jailhubinsights.service.criteria.InmateCriteria;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.service.mapper.InmateMapper;

/**
 * Integration tests for the {@link InmateResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InmateResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_OF_BIRTH = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_OF_INCARCERATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_INCARCERATION = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_OF_INCARCERATION = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_OF_EXPECTED_RELEASE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_EXPECTED_RELEASE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_OF_EXPECTED_RELEASE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/inmates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InmateRepository inmateRepository;

    @Mock
    private InmateRepository inmateRepositoryMock;

    @Autowired
    private InmateMapper inmateMapper;

    @Mock
    private InmateService inmateServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInmateMockMvc;

    private Inmate inmate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inmate createEntity(EntityManager em) {
        Inmate inmate = new Inmate()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
            .dateOfIncarceration(DEFAULT_DATE_OF_INCARCERATION)
            .dateOfExpectedRelease(DEFAULT_DATE_OF_EXPECTED_RELEASE);
        return inmate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inmate createUpdatedEntity(EntityManager em) {
        Inmate inmate = new Inmate()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .dateOfIncarceration(UPDATED_DATE_OF_INCARCERATION)
            .dateOfExpectedRelease(UPDATED_DATE_OF_EXPECTED_RELEASE);
        return inmate;
    }

    @BeforeEach
    public void initTest() {
        inmate = createEntity(em);
    }

    @Test
    @Transactional
    void createInmate() throws Exception {
        int databaseSizeBeforeCreate = inmateRepository.findAll().size();
        // Create the Inmate
        InmateDTO inmateDTO = inmateMapper.toDto(inmate);
        restInmateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inmateDTO)))
            .andExpect(status().isCreated());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeCreate + 1);
        Inmate testInmate = inmateList.get(inmateList.size() - 1);
        assertThat(testInmate.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testInmate.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testInmate.getDateOfBirth()).isEqualTo(DEFAULT_DATE_OF_BIRTH);
        assertThat(testInmate.getDateOfIncarceration()).isEqualTo(DEFAULT_DATE_OF_INCARCERATION);
        assertThat(testInmate.getDateOfExpectedRelease()).isEqualTo(DEFAULT_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void createInmateWithExistingId() throws Exception {
        // Create the Inmate with an existing ID
        inmate.setId(1L);
        InmateDTO inmateDTO = inmateMapper.toDto(inmate);

        int databaseSizeBeforeCreate = inmateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInmateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inmateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInmates() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList
        restInmateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inmate.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].dateOfIncarceration").value(hasItem(DEFAULT_DATE_OF_INCARCERATION.toString())))
            .andExpect(jsonPath("$.[*].dateOfExpectedRelease").value(hasItem(DEFAULT_DATE_OF_EXPECTED_RELEASE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInmatesWithEagerRelationshipsIsEnabled() throws Exception {
        when(inmateServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInmateMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inmateServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInmatesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inmateServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInmateMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inmateRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInmate() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get the inmate
        restInmateMockMvc
            .perform(get(ENTITY_API_URL_ID, inmate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inmate.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.dateOfBirth").value(DEFAULT_DATE_OF_BIRTH.toString()))
            .andExpect(jsonPath("$.dateOfIncarceration").value(DEFAULT_DATE_OF_INCARCERATION.toString()))
            .andExpect(jsonPath("$.dateOfExpectedRelease").value(DEFAULT_DATE_OF_EXPECTED_RELEASE.toString()));
    }

    @Test
    @Transactional
    void getInmatesByIdFiltering() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        Long id = inmate.getId();

        defaultInmateShouldBeFound("id.equals=" + id);
        defaultInmateShouldNotBeFound("id.notEquals=" + id);

        defaultInmateShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInmateShouldNotBeFound("id.greaterThan=" + id);

        defaultInmateShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInmateShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInmatesByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where firstName equals to DEFAULT_FIRST_NAME
        defaultInmateShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the inmateList where firstName equals to UPDATED_FIRST_NAME
        defaultInmateShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllInmatesByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultInmateShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the inmateList where firstName equals to UPDATED_FIRST_NAME
        defaultInmateShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllInmatesByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where firstName is not null
        defaultInmateShouldBeFound("firstName.specified=true");

        // Get all the inmateList where firstName is null
        defaultInmateShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllInmatesByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where firstName contains DEFAULT_FIRST_NAME
        defaultInmateShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the inmateList where firstName contains UPDATED_FIRST_NAME
        defaultInmateShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllInmatesByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where firstName does not contain DEFAULT_FIRST_NAME
        defaultInmateShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the inmateList where firstName does not contain UPDATED_FIRST_NAME
        defaultInmateShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllInmatesByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where lastName equals to DEFAULT_LAST_NAME
        defaultInmateShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the inmateList where lastName equals to UPDATED_LAST_NAME
        defaultInmateShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllInmatesByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultInmateShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the inmateList where lastName equals to UPDATED_LAST_NAME
        defaultInmateShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllInmatesByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where lastName is not null
        defaultInmateShouldBeFound("lastName.specified=true");

        // Get all the inmateList where lastName is null
        defaultInmateShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllInmatesByLastNameContainsSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where lastName contains DEFAULT_LAST_NAME
        defaultInmateShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the inmateList where lastName contains UPDATED_LAST_NAME
        defaultInmateShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllInmatesByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where lastName does not contain DEFAULT_LAST_NAME
        defaultInmateShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the inmateList where lastName does not contain UPDATED_LAST_NAME
        defaultInmateShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfBirthIsEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfBirth equals to DEFAULT_DATE_OF_BIRTH
        defaultInmateShouldBeFound("dateOfBirth.equals=" + DEFAULT_DATE_OF_BIRTH);

        // Get all the inmateList where dateOfBirth equals to UPDATED_DATE_OF_BIRTH
        defaultInmateShouldNotBeFound("dateOfBirth.equals=" + UPDATED_DATE_OF_BIRTH);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfBirthIsInShouldWork() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfBirth in DEFAULT_DATE_OF_BIRTH or UPDATED_DATE_OF_BIRTH
        defaultInmateShouldBeFound("dateOfBirth.in=" + DEFAULT_DATE_OF_BIRTH + "," + UPDATED_DATE_OF_BIRTH);

        // Get all the inmateList where dateOfBirth equals to UPDATED_DATE_OF_BIRTH
        defaultInmateShouldNotBeFound("dateOfBirth.in=" + UPDATED_DATE_OF_BIRTH);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfBirthIsNullOrNotNull() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfBirth is not null
        defaultInmateShouldBeFound("dateOfBirth.specified=true");

        // Get all the inmateList where dateOfBirth is null
        defaultInmateShouldNotBeFound("dateOfBirth.specified=false");
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfBirthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfBirth is greater than or equal to DEFAULT_DATE_OF_BIRTH
        defaultInmateShouldBeFound("dateOfBirth.greaterThanOrEqual=" + DEFAULT_DATE_OF_BIRTH);

        // Get all the inmateList where dateOfBirth is greater than or equal to UPDATED_DATE_OF_BIRTH
        defaultInmateShouldNotBeFound("dateOfBirth.greaterThanOrEqual=" + UPDATED_DATE_OF_BIRTH);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfBirthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfBirth is less than or equal to DEFAULT_DATE_OF_BIRTH
        defaultInmateShouldBeFound("dateOfBirth.lessThanOrEqual=" + DEFAULT_DATE_OF_BIRTH);

        // Get all the inmateList where dateOfBirth is less than or equal to SMALLER_DATE_OF_BIRTH
        defaultInmateShouldNotBeFound("dateOfBirth.lessThanOrEqual=" + SMALLER_DATE_OF_BIRTH);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfBirthIsLessThanSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfBirth is less than DEFAULT_DATE_OF_BIRTH
        defaultInmateShouldNotBeFound("dateOfBirth.lessThan=" + DEFAULT_DATE_OF_BIRTH);

        // Get all the inmateList where dateOfBirth is less than UPDATED_DATE_OF_BIRTH
        defaultInmateShouldBeFound("dateOfBirth.lessThan=" + UPDATED_DATE_OF_BIRTH);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfBirthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfBirth is greater than DEFAULT_DATE_OF_BIRTH
        defaultInmateShouldNotBeFound("dateOfBirth.greaterThan=" + DEFAULT_DATE_OF_BIRTH);

        // Get all the inmateList where dateOfBirth is greater than SMALLER_DATE_OF_BIRTH
        defaultInmateShouldBeFound("dateOfBirth.greaterThan=" + SMALLER_DATE_OF_BIRTH);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfIncarcerationIsEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfIncarceration equals to DEFAULT_DATE_OF_INCARCERATION
        defaultInmateShouldBeFound("dateOfIncarceration.equals=" + DEFAULT_DATE_OF_INCARCERATION);

        // Get all the inmateList where dateOfIncarceration equals to UPDATED_DATE_OF_INCARCERATION
        defaultInmateShouldNotBeFound("dateOfIncarceration.equals=" + UPDATED_DATE_OF_INCARCERATION);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfIncarcerationIsInShouldWork() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfIncarceration in DEFAULT_DATE_OF_INCARCERATION or UPDATED_DATE_OF_INCARCERATION
        defaultInmateShouldBeFound("dateOfIncarceration.in=" + DEFAULT_DATE_OF_INCARCERATION + "," + UPDATED_DATE_OF_INCARCERATION);

        // Get all the inmateList where dateOfIncarceration equals to UPDATED_DATE_OF_INCARCERATION
        defaultInmateShouldNotBeFound("dateOfIncarceration.in=" + UPDATED_DATE_OF_INCARCERATION);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfIncarcerationIsNullOrNotNull() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfIncarceration is not null
        defaultInmateShouldBeFound("dateOfIncarceration.specified=true");

        // Get all the inmateList where dateOfIncarceration is null
        defaultInmateShouldNotBeFound("dateOfIncarceration.specified=false");
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfIncarcerationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfIncarceration is greater than or equal to DEFAULT_DATE_OF_INCARCERATION
        defaultInmateShouldBeFound("dateOfIncarceration.greaterThanOrEqual=" + DEFAULT_DATE_OF_INCARCERATION);

        // Get all the inmateList where dateOfIncarceration is greater than or equal to UPDATED_DATE_OF_INCARCERATION
        defaultInmateShouldNotBeFound("dateOfIncarceration.greaterThanOrEqual=" + UPDATED_DATE_OF_INCARCERATION);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfIncarcerationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfIncarceration is less than or equal to DEFAULT_DATE_OF_INCARCERATION
        defaultInmateShouldBeFound("dateOfIncarceration.lessThanOrEqual=" + DEFAULT_DATE_OF_INCARCERATION);

        // Get all the inmateList where dateOfIncarceration is less than or equal to SMALLER_DATE_OF_INCARCERATION
        defaultInmateShouldNotBeFound("dateOfIncarceration.lessThanOrEqual=" + SMALLER_DATE_OF_INCARCERATION);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfIncarcerationIsLessThanSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfIncarceration is less than DEFAULT_DATE_OF_INCARCERATION
        defaultInmateShouldNotBeFound("dateOfIncarceration.lessThan=" + DEFAULT_DATE_OF_INCARCERATION);

        // Get all the inmateList where dateOfIncarceration is less than UPDATED_DATE_OF_INCARCERATION
        defaultInmateShouldBeFound("dateOfIncarceration.lessThan=" + UPDATED_DATE_OF_INCARCERATION);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfIncarcerationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfIncarceration is greater than DEFAULT_DATE_OF_INCARCERATION
        defaultInmateShouldNotBeFound("dateOfIncarceration.greaterThan=" + DEFAULT_DATE_OF_INCARCERATION);

        // Get all the inmateList where dateOfIncarceration is greater than SMALLER_DATE_OF_INCARCERATION
        defaultInmateShouldBeFound("dateOfIncarceration.greaterThan=" + SMALLER_DATE_OF_INCARCERATION);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfExpectedReleaseIsEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfExpectedRelease equals to DEFAULT_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldBeFound("dateOfExpectedRelease.equals=" + DEFAULT_DATE_OF_EXPECTED_RELEASE);

        // Get all the inmateList where dateOfExpectedRelease equals to UPDATED_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldNotBeFound("dateOfExpectedRelease.equals=" + UPDATED_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfExpectedReleaseIsInShouldWork() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfExpectedRelease in DEFAULT_DATE_OF_EXPECTED_RELEASE or UPDATED_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldBeFound("dateOfExpectedRelease.in=" + DEFAULT_DATE_OF_EXPECTED_RELEASE + "," + UPDATED_DATE_OF_EXPECTED_RELEASE);

        // Get all the inmateList where dateOfExpectedRelease equals to UPDATED_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldNotBeFound("dateOfExpectedRelease.in=" + UPDATED_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfExpectedReleaseIsNullOrNotNull() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfExpectedRelease is not null
        defaultInmateShouldBeFound("dateOfExpectedRelease.specified=true");

        // Get all the inmateList where dateOfExpectedRelease is null
        defaultInmateShouldNotBeFound("dateOfExpectedRelease.specified=false");
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfExpectedReleaseIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfExpectedRelease is greater than or equal to DEFAULT_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldBeFound("dateOfExpectedRelease.greaterThanOrEqual=" + DEFAULT_DATE_OF_EXPECTED_RELEASE);

        // Get all the inmateList where dateOfExpectedRelease is greater than or equal to UPDATED_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldNotBeFound("dateOfExpectedRelease.greaterThanOrEqual=" + UPDATED_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfExpectedReleaseIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfExpectedRelease is less than or equal to DEFAULT_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldBeFound("dateOfExpectedRelease.lessThanOrEqual=" + DEFAULT_DATE_OF_EXPECTED_RELEASE);

        // Get all the inmateList where dateOfExpectedRelease is less than or equal to SMALLER_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldNotBeFound("dateOfExpectedRelease.lessThanOrEqual=" + SMALLER_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfExpectedReleaseIsLessThanSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfExpectedRelease is less than DEFAULT_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldNotBeFound("dateOfExpectedRelease.lessThan=" + DEFAULT_DATE_OF_EXPECTED_RELEASE);

        // Get all the inmateList where dateOfExpectedRelease is less than UPDATED_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldBeFound("dateOfExpectedRelease.lessThan=" + UPDATED_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void getAllInmatesByDateOfExpectedReleaseIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        // Get all the inmateList where dateOfExpectedRelease is greater than DEFAULT_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldNotBeFound("dateOfExpectedRelease.greaterThan=" + DEFAULT_DATE_OF_EXPECTED_RELEASE);

        // Get all the inmateList where dateOfExpectedRelease is greater than SMALLER_DATE_OF_EXPECTED_RELEASE
        defaultInmateShouldBeFound("dateOfExpectedRelease.greaterThan=" + SMALLER_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void getAllInmatesByPrisonIsEqualToSomething() throws Exception {
        Prison prison;
        if (TestUtil.findAll(em, Prison.class).isEmpty()) {
            inmateRepository.saveAndFlush(inmate);
            prison = PrisonResourceIT.createEntity(em);
        } else {
            prison = TestUtil.findAll(em, Prison.class).get(0);
        }
        em.persist(prison);
        em.flush();
        inmate.setPrison(prison);
        inmateRepository.saveAndFlush(inmate);
        Long prisonId = prison.getId();

        // Get all the inmateList where prison equals to prisonId
        defaultInmateShouldBeFound("prisonId.equals=" + prisonId);

        // Get all the inmateList where prison equals to (prisonId + 1)
        defaultInmateShouldNotBeFound("prisonId.equals=" + (prisonId + 1));
    }

    @Test
    @Transactional
    void getAllInmatesByAssignedCellIsEqualToSomething() throws Exception {
        Area assignedCell;
        if (TestUtil.findAll(em, Area.class).isEmpty()) {
            inmateRepository.saveAndFlush(inmate);
            assignedCell = AreaResourceIT.createEntity(em);
        } else {
            assignedCell = TestUtil.findAll(em, Area.class).get(0);
        }
        em.persist(assignedCell);
        em.flush();
        inmate.setAssignedCell(assignedCell);
        inmateRepository.saveAndFlush(inmate);
        Long assignedCellId = assignedCell.getId();

        // Get all the inmateList where assignedCell equals to assignedCellId
        defaultInmateShouldBeFound("assignedCellId.equals=" + assignedCellId);

        // Get all the inmateList where assignedCell equals to (assignedCellId + 1)
        defaultInmateShouldNotBeFound("assignedCellId.equals=" + (assignedCellId + 1));
    }

    @Test
    @Transactional
    void getAllInmatesByActivityIsEqualToSomething() throws Exception {
        Activity activity;
        if (TestUtil.findAll(em, Activity.class).isEmpty()) {
            inmateRepository.saveAndFlush(inmate);
            activity = ActivityResourceIT.createEntity(em);
        } else {
            activity = TestUtil.findAll(em, Activity.class).get(0);
        }
        em.persist(activity);
        em.flush();
        inmate.addActivity(activity);
        inmateRepository.saveAndFlush(inmate);
        Long activityId = activity.getId();

        // Get all the inmateList where activity equals to activityId
        defaultInmateShouldBeFound("activityId.equals=" + activityId);

        // Get all the inmateList where activity equals to (activityId + 1)
        defaultInmateShouldNotBeFound("activityId.equals=" + (activityId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInmateShouldBeFound(String filter) throws Exception {
        restInmateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inmate.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].dateOfIncarceration").value(hasItem(DEFAULT_DATE_OF_INCARCERATION.toString())))
            .andExpect(jsonPath("$.[*].dateOfExpectedRelease").value(hasItem(DEFAULT_DATE_OF_EXPECTED_RELEASE.toString())));

        // Check, that the count call also returns 1
        restInmateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInmateShouldNotBeFound(String filter) throws Exception {
        restInmateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInmateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInmate() throws Exception {
        // Get the inmate
        restInmateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInmate() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();

        // Update the inmate
        Inmate updatedInmate = inmateRepository.findById(inmate.getId()).get();
        // Disconnect from session so that the updates on updatedInmate are not directly saved in db
        em.detach(updatedInmate);
        updatedInmate
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .dateOfIncarceration(UPDATED_DATE_OF_INCARCERATION)
            .dateOfExpectedRelease(UPDATED_DATE_OF_EXPECTED_RELEASE);
        InmateDTO inmateDTO = inmateMapper.toDto(updatedInmate);

        restInmateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inmateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inmateDTO))
            )
            .andExpect(status().isOk());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
        Inmate testInmate = inmateList.get(inmateList.size() - 1);
        assertThat(testInmate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testInmate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testInmate.getDateOfBirth()).isEqualTo(UPDATED_DATE_OF_BIRTH);
        assertThat(testInmate.getDateOfIncarceration()).isEqualTo(UPDATED_DATE_OF_INCARCERATION);
        assertThat(testInmate.getDateOfExpectedRelease()).isEqualTo(UPDATED_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void putNonExistingInmate() throws Exception {
        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();
        inmate.setId(count.incrementAndGet());

        // Create the Inmate
        InmateDTO inmateDTO = inmateMapper.toDto(inmate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInmateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inmateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inmateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInmate() throws Exception {
        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();
        inmate.setId(count.incrementAndGet());

        // Create the Inmate
        InmateDTO inmateDTO = inmateMapper.toDto(inmate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInmateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inmateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInmate() throws Exception {
        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();
        inmate.setId(count.incrementAndGet());

        // Create the Inmate
        InmateDTO inmateDTO = inmateMapper.toDto(inmate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInmateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inmateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInmateWithPatch() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();

        // Update the inmate using partial update
        Inmate partialUpdatedInmate = new Inmate();
        partialUpdatedInmate.setId(inmate.getId());

        partialUpdatedInmate.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restInmateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInmate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInmate))
            )
            .andExpect(status().isOk());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
        Inmate testInmate = inmateList.get(inmateList.size() - 1);
        assertThat(testInmate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testInmate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testInmate.getDateOfBirth()).isEqualTo(DEFAULT_DATE_OF_BIRTH);
        assertThat(testInmate.getDateOfIncarceration()).isEqualTo(DEFAULT_DATE_OF_INCARCERATION);
        assertThat(testInmate.getDateOfExpectedRelease()).isEqualTo(DEFAULT_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void fullUpdateInmateWithPatch() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();

        // Update the inmate using partial update
        Inmate partialUpdatedInmate = new Inmate();
        partialUpdatedInmate.setId(inmate.getId());

        partialUpdatedInmate
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .dateOfIncarceration(UPDATED_DATE_OF_INCARCERATION)
            .dateOfExpectedRelease(UPDATED_DATE_OF_EXPECTED_RELEASE);

        restInmateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInmate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInmate))
            )
            .andExpect(status().isOk());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
        Inmate testInmate = inmateList.get(inmateList.size() - 1);
        assertThat(testInmate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testInmate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testInmate.getDateOfBirth()).isEqualTo(UPDATED_DATE_OF_BIRTH);
        assertThat(testInmate.getDateOfIncarceration()).isEqualTo(UPDATED_DATE_OF_INCARCERATION);
        assertThat(testInmate.getDateOfExpectedRelease()).isEqualTo(UPDATED_DATE_OF_EXPECTED_RELEASE);
    }

    @Test
    @Transactional
    void patchNonExistingInmate() throws Exception {
        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();
        inmate.setId(count.incrementAndGet());

        // Create the Inmate
        InmateDTO inmateDTO = inmateMapper.toDto(inmate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInmateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inmateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inmateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInmate() throws Exception {
        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();
        inmate.setId(count.incrementAndGet());

        // Create the Inmate
        InmateDTO inmateDTO = inmateMapper.toDto(inmate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInmateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inmateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInmate() throws Exception {
        int databaseSizeBeforeUpdate = inmateRepository.findAll().size();
        inmate.setId(count.incrementAndGet());

        // Create the Inmate
        InmateDTO inmateDTO = inmateMapper.toDto(inmate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInmateMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(inmateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inmate in the database
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInmate() throws Exception {
        // Initialize the database
        inmateRepository.saveAndFlush(inmate);

        int databaseSizeBeforeDelete = inmateRepository.findAll().size();

        // Delete the inmate
        restInmateMockMvc
            .perform(delete(ENTITY_API_URL_ID, inmate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Inmate> inmateList = inmateRepository.findAll();
        assertThat(inmateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
