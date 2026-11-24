package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.IncidentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.IncidentStatus;
import ar.com.heliguard.app.repository.IncidentRepository;
import ar.com.heliguard.app.service.IncidentService;
import ar.com.heliguard.app.service.dto.IncidentDTO;
import ar.com.heliguard.app.service.mapper.IncidentMapper;
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
 * Integration tests for the {@link IncidentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class IncidentResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final AlertSeverity DEFAULT_SEVERITY = AlertSeverity.INFO;
    private static final AlertSeverity UPDATED_SEVERITY = AlertSeverity.LOW;

    private static final IncidentStatus DEFAULT_STATUS = IncidentStatus.OPEN;
    private static final IncidentStatus UPDATED_STATUS = IncidentStatus.ACKNOWLEDGED;

    private static final Instant DEFAULT_FIRST_SEEN_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FIRST_SEEN_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_LAST_SEEN_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_SEEN_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_ACKNOWLEDGED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACKNOWLEDGED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_RESOLVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESOLVED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_DEDUP_KEY = "AAAAAAAAAA";
    private static final String UPDATED_DEDUP_KEY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/incidents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentRepository incidentRepositoryMock;

    @Autowired
    private IncidentMapper incidentMapper;

    @Mock
    private IncidentService incidentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Incident incident;

    private Incident insertedIncident;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Incident createEntity(R2dbcEntityTemplate template) {
        Incident incident = new Incident()
            .title(DEFAULT_TITLE)
            .severity(DEFAULT_SEVERITY)
            .status(DEFAULT_STATUS)
            .firstSeenAt(DEFAULT_FIRST_SEEN_AT)
            .lastSeenAt(DEFAULT_LAST_SEEN_AT)
            .acknowledgedAt(DEFAULT_ACKNOWLEDGED_AT)
            .resolvedAt(DEFAULT_RESOLVED_AT)
            .dedupKey(DEFAULT_DEDUP_KEY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        incident.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        incident.setInstallation(installation);
        return incident;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Incident createUpdatedEntity(R2dbcEntityTemplate template) {
        Incident updatedIncident = new Incident()
            .title(UPDATED_TITLE)
            .severity(UPDATED_SEVERITY)
            .status(UPDATED_STATUS)
            .firstSeenAt(UPDATED_FIRST_SEEN_AT)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .acknowledgedAt(UPDATED_ACKNOWLEDGED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .dedupKey(UPDATED_DEDUP_KEY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedIncident.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedIncident.setInstallation(installation);
        return updatedIncident;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Incident.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        incident = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedIncident != null) {
            incidentRepository.delete(insertedIncident).block();
            insertedIncident = null;
        }
        deleteEntities(template);
    }

    @Test
    void createIncident() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);
        var returnedIncidentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(IncidentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Incident in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIncident = incidentMapper.toEntity(returnedIncidentDTO);
        assertIncidentUpdatableFieldsEquals(returnedIncident, getPersistedIncident(returnedIncident));

        insertedIncident = returnedIncident;
    }

    @Test
    void createIncidentWithExistingId() throws Exception {
        // Create the Incident with an existing ID
        incident.setId(1L);
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incident.setTitle(null);

        // Create the Incident, which fails.
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSeverityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incident.setSeverity(null);

        // Create the Incident, which fails.
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incident.setStatus(null);

        // Create the Incident, which fails.
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFirstSeenAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incident.setFirstSeenAt(null);

        // Create the Incident, which fails.
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLastSeenAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incident.setLastSeenAt(null);

        // Create the Incident, which fails.
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDedupKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incident.setDedupKey(null);

        // Create the Incident, which fails.
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllIncidents() {
        // Initialize the database
        insertedIncident = incidentRepository.save(incident).block();

        // Get all the incidentList
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
            .value(hasItem(incident.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].severity")
            .value(hasItem(DEFAULT_SEVERITY.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].firstSeenAt")
            .value(hasItem(DEFAULT_FIRST_SEEN_AT.toString()))
            .jsonPath("$.[*].lastSeenAt")
            .value(hasItem(DEFAULT_LAST_SEEN_AT.toString()))
            .jsonPath("$.[*].acknowledgedAt")
            .value(hasItem(DEFAULT_ACKNOWLEDGED_AT.toString()))
            .jsonPath("$.[*].resolvedAt")
            .value(hasItem(DEFAULT_RESOLVED_AT.toString()))
            .jsonPath("$.[*].dedupKey")
            .value(hasItem(DEFAULT_DEDUP_KEY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIncidentsWithEagerRelationshipsIsEnabled() {
        when(incidentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(incidentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIncidentsWithEagerRelationshipsIsNotEnabled() {
        when(incidentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(incidentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getIncident() {
        // Initialize the database
        insertedIncident = incidentRepository.save(incident).block();

        // Get the incident
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, incident.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(incident.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.severity")
            .value(is(DEFAULT_SEVERITY.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.firstSeenAt")
            .value(is(DEFAULT_FIRST_SEEN_AT.toString()))
            .jsonPath("$.lastSeenAt")
            .value(is(DEFAULT_LAST_SEEN_AT.toString()))
            .jsonPath("$.acknowledgedAt")
            .value(is(DEFAULT_ACKNOWLEDGED_AT.toString()))
            .jsonPath("$.resolvedAt")
            .value(is(DEFAULT_RESOLVED_AT.toString()))
            .jsonPath("$.dedupKey")
            .value(is(DEFAULT_DEDUP_KEY));
    }

    @Test
    void getNonExistingIncident() {
        // Get the incident
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingIncident() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.save(incident).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incident
        Incident updatedIncident = incidentRepository.findById(incident.getId()).block();
        updatedIncident
            .title(UPDATED_TITLE)
            .severity(UPDATED_SEVERITY)
            .status(UPDATED_STATUS)
            .firstSeenAt(UPDATED_FIRST_SEEN_AT)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .acknowledgedAt(UPDATED_ACKNOWLEDGED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .dedupKey(UPDATED_DEDUP_KEY);
        IncidentDTO incidentDTO = incidentMapper.toDto(updatedIncident);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, incidentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIncidentToMatchAllProperties(updatedIncident);
    }

    @Test
    void putNonExistingIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, incidentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateIncidentWithPatch() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.save(incident).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incident using partial update
        Incident partialUpdatedIncident = new Incident();
        partialUpdatedIncident.setId(incident.getId());

        partialUpdatedIncident
            .title(UPDATED_TITLE)
            .severity(UPDATED_SEVERITY)
            .status(UPDATED_STATUS)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .acknowledgedAt(UPDATED_ACKNOWLEDGED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIncident.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedIncident))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Incident in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedIncident, incident), getPersistedIncident(incident));
    }

    @Test
    void fullUpdateIncidentWithPatch() throws Exception {
        // Initialize the database
        insertedIncident = incidentRepository.save(incident).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incident using partial update
        Incident partialUpdatedIncident = new Incident();
        partialUpdatedIncident.setId(incident.getId());

        partialUpdatedIncident
            .title(UPDATED_TITLE)
            .severity(UPDATED_SEVERITY)
            .status(UPDATED_STATUS)
            .firstSeenAt(UPDATED_FIRST_SEEN_AT)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .acknowledgedAt(UPDATED_ACKNOWLEDGED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .dedupKey(UPDATED_DEDUP_KEY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIncident.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedIncident))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Incident in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidentUpdatableFieldsEquals(partialUpdatedIncident, getPersistedIncident(partialUpdatedIncident));
    }

    @Test
    void patchNonExistingIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, incidentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamIncident() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incident.setId(longCount.incrementAndGet());

        // Create the Incident
        IncidentDTO incidentDTO = incidentMapper.toDto(incident);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Incident in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteIncident() {
        // Initialize the database
        insertedIncident = incidentRepository.save(incident).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the incident
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, incident.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return incidentRepository.count().block();
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

    protected Incident getPersistedIncident(Incident incident) {
        return incidentRepository.findById(incident.getId()).block();
    }

    protected void assertPersistedIncidentToMatchAllProperties(Incident expectedIncident) {
        // Test fails because reactive api returns an empty object instead of null
        // assertIncidentAllPropertiesEquals(expectedIncident, getPersistedIncident(expectedIncident));
        assertIncidentUpdatableFieldsEquals(expectedIncident, getPersistedIncident(expectedIncident));
    }

    protected void assertPersistedIncidentToMatchUpdatableProperties(Incident expectedIncident) {
        // Test fails because reactive api returns an empty object instead of null
        // assertIncidentAllUpdatablePropertiesEquals(expectedIncident, getPersistedIncident(expectedIncident));
        assertIncidentUpdatableFieldsEquals(expectedIncident, getPersistedIncident(expectedIncident));
    }
}
