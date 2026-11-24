package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AuditFindingAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AuditFinding;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.FindingSeverity;
import ar.com.heliguard.app.repository.AuditFindingRepository;
import ar.com.heliguard.app.service.AuditFindingService;
import ar.com.heliguard.app.service.dto.AuditFindingDTO;
import ar.com.heliguard.app.service.mapper.AuditFindingMapper;
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
 * Integration tests for the {@link AuditFindingResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuditFindingResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final FindingSeverity DEFAULT_SEVERITY = FindingSeverity.LOW;
    private static final FindingSeverity UPDATED_SEVERITY = FindingSeverity.MEDIUM;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_RECOMMENDATION = "AAAAAAAAAA";
    private static final String UPDATED_RECOMMENDATION = "BBBBBBBBBB";

    private static final Instant DEFAULT_RESOLVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESOLVED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/audit-findings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditFindingRepository auditFindingRepository;

    @Mock
    private AuditFindingRepository auditFindingRepositoryMock;

    @Autowired
    private AuditFindingMapper auditFindingMapper;

    @Mock
    private AuditFindingService auditFindingServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AuditFinding auditFinding;

    private AuditFinding insertedAuditFinding;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditFinding createEntity(R2dbcEntityTemplate template) {
        AuditFinding auditFinding = new AuditFinding()
            .title(DEFAULT_TITLE)
            .severity(DEFAULT_SEVERITY)
            .description(DEFAULT_DESCRIPTION)
            .recommendation(DEFAULT_RECOMMENDATION)
            .resolvedAt(DEFAULT_RESOLVED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        auditFinding.setOrganization(organization);
        // Add required entity
        AuditReport auditReport;
        auditReport = template.insert(AuditReportResourceIT.createEntity(template)).block();
        auditFinding.setAuditReport(auditReport);
        return auditFinding;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditFinding createUpdatedEntity(R2dbcEntityTemplate template) {
        AuditFinding updatedAuditFinding = new AuditFinding()
            .title(UPDATED_TITLE)
            .severity(UPDATED_SEVERITY)
            .description(UPDATED_DESCRIPTION)
            .recommendation(UPDATED_RECOMMENDATION)
            .resolvedAt(UPDATED_RESOLVED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAuditFinding.setOrganization(organization);
        // Add required entity
        AuditReport auditReport;
        auditReport = template.insert(AuditReportResourceIT.createUpdatedEntity(template)).block();
        updatedAuditFinding.setAuditReport(auditReport);
        return updatedAuditFinding;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AuditFinding.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        AuditReportResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        auditFinding = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditFinding != null) {
            auditFindingRepository.delete(insertedAuditFinding).block();
            insertedAuditFinding = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAuditFinding() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditFinding
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);
        var returnedAuditFindingDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuditFindingDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AuditFinding in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditFinding = auditFindingMapper.toEntity(returnedAuditFindingDTO);
        assertAuditFindingUpdatableFieldsEquals(returnedAuditFinding, getPersistedAuditFinding(returnedAuditFinding));

        insertedAuditFinding = returnedAuditFinding;
    }

    @Test
    void createAuditFindingWithExistingId() throws Exception {
        // Create the AuditFinding with an existing ID
        auditFinding.setId(1L);
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditFinding in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditFinding.setTitle(null);

        // Create the AuditFinding, which fails.
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSeverityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditFinding.setSeverity(null);

        // Create the AuditFinding, which fails.
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAuditFindingsAsStream() {
        // Initialize the database
        auditFindingRepository.save(auditFinding).block();

        List<AuditFinding> auditFindingList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AuditFindingDTO.class)
            .getResponseBody()
            .map(auditFindingMapper::toEntity)
            .filter(auditFinding::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(auditFindingList).isNotNull();
        assertThat(auditFindingList).hasSize(1);
        AuditFinding testAuditFinding = auditFindingList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertAuditFindingAllPropertiesEquals(auditFinding, testAuditFinding);
        assertAuditFindingUpdatableFieldsEquals(auditFinding, testAuditFinding);
    }

    @Test
    void getAllAuditFindings() {
        // Initialize the database
        insertedAuditFinding = auditFindingRepository.save(auditFinding).block();

        // Get all the auditFindingList
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
            .value(hasItem(auditFinding.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].severity")
            .value(hasItem(DEFAULT_SEVERITY.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].recommendation")
            .value(hasItem(DEFAULT_RECOMMENDATION))
            .jsonPath("$.[*].resolvedAt")
            .value(hasItem(DEFAULT_RESOLVED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditFindingsWithEagerRelationshipsIsEnabled() {
        when(auditFindingServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(auditFindingServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditFindingsWithEagerRelationshipsIsNotEnabled() {
        when(auditFindingServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(auditFindingRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAuditFinding() {
        // Initialize the database
        insertedAuditFinding = auditFindingRepository.save(auditFinding).block();

        // Get the auditFinding
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, auditFinding.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(auditFinding.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.severity")
            .value(is(DEFAULT_SEVERITY.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.recommendation")
            .value(is(DEFAULT_RECOMMENDATION))
            .jsonPath("$.resolvedAt")
            .value(is(DEFAULT_RESOLVED_AT.toString()));
    }

    @Test
    void getNonExistingAuditFinding() {
        // Get the auditFinding
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuditFinding() throws Exception {
        // Initialize the database
        insertedAuditFinding = auditFindingRepository.save(auditFinding).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditFinding
        AuditFinding updatedAuditFinding = auditFindingRepository.findById(auditFinding.getId()).block();
        updatedAuditFinding
            .title(UPDATED_TITLE)
            .severity(UPDATED_SEVERITY)
            .description(UPDATED_DESCRIPTION)
            .recommendation(UPDATED_RECOMMENDATION)
            .resolvedAt(UPDATED_RESOLVED_AT);
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(updatedAuditFinding);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditFindingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditFinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditFindingToMatchAllProperties(updatedAuditFinding);
    }

    @Test
    void putNonExistingAuditFinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditFinding.setId(longCount.incrementAndGet());

        // Create the AuditFinding
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditFindingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditFinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuditFinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditFinding.setId(longCount.incrementAndGet());

        // Create the AuditFinding
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditFinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuditFinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditFinding.setId(longCount.incrementAndGet());

        // Create the AuditFinding
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditFinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuditFindingWithPatch() throws Exception {
        // Initialize the database
        insertedAuditFinding = auditFindingRepository.save(auditFinding).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditFinding using partial update
        AuditFinding partialUpdatedAuditFinding = new AuditFinding();
        partialUpdatedAuditFinding.setId(auditFinding.getId());

        partialUpdatedAuditFinding.description(UPDATED_DESCRIPTION).resolvedAt(UPDATED_RESOLVED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditFinding.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditFinding))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditFinding in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditFindingUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditFinding, auditFinding),
            getPersistedAuditFinding(auditFinding)
        );
    }

    @Test
    void fullUpdateAuditFindingWithPatch() throws Exception {
        // Initialize the database
        insertedAuditFinding = auditFindingRepository.save(auditFinding).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditFinding using partial update
        AuditFinding partialUpdatedAuditFinding = new AuditFinding();
        partialUpdatedAuditFinding.setId(auditFinding.getId());

        partialUpdatedAuditFinding
            .title(UPDATED_TITLE)
            .severity(UPDATED_SEVERITY)
            .description(UPDATED_DESCRIPTION)
            .recommendation(UPDATED_RECOMMENDATION)
            .resolvedAt(UPDATED_RESOLVED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditFinding.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditFinding))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditFinding in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditFindingUpdatableFieldsEquals(partialUpdatedAuditFinding, getPersistedAuditFinding(partialUpdatedAuditFinding));
    }

    @Test
    void patchNonExistingAuditFinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditFinding.setId(longCount.incrementAndGet());

        // Create the AuditFinding
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, auditFindingDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditFinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuditFinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditFinding.setId(longCount.incrementAndGet());

        // Create the AuditFinding
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditFinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuditFinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditFinding.setId(longCount.incrementAndGet());

        // Create the AuditFinding
        AuditFindingDTO auditFindingDTO = auditFindingMapper.toDto(auditFinding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditFindingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditFinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuditFinding() {
        // Initialize the database
        insertedAuditFinding = auditFindingRepository.save(auditFinding).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditFinding
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, auditFinding.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditFindingRepository.count().block();
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

    protected AuditFinding getPersistedAuditFinding(AuditFinding auditFinding) {
        return auditFindingRepository.findById(auditFinding.getId()).block();
    }

    protected void assertPersistedAuditFindingToMatchAllProperties(AuditFinding expectedAuditFinding) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditFindingAllPropertiesEquals(expectedAuditFinding, getPersistedAuditFinding(expectedAuditFinding));
        assertAuditFindingUpdatableFieldsEquals(expectedAuditFinding, getPersistedAuditFinding(expectedAuditFinding));
    }

    protected void assertPersistedAuditFindingToMatchUpdatableProperties(AuditFinding expectedAuditFinding) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditFindingAllUpdatablePropertiesEquals(expectedAuditFinding, getPersistedAuditFinding(expectedAuditFinding));
        assertAuditFindingUpdatableFieldsEquals(expectedAuditFinding, getPersistedAuditFinding(expectedAuditFinding));
    }
}
