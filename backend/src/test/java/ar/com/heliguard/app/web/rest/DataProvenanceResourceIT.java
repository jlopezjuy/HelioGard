package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.DataProvenanceAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DataProvenance;
import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import ar.com.heliguard.app.repository.DataProvenanceRepository;
import ar.com.heliguard.app.service.DataProvenanceService;
import ar.com.heliguard.app.service.dto.DataProvenanceDTO;
import ar.com.heliguard.app.service.mapper.DataProvenanceMapper;
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
 * Integration tests for the {@link DataProvenanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DataProvenanceResourceIT {

    private static final String DEFAULT_SOURCE_RECORD_KEY = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_RECORD_KEY = "BBBBBBBBBB";

    private static final Instant DEFAULT_OBSERVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OBSERVED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_PERIOD_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PERIOD_START = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_PERIOD_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PERIOD_END = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_INGESTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INGESTED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_SOURCE_PRODUCT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_PRODUCT_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_SPATIAL_RESOLUTION_M = new BigDecimal(1);
    private static final BigDecimal UPDATED_SPATIAL_RESOLUTION_M = new BigDecimal(2);

    private static final String DEFAULT_CONTENT_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_SHA_256 = "BBBBBBBBBB";

    private static final DataTrustLevel DEFAULT_TRUST_LEVEL = DataTrustLevel.VALIDATED;
    private static final DataTrustLevel UPDATED_TRUST_LEVEL = DataTrustLevel.ESTIMATED;

    private static final String DEFAULT_QUALITY_FLAGS = "AAAAAAAAAA";
    private static final String UPDATED_QUALITY_FLAGS = "BBBBBBBBBB";

    private static final String DEFAULT_ALGORITHM_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_ALGORITHM_VERSION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/data-provenances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DataProvenanceRepository dataProvenanceRepository;

    @Mock
    private DataProvenanceRepository dataProvenanceRepositoryMock;

    @Autowired
    private DataProvenanceMapper dataProvenanceMapper;

    @Mock
    private DataProvenanceService dataProvenanceServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private DataProvenance dataProvenance;

    private DataProvenance insertedDataProvenance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataProvenance createEntity(R2dbcEntityTemplate template) {
        DataProvenance dataProvenance = new DataProvenance()
            .sourceRecordKey(DEFAULT_SOURCE_RECORD_KEY)
            .observedAt(DEFAULT_OBSERVED_AT)
            .periodStart(DEFAULT_PERIOD_START)
            .periodEnd(DEFAULT_PERIOD_END)
            .ingestedAt(DEFAULT_INGESTED_AT)
            .sourceProductCode(DEFAULT_SOURCE_PRODUCT_CODE)
            .spatialResolutionM(DEFAULT_SPATIAL_RESOLUTION_M)
            .contentSha256(DEFAULT_CONTENT_SHA_256)
            .trustLevel(DEFAULT_TRUST_LEVEL)
            .qualityFlags(DEFAULT_QUALITY_FLAGS)
            .algorithmVersion(DEFAULT_ALGORITHM_VERSION);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        dataProvenance.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        dataProvenance.setInstallation(installation);
        // Add required entity
        EvidenceSource evidenceSource;
        evidenceSource = template.insert(EvidenceSourceResourceIT.createEntity()).block();
        dataProvenance.setEvidenceSource(evidenceSource);
        return dataProvenance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataProvenance createUpdatedEntity(R2dbcEntityTemplate template) {
        DataProvenance updatedDataProvenance = new DataProvenance()
            .sourceRecordKey(UPDATED_SOURCE_RECORD_KEY)
            .observedAt(UPDATED_OBSERVED_AT)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .ingestedAt(UPDATED_INGESTED_AT)
            .sourceProductCode(UPDATED_SOURCE_PRODUCT_CODE)
            .spatialResolutionM(UPDATED_SPATIAL_RESOLUTION_M)
            .contentSha256(UPDATED_CONTENT_SHA_256)
            .trustLevel(UPDATED_TRUST_LEVEL)
            .qualityFlags(UPDATED_QUALITY_FLAGS)
            .algorithmVersion(UPDATED_ALGORITHM_VERSION);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedDataProvenance.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedDataProvenance.setInstallation(installation);
        // Add required entity
        EvidenceSource evidenceSource;
        evidenceSource = template.insert(EvidenceSourceResourceIT.createUpdatedEntity()).block();
        updatedDataProvenance.setEvidenceSource(evidenceSource);
        return updatedDataProvenance;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(DataProvenance.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
        EvidenceSourceResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        dataProvenance = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedDataProvenance != null) {
            dataProvenanceRepository.delete(insertedDataProvenance).block();
            insertedDataProvenance = null;
        }
        deleteEntities(template);
    }

    @Test
    void createDataProvenance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DataProvenance
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);
        var returnedDataProvenanceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DataProvenanceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the DataProvenance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDataProvenance = dataProvenanceMapper.toEntity(returnedDataProvenanceDTO);
        assertDataProvenanceUpdatableFieldsEquals(returnedDataProvenance, getPersistedDataProvenance(returnedDataProvenance));

        insertedDataProvenance = returnedDataProvenance;
    }

    @Test
    void createDataProvenanceWithExistingId() throws Exception {
        // Create the DataProvenance with an existing ID
        dataProvenance.setId(1L);
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DataProvenance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkSourceRecordKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dataProvenance.setSourceRecordKey(null);

        // Create the DataProvenance, which fails.
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkObservedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dataProvenance.setObservedAt(null);

        // Create the DataProvenance, which fails.
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkIngestedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dataProvenance.setIngestedAt(null);

        // Create the DataProvenance, which fails.
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTrustLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dataProvenance.setTrustLevel(null);

        // Create the DataProvenance, which fails.
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDataProvenancesAsStream() {
        // Initialize the database
        dataProvenanceRepository.save(dataProvenance).block();

        List<DataProvenance> dataProvenanceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(DataProvenanceDTO.class)
            .getResponseBody()
            .map(dataProvenanceMapper::toEntity)
            .filter(dataProvenance::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(dataProvenanceList).isNotNull();
        assertThat(dataProvenanceList).hasSize(1);
        DataProvenance testDataProvenance = dataProvenanceList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertDataProvenanceAllPropertiesEquals(dataProvenance, testDataProvenance);
        assertDataProvenanceUpdatableFieldsEquals(dataProvenance, testDataProvenance);
    }

    @Test
    void getAllDataProvenances() {
        // Initialize the database
        insertedDataProvenance = dataProvenanceRepository.save(dataProvenance).block();

        // Get all the dataProvenanceList
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
            .value(hasItem(dataProvenance.getId().intValue()))
            .jsonPath("$.[*].sourceRecordKey")
            .value(hasItem(DEFAULT_SOURCE_RECORD_KEY))
            .jsonPath("$.[*].observedAt")
            .value(hasItem(DEFAULT_OBSERVED_AT.toString()))
            .jsonPath("$.[*].periodStart")
            .value(hasItem(DEFAULT_PERIOD_START.toString()))
            .jsonPath("$.[*].periodEnd")
            .value(hasItem(DEFAULT_PERIOD_END.toString()))
            .jsonPath("$.[*].ingestedAt")
            .value(hasItem(DEFAULT_INGESTED_AT.toString()))
            .jsonPath("$.[*].sourceProductCode")
            .value(hasItem(DEFAULT_SOURCE_PRODUCT_CODE))
            .jsonPath("$.[*].spatialResolutionM")
            .value(hasItem(sameNumber(DEFAULT_SPATIAL_RESOLUTION_M)))
            .jsonPath("$.[*].contentSha256")
            .value(hasItem(DEFAULT_CONTENT_SHA_256))
            .jsonPath("$.[*].trustLevel")
            .value(hasItem(DEFAULT_TRUST_LEVEL.toString()))
            .jsonPath("$.[*].qualityFlags")
            .value(hasItem(DEFAULT_QUALITY_FLAGS))
            .jsonPath("$.[*].algorithmVersion")
            .value(hasItem(DEFAULT_ALGORITHM_VERSION));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDataProvenancesWithEagerRelationshipsIsEnabled() {
        when(dataProvenanceServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(dataProvenanceServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDataProvenancesWithEagerRelationshipsIsNotEnabled() {
        when(dataProvenanceServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(dataProvenanceRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDataProvenance() {
        // Initialize the database
        insertedDataProvenance = dataProvenanceRepository.save(dataProvenance).block();

        // Get the dataProvenance
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, dataProvenance.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(dataProvenance.getId().intValue()))
            .jsonPath("$.sourceRecordKey")
            .value(is(DEFAULT_SOURCE_RECORD_KEY))
            .jsonPath("$.observedAt")
            .value(is(DEFAULT_OBSERVED_AT.toString()))
            .jsonPath("$.periodStart")
            .value(is(DEFAULT_PERIOD_START.toString()))
            .jsonPath("$.periodEnd")
            .value(is(DEFAULT_PERIOD_END.toString()))
            .jsonPath("$.ingestedAt")
            .value(is(DEFAULT_INGESTED_AT.toString()))
            .jsonPath("$.sourceProductCode")
            .value(is(DEFAULT_SOURCE_PRODUCT_CODE))
            .jsonPath("$.spatialResolutionM")
            .value(is(sameNumber(DEFAULT_SPATIAL_RESOLUTION_M)))
            .jsonPath("$.contentSha256")
            .value(is(DEFAULT_CONTENT_SHA_256))
            .jsonPath("$.trustLevel")
            .value(is(DEFAULT_TRUST_LEVEL.toString()))
            .jsonPath("$.qualityFlags")
            .value(is(DEFAULT_QUALITY_FLAGS))
            .jsonPath("$.algorithmVersion")
            .value(is(DEFAULT_ALGORITHM_VERSION));
    }

    @Test
    void getNonExistingDataProvenance() {
        // Get the dataProvenance
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDataProvenance() throws Exception {
        // Initialize the database
        insertedDataProvenance = dataProvenanceRepository.save(dataProvenance).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dataProvenance
        DataProvenance updatedDataProvenance = dataProvenanceRepository.findById(dataProvenance.getId()).block();
        updatedDataProvenance
            .sourceRecordKey(UPDATED_SOURCE_RECORD_KEY)
            .observedAt(UPDATED_OBSERVED_AT)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .ingestedAt(UPDATED_INGESTED_AT)
            .sourceProductCode(UPDATED_SOURCE_PRODUCT_CODE)
            .spatialResolutionM(UPDATED_SPATIAL_RESOLUTION_M)
            .contentSha256(UPDATED_CONTENT_SHA_256)
            .trustLevel(UPDATED_TRUST_LEVEL)
            .qualityFlags(UPDATED_QUALITY_FLAGS)
            .algorithmVersion(UPDATED_ALGORITHM_VERSION);
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(updatedDataProvenance);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, dataProvenanceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DataProvenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDataProvenanceToMatchAllProperties(updatedDataProvenance);
    }

    @Test
    void putNonExistingDataProvenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataProvenance.setId(longCount.incrementAndGet());

        // Create the DataProvenance
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, dataProvenanceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DataProvenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDataProvenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataProvenance.setId(longCount.incrementAndGet());

        // Create the DataProvenance
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DataProvenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDataProvenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataProvenance.setId(longCount.incrementAndGet());

        // Create the DataProvenance
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DataProvenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDataProvenanceWithPatch() throws Exception {
        // Initialize the database
        insertedDataProvenance = dataProvenanceRepository.save(dataProvenance).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dataProvenance using partial update
        DataProvenance partialUpdatedDataProvenance = new DataProvenance();
        partialUpdatedDataProvenance.setId(dataProvenance.getId());

        partialUpdatedDataProvenance
            .sourceRecordKey(UPDATED_SOURCE_RECORD_KEY)
            .spatialResolutionM(UPDATED_SPATIAL_RESOLUTION_M)
            .trustLevel(UPDATED_TRUST_LEVEL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDataProvenance.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDataProvenance))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DataProvenance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDataProvenanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDataProvenance, dataProvenance),
            getPersistedDataProvenance(dataProvenance)
        );
    }

    @Test
    void fullUpdateDataProvenanceWithPatch() throws Exception {
        // Initialize the database
        insertedDataProvenance = dataProvenanceRepository.save(dataProvenance).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dataProvenance using partial update
        DataProvenance partialUpdatedDataProvenance = new DataProvenance();
        partialUpdatedDataProvenance.setId(dataProvenance.getId());

        partialUpdatedDataProvenance
            .sourceRecordKey(UPDATED_SOURCE_RECORD_KEY)
            .observedAt(UPDATED_OBSERVED_AT)
            .periodStart(UPDATED_PERIOD_START)
            .periodEnd(UPDATED_PERIOD_END)
            .ingestedAt(UPDATED_INGESTED_AT)
            .sourceProductCode(UPDATED_SOURCE_PRODUCT_CODE)
            .spatialResolutionM(UPDATED_SPATIAL_RESOLUTION_M)
            .contentSha256(UPDATED_CONTENT_SHA_256)
            .trustLevel(UPDATED_TRUST_LEVEL)
            .qualityFlags(UPDATED_QUALITY_FLAGS)
            .algorithmVersion(UPDATED_ALGORITHM_VERSION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDataProvenance.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDataProvenance))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DataProvenance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDataProvenanceUpdatableFieldsEquals(partialUpdatedDataProvenance, getPersistedDataProvenance(partialUpdatedDataProvenance));
    }

    @Test
    void patchNonExistingDataProvenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataProvenance.setId(longCount.incrementAndGet());

        // Create the DataProvenance
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, dataProvenanceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DataProvenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDataProvenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataProvenance.setId(longCount.incrementAndGet());

        // Create the DataProvenance
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DataProvenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDataProvenance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataProvenance.setId(longCount.incrementAndGet());

        // Create the DataProvenance
        DataProvenanceDTO dataProvenanceDTO = dataProvenanceMapper.toDto(dataProvenance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(dataProvenanceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DataProvenance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDataProvenance() {
        // Initialize the database
        insertedDataProvenance = dataProvenanceRepository.save(dataProvenance).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dataProvenance
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, dataProvenance.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dataProvenanceRepository.count().block();
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

    protected DataProvenance getPersistedDataProvenance(DataProvenance dataProvenance) {
        return dataProvenanceRepository.findById(dataProvenance.getId()).block();
    }

    protected void assertPersistedDataProvenanceToMatchAllProperties(DataProvenance expectedDataProvenance) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDataProvenanceAllPropertiesEquals(expectedDataProvenance, getPersistedDataProvenance(expectedDataProvenance));
        assertDataProvenanceUpdatableFieldsEquals(expectedDataProvenance, getPersistedDataProvenance(expectedDataProvenance));
    }

    protected void assertPersistedDataProvenanceToMatchUpdatableProperties(DataProvenance expectedDataProvenance) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDataProvenanceAllUpdatablePropertiesEquals(expectedDataProvenance, getPersistedDataProvenance(expectedDataProvenance));
        assertDataProvenanceUpdatableFieldsEquals(expectedDataProvenance, getPersistedDataProvenance(expectedDataProvenance));
    }
}
