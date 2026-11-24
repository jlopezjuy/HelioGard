package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.DroneMissionAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.DroneMissionKind;
import ar.com.heliguard.app.domain.enumeration.DroneMissionStatus;
import ar.com.heliguard.app.repository.DroneMissionRepository;
import ar.com.heliguard.app.service.DroneMissionService;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.mapper.DroneMissionMapper;
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
 * Integration tests for the {@link DroneMissionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DroneMissionResourceIT {

    private static final String DEFAULT_MISSION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_MISSION_CODE = "BBBBBBBBBB";

    private static final DroneMissionKind DEFAULT_KIND = DroneMissionKind.VISUAL_INSPECTION;
    private static final DroneMissionKind UPDATED_KIND = DroneMissionKind.THERMAL_INSPECTION;

    private static final DroneMissionStatus DEFAULT_STATUS = DroneMissionStatus.DRAFT;
    private static final DroneMissionStatus UPDATED_STATUS = DroneMissionStatus.SAFETY_REVIEW;

    private static final Instant DEFAULT_PROPOSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PROPOSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_SAFETY_CHECKLIST_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SAFETY_CHECKLIST_SUMMARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/drone-missions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DroneMissionRepository droneMissionRepository;

    @Mock
    private DroneMissionRepository droneMissionRepositoryMock;

    @Autowired
    private DroneMissionMapper droneMissionMapper;

    @Mock
    private DroneMissionService droneMissionServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private DroneMission droneMission;

    private DroneMission insertedDroneMission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroneMission createEntity(R2dbcEntityTemplate template) {
        DroneMission droneMission = new DroneMission()
            .missionCode(DEFAULT_MISSION_CODE)
            .kind(DEFAULT_KIND)
            .status(DEFAULT_STATUS)
            .proposedAt(DEFAULT_PROPOSED_AT)
            .startedAt(DEFAULT_STARTED_AT)
            .completedAt(DEFAULT_COMPLETED_AT)
            .safetyChecklistSummary(DEFAULT_SAFETY_CHECKLIST_SUMMARY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        droneMission.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        droneMission.setInstallation(installation);
        return droneMission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroneMission createUpdatedEntity(R2dbcEntityTemplate template) {
        DroneMission updatedDroneMission = new DroneMission()
            .missionCode(UPDATED_MISSION_CODE)
            .kind(UPDATED_KIND)
            .status(UPDATED_STATUS)
            .proposedAt(UPDATED_PROPOSED_AT)
            .startedAt(UPDATED_STARTED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .safetyChecklistSummary(UPDATED_SAFETY_CHECKLIST_SUMMARY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedDroneMission.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedDroneMission.setInstallation(installation);
        return updatedDroneMission;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(DroneMission.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        droneMission = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedDroneMission != null) {
            droneMissionRepository.delete(insertedDroneMission).block();
            insertedDroneMission = null;
        }
        deleteEntities(template);
    }

    @Test
    void createDroneMission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DroneMission
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);
        var returnedDroneMissionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DroneMissionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the DroneMission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDroneMission = droneMissionMapper.toEntity(returnedDroneMissionDTO);
        assertDroneMissionUpdatableFieldsEquals(returnedDroneMission, getPersistedDroneMission(returnedDroneMission));

        insertedDroneMission = returnedDroneMission;
    }

    @Test
    void createDroneMissionWithExistingId() throws Exception {
        // Create the DroneMission with an existing ID
        droneMission.setId(1L);
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkMissionCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneMission.setMissionCode(null);

        // Create the DroneMission, which fails.
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneMission.setKind(null);

        // Create the DroneMission, which fails.
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneMission.setStatus(null);

        // Create the DroneMission, which fails.
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkProposedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneMission.setProposedAt(null);

        // Create the DroneMission, which fails.
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDroneMissions() {
        // Initialize the database
        insertedDroneMission = droneMissionRepository.save(droneMission).block();

        // Get all the droneMissionList
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
            .value(hasItem(droneMission.getId().intValue()))
            .jsonPath("$.[*].missionCode")
            .value(hasItem(DEFAULT_MISSION_CODE))
            .jsonPath("$.[*].kind")
            .value(hasItem(DEFAULT_KIND.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].proposedAt")
            .value(hasItem(DEFAULT_PROPOSED_AT.toString()))
            .jsonPath("$.[*].startedAt")
            .value(hasItem(DEFAULT_STARTED_AT.toString()))
            .jsonPath("$.[*].completedAt")
            .value(hasItem(DEFAULT_COMPLETED_AT.toString()))
            .jsonPath("$.[*].safetyChecklistSummary")
            .value(hasItem(DEFAULT_SAFETY_CHECKLIST_SUMMARY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDroneMissionsWithEagerRelationshipsIsEnabled() {
        when(droneMissionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(droneMissionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDroneMissionsWithEagerRelationshipsIsNotEnabled() {
        when(droneMissionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(droneMissionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDroneMission() {
        // Initialize the database
        insertedDroneMission = droneMissionRepository.save(droneMission).block();

        // Get the droneMission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, droneMission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(droneMission.getId().intValue()))
            .jsonPath("$.missionCode")
            .value(is(DEFAULT_MISSION_CODE))
            .jsonPath("$.kind")
            .value(is(DEFAULT_KIND.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.proposedAt")
            .value(is(DEFAULT_PROPOSED_AT.toString()))
            .jsonPath("$.startedAt")
            .value(is(DEFAULT_STARTED_AT.toString()))
            .jsonPath("$.completedAt")
            .value(is(DEFAULT_COMPLETED_AT.toString()))
            .jsonPath("$.safetyChecklistSummary")
            .value(is(DEFAULT_SAFETY_CHECKLIST_SUMMARY));
    }

    @Test
    void getNonExistingDroneMission() {
        // Get the droneMission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDroneMission() throws Exception {
        // Initialize the database
        insertedDroneMission = droneMissionRepository.save(droneMission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMission
        DroneMission updatedDroneMission = droneMissionRepository.findById(droneMission.getId()).block();
        updatedDroneMission
            .missionCode(UPDATED_MISSION_CODE)
            .kind(UPDATED_KIND)
            .status(UPDATED_STATUS)
            .proposedAt(UPDATED_PROPOSED_AT)
            .startedAt(UPDATED_STARTED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .safetyChecklistSummary(UPDATED_SAFETY_CHECKLIST_SUMMARY);
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(updatedDroneMission);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, droneMissionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDroneMissionToMatchAllProperties(updatedDroneMission);
    }

    @Test
    void putNonExistingDroneMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMission.setId(longCount.incrementAndGet());

        // Create the DroneMission
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, droneMissionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDroneMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMission.setId(longCount.incrementAndGet());

        // Create the DroneMission
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDroneMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMission.setId(longCount.incrementAndGet());

        // Create the DroneMission
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DroneMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDroneMissionWithPatch() throws Exception {
        // Initialize the database
        insertedDroneMission = droneMissionRepository.save(droneMission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMission using partial update
        DroneMission partialUpdatedDroneMission = new DroneMission();
        partialUpdatedDroneMission.setId(droneMission.getId());

        partialUpdatedDroneMission
            .missionCode(UPDATED_MISSION_CODE)
            .status(UPDATED_STATUS)
            .completedAt(UPDATED_COMPLETED_AT)
            .safetyChecklistSummary(UPDATED_SAFETY_CHECKLIST_SUMMARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDroneMission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDroneMission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDroneMissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDroneMission, droneMission),
            getPersistedDroneMission(droneMission)
        );
    }

    @Test
    void fullUpdateDroneMissionWithPatch() throws Exception {
        // Initialize the database
        insertedDroneMission = droneMissionRepository.save(droneMission).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMission using partial update
        DroneMission partialUpdatedDroneMission = new DroneMission();
        partialUpdatedDroneMission.setId(droneMission.getId());

        partialUpdatedDroneMission
            .missionCode(UPDATED_MISSION_CODE)
            .kind(UPDATED_KIND)
            .status(UPDATED_STATUS)
            .proposedAt(UPDATED_PROPOSED_AT)
            .startedAt(UPDATED_STARTED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .safetyChecklistSummary(UPDATED_SAFETY_CHECKLIST_SUMMARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDroneMission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDroneMission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDroneMissionUpdatableFieldsEquals(partialUpdatedDroneMission, getPersistedDroneMission(partialUpdatedDroneMission));
    }

    @Test
    void patchNonExistingDroneMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMission.setId(longCount.incrementAndGet());

        // Create the DroneMission
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, droneMissionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDroneMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMission.setId(longCount.incrementAndGet());

        // Create the DroneMission
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDroneMission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMission.setId(longCount.incrementAndGet());

        // Create the DroneMission
        DroneMissionDTO droneMissionDTO = droneMissionMapper.toDto(droneMission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DroneMission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDroneMission() {
        // Initialize the database
        insertedDroneMission = droneMissionRepository.save(droneMission).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the droneMission
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, droneMission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return droneMissionRepository.count().block();
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

    protected DroneMission getPersistedDroneMission(DroneMission droneMission) {
        return droneMissionRepository.findById(droneMission.getId()).block();
    }

    protected void assertPersistedDroneMissionToMatchAllProperties(DroneMission expectedDroneMission) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDroneMissionAllPropertiesEquals(expectedDroneMission, getPersistedDroneMission(expectedDroneMission));
        assertDroneMissionUpdatableFieldsEquals(expectedDroneMission, getPersistedDroneMission(expectedDroneMission));
    }

    protected void assertPersistedDroneMissionToMatchUpdatableProperties(DroneMission expectedDroneMission) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDroneMissionAllUpdatablePropertiesEquals(expectedDroneMission, getPersistedDroneMission(expectedDroneMission));
        assertDroneMissionUpdatableFieldsEquals(expectedDroneMission, getPersistedDroneMission(expectedDroneMission));
    }
}
