package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.LossCauseEstimateAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.LossCauseEstimate;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.LossCause;
import ar.com.heliguard.app.repository.LossCauseEstimateRepository;
import ar.com.heliguard.app.service.LossCauseEstimateService;
import ar.com.heliguard.app.service.dto.LossCauseEstimateDTO;
import ar.com.heliguard.app.service.mapper.LossCauseEstimateMapper;
import java.math.BigDecimal;
import java.time.Duration;
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
 * Integration tests for the {@link LossCauseEstimateResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class LossCauseEstimateResourceIT {

    private static final LossCause DEFAULT_CAUSE = LossCause.SOILING;
    private static final LossCause UPDATED_CAUSE = LossCause.HARDWARE;

    private static final BigDecimal DEFAULT_ESTIMATED_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_ESTIMATED_WH = new BigDecimal(2);

    private static final String DEFAULT_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_METHOD = "BBBBBBBBBB";

    private static final String DEFAULT_EVIDENCE_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_EVIDENCE_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/loss-cause-estimates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LossCauseEstimateRepository lossCauseEstimateRepository;

    @Mock
    private LossCauseEstimateRepository lossCauseEstimateRepositoryMock;

    @Autowired
    private LossCauseEstimateMapper lossCauseEstimateMapper;

    @Mock
    private LossCauseEstimateService lossCauseEstimateServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private LossCauseEstimate lossCauseEstimate;

    private LossCauseEstimate insertedLossCauseEstimate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LossCauseEstimate createEntity(R2dbcEntityTemplate template) {
        LossCauseEstimate lossCauseEstimate = new LossCauseEstimate()
            .cause(DEFAULT_CAUSE)
            .estimatedWh(DEFAULT_ESTIMATED_WH)
            .method(DEFAULT_METHOD)
            .evidenceNote(DEFAULT_EVIDENCE_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        lossCauseEstimate.setOrganization(organization);
        // Add required entity
        LossAssessment lossAssessment;
        lossAssessment = template.insert(LossAssessmentResourceIT.createEntity(template)).block();
        lossCauseEstimate.setLossAssessment(lossAssessment);
        return lossCauseEstimate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LossCauseEstimate createUpdatedEntity(R2dbcEntityTemplate template) {
        LossCauseEstimate updatedLossCauseEstimate = new LossCauseEstimate()
            .cause(UPDATED_CAUSE)
            .estimatedWh(UPDATED_ESTIMATED_WH)
            .method(UPDATED_METHOD)
            .evidenceNote(UPDATED_EVIDENCE_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedLossCauseEstimate.setOrganization(organization);
        // Add required entity
        LossAssessment lossAssessment;
        lossAssessment = template.insert(LossAssessmentResourceIT.createUpdatedEntity(template)).block();
        updatedLossCauseEstimate.setLossAssessment(lossAssessment);
        return updatedLossCauseEstimate;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(LossCauseEstimate.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        LossAssessmentResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        lossCauseEstimate = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedLossCauseEstimate != null) {
            lossCauseEstimateRepository.delete(insertedLossCauseEstimate).block();
            insertedLossCauseEstimate = null;
        }
        deleteEntities(template);
    }

    @Test
    void createLossCauseEstimate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LossCauseEstimate
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);
        var returnedLossCauseEstimateDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(LossCauseEstimateDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the LossCauseEstimate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLossCauseEstimate = lossCauseEstimateMapper.toEntity(returnedLossCauseEstimateDTO);
        assertLossCauseEstimateUpdatableFieldsEquals(returnedLossCauseEstimate, getPersistedLossCauseEstimate(returnedLossCauseEstimate));

        insertedLossCauseEstimate = returnedLossCauseEstimate;
    }

    @Test
    void createLossCauseEstimateWithExistingId() throws Exception {
        // Create the LossCauseEstimate with an existing ID
        lossCauseEstimate.setId(1L);
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossCauseEstimate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCauseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lossCauseEstimate.setCause(null);

        // Create the LossCauseEstimate, which fails.
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstimatedWhIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lossCauseEstimate.setEstimatedWh(null);

        // Create the LossCauseEstimate, which fails.
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lossCauseEstimate.setMethod(null);

        // Create the LossCauseEstimate, which fails.
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllLossCauseEstimatesAsStream() {
        // Initialize the database
        lossCauseEstimateRepository.save(lossCauseEstimate).block();

        List<LossCauseEstimate> lossCauseEstimateList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(LossCauseEstimateDTO.class)
            .getResponseBody()
            .map(lossCauseEstimateMapper::toEntity)
            .filter(lossCauseEstimate::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(lossCauseEstimateList).isNotNull();
        assertThat(lossCauseEstimateList).hasSize(1);
        LossCauseEstimate testLossCauseEstimate = lossCauseEstimateList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertLossCauseEstimateAllPropertiesEquals(lossCauseEstimate, testLossCauseEstimate);
        assertLossCauseEstimateUpdatableFieldsEquals(lossCauseEstimate, testLossCauseEstimate);
    }

    @Test
    void getAllLossCauseEstimates() {
        // Initialize the database
        insertedLossCauseEstimate = lossCauseEstimateRepository.save(lossCauseEstimate).block();

        // Get all the lossCauseEstimateList
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
            .value(hasItem(lossCauseEstimate.getId().intValue()))
            .jsonPath("$.[*].cause")
            .value(hasItem(DEFAULT_CAUSE.toString()))
            .jsonPath("$.[*].estimatedWh")
            .value(hasItem(sameNumber(DEFAULT_ESTIMATED_WH)))
            .jsonPath("$.[*].method")
            .value(hasItem(DEFAULT_METHOD))
            .jsonPath("$.[*].evidenceNote")
            .value(hasItem(DEFAULT_EVIDENCE_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLossCauseEstimatesWithEagerRelationshipsIsEnabled() {
        when(lossCauseEstimateServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(lossCauseEstimateServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLossCauseEstimatesWithEagerRelationshipsIsNotEnabled() {
        when(lossCauseEstimateServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(lossCauseEstimateRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getLossCauseEstimate() {
        // Initialize the database
        insertedLossCauseEstimate = lossCauseEstimateRepository.save(lossCauseEstimate).block();

        // Get the lossCauseEstimate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, lossCauseEstimate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(lossCauseEstimate.getId().intValue()))
            .jsonPath("$.cause")
            .value(is(DEFAULT_CAUSE.toString()))
            .jsonPath("$.estimatedWh")
            .value(is(sameNumber(DEFAULT_ESTIMATED_WH)))
            .jsonPath("$.method")
            .value(is(DEFAULT_METHOD))
            .jsonPath("$.evidenceNote")
            .value(is(DEFAULT_EVIDENCE_NOTE));
    }

    @Test
    void getNonExistingLossCauseEstimate() {
        // Get the lossCauseEstimate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingLossCauseEstimate() throws Exception {
        // Initialize the database
        insertedLossCauseEstimate = lossCauseEstimateRepository.save(lossCauseEstimate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lossCauseEstimate
        LossCauseEstimate updatedLossCauseEstimate = lossCauseEstimateRepository.findById(lossCauseEstimate.getId()).block();
        updatedLossCauseEstimate
            .cause(UPDATED_CAUSE)
            .estimatedWh(UPDATED_ESTIMATED_WH)
            .method(UPDATED_METHOD)
            .evidenceNote(UPDATED_EVIDENCE_NOTE);
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(updatedLossCauseEstimate);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, lossCauseEstimateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LossCauseEstimate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLossCauseEstimateToMatchAllProperties(updatedLossCauseEstimate);
    }

    @Test
    void putNonExistingLossCauseEstimate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossCauseEstimate.setId(longCount.incrementAndGet());

        // Create the LossCauseEstimate
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, lossCauseEstimateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossCauseEstimate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLossCauseEstimate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossCauseEstimate.setId(longCount.incrementAndGet());

        // Create the LossCauseEstimate
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossCauseEstimate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLossCauseEstimate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossCauseEstimate.setId(longCount.incrementAndGet());

        // Create the LossCauseEstimate
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the LossCauseEstimate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLossCauseEstimateWithPatch() throws Exception {
        // Initialize the database
        insertedLossCauseEstimate = lossCauseEstimateRepository.save(lossCauseEstimate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lossCauseEstimate using partial update
        LossCauseEstimate partialUpdatedLossCauseEstimate = new LossCauseEstimate();
        partialUpdatedLossCauseEstimate.setId(lossCauseEstimate.getId());

        partialUpdatedLossCauseEstimate.cause(UPDATED_CAUSE).method(UPDATED_METHOD).evidenceNote(UPDATED_EVIDENCE_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLossCauseEstimate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedLossCauseEstimate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LossCauseEstimate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLossCauseEstimateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLossCauseEstimate, lossCauseEstimate),
            getPersistedLossCauseEstimate(lossCauseEstimate)
        );
    }

    @Test
    void fullUpdateLossCauseEstimateWithPatch() throws Exception {
        // Initialize the database
        insertedLossCauseEstimate = lossCauseEstimateRepository.save(lossCauseEstimate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lossCauseEstimate using partial update
        LossCauseEstimate partialUpdatedLossCauseEstimate = new LossCauseEstimate();
        partialUpdatedLossCauseEstimate.setId(lossCauseEstimate.getId());

        partialUpdatedLossCauseEstimate
            .cause(UPDATED_CAUSE)
            .estimatedWh(UPDATED_ESTIMATED_WH)
            .method(UPDATED_METHOD)
            .evidenceNote(UPDATED_EVIDENCE_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLossCauseEstimate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedLossCauseEstimate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LossCauseEstimate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLossCauseEstimateUpdatableFieldsEquals(
            partialUpdatedLossCauseEstimate,
            getPersistedLossCauseEstimate(partialUpdatedLossCauseEstimate)
        );
    }

    @Test
    void patchNonExistingLossCauseEstimate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossCauseEstimate.setId(longCount.incrementAndGet());

        // Create the LossCauseEstimate
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, lossCauseEstimateDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossCauseEstimate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLossCauseEstimate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossCauseEstimate.setId(longCount.incrementAndGet());

        // Create the LossCauseEstimate
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossCauseEstimate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLossCauseEstimate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossCauseEstimate.setId(longCount.incrementAndGet());

        // Create the LossCauseEstimate
        LossCauseEstimateDTO lossCauseEstimateDTO = lossCauseEstimateMapper.toDto(lossCauseEstimate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(lossCauseEstimateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the LossCauseEstimate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLossCauseEstimate() {
        // Initialize the database
        insertedLossCauseEstimate = lossCauseEstimateRepository.save(lossCauseEstimate).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the lossCauseEstimate
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, lossCauseEstimate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return lossCauseEstimateRepository.count().block();
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

    protected LossCauseEstimate getPersistedLossCauseEstimate(LossCauseEstimate lossCauseEstimate) {
        return lossCauseEstimateRepository.findById(lossCauseEstimate.getId()).block();
    }

    protected void assertPersistedLossCauseEstimateToMatchAllProperties(LossCauseEstimate expectedLossCauseEstimate) {
        // Test fails because reactive api returns an empty object instead of null
        // assertLossCauseEstimateAllPropertiesEquals(expectedLossCauseEstimate, getPersistedLossCauseEstimate(expectedLossCauseEstimate));
        assertLossCauseEstimateUpdatableFieldsEquals(expectedLossCauseEstimate, getPersistedLossCauseEstimate(expectedLossCauseEstimate));
    }

    protected void assertPersistedLossCauseEstimateToMatchUpdatableProperties(LossCauseEstimate expectedLossCauseEstimate) {
        // Test fails because reactive api returns an empty object instead of null
        // assertLossCauseEstimateAllUpdatablePropertiesEquals(expectedLossCauseEstimate, getPersistedLossCauseEstimate(expectedLossCauseEstimate));
        assertLossCauseEstimateUpdatableFieldsEquals(expectedLossCauseEstimate, getPersistedLossCauseEstimate(expectedLossCauseEstimate));
    }
}
