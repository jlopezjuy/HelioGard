package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.MissionAuthorizationAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.MissionAuthorization;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.MissionAuthorizationStatus;
import ar.com.heliguard.app.repository.MissionAuthorizationRepository;
import ar.com.heliguard.app.service.MissionAuthorizationService;
import ar.com.heliguard.app.service.dto.MissionAuthorizationDTO;
import ar.com.heliguard.app.service.mapper.MissionAuthorizationMapper;
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
 * Integration tests for the {@link MissionAuthorizationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MissionAuthorizationResourceIT {

    private static final MissionAuthorizationStatus DEFAULT_STATUS = MissionAuthorizationStatus.PENDING;
    private static final MissionAuthorizationStatus UPDATED_STATUS = MissionAuthorizationStatus.APPROVED;

    private static final Instant DEFAULT_DECIDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DECIDED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_OPERATOR_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_OPERATOR_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_DECISION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_DECISION_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mission-authorizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MissionAuthorizationRepository missionAuthorizationRepository;

    @Mock
    private MissionAuthorizationRepository missionAuthorizationRepositoryMock;

    @Autowired
    private MissionAuthorizationMapper missionAuthorizationMapper;

    @Mock
    private MissionAuthorizationService missionAuthorizationServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private MissionAuthorization missionAuthorization;

    private MissionAuthorization insertedMissionAuthorization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MissionAuthorization createEntity(R2dbcEntityTemplate template) {
        MissionAuthorization missionAuthorization = new MissionAuthorization()
            .status(DEFAULT_STATUS)
            .decidedAt(DEFAULT_DECIDED_AT)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .operatorReference(DEFAULT_OPERATOR_REFERENCE)
            .decisionNote(DEFAULT_DECISION_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        missionAuthorization.setOrganization(organization);
        // Add required entity
        DroneMission droneMission;
        droneMission = template.insert(DroneMissionResourceIT.createEntity(template)).block();
        missionAuthorization.setMission(droneMission);
        return missionAuthorization;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MissionAuthorization createUpdatedEntity(R2dbcEntityTemplate template) {
        MissionAuthorization updatedMissionAuthorization = new MissionAuthorization()
            .status(UPDATED_STATUS)
            .decidedAt(UPDATED_DECIDED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .operatorReference(UPDATED_OPERATOR_REFERENCE)
            .decisionNote(UPDATED_DECISION_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedMissionAuthorization.setOrganization(organization);
        // Add required entity
        DroneMission droneMission;
        droneMission = template.insert(DroneMissionResourceIT.createUpdatedEntity(template)).block();
        updatedMissionAuthorization.setMission(droneMission);
        return updatedMissionAuthorization;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(MissionAuthorization.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        DroneMissionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        missionAuthorization = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedMissionAuthorization != null) {
            missionAuthorizationRepository.delete(insertedMissionAuthorization).block();
            insertedMissionAuthorization = null;
        }
        deleteEntities(template);
    }

    @Test
    void createMissionAuthorization() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MissionAuthorization
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);
        var returnedMissionAuthorizationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MissionAuthorizationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MissionAuthorization in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMissionAuthorization = missionAuthorizationMapper.toEntity(returnedMissionAuthorizationDTO);
        assertMissionAuthorizationUpdatableFieldsEquals(
            returnedMissionAuthorization,
            getPersistedMissionAuthorization(returnedMissionAuthorization)
        );

        insertedMissionAuthorization = returnedMissionAuthorization;
    }

    @Test
    void createMissionAuthorizationWithExistingId() throws Exception {
        // Create the MissionAuthorization with an existing ID
        missionAuthorization.setId(1L);
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionAuthorization in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        missionAuthorization.setStatus(null);

        // Create the MissionAuthorization, which fails.
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMissionAuthorizationsAsStream() {
        // Initialize the database
        missionAuthorizationRepository.save(missionAuthorization).block();

        List<MissionAuthorization> missionAuthorizationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(MissionAuthorizationDTO.class)
            .getResponseBody()
            .map(missionAuthorizationMapper::toEntity)
            .filter(missionAuthorization::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(missionAuthorizationList).isNotNull();
        assertThat(missionAuthorizationList).hasSize(1);
        MissionAuthorization testMissionAuthorization = missionAuthorizationList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertMissionAuthorizationAllPropertiesEquals(missionAuthorization, testMissionAuthorization);
        assertMissionAuthorizationUpdatableFieldsEquals(missionAuthorization, testMissionAuthorization);
    }

    @Test
    void getAllMissionAuthorizations() {
        // Initialize the database
        insertedMissionAuthorization = missionAuthorizationRepository.save(missionAuthorization).block();

        // Get all the missionAuthorizationList
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
            .value(hasItem(missionAuthorization.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].decidedAt")
            .value(hasItem(DEFAULT_DECIDED_AT.toString()))
            .jsonPath("$.[*].expiresAt")
            .value(hasItem(DEFAULT_EXPIRES_AT.toString()))
            .jsonPath("$.[*].operatorReference")
            .value(hasItem(DEFAULT_OPERATOR_REFERENCE))
            .jsonPath("$.[*].decisionNote")
            .value(hasItem(DEFAULT_DECISION_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMissionAuthorizationsWithEagerRelationshipsIsEnabled() {
        when(missionAuthorizationServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(missionAuthorizationServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMissionAuthorizationsWithEagerRelationshipsIsNotEnabled() {
        when(missionAuthorizationServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(missionAuthorizationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMissionAuthorization() {
        // Initialize the database
        insertedMissionAuthorization = missionAuthorizationRepository.save(missionAuthorization).block();

        // Get the missionAuthorization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, missionAuthorization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(missionAuthorization.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.decidedAt")
            .value(is(DEFAULT_DECIDED_AT.toString()))
            .jsonPath("$.expiresAt")
            .value(is(DEFAULT_EXPIRES_AT.toString()))
            .jsonPath("$.operatorReference")
            .value(is(DEFAULT_OPERATOR_REFERENCE))
            .jsonPath("$.decisionNote")
            .value(is(DEFAULT_DECISION_NOTE));
    }

    @Test
    void getNonExistingMissionAuthorization() {
        // Get the missionAuthorization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMissionAuthorization() throws Exception {
        // Initialize the database
        insertedMissionAuthorization = missionAuthorizationRepository.save(missionAuthorization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionAuthorization
        MissionAuthorization updatedMissionAuthorization = missionAuthorizationRepository.findById(missionAuthorization.getId()).block();
        updatedMissionAuthorization
            .status(UPDATED_STATUS)
            .decidedAt(UPDATED_DECIDED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .operatorReference(UPDATED_OPERATOR_REFERENCE)
            .decisionNote(UPDATED_DECISION_NOTE);
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(updatedMissionAuthorization);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, missionAuthorizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MissionAuthorization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMissionAuthorizationToMatchAllProperties(updatedMissionAuthorization);
    }

    @Test
    void putNonExistingMissionAuthorization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionAuthorization.setId(longCount.incrementAndGet());

        // Create the MissionAuthorization
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, missionAuthorizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionAuthorization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMissionAuthorization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionAuthorization.setId(longCount.incrementAndGet());

        // Create the MissionAuthorization
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionAuthorization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMissionAuthorization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionAuthorization.setId(longCount.incrementAndGet());

        // Create the MissionAuthorization
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MissionAuthorization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMissionAuthorizationWithPatch() throws Exception {
        // Initialize the database
        insertedMissionAuthorization = missionAuthorizationRepository.save(missionAuthorization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionAuthorization using partial update
        MissionAuthorization partialUpdatedMissionAuthorization = new MissionAuthorization();
        partialUpdatedMissionAuthorization.setId(missionAuthorization.getId());

        partialUpdatedMissionAuthorization.decidedAt(UPDATED_DECIDED_AT).decisionNote(UPDATED_DECISION_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMissionAuthorization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMissionAuthorization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MissionAuthorization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionAuthorizationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMissionAuthorization, missionAuthorization),
            getPersistedMissionAuthorization(missionAuthorization)
        );
    }

    @Test
    void fullUpdateMissionAuthorizationWithPatch() throws Exception {
        // Initialize the database
        insertedMissionAuthorization = missionAuthorizationRepository.save(missionAuthorization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionAuthorization using partial update
        MissionAuthorization partialUpdatedMissionAuthorization = new MissionAuthorization();
        partialUpdatedMissionAuthorization.setId(missionAuthorization.getId());

        partialUpdatedMissionAuthorization
            .status(UPDATED_STATUS)
            .decidedAt(UPDATED_DECIDED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .operatorReference(UPDATED_OPERATOR_REFERENCE)
            .decisionNote(UPDATED_DECISION_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMissionAuthorization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMissionAuthorization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MissionAuthorization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionAuthorizationUpdatableFieldsEquals(
            partialUpdatedMissionAuthorization,
            getPersistedMissionAuthorization(partialUpdatedMissionAuthorization)
        );
    }

    @Test
    void patchNonExistingMissionAuthorization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionAuthorization.setId(longCount.incrementAndGet());

        // Create the MissionAuthorization
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, missionAuthorizationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionAuthorization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMissionAuthorization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionAuthorization.setId(longCount.incrementAndGet());

        // Create the MissionAuthorization
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionAuthorization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMissionAuthorization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionAuthorization.setId(longCount.incrementAndGet());

        // Create the MissionAuthorization
        MissionAuthorizationDTO missionAuthorizationDTO = missionAuthorizationMapper.toDto(missionAuthorization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(missionAuthorizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MissionAuthorization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMissionAuthorization() {
        // Initialize the database
        insertedMissionAuthorization = missionAuthorizationRepository.save(missionAuthorization).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the missionAuthorization
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, missionAuthorization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return missionAuthorizationRepository.count().block();
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

    protected MissionAuthorization getPersistedMissionAuthorization(MissionAuthorization missionAuthorization) {
        return missionAuthorizationRepository.findById(missionAuthorization.getId()).block();
    }

    protected void assertPersistedMissionAuthorizationToMatchAllProperties(MissionAuthorization expectedMissionAuthorization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMissionAuthorizationAllPropertiesEquals(expectedMissionAuthorization, getPersistedMissionAuthorization(expectedMissionAuthorization));
        assertMissionAuthorizationUpdatableFieldsEquals(
            expectedMissionAuthorization,
            getPersistedMissionAuthorization(expectedMissionAuthorization)
        );
    }

    protected void assertPersistedMissionAuthorizationToMatchUpdatableProperties(MissionAuthorization expectedMissionAuthorization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMissionAuthorizationAllUpdatablePropertiesEquals(expectedMissionAuthorization, getPersistedMissionAuthorization(expectedMissionAuthorization));
        assertMissionAuthorizationUpdatableFieldsEquals(
            expectedMissionAuthorization,
            getPersistedMissionAuthorization(expectedMissionAuthorization)
        );
    }
}
