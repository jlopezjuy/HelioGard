package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.InterventionEvidenceAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.InterventionEvidence;
import ar.com.heliguard.app.domain.MaintenanceIntervention;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.InterventionEvidenceRepository;
import ar.com.heliguard.app.service.InterventionEvidenceService;
import ar.com.heliguard.app.service.dto.InterventionEvidenceDTO;
import ar.com.heliguard.app.service.mapper.InterventionEvidenceMapper;
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
 * Integration tests for the {@link InterventionEvidenceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InterventionEvidenceResourceIT {

    private static final String DEFAULT_STORAGE_KEY = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_MIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MIME_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_SHA_256 = "BBBBBBBBBB";

    private static final Instant DEFAULT_CAPTURED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CAPTURED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/intervention-evidences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InterventionEvidenceRepository interventionEvidenceRepository;

    @Mock
    private InterventionEvidenceRepository interventionEvidenceRepositoryMock;

    @Autowired
    private InterventionEvidenceMapper interventionEvidenceMapper;

    @Mock
    private InterventionEvidenceService interventionEvidenceServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private InterventionEvidence interventionEvidence;

    private InterventionEvidence insertedInterventionEvidence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InterventionEvidence createEntity(R2dbcEntityTemplate template) {
        InterventionEvidence interventionEvidence = new InterventionEvidence()
            .storageKey(DEFAULT_STORAGE_KEY)
            .mimeType(DEFAULT_MIME_TYPE)
            .sha256(DEFAULT_SHA_256)
            .capturedAt(DEFAULT_CAPTURED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        interventionEvidence.setOrganization(organization);
        // Add required entity
        MaintenanceIntervention maintenanceIntervention;
        maintenanceIntervention = template.insert(MaintenanceInterventionResourceIT.createEntity(template)).block();
        interventionEvidence.setIntervention(maintenanceIntervention);
        return interventionEvidence;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InterventionEvidence createUpdatedEntity(R2dbcEntityTemplate template) {
        InterventionEvidence updatedInterventionEvidence = new InterventionEvidence()
            .storageKey(UPDATED_STORAGE_KEY)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256(UPDATED_SHA_256)
            .capturedAt(UPDATED_CAPTURED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedInterventionEvidence.setOrganization(organization);
        // Add required entity
        MaintenanceIntervention maintenanceIntervention;
        maintenanceIntervention = template.insert(MaintenanceInterventionResourceIT.createUpdatedEntity(template)).block();
        updatedInterventionEvidence.setIntervention(maintenanceIntervention);
        return updatedInterventionEvidence;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(InterventionEvidence.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        MaintenanceInterventionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        interventionEvidence = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedInterventionEvidence != null) {
            interventionEvidenceRepository.delete(insertedInterventionEvidence).block();
            insertedInterventionEvidence = null;
        }
        deleteEntities(template);
    }

    @Test
    void createInterventionEvidence() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InterventionEvidence
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);
        var returnedInterventionEvidenceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InterventionEvidenceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the InterventionEvidence in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInterventionEvidence = interventionEvidenceMapper.toEntity(returnedInterventionEvidenceDTO);
        assertInterventionEvidenceUpdatableFieldsEquals(
            returnedInterventionEvidence,
            getPersistedInterventionEvidence(returnedInterventionEvidence)
        );

        insertedInterventionEvidence = returnedInterventionEvidence;
    }

    @Test
    void createInterventionEvidenceWithExistingId() throws Exception {
        // Create the InterventionEvidence with an existing ID
        interventionEvidence.setId(1L);
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InterventionEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStorageKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        interventionEvidence.setStorageKey(null);

        // Create the InterventionEvidence, which fails.
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMimeTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        interventionEvidence.setMimeType(null);

        // Create the InterventionEvidence, which fails.
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInterventionEvidencesAsStream() {
        // Initialize the database
        interventionEvidenceRepository.save(interventionEvidence).block();

        List<InterventionEvidence> interventionEvidenceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(InterventionEvidenceDTO.class)
            .getResponseBody()
            .map(interventionEvidenceMapper::toEntity)
            .filter(interventionEvidence::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(interventionEvidenceList).isNotNull();
        assertThat(interventionEvidenceList).hasSize(1);
        InterventionEvidence testInterventionEvidence = interventionEvidenceList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertInterventionEvidenceAllPropertiesEquals(interventionEvidence, testInterventionEvidence);
        assertInterventionEvidenceUpdatableFieldsEquals(interventionEvidence, testInterventionEvidence);
    }

    @Test
    void getAllInterventionEvidences() {
        // Initialize the database
        insertedInterventionEvidence = interventionEvidenceRepository.save(interventionEvidence).block();

        // Get all the interventionEvidenceList
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
            .value(hasItem(interventionEvidence.getId().intValue()))
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
    void getAllInterventionEvidencesWithEagerRelationshipsIsEnabled() {
        when(interventionEvidenceServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(interventionEvidenceServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInterventionEvidencesWithEagerRelationshipsIsNotEnabled() {
        when(interventionEvidenceServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(interventionEvidenceRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInterventionEvidence() {
        // Initialize the database
        insertedInterventionEvidence = interventionEvidenceRepository.save(interventionEvidence).block();

        // Get the interventionEvidence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, interventionEvidence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(interventionEvidence.getId().intValue()))
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
    void getNonExistingInterventionEvidence() {
        // Get the interventionEvidence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInterventionEvidence() throws Exception {
        // Initialize the database
        insertedInterventionEvidence = interventionEvidenceRepository.save(interventionEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the interventionEvidence
        InterventionEvidence updatedInterventionEvidence = interventionEvidenceRepository.findById(interventionEvidence.getId()).block();
        updatedInterventionEvidence
            .storageKey(UPDATED_STORAGE_KEY)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256(UPDATED_SHA_256)
            .capturedAt(UPDATED_CAPTURED_AT);
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(updatedInterventionEvidence);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, interventionEvidenceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InterventionEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInterventionEvidenceToMatchAllProperties(updatedInterventionEvidence);
    }

    @Test
    void putNonExistingInterventionEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interventionEvidence.setId(longCount.incrementAndGet());

        // Create the InterventionEvidence
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, interventionEvidenceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InterventionEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInterventionEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interventionEvidence.setId(longCount.incrementAndGet());

        // Create the InterventionEvidence
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InterventionEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInterventionEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interventionEvidence.setId(longCount.incrementAndGet());

        // Create the InterventionEvidence
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InterventionEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInterventionEvidenceWithPatch() throws Exception {
        // Initialize the database
        insertedInterventionEvidence = interventionEvidenceRepository.save(interventionEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the interventionEvidence using partial update
        InterventionEvidence partialUpdatedInterventionEvidence = new InterventionEvidence();
        partialUpdatedInterventionEvidence.setId(interventionEvidence.getId());

        partialUpdatedInterventionEvidence.storageKey(UPDATED_STORAGE_KEY).sha256(UPDATED_SHA_256);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInterventionEvidence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInterventionEvidence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InterventionEvidence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInterventionEvidenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInterventionEvidence, interventionEvidence),
            getPersistedInterventionEvidence(interventionEvidence)
        );
    }

    @Test
    void fullUpdateInterventionEvidenceWithPatch() throws Exception {
        // Initialize the database
        insertedInterventionEvidence = interventionEvidenceRepository.save(interventionEvidence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the interventionEvidence using partial update
        InterventionEvidence partialUpdatedInterventionEvidence = new InterventionEvidence();
        partialUpdatedInterventionEvidence.setId(interventionEvidence.getId());

        partialUpdatedInterventionEvidence
            .storageKey(UPDATED_STORAGE_KEY)
            .mimeType(UPDATED_MIME_TYPE)
            .sha256(UPDATED_SHA_256)
            .capturedAt(UPDATED_CAPTURED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInterventionEvidence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInterventionEvidence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InterventionEvidence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInterventionEvidenceUpdatableFieldsEquals(
            partialUpdatedInterventionEvidence,
            getPersistedInterventionEvidence(partialUpdatedInterventionEvidence)
        );
    }

    @Test
    void patchNonExistingInterventionEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interventionEvidence.setId(longCount.incrementAndGet());

        // Create the InterventionEvidence
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, interventionEvidenceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InterventionEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInterventionEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interventionEvidence.setId(longCount.incrementAndGet());

        // Create the InterventionEvidence
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InterventionEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInterventionEvidence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interventionEvidence.setId(longCount.incrementAndGet());

        // Create the InterventionEvidence
        InterventionEvidenceDTO interventionEvidenceDTO = interventionEvidenceMapper.toDto(interventionEvidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(interventionEvidenceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InterventionEvidence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInterventionEvidence() {
        // Initialize the database
        insertedInterventionEvidence = interventionEvidenceRepository.save(interventionEvidence).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the interventionEvidence
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, interventionEvidence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return interventionEvidenceRepository.count().block();
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

    protected InterventionEvidence getPersistedInterventionEvidence(InterventionEvidence interventionEvidence) {
        return interventionEvidenceRepository.findById(interventionEvidence.getId()).block();
    }

    protected void assertPersistedInterventionEvidenceToMatchAllProperties(InterventionEvidence expectedInterventionEvidence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInterventionEvidenceAllPropertiesEquals(expectedInterventionEvidence, getPersistedInterventionEvidence(expectedInterventionEvidence));
        assertInterventionEvidenceUpdatableFieldsEquals(
            expectedInterventionEvidence,
            getPersistedInterventionEvidence(expectedInterventionEvidence)
        );
    }

    protected void assertPersistedInterventionEvidenceToMatchUpdatableProperties(InterventionEvidence expectedInterventionEvidence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInterventionEvidenceAllUpdatablePropertiesEquals(expectedInterventionEvidence, getPersistedInterventionEvidence(expectedInterventionEvidence));
        assertInterventionEvidenceUpdatableFieldsEquals(
            expectedInterventionEvidence,
            getPersistedInterventionEvidence(expectedInterventionEvidence)
        );
    }
}
