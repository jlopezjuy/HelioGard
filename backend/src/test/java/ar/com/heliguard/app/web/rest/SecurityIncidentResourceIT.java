package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.SecurityIncidentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityIncident;
import ar.com.heliguard.app.domain.enumeration.SecurityCaseStatus;
import ar.com.heliguard.app.repository.SecurityIncidentRepository;
import ar.com.heliguard.app.service.SecurityIncidentService;
import ar.com.heliguard.app.service.dto.SecurityIncidentDTO;
import ar.com.heliguard.app.service.mapper.SecurityIncidentMapper;
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
 * Integration tests for the {@link SecurityIncidentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SecurityIncidentResourceIT {

    private static final String DEFAULT_REFERENCE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final SecurityCaseStatus DEFAULT_STATUS = SecurityCaseStatus.OPEN;
    private static final SecurityCaseStatus UPDATED_STATUS = SecurityCaseStatus.TRIAGE;

    private static final Instant DEFAULT_OPENED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OPENED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_CLOSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CLOSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_RESOLUTION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_RESOLUTION_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/security-incidents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SecurityIncidentRepository securityIncidentRepository;

    @Mock
    private SecurityIncidentRepository securityIncidentRepositoryMock;

    @Autowired
    private SecurityIncidentMapper securityIncidentMapper;

    @Mock
    private SecurityIncidentService securityIncidentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private SecurityIncident securityIncident;

    private SecurityIncident insertedSecurityIncident;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityIncident createEntity(R2dbcEntityTemplate template) {
        SecurityIncident securityIncident = new SecurityIncident()
            .referenceCode(DEFAULT_REFERENCE_CODE)
            .title(DEFAULT_TITLE)
            .status(DEFAULT_STATUS)
            .openedAt(DEFAULT_OPENED_AT)
            .closedAt(DEFAULT_CLOSED_AT)
            .resolutionNote(DEFAULT_RESOLUTION_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        securityIncident.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        securityIncident.setInstallation(installation);
        return securityIncident;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityIncident createUpdatedEntity(R2dbcEntityTemplate template) {
        SecurityIncident updatedSecurityIncident = new SecurityIncident()
            .referenceCode(UPDATED_REFERENCE_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .openedAt(UPDATED_OPENED_AT)
            .closedAt(UPDATED_CLOSED_AT)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedSecurityIncident.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedSecurityIncident.setInstallation(installation);
        return updatedSecurityIncident;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(SecurityIncident.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        securityIncident = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedSecurityIncident != null) {
            securityIncidentRepository.delete(insertedSecurityIncident).block();
            insertedSecurityIncident = null;
        }
        deleteEntities(template);
    }

    @Test
    void createSecurityIncident() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SecurityIncident
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);
        var returnedSecurityIncidentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SecurityIncidentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the SecurityIncident in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSecurityIncident = securityIncidentMapper.toEntity(returnedSecurityIncidentDTO);
        assertSecurityIncidentUpdatableFieldsEquals(returnedSecurityIncident, getPersistedSecurityIncident(returnedSecurityIncident));

        insertedSecurityIncident = returnedSecurityIncident;
    }

    @Test
    void createSecurityIncidentWithExistingId() throws Exception {
        // Create the SecurityIncident with an existing ID
        securityIncident.setId(1L);
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityIncident in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        securityIncident.setReferenceCode(null);

        // Create the SecurityIncident, which fails.
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        securityIncident.setTitle(null);

        // Create the SecurityIncident, which fails.
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        securityIncident.setStatus(null);

        // Create the SecurityIncident, which fails.
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkOpenedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        securityIncident.setOpenedAt(null);

        // Create the SecurityIncident, which fails.
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSecurityIncidents() {
        // Initialize the database
        insertedSecurityIncident = securityIncidentRepository.save(securityIncident).block();

        // Get all the securityIncidentList
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
            .value(hasItem(securityIncident.getId().intValue()))
            .jsonPath("$.[*].referenceCode")
            .value(hasItem(DEFAULT_REFERENCE_CODE))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].openedAt")
            .value(hasItem(DEFAULT_OPENED_AT.toString()))
            .jsonPath("$.[*].closedAt")
            .value(hasItem(DEFAULT_CLOSED_AT.toString()))
            .jsonPath("$.[*].resolutionNote")
            .value(hasItem(DEFAULT_RESOLUTION_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSecurityIncidentsWithEagerRelationshipsIsEnabled() {
        when(securityIncidentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(securityIncidentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSecurityIncidentsWithEagerRelationshipsIsNotEnabled() {
        when(securityIncidentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(securityIncidentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSecurityIncident() {
        // Initialize the database
        insertedSecurityIncident = securityIncidentRepository.save(securityIncident).block();

        // Get the securityIncident
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, securityIncident.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(securityIncident.getId().intValue()))
            .jsonPath("$.referenceCode")
            .value(is(DEFAULT_REFERENCE_CODE))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.openedAt")
            .value(is(DEFAULT_OPENED_AT.toString()))
            .jsonPath("$.closedAt")
            .value(is(DEFAULT_CLOSED_AT.toString()))
            .jsonPath("$.resolutionNote")
            .value(is(DEFAULT_RESOLUTION_NOTE));
    }

    @Test
    void getNonExistingSecurityIncident() {
        // Get the securityIncident
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSecurityIncident() throws Exception {
        // Initialize the database
        insertedSecurityIncident = securityIncidentRepository.save(securityIncident).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityIncident
        SecurityIncident updatedSecurityIncident = securityIncidentRepository.findById(securityIncident.getId()).block();
        updatedSecurityIncident
            .referenceCode(UPDATED_REFERENCE_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .openedAt(UPDATED_OPENED_AT)
            .closedAt(UPDATED_CLOSED_AT)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(updatedSecurityIncident);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, securityIncidentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityIncident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSecurityIncidentToMatchAllProperties(updatedSecurityIncident);
    }

    @Test
    void putNonExistingSecurityIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityIncident.setId(longCount.incrementAndGet());

        // Create the SecurityIncident
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, securityIncidentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityIncident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSecurityIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityIncident.setId(longCount.incrementAndGet());

        // Create the SecurityIncident
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityIncident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSecurityIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityIncident.setId(longCount.incrementAndGet());

        // Create the SecurityIncident
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SecurityIncident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSecurityIncidentWithPatch() throws Exception {
        // Initialize the database
        insertedSecurityIncident = securityIncidentRepository.save(securityIncident).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityIncident using partial update
        SecurityIncident partialUpdatedSecurityIncident = new SecurityIncident();
        partialUpdatedSecurityIncident.setId(securityIncident.getId());

        partialUpdatedSecurityIncident
            .referenceCode(UPDATED_REFERENCE_CODE)
            .status(UPDATED_STATUS)
            .closedAt(UPDATED_CLOSED_AT)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSecurityIncident.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSecurityIncident))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityIncident in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSecurityIncidentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSecurityIncident, securityIncident),
            getPersistedSecurityIncident(securityIncident)
        );
    }

    @Test
    void fullUpdateSecurityIncidentWithPatch() throws Exception {
        // Initialize the database
        insertedSecurityIncident = securityIncidentRepository.save(securityIncident).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityIncident using partial update
        SecurityIncident partialUpdatedSecurityIncident = new SecurityIncident();
        partialUpdatedSecurityIncident.setId(securityIncident.getId());

        partialUpdatedSecurityIncident
            .referenceCode(UPDATED_REFERENCE_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .openedAt(UPDATED_OPENED_AT)
            .closedAt(UPDATED_CLOSED_AT)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSecurityIncident.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSecurityIncident))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityIncident in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSecurityIncidentUpdatableFieldsEquals(
            partialUpdatedSecurityIncident,
            getPersistedSecurityIncident(partialUpdatedSecurityIncident)
        );
    }

    @Test
    void patchNonExistingSecurityIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityIncident.setId(longCount.incrementAndGet());

        // Create the SecurityIncident
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, securityIncidentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityIncident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSecurityIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityIncident.setId(longCount.incrementAndGet());

        // Create the SecurityIncident
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityIncident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSecurityIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityIncident.setId(longCount.incrementAndGet());

        // Create the SecurityIncident
        SecurityIncidentDTO securityIncidentDTO = securityIncidentMapper.toDto(securityIncident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityIncidentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SecurityIncident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSecurityIncident() {
        // Initialize the database
        insertedSecurityIncident = securityIncidentRepository.save(securityIncident).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the securityIncident
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, securityIncident.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return securityIncidentRepository.count().block();
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

    protected SecurityIncident getPersistedSecurityIncident(SecurityIncident securityIncident) {
        return securityIncidentRepository.findById(securityIncident.getId()).block();
    }

    protected void assertPersistedSecurityIncidentToMatchAllProperties(SecurityIncident expectedSecurityIncident) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSecurityIncidentAllPropertiesEquals(expectedSecurityIncident, getPersistedSecurityIncident(expectedSecurityIncident));
        assertSecurityIncidentUpdatableFieldsEquals(expectedSecurityIncident, getPersistedSecurityIncident(expectedSecurityIncident));
    }

    protected void assertPersistedSecurityIncidentToMatchUpdatableProperties(SecurityIncident expectedSecurityIncident) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSecurityIncidentAllUpdatablePropertiesEquals(expectedSecurityIncident, getPersistedSecurityIncident(expectedSecurityIncident));
        assertSecurityIncidentUpdatableFieldsEquals(expectedSecurityIncident, getPersistedSecurityIncident(expectedSecurityIncident));
    }
}
