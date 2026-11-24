package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.MissionSafetyCheckAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.MissionSafetyCheck;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.SafetyCheckResult;
import ar.com.heliguard.app.repository.MissionSafetyCheckRepository;
import ar.com.heliguard.app.service.MissionSafetyCheckService;
import ar.com.heliguard.app.service.dto.MissionSafetyCheckDTO;
import ar.com.heliguard.app.service.mapper.MissionSafetyCheckMapper;
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
 * Integration tests for the {@link MissionSafetyCheckResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MissionSafetyCheckResourceIT {

    private static final String DEFAULT_CHECK_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CHECK_CODE = "BBBBBBBBBB";

    private static final SafetyCheckResult DEFAULT_RESULT = SafetyCheckResult.PASS;
    private static final SafetyCheckResult UPDATED_RESULT = SafetyCheckResult.FAIL;

    private static final Instant DEFAULT_CHECKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHECKED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_EVIDENCE_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_EVIDENCE_REFERENCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mission-safety-checks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MissionSafetyCheckRepository missionSafetyCheckRepository;

    @Mock
    private MissionSafetyCheckRepository missionSafetyCheckRepositoryMock;

    @Autowired
    private MissionSafetyCheckMapper missionSafetyCheckMapper;

    @Mock
    private MissionSafetyCheckService missionSafetyCheckServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private MissionSafetyCheck missionSafetyCheck;

    private MissionSafetyCheck insertedMissionSafetyCheck;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MissionSafetyCheck createEntity(R2dbcEntityTemplate template) {
        MissionSafetyCheck missionSafetyCheck = new MissionSafetyCheck()
            .checkCode(DEFAULT_CHECK_CODE)
            .result(DEFAULT_RESULT)
            .checkedAt(DEFAULT_CHECKED_AT)
            .evidenceReference(DEFAULT_EVIDENCE_REFERENCE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        missionSafetyCheck.setOrganization(organization);
        // Add required entity
        DroneMission droneMission;
        droneMission = template.insert(DroneMissionResourceIT.createEntity(template)).block();
        missionSafetyCheck.setMission(droneMission);
        return missionSafetyCheck;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MissionSafetyCheck createUpdatedEntity(R2dbcEntityTemplate template) {
        MissionSafetyCheck updatedMissionSafetyCheck = new MissionSafetyCheck()
            .checkCode(UPDATED_CHECK_CODE)
            .result(UPDATED_RESULT)
            .checkedAt(UPDATED_CHECKED_AT)
            .evidenceReference(UPDATED_EVIDENCE_REFERENCE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedMissionSafetyCheck.setOrganization(organization);
        // Add required entity
        DroneMission droneMission;
        droneMission = template.insert(DroneMissionResourceIT.createUpdatedEntity(template)).block();
        updatedMissionSafetyCheck.setMission(droneMission);
        return updatedMissionSafetyCheck;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(MissionSafetyCheck.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        DroneMissionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        missionSafetyCheck = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedMissionSafetyCheck != null) {
            missionSafetyCheckRepository.delete(insertedMissionSafetyCheck).block();
            insertedMissionSafetyCheck = null;
        }
        deleteEntities(template);
    }

    @Test
    void createMissionSafetyCheck() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MissionSafetyCheck
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);
        var returnedMissionSafetyCheckDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MissionSafetyCheckDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MissionSafetyCheck in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMissionSafetyCheck = missionSafetyCheckMapper.toEntity(returnedMissionSafetyCheckDTO);
        assertMissionSafetyCheckUpdatableFieldsEquals(
            returnedMissionSafetyCheck,
            getPersistedMissionSafetyCheck(returnedMissionSafetyCheck)
        );

        insertedMissionSafetyCheck = returnedMissionSafetyCheck;
    }

    @Test
    void createMissionSafetyCheckWithExistingId() throws Exception {
        // Create the MissionSafetyCheck with an existing ID
        missionSafetyCheck.setId(1L);
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionSafetyCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCheckCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        missionSafetyCheck.setCheckCode(null);

        // Create the MissionSafetyCheck, which fails.
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkResultIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        missionSafetyCheck.setResult(null);

        // Create the MissionSafetyCheck, which fails.
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCheckedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        missionSafetyCheck.setCheckedAt(null);

        // Create the MissionSafetyCheck, which fails.
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMissionSafetyChecksAsStream() {
        // Initialize the database
        missionSafetyCheckRepository.save(missionSafetyCheck).block();

        List<MissionSafetyCheck> missionSafetyCheckList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(MissionSafetyCheckDTO.class)
            .getResponseBody()
            .map(missionSafetyCheckMapper::toEntity)
            .filter(missionSafetyCheck::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(missionSafetyCheckList).isNotNull();
        assertThat(missionSafetyCheckList).hasSize(1);
        MissionSafetyCheck testMissionSafetyCheck = missionSafetyCheckList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertMissionSafetyCheckAllPropertiesEquals(missionSafetyCheck, testMissionSafetyCheck);
        assertMissionSafetyCheckUpdatableFieldsEquals(missionSafetyCheck, testMissionSafetyCheck);
    }

    @Test
    void getAllMissionSafetyChecks() {
        // Initialize the database
        insertedMissionSafetyCheck = missionSafetyCheckRepository.save(missionSafetyCheck).block();

        // Get all the missionSafetyCheckList
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
            .value(hasItem(missionSafetyCheck.getId().intValue()))
            .jsonPath("$.[*].checkCode")
            .value(hasItem(DEFAULT_CHECK_CODE))
            .jsonPath("$.[*].result")
            .value(hasItem(DEFAULT_RESULT.toString()))
            .jsonPath("$.[*].checkedAt")
            .value(hasItem(DEFAULT_CHECKED_AT.toString()))
            .jsonPath("$.[*].evidenceReference")
            .value(hasItem(DEFAULT_EVIDENCE_REFERENCE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMissionSafetyChecksWithEagerRelationshipsIsEnabled() {
        when(missionSafetyCheckServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(missionSafetyCheckServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMissionSafetyChecksWithEagerRelationshipsIsNotEnabled() {
        when(missionSafetyCheckServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(missionSafetyCheckRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMissionSafetyCheck() {
        // Initialize the database
        insertedMissionSafetyCheck = missionSafetyCheckRepository.save(missionSafetyCheck).block();

        // Get the missionSafetyCheck
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, missionSafetyCheck.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(missionSafetyCheck.getId().intValue()))
            .jsonPath("$.checkCode")
            .value(is(DEFAULT_CHECK_CODE))
            .jsonPath("$.result")
            .value(is(DEFAULT_RESULT.toString()))
            .jsonPath("$.checkedAt")
            .value(is(DEFAULT_CHECKED_AT.toString()))
            .jsonPath("$.evidenceReference")
            .value(is(DEFAULT_EVIDENCE_REFERENCE));
    }

    @Test
    void getNonExistingMissionSafetyCheck() {
        // Get the missionSafetyCheck
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMissionSafetyCheck() throws Exception {
        // Initialize the database
        insertedMissionSafetyCheck = missionSafetyCheckRepository.save(missionSafetyCheck).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionSafetyCheck
        MissionSafetyCheck updatedMissionSafetyCheck = missionSafetyCheckRepository.findById(missionSafetyCheck.getId()).block();
        updatedMissionSafetyCheck
            .checkCode(UPDATED_CHECK_CODE)
            .result(UPDATED_RESULT)
            .checkedAt(UPDATED_CHECKED_AT)
            .evidenceReference(UPDATED_EVIDENCE_REFERENCE);
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(updatedMissionSafetyCheck);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, missionSafetyCheckDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MissionSafetyCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMissionSafetyCheckToMatchAllProperties(updatedMissionSafetyCheck);
    }

    @Test
    void putNonExistingMissionSafetyCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionSafetyCheck.setId(longCount.incrementAndGet());

        // Create the MissionSafetyCheck
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, missionSafetyCheckDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionSafetyCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMissionSafetyCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionSafetyCheck.setId(longCount.incrementAndGet());

        // Create the MissionSafetyCheck
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionSafetyCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMissionSafetyCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionSafetyCheck.setId(longCount.incrementAndGet());

        // Create the MissionSafetyCheck
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MissionSafetyCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMissionSafetyCheckWithPatch() throws Exception {
        // Initialize the database
        insertedMissionSafetyCheck = missionSafetyCheckRepository.save(missionSafetyCheck).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionSafetyCheck using partial update
        MissionSafetyCheck partialUpdatedMissionSafetyCheck = new MissionSafetyCheck();
        partialUpdatedMissionSafetyCheck.setId(missionSafetyCheck.getId());

        partialUpdatedMissionSafetyCheck
            .checkCode(UPDATED_CHECK_CODE)
            .checkedAt(UPDATED_CHECKED_AT)
            .evidenceReference(UPDATED_EVIDENCE_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMissionSafetyCheck.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMissionSafetyCheck))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MissionSafetyCheck in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionSafetyCheckUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMissionSafetyCheck, missionSafetyCheck),
            getPersistedMissionSafetyCheck(missionSafetyCheck)
        );
    }

    @Test
    void fullUpdateMissionSafetyCheckWithPatch() throws Exception {
        // Initialize the database
        insertedMissionSafetyCheck = missionSafetyCheckRepository.save(missionSafetyCheck).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the missionSafetyCheck using partial update
        MissionSafetyCheck partialUpdatedMissionSafetyCheck = new MissionSafetyCheck();
        partialUpdatedMissionSafetyCheck.setId(missionSafetyCheck.getId());

        partialUpdatedMissionSafetyCheck
            .checkCode(UPDATED_CHECK_CODE)
            .result(UPDATED_RESULT)
            .checkedAt(UPDATED_CHECKED_AT)
            .evidenceReference(UPDATED_EVIDENCE_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMissionSafetyCheck.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMissionSafetyCheck))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MissionSafetyCheck in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMissionSafetyCheckUpdatableFieldsEquals(
            partialUpdatedMissionSafetyCheck,
            getPersistedMissionSafetyCheck(partialUpdatedMissionSafetyCheck)
        );
    }

    @Test
    void patchNonExistingMissionSafetyCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionSafetyCheck.setId(longCount.incrementAndGet());

        // Create the MissionSafetyCheck
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, missionSafetyCheckDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionSafetyCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMissionSafetyCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionSafetyCheck.setId(longCount.incrementAndGet());

        // Create the MissionSafetyCheck
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MissionSafetyCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMissionSafetyCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        missionSafetyCheck.setId(longCount.incrementAndGet());

        // Create the MissionSafetyCheck
        MissionSafetyCheckDTO missionSafetyCheckDTO = missionSafetyCheckMapper.toDto(missionSafetyCheck);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(missionSafetyCheckDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MissionSafetyCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMissionSafetyCheck() {
        // Initialize the database
        insertedMissionSafetyCheck = missionSafetyCheckRepository.save(missionSafetyCheck).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the missionSafetyCheck
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, missionSafetyCheck.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return missionSafetyCheckRepository.count().block();
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

    protected MissionSafetyCheck getPersistedMissionSafetyCheck(MissionSafetyCheck missionSafetyCheck) {
        return missionSafetyCheckRepository.findById(missionSafetyCheck.getId()).block();
    }

    protected void assertPersistedMissionSafetyCheckToMatchAllProperties(MissionSafetyCheck expectedMissionSafetyCheck) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMissionSafetyCheckAllPropertiesEquals(expectedMissionSafetyCheck, getPersistedMissionSafetyCheck(expectedMissionSafetyCheck));
        assertMissionSafetyCheckUpdatableFieldsEquals(
            expectedMissionSafetyCheck,
            getPersistedMissionSafetyCheck(expectedMissionSafetyCheck)
        );
    }

    protected void assertPersistedMissionSafetyCheckToMatchUpdatableProperties(MissionSafetyCheck expectedMissionSafetyCheck) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMissionSafetyCheckAllUpdatablePropertiesEquals(expectedMissionSafetyCheck, getPersistedMissionSafetyCheck(expectedMissionSafetyCheck));
        assertMissionSafetyCheckUpdatableFieldsEquals(
            expectedMissionSafetyCheck,
            getPersistedMissionSafetyCheck(expectedMissionSafetyCheck)
        );
    }
}
