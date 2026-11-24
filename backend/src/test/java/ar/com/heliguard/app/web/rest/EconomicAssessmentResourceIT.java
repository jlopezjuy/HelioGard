package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.EconomicAssessmentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.EconomicAssessment;
import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.EconomicAssessmentRepository;
import ar.com.heliguard.app.service.EconomicAssessmentService;
import ar.com.heliguard.app.service.dto.EconomicAssessmentDTO;
import ar.com.heliguard.app.service.mapper.EconomicAssessmentMapper;
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
 * Integration tests for the {@link EconomicAssessmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EconomicAssessmentResourceIT {

    private static final Instant DEFAULT_ASSESSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSESSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_CURRENCY_CODE = "AAA";
    private static final String UPDATED_CURRENCY_CODE = "BBB";

    private static final BigDecimal DEFAULT_SELF_CONSUMPTION_LOST_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_SELF_CONSUMPTION_LOST_WH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_EXPORTED_LOST_WH = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPORTED_LOST_WH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ESTIMATED_LOSS_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_ESTIMATED_LOSS_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_TARIFF_SNAPSHOT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_TARIFF_SNAPSHOT_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/economic-assessments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EconomicAssessmentRepository economicAssessmentRepository;

    @Mock
    private EconomicAssessmentRepository economicAssessmentRepositoryMock;

    @Autowired
    private EconomicAssessmentMapper economicAssessmentMapper;

    @Mock
    private EconomicAssessmentService economicAssessmentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private EconomicAssessment economicAssessment;

    private EconomicAssessment insertedEconomicAssessment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EconomicAssessment createEntity(R2dbcEntityTemplate template) {
        EconomicAssessment economicAssessment = new EconomicAssessment()
            .assessedAt(DEFAULT_ASSESSED_AT)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .selfConsumptionLostWh(DEFAULT_SELF_CONSUMPTION_LOST_WH)
            .exportedLostWh(DEFAULT_EXPORTED_LOST_WH)
            .estimatedLossAmount(DEFAULT_ESTIMATED_LOSS_AMOUNT)
            .tariffSnapshotNote(DEFAULT_TARIFF_SNAPSHOT_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        economicAssessment.setOrganization(organization);
        // Add required entity
        LossAssessment lossAssessment;
        lossAssessment = template.insert(LossAssessmentResourceIT.createEntity(template)).block();
        economicAssessment.setLossAssessment(lossAssessment);
        return economicAssessment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EconomicAssessment createUpdatedEntity(R2dbcEntityTemplate template) {
        EconomicAssessment updatedEconomicAssessment = new EconomicAssessment()
            .assessedAt(UPDATED_ASSESSED_AT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .selfConsumptionLostWh(UPDATED_SELF_CONSUMPTION_LOST_WH)
            .exportedLostWh(UPDATED_EXPORTED_LOST_WH)
            .estimatedLossAmount(UPDATED_ESTIMATED_LOSS_AMOUNT)
            .tariffSnapshotNote(UPDATED_TARIFF_SNAPSHOT_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedEconomicAssessment.setOrganization(organization);
        // Add required entity
        LossAssessment lossAssessment;
        lossAssessment = template.insert(LossAssessmentResourceIT.createUpdatedEntity(template)).block();
        updatedEconomicAssessment.setLossAssessment(lossAssessment);
        return updatedEconomicAssessment;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(EconomicAssessment.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        LossAssessmentResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        economicAssessment = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedEconomicAssessment != null) {
            economicAssessmentRepository.delete(insertedEconomicAssessment).block();
            insertedEconomicAssessment = null;
        }
        deleteEntities(template);
    }

    @Test
    void createEconomicAssessment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EconomicAssessment
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);
        var returnedEconomicAssessmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EconomicAssessmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EconomicAssessment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEconomicAssessment = economicAssessmentMapper.toEntity(returnedEconomicAssessmentDTO);
        assertEconomicAssessmentUpdatableFieldsEquals(
            returnedEconomicAssessment,
            getPersistedEconomicAssessment(returnedEconomicAssessment)
        );

        insertedEconomicAssessment = returnedEconomicAssessment;
    }

    @Test
    void createEconomicAssessmentWithExistingId() throws Exception {
        // Create the EconomicAssessment with an existing ID
        economicAssessment.setId(1L);
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EconomicAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAssessedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        economicAssessment.setAssessedAt(null);

        // Create the EconomicAssessment, which fails.
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        economicAssessment.setCurrencyCode(null);

        // Create the EconomicAssessment, which fails.
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstimatedLossAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        economicAssessment.setEstimatedLossAmount(null);

        // Create the EconomicAssessment, which fails.
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEconomicAssessmentsAsStream() {
        // Initialize the database
        economicAssessmentRepository.save(economicAssessment).block();

        List<EconomicAssessment> economicAssessmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(EconomicAssessmentDTO.class)
            .getResponseBody()
            .map(economicAssessmentMapper::toEntity)
            .filter(economicAssessment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(economicAssessmentList).isNotNull();
        assertThat(economicAssessmentList).hasSize(1);
        EconomicAssessment testEconomicAssessment = economicAssessmentList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertEconomicAssessmentAllPropertiesEquals(economicAssessment, testEconomicAssessment);
        assertEconomicAssessmentUpdatableFieldsEquals(economicAssessment, testEconomicAssessment);
    }

    @Test
    void getAllEconomicAssessments() {
        // Initialize the database
        insertedEconomicAssessment = economicAssessmentRepository.save(economicAssessment).block();

        // Get all the economicAssessmentList
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
            .value(hasItem(economicAssessment.getId().intValue()))
            .jsonPath("$.[*].assessedAt")
            .value(hasItem(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.[*].currencyCode")
            .value(hasItem(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.[*].selfConsumptionLostWh")
            .value(hasItem(sameNumber(DEFAULT_SELF_CONSUMPTION_LOST_WH)))
            .jsonPath("$.[*].exportedLostWh")
            .value(hasItem(sameNumber(DEFAULT_EXPORTED_LOST_WH)))
            .jsonPath("$.[*].estimatedLossAmount")
            .value(hasItem(sameNumber(DEFAULT_ESTIMATED_LOSS_AMOUNT)))
            .jsonPath("$.[*].tariffSnapshotNote")
            .value(hasItem(DEFAULT_TARIFF_SNAPSHOT_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEconomicAssessmentsWithEagerRelationshipsIsEnabled() {
        when(economicAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(economicAssessmentServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEconomicAssessmentsWithEagerRelationshipsIsNotEnabled() {
        when(economicAssessmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(economicAssessmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEconomicAssessment() {
        // Initialize the database
        insertedEconomicAssessment = economicAssessmentRepository.save(economicAssessment).block();

        // Get the economicAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, economicAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(economicAssessment.getId().intValue()))
            .jsonPath("$.assessedAt")
            .value(is(DEFAULT_ASSESSED_AT.toString()))
            .jsonPath("$.currencyCode")
            .value(is(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.selfConsumptionLostWh")
            .value(is(sameNumber(DEFAULT_SELF_CONSUMPTION_LOST_WH)))
            .jsonPath("$.exportedLostWh")
            .value(is(sameNumber(DEFAULT_EXPORTED_LOST_WH)))
            .jsonPath("$.estimatedLossAmount")
            .value(is(sameNumber(DEFAULT_ESTIMATED_LOSS_AMOUNT)))
            .jsonPath("$.tariffSnapshotNote")
            .value(is(DEFAULT_TARIFF_SNAPSHOT_NOTE));
    }

    @Test
    void getNonExistingEconomicAssessment() {
        // Get the economicAssessment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEconomicAssessment() throws Exception {
        // Initialize the database
        insertedEconomicAssessment = economicAssessmentRepository.save(economicAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the economicAssessment
        EconomicAssessment updatedEconomicAssessment = economicAssessmentRepository.findById(economicAssessment.getId()).block();
        updatedEconomicAssessment
            .assessedAt(UPDATED_ASSESSED_AT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .selfConsumptionLostWh(UPDATED_SELF_CONSUMPTION_LOST_WH)
            .exportedLostWh(UPDATED_EXPORTED_LOST_WH)
            .estimatedLossAmount(UPDATED_ESTIMATED_LOSS_AMOUNT)
            .tariffSnapshotNote(UPDATED_TARIFF_SNAPSHOT_NOTE);
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(updatedEconomicAssessment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, economicAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EconomicAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEconomicAssessmentToMatchAllProperties(updatedEconomicAssessment);
    }

    @Test
    void putNonExistingEconomicAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        economicAssessment.setId(longCount.incrementAndGet());

        // Create the EconomicAssessment
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, economicAssessmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EconomicAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEconomicAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        economicAssessment.setId(longCount.incrementAndGet());

        // Create the EconomicAssessment
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EconomicAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEconomicAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        economicAssessment.setId(longCount.incrementAndGet());

        // Create the EconomicAssessment
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EconomicAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEconomicAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedEconomicAssessment = economicAssessmentRepository.save(economicAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the economicAssessment using partial update
        EconomicAssessment partialUpdatedEconomicAssessment = new EconomicAssessment();
        partialUpdatedEconomicAssessment.setId(economicAssessment.getId());

        partialUpdatedEconomicAssessment.currencyCode(UPDATED_CURRENCY_CODE).tariffSnapshotNote(UPDATED_TARIFF_SNAPSHOT_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEconomicAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEconomicAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EconomicAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEconomicAssessmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEconomicAssessment, economicAssessment),
            getPersistedEconomicAssessment(economicAssessment)
        );
    }

    @Test
    void fullUpdateEconomicAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedEconomicAssessment = economicAssessmentRepository.save(economicAssessment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the economicAssessment using partial update
        EconomicAssessment partialUpdatedEconomicAssessment = new EconomicAssessment();
        partialUpdatedEconomicAssessment.setId(economicAssessment.getId());

        partialUpdatedEconomicAssessment
            .assessedAt(UPDATED_ASSESSED_AT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .selfConsumptionLostWh(UPDATED_SELF_CONSUMPTION_LOST_WH)
            .exportedLostWh(UPDATED_EXPORTED_LOST_WH)
            .estimatedLossAmount(UPDATED_ESTIMATED_LOSS_AMOUNT)
            .tariffSnapshotNote(UPDATED_TARIFF_SNAPSHOT_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEconomicAssessment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEconomicAssessment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EconomicAssessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEconomicAssessmentUpdatableFieldsEquals(
            partialUpdatedEconomicAssessment,
            getPersistedEconomicAssessment(partialUpdatedEconomicAssessment)
        );
    }

    @Test
    void patchNonExistingEconomicAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        economicAssessment.setId(longCount.incrementAndGet());

        // Create the EconomicAssessment
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, economicAssessmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EconomicAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEconomicAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        economicAssessment.setId(longCount.incrementAndGet());

        // Create the EconomicAssessment
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EconomicAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEconomicAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        economicAssessment.setId(longCount.incrementAndGet());

        // Create the EconomicAssessment
        EconomicAssessmentDTO economicAssessmentDTO = economicAssessmentMapper.toDto(economicAssessment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(economicAssessmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EconomicAssessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEconomicAssessment() {
        // Initialize the database
        insertedEconomicAssessment = economicAssessmentRepository.save(economicAssessment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the economicAssessment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, economicAssessment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return economicAssessmentRepository.count().block();
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

    protected EconomicAssessment getPersistedEconomicAssessment(EconomicAssessment economicAssessment) {
        return economicAssessmentRepository.findById(economicAssessment.getId()).block();
    }

    protected void assertPersistedEconomicAssessmentToMatchAllProperties(EconomicAssessment expectedEconomicAssessment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEconomicAssessmentAllPropertiesEquals(expectedEconomicAssessment, getPersistedEconomicAssessment(expectedEconomicAssessment));
        assertEconomicAssessmentUpdatableFieldsEquals(
            expectedEconomicAssessment,
            getPersistedEconomicAssessment(expectedEconomicAssessment)
        );
    }

    protected void assertPersistedEconomicAssessmentToMatchUpdatableProperties(EconomicAssessment expectedEconomicAssessment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEconomicAssessmentAllUpdatablePropertiesEquals(expectedEconomicAssessment, getPersistedEconomicAssessment(expectedEconomicAssessment));
        assertEconomicAssessmentUpdatableFieldsEquals(
            expectedEconomicAssessment,
            getPersistedEconomicAssessment(expectedEconomicAssessment)
        );
    }
}
