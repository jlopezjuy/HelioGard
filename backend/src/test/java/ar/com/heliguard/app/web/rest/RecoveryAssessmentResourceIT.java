package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.RecoveryAssessmentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.MaintenanceIntervention;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.RecoveryAssessment;
import ar.com.heliguard.app.repository.RecoveryAssessmentRepository;
import ar.com.heliguard.app.service.RecoveryAssessmentService;
import ar.com.heliguard.app.service.dto.RecoveryAssessmentDTO;
import ar.com.heliguard.app.service.mapper.RecoveryAssessmentMapper;
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
 * Integration tests for the {@link RecoveryAssessmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RecoveryAssessmentResourceIT {

    private static final Instant DEFAULT_ASSESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSESSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_BASELINE_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_BASELINE_METHOD = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_EXPECTED_RECOVERED_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPECTED_RECOVERED_WH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_VERIFIED_RECOVERED_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_VERIFIED_RECOVERED_WH = new BigDecimal(2);

    private static final String DEFAULT_CONFIDENCE_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_CONFIDENCE_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/recovery-assessments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RecoveryAssessmentRepository recoveryAssessmentRepository;

    @Mock
    private RecoveryAssessmentRepository recoveryAssessmentRepositoryMock;

    @Autowired
    private RecoveryAssessmentMapper recoveryAssessmentMapper;

    @Mock
    private RecoveryAssessmentService recoveryAssessmentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private RecoveryAssessment recoveryAssessment;

    private RecoveryAssessment insertedRecoveryAssessment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecoveryAssessment createEntity(R2dbcEntityTemplate template) {
        RecoveryAssessment recoveryAssessment = new RecoveryAssessment()
            .assessedAt(DEFAULT_ASSESSED_AT)
            .baselineMethod(DEFAULT_BASELINE_METHOD)
            .expectedRecoveredWh(DEFAULT_EXPECTED_RECOVERED_WH)
            .verifiedRecoveredWh(DEFAULT_VERIFIED_RECOVERED_WH)
            .confidenceNote(DEFAULT_CONFIDENCE_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        recoveryAssessment.setOrganization(organization);
        // Add required entity
        MaintenanceIntervention maintenanceIntervention;
        maintenanceIntervention = template.insert(MaintenanceInterventionResourceIT.createEntity(template)).block();
        recoveryAssessment.setIntervention(maintenanceIntervention);
        return recoveryAssessment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecoveryAssessment createUpdatedEntity(R2dbcEntityTemplate template) {
        RecoveryAssessment updatedRecoveryAssessment = new RecoveryAssessment()
            .assessedAt(UPDATED_ASSESSED_AT)
            .baselineMethod(UPDATED_BASELINE_METHOD)
            .expectedRecoveredWh(UPDATED_EXPECTED_RECOVERED_WH)
            .verifiedRecoveredWh(UPDATED_VERIFIED_RECOVERED_WH)
            .confidenceNote(UPDATED_CONFIDENCE_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedRecoveryAssessment.setOrganization(organization);
        // Add required entity
        MaintenanceIntervention maintenanceIntervention;
        maintenanceIntervention = template.insert(MaintenanceInterventionResourceIT.createUpdatedEntity(template)).block();
        updatedRecoveryAssessment.setIntervention(maintenanceIntervention);
        return updatedRecoveryAssessment;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(RecoveryAssessment.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        MaintenanceInterventionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        recoveryAssessment = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedRecoveryAssessment != null) {
            recoveryAssessmentRepository.delete(insertedRecoveryAssessment).block();
            insertedRecoveryAssessment = null;
        }
        deleteEntities(template);
    }

    @Test
    void createRecoveryAssessment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RecoveryAssessment
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);
        var returnedRecoveryAssessmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RecoveryAssessmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the RecoveryAssessment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRecoveryAssessment = recoveryAssessmentMapper.toEntity(returnedRecoveryAssessmentDTO);
        assertRecoveryAssessmentUpdatableFieldsEquals(
            returnedRecoveryAssessment,
            getPersistedRecoveryAssessment(returnedRecoveryAssessment)
        );

        insertedRecoveryAssessment = returnedRecoveryAssessment;
    }

    @Test
    void createRecoveryAssessmentWithExistingId() throws Exception {
        // Create the RecoveryAssessment with an existing ID
        recoveryAssessment.setId(1L);
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RecoveryAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAssessedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        recoveryAssessment.setAssessedAt(null);

        // Create the RecoveryAssessment, which fails.
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkBaselineMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        recoveryAssessment.setBaselineMethod(null);

        // Create the RecoveryAssessment, which fails.
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRecoveryAssessmentsAsStream() {
        // Initialize the database
        recoveryAssessmentRepository.save(recoveryAssessment).block();

        List<RecoveryAssessment> recoveryAssessmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RecoveryAssessmentDTO.class)
            .getResponseBody()
            .map(recoveryAssessmentMapper::toEntity)
            .filter(recoveryAssessment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(recoveryAssessmentList).isNotNull();
        assertThat(recoveryAssessmentList).hasSize(1);
        RecoveryAssessment testRecoveryAssessment = recoveryAssessmentList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertRecoveryAssessmentAllPropertiesEquals(recoveryAssessment, testRecoveryAssessment);
        assertRecoveryAssessmentUpdatableFieldsEquals(recoveryAssessment, testRecoveryAssessment);
    }

    @Test
    void getAllRecoveryAssessments() {
        // Initialize the database
        insertedRecoveryAssessment = recoveryAssessmentRepository.save(recoveryAssessment).block();

        // Get all the recoveryAssessmentList
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
            .value(hasItem(recoveryAssessment.getId().intValue()))
            .jsonPath("$.[*].assessedAt")
            .value(hasItem(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.[*].baselineMethod")
            .value(hasItem(DEFAULT_BASELINE_METHOD))
            .jsonPath("$.[*].expectedRecoveredWh")
            .value(hasItem(sameNumber(DEFAULT_EXPECTED_RECOVERED_WH)))
            .jsonPath("$.[*].verifiedRecoveredWh")
            .value(hasItem(sameNumber(DEFAULT_VERIFIED_RECOVERED_WH)))
            .jsonPath("$.[*].confidenceNote")
            .value(hasItem(DEFAULT_CONFIDENCE_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRecoveryAssessmentsWithEagerRelationshipsIsEnabled() {
        when(recoveryAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(recoveryAssessmentServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRecoveryAssessmentsWithEagerRelationshipsIsNotEnabled() {
        when(recoveryAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(recoveryAssessmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getRecoveryAssessment() {
        // Initialize the database
        insertedRecoveryAssessment = recoveryAssessmentRepository.save(recoveryAssessment).block();

        // Get the recoveryAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, recoveryAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(recoveryAssessment.getId().intValue()))
            .jsonPath("$.assessedAt")
            .value(is(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.baselineMethod")
            .value(is(DEFAULT_BASELINE_METHOD))
            .jsonPath("$.expectedRecoveredWh")
            .value(is(sameNumber(DEFAULT_EXPECTED_RECOVERED_WH)))
            .jsonPath("$.verifiedRecoveredWh")
            .value(is(sameNumber(DEFAULT_VERIFIED_RECOVERED_WH)))
            .jsonPath("$.confidenceNote")
            .value(is(DEFAULT_CONFIDENCE_NOTE));
    }

    @Test
    void getNonExistingRecoveryAssessment() {
        // Get the recoveryAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRecoveryAssessment() throws Exception {
        // Initialize the database
        insertedRecoveryAssessment = recoveryAssessmentRepository.save(recoveryAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recoveryAssessment
        RecoveryAssessment updatedRecoveryAssessment = recoveryAssessmentRepository.findById(recoveryAssessment.getId()).block();
        updatedRecoveryAssessment
            .assessedAt(UPDATED_ASSESSED_AT)
            .baselineMethod(UPDATED_BASELINE_METHOD)
            .expectedRecoveredWh(UPDATED_EXPECTED_RECOVERED_WH)
            .verifiedRecoveredWh(UPDATED_VERIFIED_RECOVERED_WH)
            .confidenceNote(UPDATED_CONFIDENCE_NOTE);
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(updatedRecoveryAssessment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, recoveryAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RecoveryAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRecoveryAssessmentToMatchAllProperties(updatedRecoveryAssessment);
    }

    @Test
    void putNonExistingRecoveryAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recoveryAssessment.setId(longCount.incrementAndGet());

        // Create the RecoveryAssessment
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, recoveryAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RecoveryAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRecoveryAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recoveryAssessment.setId(longCount.incrementAndGet());

        // Create the RecoveryAssessment
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RecoveryAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRecoveryAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recoveryAssessment.setId(longCount.incrementAndGet());

        // Create the RecoveryAssessment
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RecoveryAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRecoveryAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedRecoveryAssessment = recoveryAssessmentRepository.save(recoveryAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recoveryAssessment using partial update
        RecoveryAssessment partialUpdatedRecoveryAssessment = new RecoveryAssessment();
        partialUpdatedRecoveryAssessment.setId(recoveryAssessment.getId());

        partialUpdatedRecoveryAssessment.assessedAt(UPDATED_ASSESSED_AT).verifiedRecoveredWh(UPDATED_VERIFIED_RECOVERED_WH);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRecoveryAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRecoveryAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RecoveryAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRecoveryAssessmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRecoveryAssessment, recoveryAssessment),
            getPersistedRecoveryAssessment(recoveryAssessment)
        );
    }

    @Test
    void fullUpdateRecoveryAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedRecoveryAssessment = recoveryAssessmentRepository.save(recoveryAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recoveryAssessment using partial update
        RecoveryAssessment partialUpdatedRecoveryAssessment = new RecoveryAssessment();
        partialUpdatedRecoveryAssessment.setId(recoveryAssessment.getId());

        partialUpdatedRecoveryAssessment
            .assessedAt(UPDATED_ASSESSED_AT)
            .baselineMethod(UPDATED_BASELINE_METHOD)
            .expectedRecoveredWh(UPDATED_EXPECTED_RECOVERED_WH)
            .verifiedRecoveredWh(UPDATED_VERIFIED_RECOVERED_WH)
            .confidenceNote(UPDATED_CONFIDENCE_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRecoveryAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRecoveryAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RecoveryAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRecoveryAssessmentUpdatableFieldsEquals(
            partialUpdatedRecoveryAssessment,
            getPersistedRecoveryAssessment(partialUpdatedRecoveryAssessment)
        );
    }

    @Test
    void patchNonExistingRecoveryAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recoveryAssessment.setId(longCount.incrementAndGet());

        // Create the RecoveryAssessment
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, recoveryAssessmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RecoveryAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRecoveryAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recoveryAssessment.setId(longCount.incrementAndGet());

        // Create the RecoveryAssessment
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RecoveryAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRecoveryAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recoveryAssessment.setId(longCount.incrementAndGet());

        // Create the RecoveryAssessment
        RecoveryAssessmentDTO recoveryAssessmentDTO = recoveryAssessmentMapper.toDto(recoveryAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(recoveryAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RecoveryAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRecoveryAssessment() {
        // Initialize the database
        insertedRecoveryAssessment = recoveryAssessmentRepository.save(recoveryAssessment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the recoveryAssessment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, recoveryAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return recoveryAssessmentRepository.count().block();
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

    protected RecoveryAssessment getPersistedRecoveryAssessment(RecoveryAssessment recoveryAssessment) {
        return recoveryAssessmentRepository.findById(recoveryAssessment.getId()).block();
    }

    protected void assertPersistedRecoveryAssessmentToMatchAllProperties(RecoveryAssessment expectedRecoveryAssessment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRecoveryAssessmentAllPropertiesEquals(expectedRecoveryAssessment, getPersistedRecoveryAssessment(expectedRecoveryAssessment));
        assertRecoveryAssessmentUpdatableFieldsEquals(
            expectedRecoveryAssessment,
            getPersistedRecoveryAssessment(expectedRecoveryAssessment)
        );
    }

    protected void assertPersistedRecoveryAssessmentToMatchUpdatableProperties(RecoveryAssessment expectedRecoveryAssessment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRecoveryAssessmentAllUpdatablePropertiesEquals(expectedRecoveryAssessment, getPersistedRecoveryAssessment(expectedRecoveryAssessment));
        assertRecoveryAssessmentUpdatableFieldsEquals(
            expectedRecoveryAssessment,
            getPersistedRecoveryAssessment(expectedRecoveryAssessment)
        );
    }
}
