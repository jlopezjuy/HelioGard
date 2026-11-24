package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.EvidenceSourceAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.domain.enumeration.EvidenceSourceKind;
import ar.com.heliguard.app.repository.EvidenceSourceRepository;
import ar.com.heliguard.app.service.dto.EvidenceSourceDTO;
import ar.com.heliguard.app.service.mapper.EvidenceSourceMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link EvidenceSourceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EvidenceSourceResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PROVIDER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PROVIDER_NAME = "BBBBBBBBBB";

    private static final EvidenceSourceKind DEFAULT_KIND = EvidenceSourceKind.SATELLITE_WEATHER;
    private static final EvidenceSourceKind UPDATED_KIND = EvidenceSourceKind.ONSITE_SENSOR;

    private static final BigDecimal DEFAULT_SPATIAL_RESOLUTION_M = new BigDecimal(1);
    private static final BigDecimal UPDATED_SPATIAL_RESOLUTION_M = new BigDecimal(2);

    private static final String DEFAULT_LICENSE_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_LICENSE_REFERENCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/evidence-sources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EvidenceSourceRepository evidenceSourceRepository;

    @Autowired
    private EvidenceSourceMapper evidenceSourceMapper;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private EvidenceSource evidenceSource;

    private EvidenceSource insertedEvidenceSource;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EvidenceSource createEntity() {
        return new EvidenceSource()
            .code(DEFAULT_CODE)
            .providerName(DEFAULT_PROVIDER_NAME)
            .kind(DEFAULT_KIND)
            .spatialResolutionM(DEFAULT_SPATIAL_RESOLUTION_M)
            .licenseReference(DEFAULT_LICENSE_REFERENCE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EvidenceSource createUpdatedEntity() {
        return new EvidenceSource()
            .code(UPDATED_CODE)
            .providerName(UPDATED_PROVIDER_NAME)
            .kind(UPDATED_KIND)
            .spatialResolutionM(UPDATED_SPATIAL_RESOLUTION_M)
            .licenseReference(UPDATED_LICENSE_REFERENCE);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(EvidenceSource.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        evidenceSource = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEvidenceSource != null) {
            evidenceSourceRepository.delete(insertedEvidenceSource).block();
            insertedEvidenceSource = null;
        }
        deleteEntities(template);
    }

    @Test
    void createEvidenceSource() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EvidenceSource
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);
        var returnedEvidenceSourceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EvidenceSourceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EvidenceSource in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEvidenceSource = evidenceSourceMapper.toEntity(returnedEvidenceSourceDTO);
        assertEvidenceSourceUpdatableFieldsEquals(returnedEvidenceSource, getPersistedEvidenceSource(returnedEvidenceSource));

        insertedEvidenceSource = returnedEvidenceSource;
    }

    @Test
    void createEvidenceSourceWithExistingId() throws Exception {
        // Create the EvidenceSource with an existing ID
        evidenceSource.setId(1L);
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EvidenceSource in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evidenceSource.setCode(null);

        // Create the EvidenceSource, which fails.
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkProviderNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evidenceSource.setProviderName(null);

        // Create the EvidenceSource, which fails.
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evidenceSource.setKind(null);

        // Create the EvidenceSource, which fails.
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEvidenceSourcesAsStream() {
        // Initialize the database
        evidenceSourceRepository.save(evidenceSource).block();

        List<EvidenceSource> evidenceSourceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(EvidenceSourceDTO.class)
            .getResponseBody()
            .map(evidenceSourceMapper::toEntity)
            .filter(evidenceSource::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(evidenceSourceList).isNotNull();
        assertThat(evidenceSourceList).hasSize(1);
        EvidenceSource testEvidenceSource = evidenceSourceList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertEvidenceSourceAllPropertiesEquals(evidenceSource, testEvidenceSource);
        assertEvidenceSourceUpdatableFieldsEquals(evidenceSource, testEvidenceSource);
    }

    @Test
    void getAllEvidenceSources() {
        // Initialize the database
        insertedEvidenceSource = evidenceSourceRepository.save(evidenceSource).block();

        // Get all the evidenceSourceList
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
            .value(hasItem(evidenceSource.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].providerName")
            .value(hasItem(DEFAULT_PROVIDER_NAME))
            .jsonPath("$.[*].kind")
            .value(hasItem(DEFAULT_KIND.toString()))
            .jsonPath("$.[*].spatialResolutionM")
            .value(hasItem(sameNumber(DEFAULT_SPATIAL_RESOLUTION_M)))
            .jsonPath("$.[*].licenseReference")
            .value(hasItem(DEFAULT_LICENSE_REFERENCE));
    }

    @Test
    void getEvidenceSource() {
        // Initialize the database
        insertedEvidenceSource = evidenceSourceRepository.save(evidenceSource).block();

        // Get the evidenceSource
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, evidenceSource.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(evidenceSource.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.providerName")
            .value(is(DEFAULT_PROVIDER_NAME))
            .jsonPath("$.kind")
            .value(is(DEFAULT_KIND.toString()))
            .jsonPath("$.spatialResolutionM")
            .value(is(sameNumber(DEFAULT_SPATIAL_RESOLUTION_M)))
            .jsonPath("$.licenseReference")
            .value(is(DEFAULT_LICENSE_REFERENCE));
    }

    @Test
    void getNonExistingEvidenceSource() {
        // Get the evidenceSource
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEvidenceSource() throws Exception {
        // Initialize the database
        insertedEvidenceSource = evidenceSourceRepository.save(evidenceSource).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evidenceSource
        EvidenceSource updatedEvidenceSource = evidenceSourceRepository.findById(evidenceSource.getId()).block();
        updatedEvidenceSource
            .code(UPDATED_CODE)
            .providerName(UPDATED_PROVIDER_NAME)
            .kind(UPDATED_KIND)
            .spatialResolutionM(UPDATED_SPATIAL_RESOLUTION_M)
            .licenseReference(UPDATED_LICENSE_REFERENCE);
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(updatedEvidenceSource);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, evidenceSourceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EvidenceSource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEvidenceSourceToMatchAllProperties(updatedEvidenceSource);
    }

    @Test
    void putNonExistingEvidenceSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evidenceSource.setId(longCount.incrementAndGet());

        // Create the EvidenceSource
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, evidenceSourceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EvidenceSource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEvidenceSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evidenceSource.setId(longCount.incrementAndGet());

        // Create the EvidenceSource
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EvidenceSource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEvidenceSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evidenceSource.setId(longCount.incrementAndGet());

        // Create the EvidenceSource
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EvidenceSource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEvidenceSourceWithPatch() throws Exception {
        // Initialize the database
        insertedEvidenceSource = evidenceSourceRepository.save(evidenceSource).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evidenceSource using partial update
        EvidenceSource partialUpdatedEvidenceSource = new EvidenceSource();
        partialUpdatedEvidenceSource.setId(evidenceSource.getId());

        partialUpdatedEvidenceSource.spatialResolutionM(UPDATED_SPATIAL_RESOLUTION_M);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvidenceSource.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEvidenceSource))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EvidenceSource in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEvidenceSourceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEvidenceSource, evidenceSource),
            getPersistedEvidenceSource(evidenceSource)
        );
    }

    @Test
    void fullUpdateEvidenceSourceWithPatch() throws Exception {
        // Initialize the database
        insertedEvidenceSource = evidenceSourceRepository.save(evidenceSource).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evidenceSource using partial update
        EvidenceSource partialUpdatedEvidenceSource = new EvidenceSource();
        partialUpdatedEvidenceSource.setId(evidenceSource.getId());

        partialUpdatedEvidenceSource
            .code(UPDATED_CODE)
            .providerName(UPDATED_PROVIDER_NAME)
            .kind(UPDATED_KIND)
            .spatialResolutionM(UPDATED_SPATIAL_RESOLUTION_M)
            .licenseReference(UPDATED_LICENSE_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvidenceSource.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEvidenceSource))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EvidenceSource in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEvidenceSourceUpdatableFieldsEquals(partialUpdatedEvidenceSource, getPersistedEvidenceSource(partialUpdatedEvidenceSource));
    }

    @Test
    void patchNonExistingEvidenceSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evidenceSource.setId(longCount.incrementAndGet());

        // Create the EvidenceSource
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, evidenceSourceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EvidenceSource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEvidenceSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evidenceSource.setId(longCount.incrementAndGet());

        // Create the EvidenceSource
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EvidenceSource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEvidenceSource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evidenceSource.setId(longCount.incrementAndGet());

        // Create the EvidenceSource
        EvidenceSourceDTO evidenceSourceDTO = evidenceSourceMapper.toDto(evidenceSource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(evidenceSourceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EvidenceSource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEvidenceSource() {
        // Initialize the database
        insertedEvidenceSource = evidenceSourceRepository.save(evidenceSource).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the evidenceSource
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, evidenceSource.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return evidenceSourceRepository.count().block();
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

    protected EvidenceSource getPersistedEvidenceSource(EvidenceSource evidenceSource) {
        return evidenceSourceRepository.findById(evidenceSource.getId()).block();
    }

    protected void assertPersistedEvidenceSourceToMatchAllProperties(EvidenceSource expectedEvidenceSource) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEvidenceSourceAllPropertiesEquals(expectedEvidenceSource, getPersistedEvidenceSource(expectedEvidenceSource));
        assertEvidenceSourceUpdatableFieldsEquals(expectedEvidenceSource, getPersistedEvidenceSource(expectedEvidenceSource));
    }

    protected void assertPersistedEvidenceSourceToMatchUpdatableProperties(EvidenceSource expectedEvidenceSource) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEvidenceSourceAllUpdatablePropertiesEquals(expectedEvidenceSource, getPersistedEvidenceSource(expectedEvidenceSource));
        assertEvidenceSourceUpdatableFieldsEquals(expectedEvidenceSource, getPersistedEvidenceSource(expectedEvidenceSource));
    }
}
