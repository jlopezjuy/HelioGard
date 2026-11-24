package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.IntegrityAssessmentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import ar.com.heliguard.app.repository.IntegrityAssessmentRepository;
import ar.com.heliguard.app.service.IntegrityAssessmentService;
import ar.com.heliguard.app.service.dto.IntegrityAssessmentDTO;
import ar.com.heliguard.app.service.mapper.IntegrityAssessmentMapper;
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
 * Integration tests for the {@link IntegrityAssessmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class IntegrityAssessmentResourceIT {

    private static final Instant DEFAULT_ASSESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSESSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final DataTrustLevel DEFAULT_TRUST_LEVEL = DataTrustLevel.VALIDATED;
    private static final DataTrustLevel UPDATED_TRUST_LEVEL = DataTrustLevel.ESTIMATED;

    private static final String DEFAULT_RULE_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_RULE_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/integrity-assessments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IntegrityAssessmentRepository integrityAssessmentRepository;

    @Mock
    private IntegrityAssessmentRepository integrityAssessmentRepositoryMock;

    @Autowired
    private IntegrityAssessmentMapper integrityAssessmentMapper;

    @Mock
    private IntegrityAssessmentService integrityAssessmentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private IntegrityAssessment integrityAssessment;

    private IntegrityAssessment insertedIntegrityAssessment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntegrityAssessment createEntity(R2dbcEntityTemplate template) {
        IntegrityAssessment integrityAssessment = new IntegrityAssessment()
            .assessedAt(DEFAULT_ASSESSED_AT)
            .trustLevel(DEFAULT_TRUST_LEVEL)
            .ruleVersion(DEFAULT_RULE_VERSION)
            .reason(DEFAULT_REASON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        integrityAssessment.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        integrityAssessment.setInstallation(installation);
        // Add required entity
        TelemetryIntegrityEvent telemetryIntegrityEvent;
        telemetryIntegrityEvent = template.insert(TelemetryIntegrityEventResourceIT.createEntity(template)).block();
        integrityAssessment.setIntegrityEvent(telemetryIntegrityEvent);
        return integrityAssessment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntegrityAssessment createUpdatedEntity(R2dbcEntityTemplate template) {
        IntegrityAssessment updatedIntegrityAssessment = new IntegrityAssessment()
            .assessedAt(UPDATED_ASSESSED_AT)
            .trustLevel(UPDATED_TRUST_LEVEL)
            .ruleVersion(UPDATED_RULE_VERSION)
            .reason(UPDATED_REASON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedIntegrityAssessment.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedIntegrityAssessment.setInstallation(installation);
        // Add required entity
        TelemetryIntegrityEvent telemetryIntegrityEvent;
        telemetryIntegrityEvent = template.insert(TelemetryIntegrityEventResourceIT.createUpdatedEntity(template)).block();
        updatedIntegrityAssessment.setIntegrityEvent(telemetryIntegrityEvent);
        return updatedIntegrityAssessment;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(IntegrityAssessment.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
        TelemetryIntegrityEventResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        integrityAssessment = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedIntegrityAssessment != null) {
            integrityAssessmentRepository.delete(insertedIntegrityAssessment).block();
            insertedIntegrityAssessment = null;
        }
        deleteEntities(template);
    }

    @Test
    void createIntegrityAssessment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the IntegrityAssessment
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);
        var returnedIntegrityAssessmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(IntegrityAssessmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the IntegrityAssessment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIntegrityAssessment = integrityAssessmentMapper.toEntity(returnedIntegrityAssessmentDTO);
        assertIntegrityAssessmentUpdatableFieldsEquals(
            returnedIntegrityAssessment,
            getPersistedIntegrityAssessment(returnedIntegrityAssessment)
        );

        insertedIntegrityAssessment = returnedIntegrityAssessment;
    }

    @Test
    void createIntegrityAssessmentWithExistingId() throws Exception {
        // Create the IntegrityAssessment with an existing ID
        integrityAssessment.setId(1L);
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IntegrityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAssessedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        integrityAssessment.setAssessedAt(null);

        // Create the IntegrityAssessment, which fails.
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTrustLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        integrityAssessment.setTrustLevel(null);

        // Create the IntegrityAssessment, which fails.
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRuleVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        integrityAssessment.setRuleVersion(null);

        // Create the IntegrityAssessment, which fails.
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllIntegrityAssessmentsAsStream() {
        // Initialize the database
        integrityAssessmentRepository.save(integrityAssessment).block();

        List<IntegrityAssessment> integrityAssessmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(IntegrityAssessmentDTO.class)
            .getResponseBody()
            .map(integrityAssessmentMapper::toEntity)
            .filter(integrityAssessment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(integrityAssessmentList).isNotNull();
        assertThat(integrityAssessmentList).hasSize(1);
        IntegrityAssessment testIntegrityAssessment = integrityAssessmentList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertIntegrityAssessmentAllPropertiesEquals(integrityAssessment, testIntegrityAssessment);
        assertIntegrityAssessmentUpdatableFieldsEquals(integrityAssessment, testIntegrityAssessment);
    }

    @Test
    void getAllIntegrityAssessments() {
        // Initialize the database
        insertedIntegrityAssessment = integrityAssessmentRepository.save(integrityAssessment).block();

        // Get all the integrityAssessmentList
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
            .value(hasItem(integrityAssessment.getId().intValue()))
            .jsonPath("$.[*].assessedAt")
            .value(hasItem(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.[*].trustLevel")
            .value(hasItem(DEFAULT_TRUST_LEVEL.toString()))
            .jsonPath("$.[*].ruleVersion")
            .value(hasItem(DEFAULT_RULE_VERSION))
            .jsonPath("$.[*].reason")
            .value(hasItem(DEFAULT_REASON));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIntegrityAssessmentsWithEagerRelationshipsIsEnabled() {
        when(integrityAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(integrityAssessmentServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIntegrityAssessmentsWithEagerRelationshipsIsNotEnabled() {
        when(integrityAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(integrityAssessmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getIntegrityAssessment() {
        // Initialize the database
        insertedIntegrityAssessment = integrityAssessmentRepository.save(integrityAssessment).block();

        // Get the integrityAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, integrityAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(integrityAssessment.getId().intValue()))
            .jsonPath("$.assessedAt")
            .value(is(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.trustLevel")
            .value(is(DEFAULT_TRUST_LEVEL.toString()))
            .jsonPath("$.ruleVersion")
            .value(is(DEFAULT_RULE_VERSION))
            .jsonPath("$.reason")
            .value(is(DEFAULT_REASON));
    }

    @Test
    void getNonExistingIntegrityAssessment() {
        // Get the integrityAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingIntegrityAssessment() throws Exception {
        // Initialize the database
        insertedIntegrityAssessment = integrityAssessmentRepository.save(integrityAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the integrityAssessment
        IntegrityAssessment updatedIntegrityAssessment = integrityAssessmentRepository.findById(integrityAssessment.getId()).block();
        updatedIntegrityAssessment
            .assessedAt(UPDATED_ASSESSED_AT)
            .trustLevel(UPDATED_TRUST_LEVEL)
            .ruleVersion(UPDATED_RULE_VERSION)
            .reason(UPDATED_REASON);
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(updatedIntegrityAssessment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, integrityAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IntegrityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIntegrityAssessmentToMatchAllProperties(updatedIntegrityAssessment);
    }

    @Test
    void putNonExistingIntegrityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        integrityAssessment.setId(longCount.incrementAndGet());

        // Create the IntegrityAssessment
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, integrityAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IntegrityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchIntegrityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        integrityAssessment.setId(longCount.incrementAndGet());

        // Create the IntegrityAssessment
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IntegrityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamIntegrityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        integrityAssessment.setId(longCount.incrementAndGet());

        // Create the IntegrityAssessment
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the IntegrityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateIntegrityAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedIntegrityAssessment = integrityAssessmentRepository.save(integrityAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the integrityAssessment using partial update
        IntegrityAssessment partialUpdatedIntegrityAssessment = new IntegrityAssessment();
        partialUpdatedIntegrityAssessment.setId(integrityAssessment.getId());

        partialUpdatedIntegrityAssessment.assessedAt(UPDATED_ASSESSED_AT).trustLevel(UPDATED_TRUST_LEVEL).reason(UPDATED_REASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIntegrityAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedIntegrityAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IntegrityAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntegrityAssessmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIntegrityAssessment, integrityAssessment),
            getPersistedIntegrityAssessment(integrityAssessment)
        );
    }

    @Test
    void fullUpdateIntegrityAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedIntegrityAssessment = integrityAssessmentRepository.save(integrityAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the integrityAssessment using partial update
        IntegrityAssessment partialUpdatedIntegrityAssessment = new IntegrityAssessment();
        partialUpdatedIntegrityAssessment.setId(integrityAssessment.getId());

        partialUpdatedIntegrityAssessment
            .assessedAt(UPDATED_ASSESSED_AT)
            .trustLevel(UPDATED_TRUST_LEVEL)
            .ruleVersion(UPDATED_RULE_VERSION)
            .reason(UPDATED_REASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIntegrityAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedIntegrityAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IntegrityAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntegrityAssessmentUpdatableFieldsEquals(
            partialUpdatedIntegrityAssessment,
            getPersistedIntegrityAssessment(partialUpdatedIntegrityAssessment)
        );
    }

    @Test
    void patchNonExistingIntegrityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        integrityAssessment.setId(longCount.incrementAndGet());

        // Create the IntegrityAssessment
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, integrityAssessmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IntegrityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchIntegrityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        integrityAssessment.setId(longCount.incrementAndGet());

        // Create the IntegrityAssessment
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IntegrityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamIntegrityAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        integrityAssessment.setId(longCount.incrementAndGet());

        // Create the IntegrityAssessment
        IntegrityAssessmentDTO integrityAssessmentDTO = integrityAssessmentMapper.toDto(integrityAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(integrityAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the IntegrityAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteIntegrityAssessment() {
        // Initialize the database
        insertedIntegrityAssessment = integrityAssessmentRepository.save(integrityAssessment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the integrityAssessment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, integrityAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return integrityAssessmentRepository.count().block();
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

    protected IntegrityAssessment getPersistedIntegrityAssessment(IntegrityAssessment integrityAssessment) {
        return integrityAssessmentRepository.findById(integrityAssessment.getId()).block();
    }

    protected void assertPersistedIntegrityAssessmentToMatchAllProperties(IntegrityAssessment expectedIntegrityAssessment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertIntegrityAssessmentAllPropertiesEquals(expectedIntegrityAssessment, getPersistedIntegrityAssessment(expectedIntegrityAssessment));
        assertIntegrityAssessmentUpdatableFieldsEquals(
            expectedIntegrityAssessment,
            getPersistedIntegrityAssessment(expectedIntegrityAssessment)
        );
    }

    protected void assertPersistedIntegrityAssessmentToMatchUpdatableProperties(IntegrityAssessment expectedIntegrityAssessment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertIntegrityAssessmentAllUpdatablePropertiesEquals(expectedIntegrityAssessment, getPersistedIntegrityAssessment(expectedIntegrityAssessment));
        assertIntegrityAssessmentUpdatableFieldsEquals(
            expectedIntegrityAssessment,
            getPersistedIntegrityAssessment(expectedIntegrityAssessment)
        );
    }
}
