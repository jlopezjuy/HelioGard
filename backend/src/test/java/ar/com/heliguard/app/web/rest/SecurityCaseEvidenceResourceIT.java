package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.SecurityCaseEvidenceAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityCaseEvidence;
import ar.com.heliguard.app.domain.SecurityIncident;
import ar.com.heliguard.app.repository.SecurityCaseEvidenceRepository;
import ar.com.heliguard.app.service.SecurityCaseEvidenceService;
import ar.com.heliguard.app.service.dto.SecurityCaseEvidenceDTO;
import ar.com.heliguard.app.service.mapper.SecurityCaseEvidenceMapper;
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
 * Integration tests for the {@link SecurityCaseEvidenceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SecurityCaseEvidenceResourceIT {

    private static final String DEFAULT_EVIDENCE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EVIDENCE_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_STORAGE_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_SHA_256 = "BBBBBBBBBB";

    private static final Instant DEFAULT_RECORDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECORDED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/security-case-evidences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SecurityCaseEvidenceRepository securityCaseEvidenceRepository;

    @Mock
    private SecurityCaseEvidenceRepository securityCaseEvidenceRepositoryMock;

    @Autowired
    private SecurityCaseEvidenceMapper securityCaseEvidenceMapper;

    @Mock
    private SecurityCaseEvidenceService securityCaseEvidenceServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private SecurityCaseEvidence securityCaseEvidence;

    private SecurityCaseEvidence insertedSecurityCaseEvidence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityCaseEvidence createEntity(R2dbcEntityTemplate template) {
        SecurityCaseEvidence securityCaseEvidence = new SecurityCaseEvidence()
            .evidenceCode(DEFAULT_EVIDENCE_CODE)
            .storageReference(DEFAULT_STORAGE_REFERENCE)
            .sha256(DEFAULT_SHA_256)
            .recordedAt(DEFAULT_RECORDED_AT)
            .note(DEFAULT_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        securityCaseEvidence.setOrganization(organization);
        // Add required entity
        SecurityIncident securityIncident;
        securityIncident = template.insert(SecurityIncidentResourceIT.createEntity(template)).block();
        securityCaseEvidence.setSecurityIncident(securityIncident);
        return securityCaseEvidence;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityCaseEvidence createUpdatedEntity(R2dbcEntityTemplate template) {
        SecurityCaseEvidence updatedSecurityCaseEvidence = new SecurityCaseEvidence()
            .evidenceCode(UPDATED_EVIDENCE_CODE)
            .storageReference(UPDATED_STORAGE_REFERENCE)
            .sha256(UPDATED_SHA_256)
            .recordedAt(UPDATED_RECORDED_AT)
            .note(UPDATED_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedSecurityCaseEvidence.setOrganization(organization);
        // Add required entity
        SecurityIncident securityIncident;
        securityIncident = template.insert(SecurityIncidentResourceIT.createUpdatedEntity(template)).block();
        updatedSecurityCaseEvidence.setSecurityIncident(securityIncident);
        return updatedSecurityCaseEvidence;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(SecurityCaseEvidence.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        SecurityIncidentResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        securityCaseEvidence = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedSecurityCaseEvidence != null) {
            securityCaseEvidenceRepository.delete(insertedSecurityCaseEvidence).block();
            insertedSecurityCaseEvidence = null;
        }
        deleteEntities(template);
    }

    @Test
    void createSecurityCaseEvidence() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SecurityCaseEvidence
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);
        var returnedSecurityCaseEvidenceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SecurityCaseEvidenceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the SecurityCaseEvidence in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSecurityCaseEvidence = securityCaseEvidenceMapper.toEntity(returnedSecurityCaseEvidenceDTO);
        assertSecurityCaseEvidenceUpdatableFieldsEquals(
            returnedSecurityCaseEvidence,
            getPersistedSecurityCaseEvidence(returnedSecurityCaseEvidence)
        );

        insertedSecurityCaseEvidence = returnedSecurityCaseEvidence;
    }

    @Test
    void createSecurityCaseEvidenceWithExistingId() throws Exception {
        // Create the SecurityCaseEvidence with an existing ID
        securityCaseEvidence.setId(1L);
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEvidenceCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        securityCaseEvidence.setEvidenceCode(null);

        // Create the SecurityCaseEvidence, which fails.
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRecordedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        securityCaseEvidence.setRecordedAt(null);

        // Create the SecurityCaseEvidence, which fails.
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSecurityCaseEvidencesAsStream() {
        // Initialize the database
        securityCaseEvidenceRepository.save(securityCaseEvidence).block();

        List<SecurityCaseEvidence> securityCaseEvidenceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SecurityCaseEvidenceDTO.class)
            .getResponseBody()
            .map(securityCaseEvidenceMapper::toEntity)
            .filter(securityCaseEvidence::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(securityCaseEvidenceList).isNotNull();
        assertThat(securityCaseEvidenceList).hasSize(1);
        SecurityCaseEvidence testSecurityCaseEvidence = securityCaseEvidenceList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertSecurityCaseEvidenceAllPropertiesEquals(securityCaseEvidence, testSecurityCaseEvidence);
        assertSecurityCaseEvidenceUpdatableFieldsEquals(securityCaseEvidence, testSecurityCaseEvidence);
    }

    @Test
    void getAllSecurityCaseEvidences() {
        // Initialize the database
        insertedSecurityCaseEvidence = securityCaseEvidenceRepository.save(securityCaseEvidence).block();

        // Get all the securityCaseEvidenceList
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
            .value(hasItem(securityCaseEvidence.getId().intValue()))
            .jsonPath("$.[*].evidenceCode")
            .value(hasItem(DEFAULT_EVIDENCE_CODE))
            .jsonPath("$.[*].storageReference")
            .value(hasItem(DEFAULT_STORAGE_REFERENCE))
            .jsonPath("$.[*].sha256")
            .value(hasItem(DEFAULT_SHA_256))
            .jsonPath("$.[*].recordedAt")
            .value(hasItem(DEFAULT_RECORDED_AT.toString()))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSecurityCaseEvidencesWithEagerRelationshipsIsEnabled() {
        when(securityCaseEvidenceServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(securityCaseEvidenceServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSecurityCaseEvidencesWithEagerRelationshipsIsNotEnabled() {
        when(securityCaseEvidenceServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(securityCaseEvidenceRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSecurityCaseEvidence() {
        // Initialize the database
        insertedSecurityCaseEvidence = securityCaseEvidenceRepository.save(securityCaseEvidence).block();

        // Get the securityCaseEvidence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, securityCaseEvidence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(securityCaseEvidence.getId().intValue()))
            .jsonPath("$.evidenceCode")
            .value(is(DEFAULT_EVIDENCE_CODE))
            .jsonPath("$.storageReference")
            .value(is(DEFAULT_STORAGE_REFERENCE))
            .jsonPath("$.sha256")
            .value(is(DEFAULT_SHA_256))
            .jsonPath("$.recordedAt")
            .value(is(DEFAULT_RECORDED_AT.toString()))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE));
    }

    @Test
    void getNonExistingSecurityCaseEvidence() {
        // Get the securityCaseEvidence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSecurityCaseEvidence() throws Exception {
        // Initialize the database
        insertedSecurityCaseEvidence = securityCaseEvidenceRepository.save(securityCaseEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityCaseEvidence
        SecurityCaseEvidence updatedSecurityCaseEvidence = securityCaseEvidenceRepository.findById(securityCaseEvidence.getId()).block();
        updatedSecurityCaseEvidence
            .evidenceCode(UPDATED_EVIDENCE_CODE)
            .storageReference(UPDATED_STORAGE_REFERENCE)
            .sha256(UPDATED_SHA_256)
            .recordedAt(UPDATED_RECORDED_AT)
            .note(UPDATED_NOTE);
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(updatedSecurityCaseEvidence);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, securityCaseEvidenceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityCaseEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSecurityCaseEvidenceToMatchAllProperties(updatedSecurityCaseEvidence);
    }

    @Test
    void putNonExistingSecurityCaseEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvidence.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvidence
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, securityCaseEvidenceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSecurityCaseEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvidence.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvidence
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSecurityCaseEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvidence.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvidence
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SecurityCaseEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSecurityCaseEvidenceWithPatch() throws Exception {
        // Initialize the database
        insertedSecurityCaseEvidence = securityCaseEvidenceRepository.save(securityCaseEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityCaseEvidence using partial update
        SecurityCaseEvidence partialUpdatedSecurityCaseEvidence = new SecurityCaseEvidence();
        partialUpdatedSecurityCaseEvidence.setId(securityCaseEvidence.getId());

        partialUpdatedSecurityCaseEvidence.storageReference(UPDATED_STORAGE_REFERENCE).recordedAt(UPDATED_RECORDED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSecurityCaseEvidence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSecurityCaseEvidence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityCaseEvidence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSecurityCaseEvidenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSecurityCaseEvidence, securityCaseEvidence),
            getPersistedSecurityCaseEvidence(securityCaseEvidence)
        );
    }

    @Test
    void fullUpdateSecurityCaseEvidenceWithPatch() throws Exception {
        // Initialize the database
        insertedSecurityCaseEvidence = securityCaseEvidenceRepository.save(securityCaseEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityCaseEvidence using partial update
        SecurityCaseEvidence partialUpdatedSecurityCaseEvidence = new SecurityCaseEvidence();
        partialUpdatedSecurityCaseEvidence.setId(securityCaseEvidence.getId());

        partialUpdatedSecurityCaseEvidence
            .evidenceCode(UPDATED_EVIDENCE_CODE)
            .storageReference(UPDATED_STORAGE_REFERENCE)
            .sha256(UPDATED_SHA_256)
            .recordedAt(UPDATED_RECORDED_AT)
            .note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSecurityCaseEvidence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSecurityCaseEvidence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityCaseEvidence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSecurityCaseEvidenceUpdatableFieldsEquals(
            partialUpdatedSecurityCaseEvidence,
            getPersistedSecurityCaseEvidence(partialUpdatedSecurityCaseEvidence)
        );
    }

    @Test
    void patchNonExistingSecurityCaseEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvidence.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvidence
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, securityCaseEvidenceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSecurityCaseEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvidence.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvidence
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSecurityCaseEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvidence.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvidence
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = securityCaseEvidenceMapper.toDto(securityCaseEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityCaseEvidenceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SecurityCaseEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSecurityCaseEvidence() {
        // Initialize the database
        insertedSecurityCaseEvidence = securityCaseEvidenceRepository.save(securityCaseEvidence).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the securityCaseEvidence
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, securityCaseEvidence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return securityCaseEvidenceRepository.count().block();
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

    protected SecurityCaseEvidence getPersistedSecurityCaseEvidence(SecurityCaseEvidence securityCaseEvidence) {
        return securityCaseEvidenceRepository.findById(securityCaseEvidence.getId()).block();
    }

    protected void assertPersistedSecurityCaseEvidenceToMatchAllProperties(SecurityCaseEvidence expectedSecurityCaseEvidence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSecurityCaseEvidenceAllPropertiesEquals(expectedSecurityCaseEvidence, getPersistedSecurityCaseEvidence(expectedSecurityCaseEvidence));
        assertSecurityCaseEvidenceUpdatableFieldsEquals(
            expectedSecurityCaseEvidence,
            getPersistedSecurityCaseEvidence(expectedSecurityCaseEvidence)
        );
    }

    protected void assertPersistedSecurityCaseEvidenceToMatchUpdatableProperties(SecurityCaseEvidence expectedSecurityCaseEvidence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSecurityCaseEvidenceAllUpdatablePropertiesEquals(expectedSecurityCaseEvidence, getPersistedSecurityCaseEvidence(expectedSecurityCaseEvidence));
        assertSecurityCaseEvidenceUpdatableFieldsEquals(
            expectedSecurityCaseEvidence,
            getPersistedSecurityCaseEvidence(expectedSecurityCaseEvidence)
        );
    }
}
