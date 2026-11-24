package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.VerificationResultAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.VerificationResult;
import ar.com.heliguard.app.domain.enumeration.VerificationStatus;
import ar.com.heliguard.app.repository.VerificationResultRepository;
import ar.com.heliguard.app.service.VerificationResultService;
import ar.com.heliguard.app.service.dto.VerificationResultDTO;
import ar.com.heliguard.app.service.mapper.VerificationResultMapper;
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
 * Integration tests for the {@link VerificationResultResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class VerificationResultResourceIT {

    private static final VerificationStatus DEFAULT_STATUS = VerificationStatus.PENDING;
    private static final VerificationStatus UPDATED_STATUS = VerificationStatus.INCONCLUSIVE;

    private static final Instant DEFAULT_ASSESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSESSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_BASELINE_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_BASELINE_METHOD = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_RECOVERED_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_RECOVERED_WH = new BigDecimal(2);

    private static final String DEFAULT_EVIDENCE_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_EVIDENCE_SUMMARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/verification-results";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VerificationResultRepository verificationResultRepository;

    @Mock
    private VerificationResultRepository verificationResultRepositoryMock;

    @Autowired
    private VerificationResultMapper verificationResultMapper;

    @Mock
    private VerificationResultService verificationResultServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private VerificationResult verificationResult;

    private VerificationResult insertedVerificationResult;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VerificationResult createEntity(R2dbcEntityTemplate template) {
        VerificationResult verificationResult = new VerificationResult()
            .status(DEFAULT_STATUS)
            .assessedAt(DEFAULT_ASSESSED_AT)
            .baselineMethod(DEFAULT_BASELINE_METHOD)
            .recoveredWh(DEFAULT_RECOVERED_WH)
            .evidenceSummary(DEFAULT_EVIDENCE_SUMMARY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        verificationResult.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        verificationResult.setInstallation(installation);
        return verificationResult;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VerificationResult createUpdatedEntity(R2dbcEntityTemplate template) {
        VerificationResult updatedVerificationResult = new VerificationResult()
            .status(UPDATED_STATUS)
            .assessedAt(UPDATED_ASSESSED_AT)
            .baselineMethod(UPDATED_BASELINE_METHOD)
            .recoveredWh(UPDATED_RECOVERED_WH)
            .evidenceSummary(UPDATED_EVIDENCE_SUMMARY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedVerificationResult.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedVerificationResult.setInstallation(installation);
        return updatedVerificationResult;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(VerificationResult.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        verificationResult = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedVerificationResult != null) {
            verificationResultRepository.delete(insertedVerificationResult).block();
            insertedVerificationResult = null;
        }
        deleteEntities(template);
    }

    @Test
    void createVerificationResult() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VerificationResult
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);
        var returnedVerificationResultDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(VerificationResultDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the VerificationResult in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVerificationResult = verificationResultMapper.toEntity(returnedVerificationResultDTO);
        assertVerificationResultUpdatableFieldsEquals(
            returnedVerificationResult,
            getPersistedVerificationResult(returnedVerificationResult)
        );

        insertedVerificationResult = returnedVerificationResult;
    }

    @Test
    void createVerificationResultWithExistingId() throws Exception {
        // Create the VerificationResult with an existing ID
        verificationResult.setId(1L);
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the VerificationResult in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        verificationResult.setStatus(null);

        // Create the VerificationResult, which fails.
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAssessedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        verificationResult.setAssessedAt(null);

        // Create the VerificationResult, which fails.
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkBaselineMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        verificationResult.setBaselineMethod(null);

        // Create the VerificationResult, which fails.
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllVerificationResultsAsStream() {
        // Initialize the database
        verificationResultRepository.save(verificationResult).block();

        List<VerificationResult> verificationResultList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(VerificationResultDTO.class)
            .getResponseBody()
            .map(verificationResultMapper::toEntity)
            .filter(verificationResult::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(verificationResultList).isNotNull();
        assertThat(verificationResultList).hasSize(1);
        VerificationResult testVerificationResult = verificationResultList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertVerificationResultAllPropertiesEquals(verificationResult, testVerificationResult);
        assertVerificationResultUpdatableFieldsEquals(verificationResult, testVerificationResult);
    }

    @Test
    void getAllVerificationResults() {
        // Initialize the database
        insertedVerificationResult = verificationResultRepository.save(verificationResult).block();

        // Get all the verificationResultList
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
            .value(hasItem(verificationResult.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].assessedAt")
            .value(hasItem(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.[*].baselineMethod")
            .value(hasItem(DEFAULT_BASELINE_METHOD))
            .jsonPath("$.[*].recoveredWh")
            .value(hasItem(sameNumber(DEFAULT_RECOVERED_WH)))
            .jsonPath("$.[*].evidenceSummary")
            .value(hasItem(DEFAULT_EVIDENCE_SUMMARY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVerificationResultsWithEagerRelationshipsIsEnabled() {
        when(verificationResultServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(verificationResultServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVerificationResultsWithEagerRelationshipsIsNotEnabled() {
        when(verificationResultServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(verificationResultRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getVerificationResult() {
        // Initialize the database
        insertedVerificationResult = verificationResultRepository.save(verificationResult).block();

        // Get the verificationResult
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, verificationResult.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(verificationResult.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.assessedAt")
            .value(is(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.baselineMethod")
            .value(is(DEFAULT_BASELINE_METHOD))
            .jsonPath("$.recoveredWh")
            .value(is(sameNumber(DEFAULT_RECOVERED_WH)))
            .jsonPath("$.evidenceSummary")
            .value(is(DEFAULT_EVIDENCE_SUMMARY));
    }

    @Test
    void getNonExistingVerificationResult() {
        // Get the verificationResult
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingVerificationResult() throws Exception {
        // Initialize the database
        insertedVerificationResult = verificationResultRepository.save(verificationResult).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the verificationResult
        VerificationResult updatedVerificationResult = verificationResultRepository.findById(verificationResult.getId()).block();
        updatedVerificationResult
            .status(UPDATED_STATUS)
            .assessedAt(UPDATED_ASSESSED_AT)
            .baselineMethod(UPDATED_BASELINE_METHOD)
            .recoveredWh(UPDATED_RECOVERED_WH)
            .evidenceSummary(UPDATED_EVIDENCE_SUMMARY);
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(updatedVerificationResult);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, verificationResultDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the VerificationResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVerificationResultToMatchAllProperties(updatedVerificationResult);
    }

    @Test
    void putNonExistingVerificationResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        verificationResult.setId(longCount.incrementAndGet());

        // Create the VerificationResult
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, verificationResultDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the VerificationResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVerificationResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        verificationResult.setId(longCount.incrementAndGet());

        // Create the VerificationResult
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the VerificationResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVerificationResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        verificationResult.setId(longCount.incrementAndGet());

        // Create the VerificationResult
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the VerificationResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVerificationResultWithPatch() throws Exception {
        // Initialize the database
        insertedVerificationResult = verificationResultRepository.save(verificationResult).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the verificationResult using partial update
        VerificationResult partialUpdatedVerificationResult = new VerificationResult();
        partialUpdatedVerificationResult.setId(verificationResult.getId());

        partialUpdatedVerificationResult
            .status(UPDATED_STATUS)
            .assessedAt(UPDATED_ASSESSED_AT)
            .baselineMethod(UPDATED_BASELINE_METHOD)
            .recoveredWh(UPDATED_RECOVERED_WH)
            .evidenceSummary(UPDATED_EVIDENCE_SUMMARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVerificationResult.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedVerificationResult))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the VerificationResult in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVerificationResultUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVerificationResult, verificationResult),
            getPersistedVerificationResult(verificationResult)
        );
    }

    @Test
    void fullUpdateVerificationResultWithPatch() throws Exception {
        // Initialize the database
        insertedVerificationResult = verificationResultRepository.save(verificationResult).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the verificationResult using partial update
        VerificationResult partialUpdatedVerificationResult = new VerificationResult();
        partialUpdatedVerificationResult.setId(verificationResult.getId());

        partialUpdatedVerificationResult
            .status(UPDATED_STATUS)
            .assessedAt(UPDATED_ASSESSED_AT)
            .baselineMethod(UPDATED_BASELINE_METHOD)
            .recoveredWh(UPDATED_RECOVERED_WH)
            .evidenceSummary(UPDATED_EVIDENCE_SUMMARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVerificationResult.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedVerificationResult))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the VerificationResult in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVerificationResultUpdatableFieldsEquals(
            partialUpdatedVerificationResult,
            getPersistedVerificationResult(partialUpdatedVerificationResult)
        );
    }

    @Test
    void patchNonExistingVerificationResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        verificationResult.setId(longCount.incrementAndGet());

        // Create the VerificationResult
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, verificationResultDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the VerificationResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVerificationResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        verificationResult.setId(longCount.incrementAndGet());

        // Create the VerificationResult
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the VerificationResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVerificationResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        verificationResult.setId(longCount.incrementAndGet());

        // Create the VerificationResult
        VerificationResultDTO verificationResultDTO = verificationResultMapper.toDto(verificationResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(verificationResultDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the VerificationResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVerificationResult() {
        // Initialize the database
        insertedVerificationResult = verificationResultRepository.save(verificationResult).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the verificationResult
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, verificationResult.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return verificationResultRepository.count().block();
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

    protected VerificationResult getPersistedVerificationResult(VerificationResult verificationResult) {
        return verificationResultRepository.findById(verificationResult.getId()).block();
    }

    protected void assertPersistedVerificationResultToMatchAllProperties(VerificationResult expectedVerificationResult) {
        // Test fails because reactive api returns an empty object instead of null
        // assertVerificationResultAllPropertiesEquals(expectedVerificationResult, getPersistedVerificationResult(expectedVerificationResult));
        assertVerificationResultUpdatableFieldsEquals(
            expectedVerificationResult,
            getPersistedVerificationResult(expectedVerificationResult)
        );
    }

    protected void assertPersistedVerificationResultToMatchUpdatableProperties(VerificationResult expectedVerificationResult) {
        // Test fails because reactive api returns an empty object instead of null
        // assertVerificationResultAllUpdatablePropertiesEquals(expectedVerificationResult, getPersistedVerificationResult(expectedVerificationResult));
        assertVerificationResultUpdatableFieldsEquals(
            expectedVerificationResult,
            getPersistedVerificationResult(expectedVerificationResult)
        );
    }
}
