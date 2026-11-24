package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.ServiceAgreementAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.ServiceAgreement;
import ar.com.heliguard.app.domain.enumeration.ServiceAgreementStatus;
import ar.com.heliguard.app.repository.ServiceAgreementRepository;
import ar.com.heliguard.app.service.ServiceAgreementService;
import ar.com.heliguard.app.service.dto.ServiceAgreementDTO;
import ar.com.heliguard.app.service.mapper.ServiceAgreementMapper;
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
 * Integration tests for the {@link ServiceAgreementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ServiceAgreementResourceIT {

    private static final ServiceAgreementStatus DEFAULT_STATUS = ServiceAgreementStatus.REQUESTED;
    private static final ServiceAgreementStatus UPDATED_STATUS = ServiceAgreementStatus.ACTIVE;

    private static final Instant DEFAULT_STARTS_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTS_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_ENDS_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENDS_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_APPROVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_APPROVED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/service-agreements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceAgreementRepository serviceAgreementRepository;

    @Mock
    private ServiceAgreementRepository serviceAgreementRepositoryMock;

    @Autowired
    private ServiceAgreementMapper serviceAgreementMapper;

    @Mock
    private ServiceAgreementService serviceAgreementServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private ServiceAgreement serviceAgreement;

    private ServiceAgreement insertedServiceAgreement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceAgreement createEntity(R2dbcEntityTemplate template) {
        ServiceAgreement serviceAgreement = new ServiceAgreement()
            .status(DEFAULT_STATUS)
            .startsAt(DEFAULT_STARTS_AT)
            .endsAt(DEFAULT_ENDS_AT)
            .approvedAt(DEFAULT_APPROVED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        serviceAgreement.setOrganization(organization);
        // Add required entity
        serviceAgreement.setProviderOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        serviceAgreement.setInstallation(installation);
        return serviceAgreement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceAgreement createUpdatedEntity(R2dbcEntityTemplate template) {
        ServiceAgreement updatedServiceAgreement = new ServiceAgreement()
            .status(UPDATED_STATUS)
            .startsAt(UPDATED_STARTS_AT)
            .endsAt(UPDATED_ENDS_AT)
            .approvedAt(UPDATED_APPROVED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedServiceAgreement.setOrganization(organization);
        // Add required entity
        updatedServiceAgreement.setProviderOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedServiceAgreement.setInstallation(installation);
        return updatedServiceAgreement;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(ServiceAgreement.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        serviceAgreement = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedServiceAgreement != null) {
            serviceAgreementRepository.delete(insertedServiceAgreement).block();
            insertedServiceAgreement = null;
        }
        deleteEntities(template);
    }

    @Test
    void createServiceAgreement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ServiceAgreement
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);
        var returnedServiceAgreementDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ServiceAgreementDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ServiceAgreement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedServiceAgreement = serviceAgreementMapper.toEntity(returnedServiceAgreementDTO);
        assertServiceAgreementUpdatableFieldsEquals(returnedServiceAgreement, getPersistedServiceAgreement(returnedServiceAgreement));

        insertedServiceAgreement = returnedServiceAgreement;
    }

    @Test
    void createServiceAgreementWithExistingId() throws Exception {
        // Create the ServiceAgreement with an existing ID
        serviceAgreement.setId(1L);
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceAgreement.setStatus(null);

        // Create the ServiceAgreement, which fails.
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStartsAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceAgreement.setStartsAt(null);

        // Create the ServiceAgreement, which fails.
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllServiceAgreementsAsStream() {
        // Initialize the database
        serviceAgreementRepository.save(serviceAgreement).block();

        List<ServiceAgreement> serviceAgreementList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ServiceAgreementDTO.class)
            .getResponseBody()
            .map(serviceAgreementMapper::toEntity)
            .filter(serviceAgreement::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(serviceAgreementList).isNotNull();
        assertThat(serviceAgreementList).hasSize(1);
        ServiceAgreement testServiceAgreement = serviceAgreementList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertServiceAgreementAllPropertiesEquals(serviceAgreement, testServiceAgreement);
        assertServiceAgreementUpdatableFieldsEquals(serviceAgreement, testServiceAgreement);
    }

    @Test
    void getAllServiceAgreements() {
        // Initialize the database
        insertedServiceAgreement = serviceAgreementRepository.save(serviceAgreement).block();

        // Get all the serviceAgreementList
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
            .value(hasItem(serviceAgreement.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].startsAt")
            .value(hasItem(DEFAULT_STARTS_AT.toString()))
            .jsonPath("$.[*].endsAt")
            .value(hasItem(DEFAULT_ENDS_AT.toString()))
            .jsonPath("$.[*].approvedAt")
            .value(hasItem(DEFAULT_APPROVED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllServiceAgreementsWithEagerRelationshipsIsEnabled() {
        when(serviceAgreementServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(serviceAgreementServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllServiceAgreementsWithEagerRelationshipsIsNotEnabled() {
        when(serviceAgreementServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(serviceAgreementRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getServiceAgreement() {
        // Initialize the database
        insertedServiceAgreement = serviceAgreementRepository.save(serviceAgreement).block();

        // Get the serviceAgreement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, serviceAgreement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(serviceAgreement.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.startsAt")
            .value(is(DEFAULT_STARTS_AT.toString()))
            .jsonPath("$.endsAt")
            .value(is(DEFAULT_ENDS_AT.toString()))
            .jsonPath("$.approvedAt")
            .value(is(DEFAULT_APPROVED_AT.toString()));
    }

    @Test
    void getNonExistingServiceAgreement() {
        // Get the serviceAgreement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingServiceAgreement() throws Exception {
        // Initialize the database
        insertedServiceAgreement = serviceAgreementRepository.save(serviceAgreement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceAgreement
        ServiceAgreement updatedServiceAgreement = serviceAgreementRepository.findById(serviceAgreement.getId()).block();
        updatedServiceAgreement.status(UPDATED_STATUS).startsAt(UPDATED_STARTS_AT).endsAt(UPDATED_ENDS_AT).approvedAt(UPDATED_APPROVED_AT);
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(updatedServiceAgreement);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, serviceAgreementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ServiceAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceAgreementToMatchAllProperties(updatedServiceAgreement);
    }

    @Test
    void putNonExistingServiceAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceAgreement.setId(longCount.incrementAndGet());

        // Create the ServiceAgreement
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, serviceAgreementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchServiceAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceAgreement.setId(longCount.incrementAndGet());

        // Create the ServiceAgreement
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamServiceAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceAgreement.setId(longCount.incrementAndGet());

        // Create the ServiceAgreement
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ServiceAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateServiceAgreementWithPatch() throws Exception {
        // Initialize the database
        insertedServiceAgreement = serviceAgreementRepository.save(serviceAgreement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceAgreement using partial update
        ServiceAgreement partialUpdatedServiceAgreement = new ServiceAgreement();
        partialUpdatedServiceAgreement.setId(serviceAgreement.getId());

        partialUpdatedServiceAgreement.status(UPDATED_STATUS).startsAt(UPDATED_STARTS_AT).approvedAt(UPDATED_APPROVED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedServiceAgreement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedServiceAgreement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ServiceAgreement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceAgreementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedServiceAgreement, serviceAgreement),
            getPersistedServiceAgreement(serviceAgreement)
        );
    }

    @Test
    void fullUpdateServiceAgreementWithPatch() throws Exception {
        // Initialize the database
        insertedServiceAgreement = serviceAgreementRepository.save(serviceAgreement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceAgreement using partial update
        ServiceAgreement partialUpdatedServiceAgreement = new ServiceAgreement();
        partialUpdatedServiceAgreement.setId(serviceAgreement.getId());

        partialUpdatedServiceAgreement
            .status(UPDATED_STATUS)
            .startsAt(UPDATED_STARTS_AT)
            .endsAt(UPDATED_ENDS_AT)
            .approvedAt(UPDATED_APPROVED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedServiceAgreement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedServiceAgreement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ServiceAgreement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceAgreementUpdatableFieldsEquals(
            partialUpdatedServiceAgreement,
            getPersistedServiceAgreement(partialUpdatedServiceAgreement)
        );
    }

    @Test
    void patchNonExistingServiceAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceAgreement.setId(longCount.incrementAndGet());

        // Create the ServiceAgreement
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, serviceAgreementDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchServiceAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceAgreement.setId(longCount.incrementAndGet());

        // Create the ServiceAgreement
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamServiceAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceAgreement.setId(longCount.incrementAndGet());

        // Create the ServiceAgreement
        ServiceAgreementDTO serviceAgreementDTO = serviceAgreementMapper.toDto(serviceAgreement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(serviceAgreementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ServiceAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteServiceAgreement() {
        // Initialize the database
        insertedServiceAgreement = serviceAgreementRepository.save(serviceAgreement).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the serviceAgreement
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, serviceAgreement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return serviceAgreementRepository.count().block();
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

    protected ServiceAgreement getPersistedServiceAgreement(ServiceAgreement serviceAgreement) {
        return serviceAgreementRepository.findById(serviceAgreement.getId()).block();
    }

    protected void assertPersistedServiceAgreementToMatchAllProperties(ServiceAgreement expectedServiceAgreement) {
        // Test fails because reactive api returns an empty object instead of null
        // assertServiceAgreementAllPropertiesEquals(expectedServiceAgreement, getPersistedServiceAgreement(expectedServiceAgreement));
        assertServiceAgreementUpdatableFieldsEquals(expectedServiceAgreement, getPersistedServiceAgreement(expectedServiceAgreement));
    }

    protected void assertPersistedServiceAgreementToMatchUpdatableProperties(ServiceAgreement expectedServiceAgreement) {
        // Test fails because reactive api returns an empty object instead of null
        // assertServiceAgreementAllUpdatablePropertiesEquals(expectedServiceAgreement, getPersistedServiceAgreement(expectedServiceAgreement));
        assertServiceAgreementUpdatableFieldsEquals(expectedServiceAgreement, getPersistedServiceAgreement(expectedServiceAgreement));
    }
}
