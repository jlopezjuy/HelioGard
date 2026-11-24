package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AuditEvidenceAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AuditEvidence;
import ar.com.heliguard.app.domain.AuditFinding;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.AuditEvidenceRepository;
import ar.com.heliguard.app.service.AuditEvidenceService;
import ar.com.heliguard.app.service.dto.AuditEvidenceDTO;
import ar.com.heliguard.app.service.mapper.AuditEvidenceMapper;
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
 * Integration tests for the {@link AuditEvidenceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuditEvidenceResourceIT {

    private static final String DEFAULT_STORAGE_KEY = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_MIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MIME_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_SHA_256 = "BBBBBBBBBB";

    private static final Instant DEFAULT_CAPTURED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CAPTURED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/audit-evidences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditEvidenceRepository auditEvidenceRepository;

    @Mock
    private AuditEvidenceRepository auditEvidenceRepositoryMock;

    @Autowired
    private AuditEvidenceMapper auditEvidenceMapper;

    @Mock
    private AuditEvidenceService auditEvidenceServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AuditEvidence auditEvidence;

    private AuditEvidence insertedAuditEvidence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditEvidence createEntity(R2dbcEntityTemplate template) {
        AuditEvidence auditEvidence = new AuditEvidence()
            .storageKey(DEFAULT_STORAGE_KEY)
            .mimeType(DEFAULT_MIME_TYPE)
            .sha256(DEFAULT_SHA_256)
            .capturedAt(DEFAULT_CAPTURED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        auditEvidence.setOrganization(organization);
        // Add required entity
        AuditFinding auditFinding;
        auditFinding = template.insert(AuditFindingResourceIT.createEntity(template)).block();
        auditEvidence.setFinding(auditFinding);
        return auditEvidence;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditEvidence createUpdatedEntity(R2dbcEntityTemplate template) {
        AuditEvidence updatedAuditEvidence = new AuditEvidence()
            .storageKey(UPDATED_STORAGE_KEY)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256(UPDATED_SHA_256)
            .capturedAt(UPDATED_CAPTURED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAuditEvidence.setOrganization(organization);
        // Add required entity
        AuditFinding auditFinding;
        auditFinding = template.insert(AuditFindingResourceIT.createUpdatedEntity(template)).block();
        updatedAuditEvidence.setFinding(auditFinding);
        return updatedAuditEvidence;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AuditEvidence.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        AuditFindingResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        auditEvidence = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditEvidence != null) {
            auditEvidenceRepository.delete(insertedAuditEvidence).block();
            insertedAuditEvidence = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAuditEvidence() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditEvidence
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);
        var returnedAuditEvidenceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuditEvidenceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AuditEvidence in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditEvidence = auditEvidenceMapper.toEntity(returnedAuditEvidenceDTO);
        assertAuditEvidenceUpdatableFieldsEquals(returnedAuditEvidence, getPersistedAuditEvidence(returnedAuditEvidence));

        insertedAuditEvidence = returnedAuditEvidence;
    }

    @Test
    void createAuditEvidenceWithExistingId() throws Exception {
        // Create the AuditEvidence with an existing ID
        auditEvidence.setId(1L);
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStorageKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditEvidence.setStorageKey(null);

        // Create the AuditEvidence, which fails.
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMimeTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditEvidence.setMimeType(null);

        // Create the AuditEvidence, which fails.
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAuditEvidencesAsStream() {
        // Initialize the database
        auditEvidenceRepository.save(auditEvidence).block();

        List<AuditEvidence> auditEvidenceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AuditEvidenceDTO.class)
            .getResponseBody()
            .map(auditEvidenceMapper::toEntity)
            .filter(auditEvidence::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(auditEvidenceList).isNotNull();
        assertThat(auditEvidenceList).hasSize(1);
        AuditEvidence testAuditEvidence = auditEvidenceList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertAuditEvidenceAllPropertiesEquals(auditEvidence, testAuditEvidence);
        assertAuditEvidenceUpdatableFieldsEquals(auditEvidence, testAuditEvidence);
    }

    @Test
    void getAllAuditEvidences() {
        // Initialize the database
        insertedAuditEvidence = auditEvidenceRepository.save(auditEvidence).block();

        // Get all the auditEvidenceList
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
            .value(hasItem(auditEvidence.getId().intValue()))
            .jsonPath("$.[*].storageKey")
            .value(hasItem(DEFAULT_STORAGE_KEY))
            .jsonPath("$.[*].mimeType")
            .value(hasItem(DEFAULT_MIME_TYPE))
            .jsonPath("$.[*].sha256")
            .value(hasItem(DEFAULT_SHA_256))
            .jsonPath("$.[*].capturedAt")
            .value(hasItem(DEFAULT_CAPTURED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditEvidencesWithEagerRelationshipsIsEnabled() {
        when(auditEvidenceServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(auditEvidenceServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditEvidencesWithEagerRelationshipsIsNotEnabled() {
        when(auditEvidenceServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(auditEvidenceRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAuditEvidence() {
        // Initialize the database
        insertedAuditEvidence = auditEvidenceRepository.save(auditEvidence).block();

        // Get the auditEvidence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, auditEvidence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(auditEvidence.getId().intValue()))
            .jsonPath("$.storageKey")
            .value(is(DEFAULT_STORAGE_KEY))
            .jsonPath("$.mimeType")
            .value(is(DEFAULT_MIME_TYPE))
            .jsonPath("$.sha256")
            .value(is(DEFAULT_SHA_256))
            .jsonPath("$.capturedAt")
            .value(is(DEFAULT_CAPTURED_AT.toString()));
    }

    @Test
    void getNonExistingAuditEvidence() {
        // Get the auditEvidence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuditEvidence() throws Exception {
        // Initialize the database
        insertedAuditEvidence = auditEvidenceRepository.save(auditEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditEvidence
        AuditEvidence updatedAuditEvidence = auditEvidenceRepository.findById(auditEvidence.getId()).block();
        updatedAuditEvidence
            .storageKey(UPDATED_STORAGE_KEY)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256(UPDATED_SHA_256)
            .capturedAt(UPDATED_CAPTURED_AT);
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(updatedAuditEvidence);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditEvidenceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditEvidenceToMatchAllProperties(updatedAuditEvidence);
    }

    @Test
    void putNonExistingAuditEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditEvidence.setId(longCount.incrementAndGet());

        // Create the AuditEvidence
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditEvidenceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuditEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditEvidence.setId(longCount.incrementAndGet());

        // Create the AuditEvidence
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuditEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditEvidence.setId(longCount.incrementAndGet());

        // Create the AuditEvidence
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuditEvidenceWithPatch() throws Exception {
        // Initialize the database
        insertedAuditEvidence = auditEvidenceRepository.save(auditEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditEvidence using partial update
        AuditEvidence partialUpdatedAuditEvidence = new AuditEvidence();
        partialUpdatedAuditEvidence.setId(auditEvidence.getId());

        partialUpdatedAuditEvidence
            .storageKey(UPDATED_STORAGE_KEY)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256(UPDATED_SHA_256)
            .capturedAt(UPDATED_CAPTURED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditEvidence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditEvidence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditEvidence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditEvidenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditEvidence, auditEvidence),
            getPersistedAuditEvidence(auditEvidence)
        );
    }

    @Test
    void fullUpdateAuditEvidenceWithPatch() throws Exception {
        // Initialize the database
        insertedAuditEvidence = auditEvidenceRepository.save(auditEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditEvidence using partial update
        AuditEvidence partialUpdatedAuditEvidence = new AuditEvidence();
        partialUpdatedAuditEvidence.setId(auditEvidence.getId());

        partialUpdatedAuditEvidence
            .storageKey(UPDATED_STORAGE_KEY)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256(UPDATED_SHA_256)
            .capturedAt(UPDATED_CAPTURED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditEvidence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditEvidence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditEvidence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditEvidenceUpdatableFieldsEquals(partialUpdatedAuditEvidence, getPersistedAuditEvidence(partialUpdatedAuditEvidence));
    }

    @Test
    void patchNonExistingAuditEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditEvidence.setId(longCount.incrementAndGet());

        // Create the AuditEvidence
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, auditEvidenceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuditEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditEvidence.setId(longCount.incrementAndGet());

        // Create the AuditEvidence
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuditEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditEvidence.setId(longCount.incrementAndGet());

        // Create the AuditEvidence
        AuditEvidenceDTO auditEvidenceDTO = auditEvidenceMapper.toDto(auditEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditEvidenceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuditEvidence() {
        // Initialize the database
        insertedAuditEvidence = auditEvidenceRepository.save(auditEvidence).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditEvidence
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, auditEvidence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditEvidenceRepository.count().block();
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

    protected AuditEvidence getPersistedAuditEvidence(AuditEvidence auditEvidence) {
        return auditEvidenceRepository.findById(auditEvidence.getId()).block();
    }

    protected void assertPersistedAuditEvidenceToMatchAllProperties(AuditEvidence expectedAuditEvidence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditEvidenceAllPropertiesEquals(expectedAuditEvidence, getPersistedAuditEvidence(expectedAuditEvidence));
        assertAuditEvidenceUpdatableFieldsEquals(expectedAuditEvidence, getPersistedAuditEvidence(expectedAuditEvidence));
    }

    protected void assertPersistedAuditEvidenceToMatchUpdatableProperties(AuditEvidence expectedAuditEvidence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditEvidenceAllUpdatablePropertiesEquals(expectedAuditEvidence, getPersistedAuditEvidence(expectedAuditEvidence));
        assertAuditEvidenceUpdatableFieldsEquals(expectedAuditEvidence, getPersistedAuditEvidence(expectedAuditEvidence));
    }
}
