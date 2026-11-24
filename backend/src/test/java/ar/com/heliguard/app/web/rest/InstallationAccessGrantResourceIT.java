package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.InstallationAccessGrantAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.InstallationAccessGrant;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import ar.com.heliguard.app.repository.InstallationAccessGrantRepository;
import ar.com.heliguard.app.service.InstallationAccessGrantService;
import ar.com.heliguard.app.service.dto.InstallationAccessGrantDTO;
import ar.com.heliguard.app.service.mapper.InstallationAccessGrantMapper;
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
 * Integration tests for the {@link InstallationAccessGrantResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InstallationAccessGrantResourceIT {

    private static final PermissionScope DEFAULT_SCOPE = PermissionScope.READ_ONLY;
    private static final PermissionScope UPDATED_SCOPE = PermissionScope.AUDIT;

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_REVOKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REVOKED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/installation-access-grants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstallationAccessGrantRepository installationAccessGrantRepository;

    @Mock
    private InstallationAccessGrantRepository installationAccessGrantRepositoryMock;

    @Autowired
    private InstallationAccessGrantMapper installationAccessGrantMapper;

    @Mock
    private InstallationAccessGrantService installationAccessGrantServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private InstallationAccessGrant installationAccessGrant;

    private InstallationAccessGrant insertedInstallationAccessGrant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstallationAccessGrant createEntity(R2dbcEntityTemplate template) {
        InstallationAccessGrant installationAccessGrant = new InstallationAccessGrant()
            .scope(DEFAULT_SCOPE)
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL)
            .revokedAt(DEFAULT_REVOKED_AT)
            .reason(DEFAULT_REASON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        installationAccessGrant.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        installationAccessGrant.setInstallation(installation);
        // Add required entity
        installationAccessGrant.setGranteeOrganization(organization);
        return installationAccessGrant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstallationAccessGrant createUpdatedEntity(R2dbcEntityTemplate template) {
        InstallationAccessGrant updatedInstallationAccessGrant = new InstallationAccessGrant()
            .scope(UPDATED_SCOPE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .revokedAt(UPDATED_REVOKED_AT)
            .reason(UPDATED_REASON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedInstallationAccessGrant.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedInstallationAccessGrant.setInstallation(installation);
        // Add required entity
        updatedInstallationAccessGrant.setGranteeOrganization(organization);
        return updatedInstallationAccessGrant;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(InstallationAccessGrant.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        installationAccessGrant = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedInstallationAccessGrant != null) {
            installationAccessGrantRepository.delete(insertedInstallationAccessGrant).block();
            insertedInstallationAccessGrant = null;
        }
        deleteEntities(template);
    }

    @Test
    void createInstallationAccessGrant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InstallationAccessGrant
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);
        var returnedInstallationAccessGrantDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InstallationAccessGrantDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the InstallationAccessGrant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstallationAccessGrant = installationAccessGrantMapper.toEntity(returnedInstallationAccessGrantDTO);
        assertInstallationAccessGrantUpdatableFieldsEquals(
            returnedInstallationAccessGrant,
            getPersistedInstallationAccessGrant(returnedInstallationAccessGrant)
        );

        insertedInstallationAccessGrant = returnedInstallationAccessGrant;
    }

    @Test
    void createInstallationAccessGrantWithExistingId() throws Exception {
        // Create the InstallationAccessGrant with an existing ID
        installationAccessGrant.setId(1L);
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstallationAccessGrant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkScopeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        installationAccessGrant.setScope(null);

        // Create the InstallationAccessGrant, which fails.
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        installationAccessGrant.setValidFrom(null);

        // Create the InstallationAccessGrant, which fails.
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInstallationAccessGrantsAsStream() {
        // Initialize the database
        installationAccessGrantRepository.save(installationAccessGrant).block();

        List<InstallationAccessGrant> installationAccessGrantList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(InstallationAccessGrantDTO.class)
            .getResponseBody()
            .map(installationAccessGrantMapper::toEntity)
            .filter(installationAccessGrant::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(installationAccessGrantList).isNotNull();
        assertThat(installationAccessGrantList).hasSize(1);
        InstallationAccessGrant testInstallationAccessGrant = installationAccessGrantList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertInstallationAccessGrantAllPropertiesEquals(installationAccessGrant, testInstallationAccessGrant);
        assertInstallationAccessGrantUpdatableFieldsEquals(installationAccessGrant, testInstallationAccessGrant);
    }

    @Test
    void getAllInstallationAccessGrants() {
        // Initialize the database
        insertedInstallationAccessGrant = installationAccessGrantRepository.save(installationAccessGrant).block();

        // Get all the installationAccessGrantList
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
            .value(hasItem(installationAccessGrant.getId().intValue()))
            .jsonPath("$.[*].scope")
            .value(hasItem(DEFAULT_SCOPE.toString()))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.[*].revokedAt")
            .value(hasItem(DEFAULT_REVOKED_AT.toString()))
            .jsonPath("$.[*].reason")
            .value(hasItem(DEFAULT_REASON));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstallationAccessGrantsWithEagerRelationshipsIsEnabled() {
        when(installationAccessGrantServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(installationAccessGrantServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstallationAccessGrantsWithEagerRelationshipsIsNotEnabled() {
        when(installationAccessGrantServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(installationAccessGrantRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInstallationAccessGrant() {
        // Initialize the database
        insertedInstallationAccessGrant = installationAccessGrantRepository.save(installationAccessGrant).block();

        // Get the installationAccessGrant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, installationAccessGrant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(installationAccessGrant.getId().intValue()))
            .jsonPath("$.scope")
            .value(is(DEFAULT_SCOPE.toString()))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.revokedAt")
            .value(is(DEFAULT_REVOKED_AT.toString()))
            .jsonPath("$.reason")
            .value(is(DEFAULT_REASON));
    }

    @Test
    void getNonExistingInstallationAccessGrant() {
        // Get the installationAccessGrant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInstallationAccessGrant() throws Exception {
        // Initialize the database
        insertedInstallationAccessGrant = installationAccessGrantRepository.save(installationAccessGrant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the installationAccessGrant
        InstallationAccessGrant updatedInstallationAccessGrant = installationAccessGrantRepository
            .findById(installationAccessGrant.getId())
            .block();
        updatedInstallationAccessGrant
            .scope(UPDATED_SCOPE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .revokedAt(UPDATED_REVOKED_AT)
            .reason(UPDATED_REASON);
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(updatedInstallationAccessGrant);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, installationAccessGrantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InstallationAccessGrant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstallationAccessGrantToMatchAllProperties(updatedInstallationAccessGrant);
    }

    @Test
    void putNonExistingInstallationAccessGrant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installationAccessGrant.setId(longCount.incrementAndGet());

        // Create the InstallationAccessGrant
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, installationAccessGrantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstallationAccessGrant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInstallationAccessGrant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installationAccessGrant.setId(longCount.incrementAndGet());

        // Create the InstallationAccessGrant
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstallationAccessGrant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInstallationAccessGrant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installationAccessGrant.setId(longCount.incrementAndGet());

        // Create the InstallationAccessGrant
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InstallationAccessGrant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInstallationAccessGrantWithPatch() throws Exception {
        // Initialize the database
        insertedInstallationAccessGrant = installationAccessGrantRepository.save(installationAccessGrant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the installationAccessGrant using partial update
        InstallationAccessGrant partialUpdatedInstallationAccessGrant = new InstallationAccessGrant();
        partialUpdatedInstallationAccessGrant.setId(installationAccessGrant.getId());

        partialUpdatedInstallationAccessGrant
            .scope(UPDATED_SCOPE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .reason(UPDATED_REASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstallationAccessGrant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstallationAccessGrant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InstallationAccessGrant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstallationAccessGrantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstallationAccessGrant, installationAccessGrant),
            getPersistedInstallationAccessGrant(installationAccessGrant)
        );
    }

    @Test
    void fullUpdateInstallationAccessGrantWithPatch() throws Exception {
        // Initialize the database
        insertedInstallationAccessGrant = installationAccessGrantRepository.save(installationAccessGrant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the installationAccessGrant using partial update
        InstallationAccessGrant partialUpdatedInstallationAccessGrant = new InstallationAccessGrant();
        partialUpdatedInstallationAccessGrant.setId(installationAccessGrant.getId());

        partialUpdatedInstallationAccessGrant
            .scope(UPDATED_SCOPE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .revokedAt(UPDATED_REVOKED_AT)
            .reason(UPDATED_REASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstallationAccessGrant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstallationAccessGrant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InstallationAccessGrant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstallationAccessGrantUpdatableFieldsEquals(
            partialUpdatedInstallationAccessGrant,
            getPersistedInstallationAccessGrant(partialUpdatedInstallationAccessGrant)
        );
    }

    @Test
    void patchNonExistingInstallationAccessGrant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installationAccessGrant.setId(longCount.incrementAndGet());

        // Create the InstallationAccessGrant
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, installationAccessGrantDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstallationAccessGrant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInstallationAccessGrant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installationAccessGrant.setId(longCount.incrementAndGet());

        // Create the InstallationAccessGrant
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InstallationAccessGrant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInstallationAccessGrant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installationAccessGrant.setId(longCount.incrementAndGet());

        // Create the InstallationAccessGrant
        InstallationAccessGrantDTO installationAccessGrantDTO = installationAccessGrantMapper.toDto(installationAccessGrant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(installationAccessGrantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InstallationAccessGrant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInstallationAccessGrant() {
        // Initialize the database
        insertedInstallationAccessGrant = installationAccessGrantRepository.save(installationAccessGrant).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the installationAccessGrant
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, installationAccessGrant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return installationAccessGrantRepository.count().block();
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

    protected InstallationAccessGrant getPersistedInstallationAccessGrant(InstallationAccessGrant installationAccessGrant) {
        return installationAccessGrantRepository.findById(installationAccessGrant.getId()).block();
    }

    protected void assertPersistedInstallationAccessGrantToMatchAllProperties(InstallationAccessGrant expectedInstallationAccessGrant) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstallationAccessGrantAllPropertiesEquals(expectedInstallationAccessGrant, getPersistedInstallationAccessGrant(expectedInstallationAccessGrant));
        assertInstallationAccessGrantUpdatableFieldsEquals(
            expectedInstallationAccessGrant,
            getPersistedInstallationAccessGrant(expectedInstallationAccessGrant)
        );
    }

    protected void assertPersistedInstallationAccessGrantToMatchUpdatableProperties(
        InstallationAccessGrant expectedInstallationAccessGrant
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstallationAccessGrantAllUpdatablePropertiesEquals(expectedInstallationAccessGrant, getPersistedInstallationAccessGrant(expectedInstallationAccessGrant));
        assertInstallationAccessGrantUpdatableFieldsEquals(
            expectedInstallationAccessGrant,
            getPersistedInstallationAccessGrant(expectedInstallationAccessGrant)
        );
    }
}
