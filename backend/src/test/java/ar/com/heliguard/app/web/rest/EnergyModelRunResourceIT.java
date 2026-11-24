package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.EnergyModelRunAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import ar.com.heliguard.app.repository.EnergyModelRunRepository;
import ar.com.heliguard.app.service.EnergyModelRunService;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import ar.com.heliguard.app.service.mapper.EnergyModelRunMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link EnergyModelRunResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EnergyModelRunResourceIT {

    private static final String DEFAULT_MODEL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_VERSION = "BBBBBBBBBB";

    private static final Instant DEFAULT_PERIOD_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PERIOD_START = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_PERIOD_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PERIOD_END = Instant.ofEpochMilli(1795370143700L);

    private static final EnergyModelRunStatus DEFAULT_STATUS = EnergyModelRunStatus.SUCCEEDED;
    private static final EnergyModelRunStatus UPDATED_STATUS = EnergyModelRunStatus.INSUFFICIENT_DATA;

    private static final BigDecimal DEFAULT_EXPECTED_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPECTED_WH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_EXPECTED_LOWER_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPECTED_LOWER_WH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_EXPECTED_UPPER_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPECTED_UPPER_WH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ACTUAL_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_ACTUAL_WH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_COVERAGE_PCT = new BigDecimal(0);
    private static final BigDecimal UPDATED_COVERAGE_PCT = new BigDecimal(1);

    private static final String DEFAULT_INPUT_FINGERPRINT = "AAAAAAAAAA";
    private static final String UPDATED_INPUT_FINGERPRINT = "BBBBBBBBBB";

    private static final String DEFAULT_QUALITY_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_QUALITY_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CALCULATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CALCULATED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/energy-model-runs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnergyModelRunRepository energyModelRunRepository;

    @Mock
    private EnergyModelRunRepository energyModelRunRepositoryMock;

    @Autowired
    private EnergyModelRunMapper energyModelRunMapper;

    @Mock
    private EnergyModelRunService energyModelRunServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private EnergyModelRun energyModelRun;

    private EnergyModelRun insertedEnergyModelRun;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnergyModelRun createEntity(R2dbcEntityTemplate template) {
        EnergyModelRun energyModelRun = new EnergyModelRun()
            .modelName(DEFAULT_MODEL_NAME)
            .modelVersion(DEFAULT_MODEL_VERSION)
            .periodStart(DEFAULT_PERIOD_START)
            .periodEnd(DEFAULT_PERIOD_END)
            .status(DEFAULT_STATUS)
            .expectedWh(DEFAULT_EXPECTED_WH)
            .expectedLowerWh(DEFAULT_EXPECTED_LOWER_WH)
            .expectedUpperWh(DEFAULT_EXPECTED_UPPER_WH)
            .actualWh(DEFAULT_ACTUAL_WH)
            .coveragePct(DEFAULT_COVERAGE_PCT)
            .inputFingerprint(DEFAULT_INPUT_FINGERPRINT)
            .qualityNote(DEFAULT_QUALITY_NOTE)
            .calculatedAt(DEFAULT_CALCULATED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        energyModelRun.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        energyModelRun.setInstallation(installation);
        return energyModelRun;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnergyModelRun createUpdatedEntity(R2dbcEntityTemplate template) {
        EnergyModelRun updatedEnergyModelRun = new EnergyModelRun()
            .modelName(UPDATED_MODEL_NAME)
            .modelVersion(UPDATED_MODEL_VERSION)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .status(UPDATED_STATUS)
            .expectedWh(UPDATED_EXPECTED_WH)
            .expectedLowerWh(UPDATED_EXPECTED_LOWER_WH)
            .expectedUpperWh(UPDATED_EXPECTED_UPPER_WH)
            .actualWh(UPDATED_ACTUAL_WH)
            .coveragePct(UPDATED_COVERAGE_PCT)
            .inputFingerprint(UPDATED_INPUT_FINGERPRINT)
            .qualityNote(UPDATED_QUALITY_NOTE)
            .calculatedAt(UPDATED_CALCULATED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedEnergyModelRun.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedEnergyModelRun.setInstallation(installation);
        return updatedEnergyModelRun;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(EnergyModelRun.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        energyModelRun = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedEnergyModelRun != null) {
            energyModelRunRepository.delete(insertedEnergyModelRun).block();
            insertedEnergyModelRun = null;
        }
        deleteEntities(template);
    }

    @Test
    void createEnergyModelRun() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EnergyModelRun
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);
        var returnedEnergyModelRunDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EnergyModelRunDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EnergyModelRun in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEnergyModelRun = energyModelRunMapper.toEntity(returnedEnergyModelRunDTO);
        assertEnergyModelRunUpdatableFieldsEquals(returnedEnergyModelRun, getPersistedEnergyModelRun(returnedEnergyModelRun));

        insertedEnergyModelRun = returnedEnergyModelRun;
    }

    @Test
    void createEnergyModelRunWithExistingId() throws Exception {
        // Create the EnergyModelRun with an existing ID
        energyModelRun.setId(1L);
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelRun in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkModelNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelRun.setModelName(null);

        // Create the EnergyModelRun, which fails.
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkModelVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelRun.setModelVersion(null);

        // Create the EnergyModelRun, which fails.
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPeriodStartIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelRun.setPeriodStart(null);

        // Create the EnergyModelRun, which fails.
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPeriodEndIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelRun.setPeriodEnd(null);

        // Create the EnergyModelRun, which fails.
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelRun.setStatus(null);

        // Create the EnergyModelRun, which fails.
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCalculatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelRun.setCalculatedAt(null);

        // Create the EnergyModelRun, which fails.
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEnergyModelRunsAsStream() {
        // Initialize the database
        energyModelRunRepository.save(energyModelRun).block();

        List<EnergyModelRun> energyModelRunList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(EnergyModelRunDTO.class)
            .getResponseBody()
            .map(energyModelRunMapper::toEntity)
            .filter(energyModelRun::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(energyModelRunList).isNotNull();
        assertThat(energyModelRunList).hasSize(1);
        EnergyModelRun testEnergyModelRun = energyModelRunList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertEnergyModelRunAllPropertiesEquals(energyModelRun, testEnergyModelRun);
        assertEnergyModelRunUpdatableFieldsEquals(energyModelRun, testEnergyModelRun);
    }

    @Test
    void getAllEnergyModelRuns() {
        // Initialize the database
        insertedEnergyModelRun = energyModelRunRepository.save(energyModelRun).block();

        // Get all the energyModelRunList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(energyModelRun.getId().intValue()))
            .jsonPath("$.[*].modelName")
            .value(hasItem(DEFAULT_MODEL_NAME))
            .jsonPath("$.[*].modelVersion")
            .value(hasItem(DEFAULT_MODEL_VERSION))
            .jsonPath("$.[*].periodStart")
            .value(hasItem(DEFAULT_PERIOD_START.toString()))
            .jsonPath("$.[*].periodEnd")
            .value(hasItem(DEFAULT_PERIOD_END.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].expectedWh")
            .value(hasItem(sameNumber(DEFAULT_EXPECTED_WH)))
            .jsonPath("$.[*].expectedLowerWh")
            .value(hasItem(sameNumber(DEFAULT_EXPECTED_LOWER_WH)))
            .jsonPath("$.[*].expectedUpperWh")
            .value(hasItem(sameNumber(DEFAULT_EXPECTED_UPPER_WH)))
            .jsonPath("$.[*].actualWh")
            .value(hasItem(sameNumber(DEFAULT_ACTUAL_WH)))
            .jsonPath("$.[*].coveragePct")
            .value(hasItem(sameNumber(DEFAULT_COVERAGE_PCT)))
            .jsonPath("$.[*].inputFingerprint")
            .value(hasItem(DEFAULT_INPUT_FINGERPRINT))
            .jsonPath("$.[*].qualityNote")
            .value(hasItem(DEFAULT_QUALITY_NOTE))
            .jsonPath("$.[*].calculatedAt")
            .value(hasItem(DEFAULT_CALCULATED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnergyModelRunsWithEagerRelationshipsIsEnabled() {
        when(energyModelRunServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(energyModelRunServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnergyModelRunsWithEagerRelationshipsIsNotEnabled() {
        when(energyModelRunServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(energyModelRunRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEnergyModelRun() {
        // Initialize the database
        insertedEnergyModelRun = energyModelRunRepository.save(energyModelRun).block();

        // Get the energyModelRun
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, energyModelRun.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(energyModelRun.getId().intValue()))
            .jsonPath("$.modelName")
            .value(is(DEFAULT_MODEL_NAME))
            .jsonPath("$.modelVersion")
            .value(is(DEFAULT_MODEL_VERSION))
            .jsonPath("$.periodStart")
            .value(is(DEFAULT_PERIOD_START.toString()))
            .jsonPath("$.periodEnd")
            .value(is(DEFAULT_PERIOD_END.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.expectedWh")
            .value(is(sameNumber(DEFAULT_EXPECTED_WH)))
            .jsonPath("$.expectedLowerWh")
            .value(is(sameNumber(DEFAULT_EXPECTED_LOWER_WH)))
            .jsonPath("$.expectedUpperWh")
            .value(is(sameNumber(DEFAULT_EXPECTED_UPPER_WH)))
            .jsonPath("$.actualWh")
            .value(is(sameNumber(DEFAULT_ACTUAL_WH)))
            .jsonPath("$.coveragePct")
            .value(is(sameNumber(DEFAULT_COVERAGE_PCT)))
            .jsonPath("$.inputFingerprint")
            .value(is(DEFAULT_INPUT_FINGERPRINT))
            .jsonPath("$.qualityNote")
            .value(is(DEFAULT_QUALITY_NOTE))
            .jsonPath("$.calculatedAt")
            .value(is(DEFAULT_CALCULATED_AT.toString()));
    }

    @Test
    void getNonExistingEnergyModelRun() {
        // Get the energyModelRun
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEnergyModelRun() throws Exception {
        // Initialize the database
        insertedEnergyModelRun = energyModelRunRepository.save(energyModelRun).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the energyModelRun
        EnergyModelRun updatedEnergyModelRun = energyModelRunRepository.findById(energyModelRun.getId()).block();
        updatedEnergyModelRun
            .modelName(UPDATED_MODEL_NAME)
            .modelVersion(UPDATED_MODEL_VERSION)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .status(UPDATED_STATUS)
            .expectedWh(UPDATED_EXPECTED_WH)
            .expectedLowerWh(UPDATED_EXPECTED_LOWER_WH)
            .expectedUpperWh(UPDATED_EXPECTED_UPPER_WH)
            .actualWh(UPDATED_ACTUAL_WH)
            .coveragePct(UPDATED_COVERAGE_PCT)
            .inputFingerprint(UPDATED_INPUT_FINGERPRINT)
            .qualityNote(UPDATED_QUALITY_NOTE)
            .calculatedAt(UPDATED_CALCULATED_AT);
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(updatedEnergyModelRun);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, energyModelRunDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnergyModelRun in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnergyModelRunToMatchAllProperties(updatedEnergyModelRun);
    }

    @Test
    void putNonExistingEnergyModelRun() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelRun.setId(longCount.incrementAndGet());

        // Create the EnergyModelRun
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, energyModelRunDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelRun in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEnergyModelRun() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelRun.setId(longCount.incrementAndGet());

        // Create the EnergyModelRun
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelRun in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEnergyModelRun() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelRun.setId(longCount.incrementAndGet());

        // Create the EnergyModelRun
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EnergyModelRun in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEnergyModelRunWithPatch() throws Exception {
        // Initialize the database
        insertedEnergyModelRun = energyModelRunRepository.save(energyModelRun).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the energyModelRun using partial update
        EnergyModelRun partialUpdatedEnergyModelRun = new EnergyModelRun();
        partialUpdatedEnergyModelRun.setId(energyModelRun.getId());

        partialUpdatedEnergyModelRun
            .periodEnd(UPDATED_PERIOD_END)
            .expectedLowerWh(UPDATED_EXPECTED_LOWER_WH)
            .expectedUpperWh(UPDATED_EXPECTED_UPPER_WH)
            .coveragePct(UPDATED_COVERAGE_PCT)
            .qualityNote(UPDATED_QUALITY_NOTE)
            .calculatedAt(UPDATED_CALCULATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnergyModelRun.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEnergyModelRun))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnergyModelRun in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnergyModelRunUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnergyModelRun, energyModelRun),
            getPersistedEnergyModelRun(energyModelRun)
        );
    }

    @Test
    void fullUpdateEnergyModelRunWithPatch() throws Exception {
        // Initialize the database
        insertedEnergyModelRun = energyModelRunRepository.save(energyModelRun).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the energyModelRun using partial update
        EnergyModelRun partialUpdatedEnergyModelRun = new EnergyModelRun();
        partialUpdatedEnergyModelRun.setId(energyModelRun.getId());

        partialUpdatedEnergyModelRun
            .modelName(UPDATED_MODEL_NAME)
            .modelVersion(UPDATED_MODEL_VERSION)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .status(UPDATED_STATUS)
            .expectedWh(UPDATED_EXPECTED_WH)
            .expectedLowerWh(UPDATED_EXPECTED_LOWER_WH)
            .expectedUpperWh(UPDATED_EXPECTED_UPPER_WH)
            .actualWh(UPDATED_ACTUAL_WH)
            .coveragePct(UPDATED_COVERAGE_PCT)
            .inputFingerprint(UPDATED_INPUT_FINGERPRINT)
            .qualityNote(UPDATED_QUALITY_NOTE)
            .calculatedAt(UPDATED_CALCULATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnergyModelRun.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEnergyModelRun))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnergyModelRun in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnergyModelRunUpdatableFieldsEquals(partialUpdatedEnergyModelRun, getPersistedEnergyModelRun(partialUpdatedEnergyModelRun));
    }

    @Test
    void patchNonExistingEnergyModelRun() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelRun.setId(longCount.incrementAndGet());

        // Create the EnergyModelRun
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, energyModelRunDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelRun in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEnergyModelRun() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelRun.setId(longCount.incrementAndGet());

        // Create the EnergyModelRun
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelRun in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEnergyModelRun() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelRun.setId(longCount.incrementAndGet());

        // Create the EnergyModelRun
        EnergyModelRunDTO energyModelRunDTO = energyModelRunMapper.toDto(energyModelRun);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(energyModelRunDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EnergyModelRun in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEnergyModelRun() {
        // Initialize the database
        insertedEnergyModelRun = energyModelRunRepository.save(energyModelRun).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the energyModelRun
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, energyModelRun.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return energyModelRunRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected EnergyModelRun getPersistedEnergyModelRun(EnergyModelRun energyModelRun) {
        return energyModelRunRepository.findById(energyModelRun.getId()).block();
    }

    protected void assertPersistedEnergyModelRunToMatchAllProperties(EnergyModelRun expectedEnergyModelRun) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEnergyModelRunAllPropertiesEquals(expectedEnergyModelRun, getPersistedEnergyModelRun(expectedEnergyModelRun));
        assertEnergyModelRunUpdatableFieldsEquals(expectedEnergyModelRun, getPersistedEnergyModelRun(expectedEnergyModelRun));
    }

    protected void assertPersistedEnergyModelRunToMatchUpdatableProperties(EnergyModelRun expectedEnergyModelRun) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEnergyModelRunAllUpdatablePropertiesEquals(expectedEnergyModelRun, getPersistedEnergyModelRun(expectedEnergyModelRun));
        assertEnergyModelRunUpdatableFieldsEquals(expectedEnergyModelRun, getPersistedEnergyModelRun(expectedEnergyModelRun));
    }
}
