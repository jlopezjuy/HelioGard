package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.ServiceProposalAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.ServiceProposal;
import ar.com.heliguard.app.repository.ServiceProposalRepository;
import ar.com.heliguard.app.service.ServiceProposalService;
import ar.com.heliguard.app.service.dto.ServiceProposalDTO;
import ar.com.heliguard.app.service.mapper.ServiceProposalMapper;
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
 * Integration tests for the {@link ServiceProposalResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ServiceProposalResourceIT {

    private static final String DEFAULT_REFERENCE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_PROPOSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PROPOSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY_CODE = "AAA";
    private static final String UPDATED_CURRENCY_CODE = "BBB";

    private static final Instant DEFAULT_ACCEPTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACCEPTED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/service-proposals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceProposalRepository serviceProposalRepository;

    @Mock
    private ServiceProposalRepository serviceProposalRepositoryMock;

    @Autowired
    private ServiceProposalMapper serviceProposalMapper;

    @Mock
    private ServiceProposalService serviceProposalServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private ServiceProposal serviceProposal;

    private ServiceProposal insertedServiceProposal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceProposal createEntity(R2dbcEntityTemplate template) {
        ServiceProposal serviceProposal = new ServiceProposal()
            .referenceCode(DEFAULT_REFERENCE_CODE)
            .proposedAt(DEFAULT_PROPOSED_AT)
            .validUntil(DEFAULT_VALID_UNTIL)
            .amount(DEFAULT_AMOUNT)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .acceptedAt(DEFAULT_ACCEPTED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        serviceProposal.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        serviceProposal.setInstallation(installation);
        return serviceProposal;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceProposal createUpdatedEntity(R2dbcEntityTemplate template) {
        ServiceProposal updatedServiceProposal = new ServiceProposal()
            .referenceCode(UPDATED_REFERENCE_CODE)
            .proposedAt(UPDATED_PROPOSED_AT)
            .validUntil(UPDATED_VALID_UNTIL)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .acceptedAt(UPDATED_ACCEPTED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedServiceProposal.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedServiceProposal.setInstallation(installation);
        return updatedServiceProposal;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(ServiceProposal.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        serviceProposal = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedServiceProposal != null) {
            serviceProposalRepository.delete(insertedServiceProposal).block();
            insertedServiceProposal = null;
        }
        deleteEntities(template);
    }

    @Test
    void createServiceProposal() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ServiceProposal
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);
        var returnedServiceProposalDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ServiceProposalDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ServiceProposal in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedServiceProposal = serviceProposalMapper.toEntity(returnedServiceProposalDTO);
        assertServiceProposalUpdatableFieldsEquals(returnedServiceProposal, getPersistedServiceProposal(returnedServiceProposal));

        insertedServiceProposal = returnedServiceProposal;
    }

    @Test
    void createServiceProposalWithExistingId() throws Exception {
        // Create the ServiceProposal with an existing ID
        serviceProposal.setId(1L);
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceProposal in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceProposal.setReferenceCode(null);

        // Create the ServiceProposal, which fails.
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkProposedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceProposal.setProposedAt(null);

        // Create the ServiceProposal, which fails.
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceProposal.setAmount(null);

        // Create the ServiceProposal, which fails.
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceProposal.setCurrencyCode(null);

        // Create the ServiceProposal, which fails.
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllServiceProposals() {
        // Initialize the database
        insertedServiceProposal = serviceProposalRepository.save(serviceProposal).block();

        // Get all the serviceProposalList
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
            .value(hasItem(serviceProposal.getId().intValue()))
            .jsonPath("$.[*].referenceCode")
            .value(hasItem(DEFAULT_REFERENCE_CODE))
            .jsonPath("$.[*].proposedAt")
            .value(hasItem(DEFAULT_PROPOSED_AT.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].currencyCode")
            .value(hasItem(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.[*].acceptedAt")
            .value(hasItem(DEFAULT_ACCEPTED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllServiceProposalsWithEagerRelationshipsIsEnabled() {
        when(serviceProposalServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(serviceProposalServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllServiceProposalsWithEagerRelationshipsIsNotEnabled() {
        when(serviceProposalServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(serviceProposalRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getServiceProposal() {
        // Initialize the database
        insertedServiceProposal = serviceProposalRepository.save(serviceProposal).block();

        // Get the serviceProposal
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, serviceProposal.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(serviceProposal.getId().intValue()))
            .jsonPath("$.referenceCode")
            .value(is(DEFAULT_REFERENCE_CODE))
            .jsonPath("$.proposedAt")
            .value(is(DEFAULT_PROPOSED_AT.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.amount")
            .value(is(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.currencyCode")
            .value(is(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.acceptedAt")
            .value(is(DEFAULT_ACCEPTED_AT.toString()));
    }

    @Test
    void getNonExistingServiceProposal() {
        // Get the serviceProposal
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingServiceProposal() throws Exception {
        // Initialize the database
        insertedServiceProposal = serviceProposalRepository.save(serviceProposal).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceProposal
        ServiceProposal updatedServiceProposal = serviceProposalRepository.findById(serviceProposal.getId()).block();
        updatedServiceProposal
            .referenceCode(UPDATED_REFERENCE_CODE)
            .proposedAt(UPDATED_PROPOSED_AT)
            .validUntil(UPDATED_VALID_UNTIL)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .acceptedAt(UPDATED_ACCEPTED_AT);
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(updatedServiceProposal);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, serviceProposalDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ServiceProposal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceProposalToMatchAllProperties(updatedServiceProposal);
    }

    @Test
    void putNonExistingServiceProposal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceProposal.setId(longCount.incrementAndGet());

        // Create the ServiceProposal
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, serviceProposalDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceProposal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchServiceProposal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceProposal.setId(longCount.incrementAndGet());

        // Create the ServiceProposal
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceProposal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamServiceProposal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceProposal.setId(longCount.incrementAndGet());

        // Create the ServiceProposal
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ServiceProposal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateServiceProposalWithPatch() throws Exception {
        // Initialize the database
        insertedServiceProposal = serviceProposalRepository.save(serviceProposal).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceProposal using partial update
        ServiceProposal partialUpdatedServiceProposal = new ServiceProposal();
        partialUpdatedServiceProposal.setId(serviceProposal.getId());

        partialUpdatedServiceProposal.referenceCode(UPDATED_REFERENCE_CODE).proposedAt(UPDATED_PROPOSED_AT).amount(UPDATED_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedServiceProposal.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedServiceProposal))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ServiceProposal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceProposalUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedServiceProposal, serviceProposal),
            getPersistedServiceProposal(serviceProposal)
        );
    }

    @Test
    void fullUpdateServiceProposalWithPatch() throws Exception {
        // Initialize the database
        insertedServiceProposal = serviceProposalRepository.save(serviceProposal).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceProposal using partial update
        ServiceProposal partialUpdatedServiceProposal = new ServiceProposal();
        partialUpdatedServiceProposal.setId(serviceProposal.getId());

        partialUpdatedServiceProposal
            .referenceCode(UPDATED_REFERENCE_CODE)
            .proposedAt(UPDATED_PROPOSED_AT)
            .validUntil(UPDATED_VALID_UNTIL)
            .amount(UPDATED_AMOUNT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .acceptedAt(UPDATED_ACCEPTED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedServiceProposal.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedServiceProposal))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ServiceProposal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceProposalUpdatableFieldsEquals(
            partialUpdatedServiceProposal,
            getPersistedServiceProposal(partialUpdatedServiceProposal)
        );
    }

    @Test
    void patchNonExistingServiceProposal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceProposal.setId(longCount.incrementAndGet());

        // Create the ServiceProposal
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, serviceProposalDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceProposal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchServiceProposal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceProposal.setId(longCount.incrementAndGet());

        // Create the ServiceProposal
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ServiceProposal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamServiceProposal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceProposal.setId(longCount.incrementAndGet());

        // Create the ServiceProposal
        ServiceProposalDTO serviceProposalDTO = serviceProposalMapper.toDto(serviceProposal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(serviceProposalDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ServiceProposal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteServiceProposal() {
        // Initialize the database
        insertedServiceProposal = serviceProposalRepository.save(serviceProposal).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the serviceProposal
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, serviceProposal.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return serviceProposalRepository.count().block();
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

    protected ServiceProposal getPersistedServiceProposal(ServiceProposal serviceProposal) {
        return serviceProposalRepository.findById(serviceProposal.getId()).block();
    }

    protected void assertPersistedServiceProposalToMatchAllProperties(ServiceProposal expectedServiceProposal) {
        // Test fails because reactive api returns an empty object instead of null
        // assertServiceProposalAllPropertiesEquals(expectedServiceProposal, getPersistedServiceProposal(expectedServiceProposal));
        assertServiceProposalUpdatableFieldsEquals(expectedServiceProposal, getPersistedServiceProposal(expectedServiceProposal));
    }

    protected void assertPersistedServiceProposalToMatchUpdatableProperties(ServiceProposal expectedServiceProposal) {
        // Test fails because reactive api returns an empty object instead of null
        // assertServiceProposalAllUpdatablePropertiesEquals(expectedServiceProposal, getPersistedServiceProposal(expectedServiceProposal));
        assertServiceProposalUpdatableFieldsEquals(expectedServiceProposal, getPersistedServiceProposal(expectedServiceProposal));
    }
}
