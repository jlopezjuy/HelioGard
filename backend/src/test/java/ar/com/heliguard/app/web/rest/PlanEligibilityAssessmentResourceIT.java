package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.PlanEligibilityAssessmentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanEligibilityAssessment;
import ar.com.heliguard.app.domain.enumeration.PlanEligibilityStatus;
import ar.com.heliguard.app.repository.PlanEligibilityAssessmentRepository;
import ar.com.heliguard.app.service.PlanEligibilityAssessmentService;
import ar.com.heliguard.app.service.dto.PlanEligibilityAssessmentDTO;
import ar.com.heliguard.app.service.mapper.PlanEligibilityAssessmentMapper;
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
 * Integration tests for the {@link PlanEligibilityAssessmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlanEligibilityAssessmentResourceIT {

    private static final PlanEligibilityStatus DEFAULT_STATUS = PlanEligibilityStatus.ELIGIBLE;
    private static final PlanEligibilityStatus UPDATED_STATUS = PlanEligibilityStatus.CONDITIONAL;

    private static final Instant DEFAULT_ASSESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSESSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_RATIONALE = "AAAAAAAAAA";
    private static final String UPDATED_RATIONALE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_REQUIRES_CORRECTION = false;
    private static final Boolean UPDATED_REQUIRES_CORRECTION = true;

    private static final String ENTITY_API_URL = "/api/plan-eligibility-assessments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlanEligibilityAssessmentRepository planEligibilityAssessmentRepository;

    @Mock
    private PlanEligibilityAssessmentRepository planEligibilityAssessmentRepositoryMock;

    @Autowired
    private PlanEligibilityAssessmentMapper planEligibilityAssessmentMapper;

    @Mock
    private PlanEligibilityAssessmentService planEligibilityAssessmentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private PlanEligibilityAssessment planEligibilityAssessment;

    private PlanEligibilityAssessment insertedPlanEligibilityAssessment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanEligibilityAssessment createEntity(R2dbcEntityTemplate template) {
        PlanEligibilityAssessment planEligibilityAssessment = new PlanEligibilityAssessment()
            .status(DEFAULT_STATUS)
            .assessedAt(DEFAULT_ASSESSED_AT)
            .rationale(DEFAULT_RATIONALE)
            .requiresCorrection(DEFAULT_REQUIRES_CORRECTION);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        planEligibilityAssessment.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        planEligibilityAssessment.setInstallation(installation);
        return planEligibilityAssessment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanEligibilityAssessment createUpdatedEntity(R2dbcEntityTemplate template) {
        PlanEligibilityAssessment updatedPlanEligibilityAssessment = new PlanEligibilityAssessment()
            .status(UPDATED_STATUS)
            .assessedAt(UPDATED_ASSESSED_AT)
            .rationale(UPDATED_RATIONALE)
            .requiresCorrection(UPDATED_REQUIRES_CORRECTION);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedPlanEligibilityAssessment.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedPlanEligibilityAssessment.setInstallation(installation);
        return updatedPlanEligibilityAssessment;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(PlanEligibilityAssessment.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        planEligibilityAssessment = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedPlanEligibilityAssessment != null) {
            planEligibilityAssessmentRepository.delete(insertedPlanEligibilityAssessment).block();
            insertedPlanEligibilityAssessment = null;
        }
        deleteEntities(template);
    }

    @Test
    void createPlanEligibilityAssessment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PlanEligibilityAssessment
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);
        var returnedPlanEligibilityAssessmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PlanEligibilityAssessmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the PlanEligibilityAssessment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlanEligibilityAssessment = planEligibilityAssessmentMapper.toEntity(returnedPlanEligibilityAssessmentDTO);
        assertPlanEligibilityAssessmentUpdatableFieldsEquals(
            returnedPlanEligibilityAssessment,
            getPersistedPlanEligibilityAssessment(returnedPlanEligibilityAssessment)
        );

        insertedPlanEligibilityAssessment = returnedPlanEligibilityAssessment;
    }

    @Test
    void createPlanEligibilityAssessmentWithExistingId() throws Exception {
        // Create the PlanEligibilityAssessment with an existing ID
        planEligibilityAssessment.setId(1L);
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanEligibilityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planEligibilityAssessment.setStatus(null);

        // Create the PlanEligibilityAssessment, which fails.
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAssessedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planEligibilityAssessment.setAssessedAt(null);

        // Create the PlanEligibilityAssessment, which fails.
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRequiresCorrectionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planEligibilityAssessment.setRequiresCorrection(null);

        // Create the PlanEligibilityAssessment, which fails.
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPlanEligibilityAssessmentsAsStream() {
        // Initialize the database
        planEligibilityAssessmentRepository.save(planEligibilityAssessment).block();

        List<PlanEligibilityAssessment> planEligibilityAssessmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PlanEligibilityAssessmentDTO.class)
            .getResponseBody()
            .map(planEligibilityAssessmentMapper::toEntity)
            .filter(planEligibilityAssessment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(planEligibilityAssessmentList).isNotNull();
        assertThat(planEligibilityAssessmentList).hasSize(1);
        PlanEligibilityAssessment testPlanEligibilityAssessment = planEligibilityAssessmentList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertPlanEligibilityAssessmentAllPropertiesEquals(planEligibilityAssessment, testPlanEligibilityAssessment);
        assertPlanEligibilityAssessmentUpdatableFieldsEquals(planEligibilityAssessment, testPlanEligibilityAssessment);
    }

    @Test
    void getAllPlanEligibilityAssessments() {
        // Initialize the database
        insertedPlanEligibilityAssessment = planEligibilityAssessmentRepository.save(planEligibilityAssessment).block();

        // Get all the planEligibilityAssessmentList
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
            .value(hasItem(planEligibilityAssessment.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].assessedAt")
            .value(hasItem(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.[*].rationale")
            .value(hasItem(DEFAULT_RATIONALE))
            .jsonPath("$.[*].requiresCorrection")
            .value(hasItem(DEFAULT_REQUIRES_CORRECTION));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlanEligibilityAssessmentsWithEagerRelationshipsIsEnabled() {
        when(planEligibilityAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(planEligibilityAssessmentServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlanEligibilityAssessmentsWithEagerRelationshipsIsNotEnabled() {
        when(planEligibilityAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(planEligibilityAssessmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPlanEligibilityAssessment() {
        // Initialize the database
        insertedPlanEligibilityAssessment = planEligibilityAssessmentRepository.save(planEligibilityAssessment).block();

        // Get the planEligibilityAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, planEligibilityAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(planEligibilityAssessment.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.assessedAt")
            .value(is(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.rationale")
            .value(is(DEFAULT_RATIONALE))
            .jsonPath("$.requiresCorrection")
            .value(is(DEFAULT_REQUIRES_CORRECTION));
    }

    @Test
    void getNonExistingPlanEligibilityAssessment() {
        // Get the planEligibilityAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlanEligibilityAssessment() throws Exception {
        // Initialize the database
        insertedPlanEligibilityAssessment = planEligibilityAssessmentRepository.save(planEligibilityAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planEligibilityAssessment
        PlanEligibilityAssessment updatedPlanEligibilityAssessment = planEligibilityAssessmentRepository
            .findById(planEligibilityAssessment.getId())
            .block();
        updatedPlanEligibilityAssessment
            .status(UPDATED_STATUS)
            .assessedAt(UPDATED_ASSESSED_AT)
            .rationale(UPDATED_RATIONALE)
            .requiresCorrection(UPDATED_REQUIRES_CORRECTION);
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(updatedPlanEligibilityAssessment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, planEligibilityAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanEligibilityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlanEligibilityAssessmentToMatchAllProperties(updatedPlanEligibilityAssessment);
    }

    @Test
    void putNonExistingPlanEligibilityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planEligibilityAssessment.setId(longCount.incrementAndGet());

        // Create the PlanEligibilityAssessment
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, planEligibilityAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanEligibilityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPlanEligibilityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planEligibilityAssessment.setId(longCount.incrementAndGet());

        // Create the PlanEligibilityAssessment
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanEligibilityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPlanEligibilityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planEligibilityAssessment.setId(longCount.incrementAndGet());

        // Create the PlanEligibilityAssessment
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlanEligibilityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePlanEligibilityAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedPlanEligibilityAssessment = planEligibilityAssessmentRepository.save(planEligibilityAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planEligibilityAssessment using partial update
        PlanEligibilityAssessment partialUpdatedPlanEligibilityAssessment = new PlanEligibilityAssessment();
        partialUpdatedPlanEligibilityAssessment.setId(planEligibilityAssessment.getId());

        partialUpdatedPlanEligibilityAssessment.status(UPDATED_STATUS).requiresCorrection(UPDATED_REQUIRES_CORRECTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlanEligibilityAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlanEligibilityAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanEligibilityAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanEligibilityAssessmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlanEligibilityAssessment, planEligibilityAssessment),
            getPersistedPlanEligibilityAssessment(planEligibilityAssessment)
        );
    }

    @Test
    void fullUpdatePlanEligibilityAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedPlanEligibilityAssessment = planEligibilityAssessmentRepository.save(planEligibilityAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planEligibilityAssessment using partial update
        PlanEligibilityAssessment partialUpdatedPlanEligibilityAssessment = new PlanEligibilityAssessment();
        partialUpdatedPlanEligibilityAssessment.setId(planEligibilityAssessment.getId());

        partialUpdatedPlanEligibilityAssessment
            .status(UPDATED_STATUS)
            .assessedAt(UPDATED_ASSESSED_AT)
            .rationale(UPDATED_RATIONALE)
            .requiresCorrection(UPDATED_REQUIRES_CORRECTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlanEligibilityAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlanEligibilityAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanEligibilityAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanEligibilityAssessmentUpdatableFieldsEquals(
            partialUpdatedPlanEligibilityAssessment,
            getPersistedPlanEligibilityAssessment(partialUpdatedPlanEligibilityAssessment)
        );
    }

    @Test
    void patchNonExistingPlanEligibilityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planEligibilityAssessment.setId(longCount.incrementAndGet());

        // Create the PlanEligibilityAssessment
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, planEligibilityAssessmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanEligibilityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPlanEligibilityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planEligibilityAssessment.setId(longCount.incrementAndGet());

        // Create the PlanEligibilityAssessment
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanEligibilityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPlanEligibilityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planEligibilityAssessment.setId(longCount.incrementAndGet());

        // Create the PlanEligibilityAssessment
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = planEligibilityAssessmentMapper.toDto(planEligibilityAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planEligibilityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlanEligibilityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePlanEligibilityAssessment() {
        // Initialize the database
        insertedPlanEligibilityAssessment = planEligibilityAssessmentRepository.save(planEligibilityAssessment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the planEligibilityAssessment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, planEligibilityAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return planEligibilityAssessmentRepository.count().block();
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

    protected PlanEligibilityAssessment getPersistedPlanEligibilityAssessment(PlanEligibilityAssessment planEligibilityAssessment) {
        return planEligibilityAssessmentRepository.findById(planEligibilityAssessment.getId()).block();
    }

    protected void assertPersistedPlanEligibilityAssessmentToMatchAllProperties(
        PlanEligibilityAssessment expectedPlanEligibilityAssessment
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlanEligibilityAssessmentAllPropertiesEquals(expectedPlanEligibilityAssessment, getPersistedPlanEligibilityAssessment(expectedPlanEligibilityAssessment));
        assertPlanEligibilityAssessmentUpdatableFieldsEquals(
            expectedPlanEligibilityAssessment,
            getPersistedPlanEligibilityAssessment(expectedPlanEligibilityAssessment)
        );
    }

    protected void assertPersistedPlanEligibilityAssessmentToMatchUpdatableProperties(
        PlanEligibilityAssessment expectedPlanEligibilityAssessment
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlanEligibilityAssessmentAllUpdatablePropertiesEquals(expectedPlanEligibilityAssessment, getPersistedPlanEligibilityAssessment(expectedPlanEligibilityAssessment));
        assertPlanEligibilityAssessmentUpdatableFieldsEquals(
            expectedPlanEligibilityAssessment,
            getPersistedPlanEligibilityAssessment(expectedPlanEligibilityAssessment)
        );
    }
}
