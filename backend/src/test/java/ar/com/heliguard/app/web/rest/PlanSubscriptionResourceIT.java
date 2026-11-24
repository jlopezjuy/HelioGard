package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.PlanSubscriptionAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanSubscription;
import ar.com.heliguard.app.domain.ServiceAgreement;
import ar.com.heliguard.app.domain.enumeration.SubscriptionStatus;
import ar.com.heliguard.app.repository.PlanSubscriptionRepository;
import ar.com.heliguard.app.service.PlanSubscriptionService;
import ar.com.heliguard.app.service.dto.PlanSubscriptionDTO;
import ar.com.heliguard.app.service.mapper.PlanSubscriptionMapper;
import java.math.BigDecimal;
import java.time.Instant;
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
 * Integration tests for the {@link PlanSubscriptionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlanSubscriptionResourceIT {

    private static final SubscriptionStatus DEFAULT_STATUS = SubscriptionStatus.PROPOSED;
    private static final SubscriptionStatus UPDATED_STATUS = SubscriptionStatus.ACTIVE;

    private static final Instant DEFAULT_ACTIVATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTIVATED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.ofEpochMilli(1795370143700L);

    private static final BigDecimal DEFAULT_AGREED_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AGREED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY_CODE = "AAA";
    private static final String UPDATED_CURRENCY_CODE = "BBB";

    private static final String ENTITY_API_URL = "/api/plan-subscriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlanSubscriptionRepository planSubscriptionRepository;

    @Mock
    private PlanSubscriptionRepository planSubscriptionRepositoryMock;

    @Autowired
    private PlanSubscriptionMapper planSubscriptionMapper;

    @Mock
    private PlanSubscriptionService planSubscriptionServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private PlanSubscription planSubscription;

    private PlanSubscription insertedPlanSubscription;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanSubscription createEntity(R2dbcEntityTemplate template) {
        PlanSubscription planSubscription = new PlanSubscription()
            .status(DEFAULT_STATUS)
            .activatedAt(DEFAULT_ACTIVATED_AT)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .agreedAmount(DEFAULT_AGREED_AMOUNT)
            .currencyCode(DEFAULT_CURRENCY_CODE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        planSubscription.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        planSubscription.setInstallation(installation);
        // Add required entity
        MaintenancePlanVersion maintenancePlanVersion;
        maintenancePlanVersion = template.insert(MaintenancePlanVersionResourceIT.createEntity(template)).block();
        planSubscription.setPlanVersion(maintenancePlanVersion);
        // Add required entity
        ServiceAgreement serviceAgreement;
        serviceAgreement = template.insert(ServiceAgreementResourceIT.createEntity(template)).block();
        planSubscription.setServiceAgreement(serviceAgreement);
        return planSubscription;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanSubscription createUpdatedEntity(R2dbcEntityTemplate template) {
        PlanSubscription updatedPlanSubscription = new PlanSubscription()
            .status(UPDATED_STATUS)
            .activatedAt(UPDATED_ACTIVATED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .agreedAmount(UPDATED_AGREED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedPlanSubscription.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedPlanSubscription.setInstallation(installation);
        // Add required entity
        MaintenancePlanVersion maintenancePlanVersion;
        maintenancePlanVersion = template.insert(MaintenancePlanVersionResourceIT.createUpdatedEntity(template)).block();
        updatedPlanSubscription.setPlanVersion(maintenancePlanVersion);
        // Add required entity
        ServiceAgreement serviceAgreement;
        serviceAgreement = template.insert(ServiceAgreementResourceIT.createUpdatedEntity(template)).block();
        updatedPlanSubscription.setServiceAgreement(serviceAgreement);
        return updatedPlanSubscription;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(PlanSubscription.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
        MaintenancePlanVersionResourceIT.deleteEntities(template);
        ServiceAgreementResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        planSubscription = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedPlanSubscription != null) {
            planSubscriptionRepository.delete(insertedPlanSubscription).block();
            insertedPlanSubscription = null;
        }
        deleteEntities(template);
    }

    @Test
    void createPlanSubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PlanSubscription
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);
        var returnedPlanSubscriptionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PlanSubscriptionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the PlanSubscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlanSubscription = planSubscriptionMapper.toEntity(returnedPlanSubscriptionDTO);
        assertPlanSubscriptionUpdatableFieldsEquals(returnedPlanSubscription, getPersistedPlanSubscription(returnedPlanSubscription));

        insertedPlanSubscription = returnedPlanSubscription;
    }

    @Test
    void createPlanSubscriptionWithExistingId() throws Exception {
        // Create the PlanSubscription with an existing ID
        planSubscription.setId(1L);
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planSubscription.setStatus(null);

        // Create the PlanSubscription, which fails.
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPlanSubscriptions() {
        // Initialize the database
        insertedPlanSubscription = planSubscriptionRepository.save(planSubscription).block();

        // Get all the planSubscriptionList
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
            .value(hasItem(planSubscription.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].activatedAt")
            .value(hasItem(DEFAULT_ACTIVATED_AT.toString()))
            .jsonPath("$.[*].expiresAt")
            .value(hasItem(DEFAULT_EXPIRES_AT.toString()))
            .jsonPath("$.[*].agreedAmount")
            .value(hasItem(sameNumber(DEFAULT_AGREED_AMOUNT)))
            .jsonPath("$.[*].currencyCode")
            .value(hasItem(DEFAULT_CURRENCY_CODE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlanSubscriptionsWithEagerRelationshipsIsEnabled() {
        when(planSubscriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(planSubscriptionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlanSubscriptionsWithEagerRelationshipsIsNotEnabled() {
        when(planSubscriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(planSubscriptionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPlanSubscription() {
        // Initialize the database
        insertedPlanSubscription = planSubscriptionRepository.save(planSubscription).block();

        // Get the planSubscription
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, planSubscription.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(planSubscription.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.activatedAt")
            .value(is(DEFAULT_ACTIVATED_AT.toString()))
            .jsonPath("$.expiresAt")
            .value(is(DEFAULT_EXPIRES_AT.toString()))
            .jsonPath("$.agreedAmount")
            .value(is(sameNumber(DEFAULT_AGREED_AMOUNT)))
            .jsonPath("$.currencyCode")
            .value(is(DEFAULT_CURRENCY_CODE));
    }

    @Test
    void getNonExistingPlanSubscription() {
        // Get the planSubscription
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlanSubscription() throws Exception {
        // Initialize the database
        insertedPlanSubscription = planSubscriptionRepository.save(planSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planSubscription
        PlanSubscription updatedPlanSubscription = planSubscriptionRepository.findById(planSubscription.getId()).block();
        updatedPlanSubscription
            .status(UPDATED_STATUS)
            .activatedAt(UPDATED_ACTIVATED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .agreedAmount(UPDATED_AGREED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE);
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(updatedPlanSubscription);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, planSubscriptionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlanSubscriptionToMatchAllProperties(updatedPlanSubscription);
    }

    @Test
    void putNonExistingPlanSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planSubscription.setId(longCount.incrementAndGet());

        // Create the PlanSubscription
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, planSubscriptionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPlanSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planSubscription.setId(longCount.incrementAndGet());

        // Create the PlanSubscription
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPlanSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planSubscription.setId(longCount.incrementAndGet());

        // Create the PlanSubscription
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlanSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePlanSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedPlanSubscription = planSubscriptionRepository.save(planSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planSubscription using partial update
        PlanSubscription partialUpdatedPlanSubscription = new PlanSubscription();
        partialUpdatedPlanSubscription.setId(planSubscription.getId());

        partialUpdatedPlanSubscription.activatedAt(UPDATED_ACTIVATED_AT).expiresAt(UPDATED_EXPIRES_AT).currencyCode(UPDATED_CURRENCY_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlanSubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlanSubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanSubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlanSubscription, planSubscription),
            getPersistedPlanSubscription(planSubscription)
        );
    }

    @Test
    void fullUpdatePlanSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedPlanSubscription = planSubscriptionRepository.save(planSubscription).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planSubscription using partial update
        PlanSubscription partialUpdatedPlanSubscription = new PlanSubscription();
        partialUpdatedPlanSubscription.setId(planSubscription.getId());

        partialUpdatedPlanSubscription
            .status(UPDATED_STATUS)
            .activatedAt(UPDATED_ACTIVATED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .agreedAmount(UPDATED_AGREED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlanSubscription.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlanSubscription))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanSubscriptionUpdatableFieldsEquals(
            partialUpdatedPlanSubscription,
            getPersistedPlanSubscription(partialUpdatedPlanSubscription)
        );
    }

    @Test
    void patchNonExistingPlanSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planSubscription.setId(longCount.incrementAndGet());

        // Create the PlanSubscription
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, planSubscriptionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPlanSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planSubscription.setId(longCount.incrementAndGet());

        // Create the PlanSubscription
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPlanSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planSubscription.setId(longCount.incrementAndGet());

        // Create the PlanSubscription
        PlanSubscriptionDTO planSubscriptionDTO = planSubscriptionMapper.toDto(planSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planSubscriptionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlanSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePlanSubscription() {
        // Initialize the database
        insertedPlanSubscription = planSubscriptionRepository.save(planSubscription).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the planSubscription
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, planSubscription.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return planSubscriptionRepository.count().block();
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

    protected PlanSubscription getPersistedPlanSubscription(PlanSubscription planSubscription) {
        return planSubscriptionRepository.findById(planSubscription.getId()).block();
    }

    protected void assertPersistedPlanSubscriptionToMatchAllProperties(PlanSubscription expectedPlanSubscription) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlanSubscriptionAllPropertiesEquals(expectedPlanSubscription, getPersistedPlanSubscription(expectedPlanSubscription));
        assertPlanSubscriptionUpdatableFieldsEquals(expectedPlanSubscription, getPersistedPlanSubscription(expectedPlanSubscription));
    }

    protected void assertPersistedPlanSubscriptionToMatchUpdatableProperties(PlanSubscription expectedPlanSubscription) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlanSubscriptionAllUpdatablePropertiesEquals(expectedPlanSubscription, getPersistedPlanSubscription(expectedPlanSubscription));
        assertPlanSubscriptionUpdatableFieldsEquals(expectedPlanSubscription, getPersistedPlanSubscription(expectedPlanSubscription));
    }
}
