package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.LossAssessmentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.LossAssessmentStatus;
import ar.com.heliguard.app.repository.LossAssessmentRepository;
import ar.com.heliguard.app.service.LossAssessmentService;
import ar.com.heliguard.app.service.dto.LossAssessmentDTO;
import ar.com.heliguard.app.service.mapper.LossAssessmentMapper;
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
 * Integration tests for the {@link LossAssessmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class LossAssessmentResourceIT {

    private static final BigDecimal DEFAULT_TOTAL_LOSS_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_LOSS_WH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_LOSS_PCT = new BigDecimal(1);
    private static final BigDecimal UPDATED_LOSS_PCT = new BigDecimal(2);

    private static final String DEFAULT_METHODOLOGY = "AAAAAAAAAA";
    private static final String UPDATED_METHODOLOGY = "BBBBBBBBBB";

    private static final Instant DEFAULT_ASSESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSESSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final LossAssessmentStatus DEFAULT_VALIDATION_STATUS = LossAssessmentStatus.ESTIMATED;
    private static final LossAssessmentStatus UPDATED_VALIDATION_STATUS = LossAssessmentStatus.INCONCLUSIVE;

    private static final String ENTITY_API_URL = "/api/loss-assessments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LossAssessmentRepository lossAssessmentRepository;

    @Mock
    private LossAssessmentRepository lossAssessmentRepositoryMock;

    @Autowired
    private LossAssessmentMapper lossAssessmentMapper;

    @Mock
    private LossAssessmentService lossAssessmentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private LossAssessment lossAssessment;

    private LossAssessment insertedLossAssessment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LossAssessment createEntity(R2dbcEntityTemplate template) {
        LossAssessment lossAssessment = new LossAssessment()
            .totalLossWh(DEFAULT_TOTAL_LOSS_WH)
            .lossPct(DEFAULT_LOSS_PCT)
            .methodology(DEFAULT_METHODOLOGY)
            .assessedAt(DEFAULT_ASSESSED_AT)
            .validationStatus(DEFAULT_VALIDATION_STATUS);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        lossAssessment.setOrganization(organization);
        // Add required entity
        EnergyModelRun energyModelRun;
        energyModelRun = template.insert(EnergyModelRunResourceIT.createEntity(template)).block();
        lossAssessment.setModelRun(energyModelRun);
        return lossAssessment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LossAssessment createUpdatedEntity(R2dbcEntityTemplate template) {
        LossAssessment updatedLossAssessment = new LossAssessment()
            .totalLossWh(UPDATED_TOTAL_LOSS_WH)
            .lossPct(UPDATED_LOSS_PCT)
            .methodology(UPDATED_METHODOLOGY)
            .assessedAt(UPDATED_ASSESSED_AT)
            .validationStatus(UPDATED_VALIDATION_STATUS);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedLossAssessment.setOrganization(organization);
        // Add required entity
        EnergyModelRun energyModelRun;
        energyModelRun = template.insert(EnergyModelRunResourceIT.createUpdatedEntity(template)).block();
        updatedLossAssessment.setModelRun(energyModelRun);
        return updatedLossAssessment;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(LossAssessment.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        EnergyModelRunResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        lossAssessment = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedLossAssessment != null) {
            lossAssessmentRepository.delete(insertedLossAssessment).block();
            insertedLossAssessment = null;
        }
        deleteEntities(template);
    }

    @Test
    void createLossAssessment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LossAssessment
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);
        var returnedLossAssessmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(LossAssessmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the LossAssessment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLossAssessment = lossAssessmentMapper.toEntity(returnedLossAssessmentDTO);
        assertLossAssessmentUpdatableFieldsEquals(returnedLossAssessment, getPersistedLossAssessment(returnedLossAssessment));

        insertedLossAssessment = returnedLossAssessment;
    }

    @Test
    void createLossAssessmentWithExistingId() throws Exception {
        // Create the LossAssessment with an existing ID
        lossAssessment.setId(1L);
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTotalLossWhIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lossAssessment.setTotalLossWh(null);

        // Create the LossAssessment, which fails.
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMethodologyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lossAssessment.setMethodology(null);

        // Create the LossAssessment, which fails.
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAssessedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lossAssessment.setAssessedAt(null);

        // Create the LossAssessment, which fails.
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidationStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lossAssessment.setValidationStatus(null);

        // Create the LossAssessment, which fails.
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllLossAssessmentsAsStream() {
        // Initialize the database
        lossAssessmentRepository.save(lossAssessment).block();

        List<LossAssessment> lossAssessmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(LossAssessmentDTO.class)
            .getResponseBody()
            .map(lossAssessmentMapper::toEntity)
            .filter(lossAssessment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(lossAssessmentList).isNotNull();
        assertThat(lossAssessmentList).hasSize(1);
        LossAssessment testLossAssessment = lossAssessmentList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertLossAssessmentAllPropertiesEquals(lossAssessment, testLossAssessment);
        assertLossAssessmentUpdatableFieldsEquals(lossAssessment, testLossAssessment);
    }

    @Test
    void getAllLossAssessments() {
        // Initialize the database
        insertedLossAssessment = lossAssessmentRepository.save(lossAssessment).block();

        // Get all the lossAssessmentList
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
            .value(hasItem(lossAssessment.getId().intValue()))
            .jsonPath("$.[*].totalLossWh")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_LOSS_WH)))
            .jsonPath("$.[*].lossPct")
            .value(hasItem(sameNumber(DEFAULT_LOSS_PCT)))
            .jsonPath("$.[*].methodology")
            .value(hasItem(DEFAULT_METHODOLOGY))
            .jsonPath("$.[*].assessedAt")
            .value(hasItem(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.[*].validationStatus")
            .value(hasItem(DEFAULT_VALIDATION_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLossAssessmentsWithEagerRelationshipsIsEnabled() {
        when(lossAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(lossAssessmentServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLossAssessmentsWithEagerRelationshipsIsNotEnabled() {
        when(lossAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(lossAssessmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getLossAssessment() {
        // Initialize the database
        insertedLossAssessment = lossAssessmentRepository.save(lossAssessment).block();

        // Get the lossAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, lossAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(lossAssessment.getId().intValue()))
            .jsonPath("$.totalLossWh")
            .value(is(sameNumber(DEFAULT_TOTAL_LOSS_WH)))
            .jsonPath("$.lossPct")
            .value(is(sameNumber(DEFAULT_LOSS_PCT)))
            .jsonPath("$.methodology")
            .value(is(DEFAULT_METHODOLOGY))
            .jsonPath("$.assessedAt")
            .value(is(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.validationStatus")
            .value(is(DEFAULT_VALIDATION_STATUS.toString()));
    }

    @Test
    void getNonExistingLossAssessment() {
        // Get the lossAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingLossAssessment() throws Exception {
        // Initialize the database
        insertedLossAssessment = lossAssessmentRepository.save(lossAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lossAssessment
        LossAssessment updatedLossAssessment = lossAssessmentRepository.findById(lossAssessment.getId()).block();
        updatedLossAssessment
            .totalLossWh(UPDATED_TOTAL_LOSS_WH)
            .lossPct(UPDATED_LOSS_PCT)
            .methodology(UPDATED_METHODOLOGY)
            .assessedAt(UPDATED_ASSESSED_AT)
            .validationStatus(UPDATED_VALIDATION_STATUS);
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(updatedLossAssessment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, lossAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LossAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLossAssessmentToMatchAllProperties(updatedLossAssessment);
    }

    @Test
    void putNonExistingLossAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossAssessment.setId(longCount.incrementAndGet());

        // Create the LossAssessment
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, lossAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLossAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossAssessment.setId(longCount.incrementAndGet());

        // Create the LossAssessment
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLossAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossAssessment.setId(longCount.incrementAndGet());

        // Create the LossAssessment
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the LossAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLossAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedLossAssessment = lossAssessmentRepository.save(lossAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lossAssessment using partial update
        LossAssessment partialUpdatedLossAssessment = new LossAssessment();
        partialUpdatedLossAssessment.setId(lossAssessment.getId());

        partialUpdatedLossAssessment
            .totalLossWh(UPDATED_TOTAL_LOSS_WH)
            .lossPct(UPDATED_LOSS_PCT)
            .methodology(UPDATED_METHODOLOGY)
            .assessedAt(UPDATED_ASSESSED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLossAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedLossAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LossAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLossAssessmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLossAssessment, lossAssessment),
            getPersistedLossAssessment(lossAssessment)
        );
    }

    @Test
    void fullUpdateLossAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedLossAssessment = lossAssessmentRepository.save(lossAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lossAssessment using partial update
        LossAssessment partialUpdatedLossAssessment = new LossAssessment();
        partialUpdatedLossAssessment.setId(lossAssessment.getId());

        partialUpdatedLossAssessment
            .totalLossWh(UPDATED_TOTAL_LOSS_WH)
            .lossPct(UPDATED_LOSS_PCT)
            .methodology(UPDATED_METHODOLOGY)
            .assessedAt(UPDATED_ASSESSED_AT)
            .validationStatus(UPDATED_VALIDATION_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLossAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedLossAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LossAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLossAssessmentUpdatableFieldsEquals(partialUpdatedLossAssessment, getPersistedLossAssessment(partialUpdatedLossAssessment));
    }

    @Test
    void patchNonExistingLossAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossAssessment.setId(longCount.incrementAndGet());

        // Create the LossAssessment
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, lossAssessmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLossAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossAssessment.setId(longCount.incrementAndGet());

        // Create the LossAssessment
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LossAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLossAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lossAssessment.setId(longCount.incrementAndGet());

        // Create the LossAssessment
        LossAssessmentDTO lossAssessmentDTO = lossAssessmentMapper.toDto(lossAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(lossAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the LossAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLossAssessment() {
        // Initialize the database
        insertedLossAssessment = lossAssessmentRepository.save(lossAssessment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the lossAssessment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, lossAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return lossAssessmentRepository.count().block();
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

    protected LossAssessment getPersistedLossAssessment(LossAssessment lossAssessment) {
        return lossAssessmentRepository.findById(lossAssessment.getId()).block();
    }

    protected void assertPersistedLossAssessmentToMatchAllProperties(LossAssessment expectedLossAssessment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertLossAssessmentAllPropertiesEquals(expectedLossAssessment, getPersistedLossAssessment(expectedLossAssessment));
        assertLossAssessmentUpdatableFieldsEquals(expectedLossAssessment, getPersistedLossAssessment(expectedLossAssessment));
    }

    protected void assertPersistedLossAssessmentToMatchUpdatableProperties(LossAssessment expectedLossAssessment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertLossAssessmentAllUpdatablePropertiesEquals(expectedLossAssessment, getPersistedLossAssessment(expectedLossAssessment));
        assertLossAssessmentUpdatableFieldsEquals(expectedLossAssessment, getPersistedLossAssessment(expectedLossAssessment));
    }
}
