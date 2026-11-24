package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.WeatherIngestionBatchAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WeatherIngestionBatch;
import ar.com.heliguard.app.domain.enumeration.WeatherBatchStatus;
import ar.com.heliguard.app.repository.WeatherIngestionBatchRepository;
import ar.com.heliguard.app.service.WeatherIngestionBatchService;
import ar.com.heliguard.app.service.dto.WeatherIngestionBatchDTO;
import ar.com.heliguard.app.service.mapper.WeatherIngestionBatchMapper;
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
 * Integration tests for the {@link WeatherIngestionBatchResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class WeatherIngestionBatchResourceIT {

    private static final String DEFAULT_PROVIDER_BATCH_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PROVIDER_BATCH_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DATASET_CODE = "AAAAAAAAAA";
    private static final String UPDATED_DATASET_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_PERIOD_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PERIOD_START = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_PERIOD_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PERIOD_END = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_FETCHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FETCHED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final WeatherBatchStatus DEFAULT_STATUS = WeatherBatchStatus.RECEIVED;
    private static final WeatherBatchStatus UPDATED_STATUS = WeatherBatchStatus.VALIDATED;

    private static final String DEFAULT_QUALITY_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_QUALITY_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/weather-ingestion-batches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WeatherIngestionBatchRepository weatherIngestionBatchRepository;

    @Mock
    private WeatherIngestionBatchRepository weatherIngestionBatchRepositoryMock;

    @Autowired
    private WeatherIngestionBatchMapper weatherIngestionBatchMapper;

    @Mock
    private WeatherIngestionBatchService weatherIngestionBatchServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private WeatherIngestionBatch weatherIngestionBatch;

    private WeatherIngestionBatch insertedWeatherIngestionBatch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WeatherIngestionBatch createEntity(R2dbcEntityTemplate template) {
        WeatherIngestionBatch weatherIngestionBatch = new WeatherIngestionBatch()
            .providerBatchCode(DEFAULT_PROVIDER_BATCH_CODE)
            .datasetCode(DEFAULT_DATASET_CODE)
            .periodStart(DEFAULT_PERIOD_START)
            .periodEnd(DEFAULT_PERIOD_END)
            .fetchedAt(DEFAULT_FETCHED_AT)
            .status(DEFAULT_STATUS)
            .qualityNote(DEFAULT_QUALITY_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        weatherIngestionBatch.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        weatherIngestionBatch.setInstallation(installation);
        // Add required entity
        EvidenceSource evidenceSource;
        evidenceSource = template.insert(EvidenceSourceResourceIT.createEntity()).block();
        weatherIngestionBatch.setEvidenceSource(evidenceSource);
        return weatherIngestionBatch;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WeatherIngestionBatch createUpdatedEntity(R2dbcEntityTemplate template) {
        WeatherIngestionBatch updatedWeatherIngestionBatch = new WeatherIngestionBatch()
            .providerBatchCode(UPDATED_PROVIDER_BATCH_CODE)
            .datasetCode(UPDATED_DATASET_CODE)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .fetchedAt(UPDATED_FETCHED_AT)
            .status(UPDATED_STATUS)
            .qualityNote(UPDATED_QUALITY_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedWeatherIngestionBatch.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedWeatherIngestionBatch.setInstallation(installation);
        // Add required entity
        EvidenceSource evidenceSource;
        evidenceSource = template.insert(EvidenceSourceResourceIT.createUpdatedEntity()).block();
        updatedWeatherIngestionBatch.setEvidenceSource(evidenceSource);
        return updatedWeatherIngestionBatch;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(WeatherIngestionBatch.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
        EvidenceSourceResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        weatherIngestionBatch = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedWeatherIngestionBatch != null) {
            weatherIngestionBatchRepository.delete(insertedWeatherIngestionBatch).block();
            insertedWeatherIngestionBatch = null;
        }
        deleteEntities(template);
    }

    @Test
    void createWeatherIngestionBatch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WeatherIngestionBatch
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);
        var returnedWeatherIngestionBatchDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(WeatherIngestionBatchDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the WeatherIngestionBatch in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWeatherIngestionBatch = weatherIngestionBatchMapper.toEntity(returnedWeatherIngestionBatchDTO);
        assertWeatherIngestionBatchUpdatableFieldsEquals(
            returnedWeatherIngestionBatch,
            getPersistedWeatherIngestionBatch(returnedWeatherIngestionBatch)
        );

        insertedWeatherIngestionBatch = returnedWeatherIngestionBatch;
    }

    @Test
    void createWeatherIngestionBatchWithExistingId() throws Exception {
        // Create the WeatherIngestionBatch with an existing ID
        weatherIngestionBatch.setId(1L);
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WeatherIngestionBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkProviderBatchCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        weatherIngestionBatch.setProviderBatchCode(null);

        // Create the WeatherIngestionBatch, which fails.
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDatasetCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        weatherIngestionBatch.setDatasetCode(null);

        // Create the WeatherIngestionBatch, which fails.
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPeriodStartIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        weatherIngestionBatch.setPeriodStart(null);

        // Create the WeatherIngestionBatch, which fails.
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPeriodEndIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        weatherIngestionBatch.setPeriodEnd(null);

        // Create the WeatherIngestionBatch, which fails.
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFetchedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        weatherIngestionBatch.setFetchedAt(null);

        // Create the WeatherIngestionBatch, which fails.
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        weatherIngestionBatch.setStatus(null);

        // Create the WeatherIngestionBatch, which fails.
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllWeatherIngestionBatchesAsStream() {
        // Initialize the database
        weatherIngestionBatchRepository.save(weatherIngestionBatch).block();

        List<WeatherIngestionBatch> weatherIngestionBatchList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(WeatherIngestionBatchDTO.class)
            .getResponseBody()
            .map(weatherIngestionBatchMapper::toEntity)
            .filter(weatherIngestionBatch::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(weatherIngestionBatchList).isNotNull();
        assertThat(weatherIngestionBatchList).hasSize(1);
        WeatherIngestionBatch testWeatherIngestionBatch = weatherIngestionBatchList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertWeatherIngestionBatchAllPropertiesEquals(weatherIngestionBatch, testWeatherIngestionBatch);
        assertWeatherIngestionBatchUpdatableFieldsEquals(weatherIngestionBatch, testWeatherIngestionBatch);
    }

    @Test
    void getAllWeatherIngestionBatches() {
        // Initialize the database
        insertedWeatherIngestionBatch = weatherIngestionBatchRepository.save(weatherIngestionBatch).block();

        // Get all the weatherIngestionBatchList
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
            .value(hasItem(weatherIngestionBatch.getId().intValue()))
            .jsonPath("$.[*].providerBatchCode")
            .value(hasItem(DEFAULT_PROVIDER_BATCH_CODE))
            .jsonPath("$.[*].datasetCode")
            .value(hasItem(DEFAULT_DATASET_CODE))
            .jsonPath("$.[*].periodStart")
            .value(hasItem(DEFAULT_PERIOD_START.toString()))
            .jsonPath("$.[*].periodEnd")
            .value(hasItem(DEFAULT_PERIOD_END.toString()))
            .jsonPath("$.[*].fetchedAt")
            .value(hasItem(DEFAULT_FETCHED_AT.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].qualityNote")
            .value(hasItem(DEFAULT_QUALITY_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWeatherIngestionBatchesWithEagerRelationshipsIsEnabled() {
        when(weatherIngestionBatchServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(weatherIngestionBatchServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWeatherIngestionBatchesWithEagerRelationshipsIsNotEnabled() {
        when(weatherIngestionBatchServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(weatherIngestionBatchRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getWeatherIngestionBatch() {
        // Initialize the database
        insertedWeatherIngestionBatch = weatherIngestionBatchRepository.save(weatherIngestionBatch).block();

        // Get the weatherIngestionBatch
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, weatherIngestionBatch.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(weatherIngestionBatch.getId().intValue()))
            .jsonPath("$.providerBatchCode")
            .value(is(DEFAULT_PROVIDER_BATCH_CODE))
            .jsonPath("$.datasetCode")
            .value(is(DEFAULT_DATASET_CODE))
            .jsonPath("$.periodStart")
            .value(is(DEFAULT_PERIOD_START.toString()))
            .jsonPath("$.periodEnd")
            .value(is(DEFAULT_PERIOD_END.toString()))
            .jsonPath("$.fetchedAt")
            .value(is(DEFAULT_FETCHED_AT.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.qualityNote")
            .value(is(DEFAULT_QUALITY_NOTE));
    }

    @Test
    void getNonExistingWeatherIngestionBatch() {
        // Get the weatherIngestionBatch
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingWeatherIngestionBatch() throws Exception {
        // Initialize the database
        insertedWeatherIngestionBatch = weatherIngestionBatchRepository.save(weatherIngestionBatch).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the weatherIngestionBatch
        WeatherIngestionBatch updatedWeatherIngestionBatch = weatherIngestionBatchRepository
            .findById(weatherIngestionBatch.getId())
            .block();
        updatedWeatherIngestionBatch
            .providerBatchCode(UPDATED_PROVIDER_BATCH_CODE)
            .datasetCode(UPDATED_DATASET_CODE)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .fetchedAt(UPDATED_FETCHED_AT)
            .status(UPDATED_STATUS)
            .qualityNote(UPDATED_QUALITY_NOTE);
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(updatedWeatherIngestionBatch);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, weatherIngestionBatchDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WeatherIngestionBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWeatherIngestionBatchToMatchAllProperties(updatedWeatherIngestionBatch);
    }

    @Test
    void putNonExistingWeatherIngestionBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        weatherIngestionBatch.setId(longCount.incrementAndGet());

        // Create the WeatherIngestionBatch
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, weatherIngestionBatchDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WeatherIngestionBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchWeatherIngestionBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        weatherIngestionBatch.setId(longCount.incrementAndGet());

        // Create the WeatherIngestionBatch
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WeatherIngestionBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamWeatherIngestionBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        weatherIngestionBatch.setId(longCount.incrementAndGet());

        // Create the WeatherIngestionBatch
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WeatherIngestionBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateWeatherIngestionBatchWithPatch() throws Exception {
        // Initialize the database
        insertedWeatherIngestionBatch = weatherIngestionBatchRepository.save(weatherIngestionBatch).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the weatherIngestionBatch using partial update
        WeatherIngestionBatch partialUpdatedWeatherIngestionBatch = new WeatherIngestionBatch();
        partialUpdatedWeatherIngestionBatch.setId(weatherIngestionBatch.getId());

        partialUpdatedWeatherIngestionBatch
            .providerBatchCode(UPDATED_PROVIDER_BATCH_CODE)
            .datasetCode(UPDATED_DATASET_CODE)
            .fetchedAt(UPDATED_FETCHED_AT)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWeatherIngestionBatch.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWeatherIngestionBatch))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WeatherIngestionBatch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWeatherIngestionBatchUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWeatherIngestionBatch, weatherIngestionBatch),
            getPersistedWeatherIngestionBatch(weatherIngestionBatch)
        );
    }

    @Test
    void fullUpdateWeatherIngestionBatchWithPatch() throws Exception {
        // Initialize the database
        insertedWeatherIngestionBatch = weatherIngestionBatchRepository.save(weatherIngestionBatch).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the weatherIngestionBatch using partial update
        WeatherIngestionBatch partialUpdatedWeatherIngestionBatch = new WeatherIngestionBatch();
        partialUpdatedWeatherIngestionBatch.setId(weatherIngestionBatch.getId());

        partialUpdatedWeatherIngestionBatch
            .providerBatchCode(UPDATED_PROVIDER_BATCH_CODE)
            .datasetCode(UPDATED_DATASET_CODE)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .fetchedAt(UPDATED_FETCHED_AT)
            .status(UPDATED_STATUS)
            .qualityNote(UPDATED_QUALITY_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWeatherIngestionBatch.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWeatherIngestionBatch))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WeatherIngestionBatch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWeatherIngestionBatchUpdatableFieldsEquals(
            partialUpdatedWeatherIngestionBatch,
            getPersistedWeatherIngestionBatch(partialUpdatedWeatherIngestionBatch)
        );
    }

    @Test
    void patchNonExistingWeatherIngestionBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        weatherIngestionBatch.setId(longCount.incrementAndGet());

        // Create the WeatherIngestionBatch
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, weatherIngestionBatchDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WeatherIngestionBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchWeatherIngestionBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        weatherIngestionBatch.setId(longCount.incrementAndGet());

        // Create the WeatherIngestionBatch
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WeatherIngestionBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamWeatherIngestionBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        weatherIngestionBatch.setId(longCount.incrementAndGet());

        // Create the WeatherIngestionBatch
        WeatherIngestionBatchDTO weatherIngestionBatchDTO = weatherIngestionBatchMapper.toDto(weatherIngestionBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(weatherIngestionBatchDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WeatherIngestionBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteWeatherIngestionBatch() {
        // Initialize the database
        insertedWeatherIngestionBatch = weatherIngestionBatchRepository.save(weatherIngestionBatch).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the weatherIngestionBatch
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, weatherIngestionBatch.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return weatherIngestionBatchRepository.count().block();
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

    protected WeatherIngestionBatch getPersistedWeatherIngestionBatch(WeatherIngestionBatch weatherIngestionBatch) {
        return weatherIngestionBatchRepository.findById(weatherIngestionBatch.getId()).block();
    }

    protected void assertPersistedWeatherIngestionBatchToMatchAllProperties(WeatherIngestionBatch expectedWeatherIngestionBatch) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWeatherIngestionBatchAllPropertiesEquals(expectedWeatherIngestionBatch, getPersistedWeatherIngestionBatch(expectedWeatherIngestionBatch));
        assertWeatherIngestionBatchUpdatableFieldsEquals(
            expectedWeatherIngestionBatch,
            getPersistedWeatherIngestionBatch(expectedWeatherIngestionBatch)
        );
    }

    protected void assertPersistedWeatherIngestionBatchToMatchUpdatableProperties(WeatherIngestionBatch expectedWeatherIngestionBatch) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWeatherIngestionBatchAllUpdatablePropertiesEquals(expectedWeatherIngestionBatch, getPersistedWeatherIngestionBatch(expectedWeatherIngestionBatch));
        assertWeatherIngestionBatchUpdatableFieldsEquals(
            expectedWeatherIngestionBatch,
            getPersistedWeatherIngestionBatch(expectedWeatherIngestionBatch)
        );
    }
}
