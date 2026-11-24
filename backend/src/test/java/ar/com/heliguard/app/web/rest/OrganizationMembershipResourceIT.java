package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.OrganizationMembershipAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.service.OrganizationMembershipService;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import ar.com.heliguard.app.service.mapper.OrganizationMembershipMapper;
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
 * Integration tests for the {@link OrganizationMembershipResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrganizationMembershipResourceIT {

    private static final MembershipRole DEFAULT_ROLE = MembershipRole.OWNER;
    private static final MembershipRole UPDATED_ROLE = MembershipRole.ADMIN;

    private static final MembershipStatus DEFAULT_STATUS = MembershipStatus.INVITED;
    private static final MembershipStatus UPDATED_STATUS = MembershipStatus.ACTIVE;

    private static final Instant DEFAULT_INVITED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INVITED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_ACTIVATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTIVATED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/organization-memberships";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrganizationMembershipRepository organizationMembershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private OrganizationMembershipRepository organizationMembershipRepositoryMock;

    @Autowired
    private OrganizationMembershipMapper organizationMembershipMapper;

    @Mock
    private OrganizationMembershipService organizationMembershipServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private OrganizationMembership organizationMembership;

    private OrganizationMembership insertedOrganizationMembership;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrganizationMembership createEntity(R2dbcEntityTemplate template) {
        OrganizationMembership organizationMembership = new OrganizationMembership()
            .role(DEFAULT_ROLE)
            .status(DEFAULT_STATUS)
            .invitedAt(DEFAULT_INVITED_AT)
            .activatedAt(DEFAULT_ACTIVATED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        organizationMembership.setOrganization(organization);
        // Add required entity
        User user = template.insert(UserResourceIT.createEntity()).block();
        organizationMembership.setMember(user);
        return organizationMembership;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrganizationMembership createUpdatedEntity(R2dbcEntityTemplate template) {
        OrganizationMembership updatedOrganizationMembership = new OrganizationMembership()
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .invitedAt(UPDATED_INVITED_AT)
            .activatedAt(UPDATED_ACTIVATED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedOrganizationMembership.setOrganization(organization);
        // Add required entity
        User user = template.insert(UserResourceIT.createEntity()).block();
        updatedOrganizationMembership.setMember(user);
        return updatedOrganizationMembership;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(OrganizationMembership.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        UserResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        organizationMembership = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedOrganizationMembership != null) {
            organizationMembershipRepository.delete(insertedOrganizationMembership).block();
            insertedOrganizationMembership = null;
        }
        deleteEntities(template);
    }

    @Test
    void createOrganizationMembership() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrganizationMembership
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);
        var returnedOrganizationMembershipDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OrganizationMembershipDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OrganizationMembership in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrganizationMembership = organizationMembershipMapper.toEntity(returnedOrganizationMembershipDTO);
        assertOrganizationMembershipUpdatableFieldsEquals(
            returnedOrganizationMembership,
            getPersistedOrganizationMembership(returnedOrganizationMembership)
        );

        insertedOrganizationMembership = returnedOrganizationMembership;
    }

    @Test
    void createOrganizationMembershipWithExistingId() throws Exception {
        // Create the OrganizationMembership with an existing ID
        organizationMembership.setId(1L);
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrganizationMembership in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organizationMembership.setRole(null);

        // Create the OrganizationMembership, which fails.
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organizationMembership.setStatus(null);

        // Create the OrganizationMembership, which fails.
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrganizationMembershipsAsStream() {
        // Initialize the database
        organizationMembershipRepository.save(organizationMembership).block();

        List<OrganizationMembership> organizationMembershipList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrganizationMembershipDTO.class)
            .getResponseBody()
            .map(organizationMembershipMapper::toEntity)
            .filter(organizationMembership::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(organizationMembershipList).isNotNull();
        assertThat(organizationMembershipList).hasSize(1);
        OrganizationMembership testOrganizationMembership = organizationMembershipList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationMembershipAllPropertiesEquals(organizationMembership, testOrganizationMembership);
        assertOrganizationMembershipUpdatableFieldsEquals(organizationMembership, testOrganizationMembership);
    }

    @Test
    void getAllOrganizationMemberships() {
        // Initialize the database
        insertedOrganizationMembership = organizationMembershipRepository.save(organizationMembership).block();

        // Get all the organizationMembershipList
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
            .value(hasItem(organizationMembership.getId().intValue()))
            .jsonPath("$.[*].role")
            .value(hasItem(DEFAULT_ROLE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].invitedAt")
            .value(hasItem(DEFAULT_INVITED_AT.toString()))
            .jsonPath("$.[*].activatedAt")
            .value(hasItem(DEFAULT_ACTIVATED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrganizationMembershipsWithEagerRelationshipsIsEnabled() {
        when(organizationMembershipServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(organizationMembershipServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrganizationMembershipsWithEagerRelationshipsIsNotEnabled() {
        when(organizationMembershipServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(organizationMembershipRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getOrganizationMembership() {
        // Initialize the database
        insertedOrganizationMembership = organizationMembershipRepository.save(organizationMembership).block();

        // Get the organizationMembership
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, organizationMembership.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(organizationMembership.getId().intValue()))
            .jsonPath("$.role")
            .value(is(DEFAULT_ROLE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.invitedAt")
            .value(is(DEFAULT_INVITED_AT.toString()))
            .jsonPath("$.activatedAt")
            .value(is(DEFAULT_ACTIVATED_AT.toString()));
    }

    @Test
    void getNonExistingOrganizationMembership() {
        // Get the organizationMembership
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrganizationMembership() throws Exception {
        // Initialize the database
        insertedOrganizationMembership = organizationMembershipRepository.save(organizationMembership).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organizationMembership
        OrganizationMembership updatedOrganizationMembership = organizationMembershipRepository
            .findById(organizationMembership.getId())
            .block();
        updatedOrganizationMembership
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .invitedAt(UPDATED_INVITED_AT)
            .activatedAt(UPDATED_ACTIVATED_AT);
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(updatedOrganizationMembership);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organizationMembershipDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrganizationMembership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrganizationMembershipToMatchAllProperties(updatedOrganizationMembership);
    }

    @Test
    void putNonExistingOrganizationMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizationMembership.setId(longCount.incrementAndGet());

        // Create the OrganizationMembership
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, organizationMembershipDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrganizationMembership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrganizationMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizationMembership.setId(longCount.incrementAndGet());

        // Create the OrganizationMembership
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrganizationMembership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrganizationMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizationMembership.setId(longCount.incrementAndGet());

        // Create the OrganizationMembership
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrganizationMembership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrganizationMembershipWithPatch() throws Exception {
        // Initialize the database
        insertedOrganizationMembership = organizationMembershipRepository.save(organizationMembership).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organizationMembership using partial update
        OrganizationMembership partialUpdatedOrganizationMembership = new OrganizationMembership();
        partialUpdatedOrganizationMembership.setId(organizationMembership.getId());

        partialUpdatedOrganizationMembership.status(UPDATED_STATUS).invitedAt(UPDATED_INVITED_AT).activatedAt(UPDATED_ACTIVATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganizationMembership.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrganizationMembership))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrganizationMembership in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganizationMembershipUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrganizationMembership, organizationMembership),
            getPersistedOrganizationMembership(organizationMembership)
        );
    }

    @Test
    void fullUpdateOrganizationMembershipWithPatch() throws Exception {
        // Initialize the database
        insertedOrganizationMembership = organizationMembershipRepository.save(organizationMembership).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organizationMembership using partial update
        OrganizationMembership partialUpdatedOrganizationMembership = new OrganizationMembership();
        partialUpdatedOrganizationMembership.setId(organizationMembership.getId());

        partialUpdatedOrganizationMembership
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .invitedAt(UPDATED_INVITED_AT)
            .activatedAt(UPDATED_ACTIVATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrganizationMembership.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrganizationMembership))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrganizationMembership in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganizationMembershipUpdatableFieldsEquals(
            partialUpdatedOrganizationMembership,
            getPersistedOrganizationMembership(partialUpdatedOrganizationMembership)
        );
    }

    @Test
    void patchNonExistingOrganizationMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizationMembership.setId(longCount.incrementAndGet());

        // Create the OrganizationMembership
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, organizationMembershipDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrganizationMembership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrganizationMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizationMembership.setId(longCount.incrementAndGet());

        // Create the OrganizationMembership
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrganizationMembership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrganizationMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizationMembership.setId(longCount.incrementAndGet());

        // Create the OrganizationMembership
        OrganizationMembershipDTO organizationMembershipDTO = organizationMembershipMapper.toDto(organizationMembership);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(organizationMembershipDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrganizationMembership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrganizationMembership() {
        // Initialize the database
        insertedOrganizationMembership = organizationMembershipRepository.save(organizationMembership).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the organizationMembership
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, organizationMembership.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return organizationMembershipRepository.count().block();
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

    protected OrganizationMembership getPersistedOrganizationMembership(OrganizationMembership organizationMembership) {
        return organizationMembershipRepository.findById(organizationMembership.getId()).block();
    }

    protected void assertPersistedOrganizationMembershipToMatchAllProperties(OrganizationMembership expectedOrganizationMembership) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationMembershipAllPropertiesEquals(expectedOrganizationMembership, getPersistedOrganizationMembership(expectedOrganizationMembership));
        assertOrganizationMembershipUpdatableFieldsEquals(
            expectedOrganizationMembership,
            getPersistedOrganizationMembership(expectedOrganizationMembership)
        );
    }

    protected void assertPersistedOrganizationMembershipToMatchUpdatableProperties(OrganizationMembership expectedOrganizationMembership) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrganizationMembershipAllUpdatablePropertiesEquals(expectedOrganizationMembership, getPersistedOrganizationMembership(expectedOrganizationMembership));
        assertOrganizationMembershipUpdatableFieldsEquals(
            expectedOrganizationMembership,
            getPersistedOrganizationMembership(expectedOrganizationMembership)
        );
    }
}
