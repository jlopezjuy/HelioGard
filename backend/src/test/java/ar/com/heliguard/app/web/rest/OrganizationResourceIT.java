package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.OrganizationAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.mapper.OrganizationMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link OrganizationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrganizationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final OrganizationStatus DEFAULT_STATUS = OrganizationStatus.ACTIVE;
    private static final OrganizationStatus UPDATED_STATUS = OrganizationStatus.SUSPENDED;

    private static final OrganizationKind DEFAULT_KIND = OrganizationKind.HOUSEHOLD;
    private static final OrganizationKind UPDATED_KIND = OrganizationKind.BUSINESS;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/organizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Organization organization;

    private Organization insertedOrganization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createEntity() {
        return new Organization()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .status(DEFAULT_STATUS)
            .kind(DEFAULT_KIND)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createUpdatedEntity() {
        return new Organization()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .status(UPDATED_STATUS)
            .kind(UPDATED_KIND)
            .createdAt(UPDATED_CREATED_AT);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Organization.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        organization = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrganization != null) {
            organizationRepository.delete(insertedOrganization).block();
            insertedOrganization = null;
        }
        deleteEntities(template);
    }

    @Test
    void createOrganization() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);
        var returnedOrganizationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OrganizationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Organization in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrganization = organizationMapper.toEntity(returnedOrganizationDTO);
        assertOrganizationUpdatableFieldsEquals(returnedOrganization, getPersistedOrganization(returnedOrganization));

        insertedOrganization = returnedOrganization;
    }

    @Test
    void createOrganizationWithExistingId() throws Exception {
        // Create the Organization with an existing ID
        organization.setId(1L);
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organization.setName(null);

        // Create the Organization, which fails.
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organization.setSlug(null);

        // Create the Organization, which fails.
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organization.setStatus(null);

        // Create the Organization, which fails.
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organization.setKind(null);

        // Create the Organization, which fails.
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organization.setCreatedAt(null);

        // Create the Organization, which fails.
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrganizationsAsStream() {
        // Initialize the database
        organizationRepository.save(organization).block();

        List<Organization> organizationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrganizationDTO.class)
            .getResponseBody()
            .map(organizationMapper::toEntity)
            .filter(organization::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(organizationList).isNotNull();
        assertThat(organizationList).hasSize(1);
        Organization testOrganization = organizationList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationAllPropertiesEquals(organization, testOrganization);
        assertOrganizationUpdatableFieldsEquals(organization, testOrganization);
    }

    @Test
    void getAllOrganizations() {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        // Get all the organizationList
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
            .value(hasItem(organization.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].kind")
            .value(hasItem(DEFAULT_KIND.toString()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getOrganization() {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        // Get the organization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(organization.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.kind")
            .value(is(DEFAULT_KIND.toString()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getNonExistingOrganization() {
        // Get the organization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrganization() throws Exception {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organization
        Organization updatedOrganization = organizationRepository.findById(organization.getId()).block();
        updatedOrganization.name(UPDATED_NAME).slug(UPDATED_SLUG).status(UPDATED_STATUS).kind(UPDATED_KIND).createdAt(UPDATED_CREATED_AT);
        OrganizationDTO organizationDTO = organizationMapper.toDto(updatedOrganization);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrganizationToMatchAllProperties(updatedOrganization);
    }

    @Test
    void putNonExistingOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization.status(UPDATED_STATUS).kind(UPDATED_KIND);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganizationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrganization, organization),
            getPersistedOrganization(organization)
        );
    }

    @Test
    void fullUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .status(UPDATED_STATUS)
            .kind(UPDATED_KIND)
            .createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrganization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Organization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganizationUpdatableFieldsEquals(partialUpdatedOrganization, getPersistedOrganization(partialUpdatedOrganization));
    }

    @Test
    void patchNonExistingOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, organizationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrganization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organization.setId(longCount.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Organization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrganization() {
        // Initialize the database
        insertedOrganization = organizationRepository.save(organization).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the organization
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, organization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return organizationRepository.count().block();
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

    protected Organization getPersistedOrganization(Organization organization) {
        return organizationRepository.findById(organization.getId()).block();
    }

    protected void assertPersistedOrganizationToMatchAllProperties(Organization expectedOrganization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationAllPropertiesEquals(expectedOrganization, getPersistedOrganization(expectedOrganization));
        assertOrganizationUpdatableFieldsEquals(expectedOrganization, getPersistedOrganization(expectedOrganization));
    }

    protected void assertPersistedOrganizationToMatchUpdatableProperties(Organization expectedOrganization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationAllUpdatablePropertiesEquals(expectedOrganization, getPersistedOrganization(expectedOrganization));
        assertOrganizationUpdatableFieldsEquals(expectedOrganization, getPersistedOrganization(expectedOrganization));
    }
}
