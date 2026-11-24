package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.DroneMissionTargetAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.DroneMissionTarget;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.MissionTargetKind;
import ar.com.heliguard.app.repository.DroneMissionTargetRepository;
import ar.com.heliguard.app.service.DroneMissionTargetService;
import ar.com.heliguard.app.service.dto.DroneMissionTargetDTO;
import ar.com.heliguard.app.service.mapper.DroneMissionTargetMapper;
import java.time.Duration;
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
 * Integration tests for the {@link DroneMissionTargetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DroneMissionTargetResourceIT {

    private static final String DEFAULT_TARGET_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_CODE = "BBBBBBBBBB";

    private static final MissionTargetKind DEFAULT_KIND = MissionTargetKind.SECTOR;
    private static final MissionTargetKind UPDATED_KIND = MissionTargetKind.STRING;

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/drone-mission-targets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DroneMissionTargetRepository droneMissionTargetRepository;

    @Mock
    private DroneMissionTargetRepository droneMissionTargetRepositoryMock;

    @Autowired
    private DroneMissionTargetMapper droneMissionTargetMapper;

    @Mock
    private DroneMissionTargetService droneMissionTargetServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private DroneMissionTarget droneMissionTarget;

    private DroneMissionTarget insertedDroneMissionTarget;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroneMissionTarget createEntity(R2dbcEntityTemplate template) {
        DroneMissionTarget droneMissionTarget = new DroneMissionTarget()
            .targetCode(DEFAULT_TARGET_CODE)
            .kind(DEFAULT_KIND)
            .priority(DEFAULT_PRIORITY)
            .notes(DEFAULT_NOTES);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        droneMissionTarget.setOrganization(organization);
        // Add required entity
        DroneMission droneMission;
        droneMission = template.insert(DroneMissionResourceIT.createEntity(template)).block();
        droneMissionTarget.setMission(droneMission);
        return droneMissionTarget;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroneMissionTarget createUpdatedEntity(R2dbcEntityTemplate template) {
        DroneMissionTarget updatedDroneMissionTarget = new DroneMissionTarget()
            .targetCode(UPDATED_TARGET_CODE)
            .kind(UPDATED_KIND)
            .priority(UPDATED_PRIORITY)
            .notes(UPDATED_NOTES);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedDroneMissionTarget.setOrganization(organization);
        // Add required entity
        DroneMission droneMission;
        droneMission = template.insert(DroneMissionResourceIT.createUpdatedEntity(template)).block();
        updatedDroneMissionTarget.setMission(droneMission);
        return updatedDroneMissionTarget;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(DroneMissionTarget.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        DroneMissionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        droneMissionTarget = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedDroneMissionTarget != null) {
            droneMissionTargetRepository.delete(insertedDroneMissionTarget).block();
            insertedDroneMissionTarget = null;
        }
        deleteEntities(template);
    }

    @Test
    void createDroneMissionTarget() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DroneMissionTarget
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);
        var returnedDroneMissionTargetDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DroneMissionTargetDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the DroneMissionTarget in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDroneMissionTarget = droneMissionTargetMapper.toEntity(returnedDroneMissionTargetDTO);
        assertDroneMissionTargetUpdatableFieldsEquals(
            returnedDroneMissionTarget,
            getPersistedDroneMissionTarget(returnedDroneMissionTarget)
        );

        insertedDroneMissionTarget = returnedDroneMissionTarget;
    }

    @Test
    void createDroneMissionTargetWithExistingId() throws Exception {
        // Create the DroneMissionTarget with an existing ID
        droneMissionTarget.setId(1L);
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTargetCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneMissionTarget.setTargetCode(null);

        // Create the DroneMissionTarget, which fails.
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneMissionTarget.setKind(null);

        // Create the DroneMissionTarget, which fails.
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDroneMissionTargetsAsStream() {
        // Initialize the database
        droneMissionTargetRepository.save(droneMissionTarget).block();

        List<DroneMissionTarget> droneMissionTargetList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(DroneMissionTargetDTO.class)
            .getResponseBody()
            .map(droneMissionTargetMapper::toEntity)
            .filter(droneMissionTarget::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(droneMissionTargetList).isNotNull();
        assertThat(droneMissionTargetList).hasSize(1);
        DroneMissionTarget testDroneMissionTarget = droneMissionTargetList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertDroneMissionTargetAllPropertiesEquals(droneMissionTarget, testDroneMissionTarget);
        assertDroneMissionTargetUpdatableFieldsEquals(droneMissionTarget, testDroneMissionTarget);
    }

    @Test
    void getAllDroneMissionTargets() {
        // Initialize the database
        insertedDroneMissionTarget = droneMissionTargetRepository.save(droneMissionTarget).block();

        // Get all the droneMissionTargetList
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
            .value(hasItem(droneMissionTarget.getId().intValue()))
            .jsonPath("$.[*].targetCode")
            .value(hasItem(DEFAULT_TARGET_CODE))
            .jsonPath("$.[*].kind")
            .value(hasItem(DEFAULT_KIND.toString()))
            .jsonPath("$.[*].priority")
            .value(hasItem(DEFAULT_PRIORITY))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDroneMissionTargetsWithEagerRelationshipsIsEnabled() {
        when(droneMissionTargetServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(droneMissionTargetServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDroneMissionTargetsWithEagerRelationshipsIsNotEnabled() {
        when(droneMissionTargetServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(droneMissionTargetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDroneMissionTarget() {
        // Initialize the database
        insertedDroneMissionTarget = droneMissionTargetRepository.save(droneMissionTarget).block();

        // Get the droneMissionTarget
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, droneMissionTarget.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(droneMissionTarget.getId().intValue()))
            .jsonPath("$.targetCode")
            .value(is(DEFAULT_TARGET_CODE))
            .jsonPath("$.kind")
            .value(is(DEFAULT_KIND.toString()))
            .jsonPath("$.priority")
            .value(is(DEFAULT_PRIORITY))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES));
    }

    @Test
    void getNonExistingDroneMissionTarget() {
        // Get the droneMissionTarget
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDroneMissionTarget() throws Exception {
        // Initialize the database
        insertedDroneMissionTarget = droneMissionTargetRepository.save(droneMissionTarget).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMissionTarget
        DroneMissionTarget updatedDroneMissionTarget = droneMissionTargetRepository.findById(droneMissionTarget.getId()).block();
        updatedDroneMissionTarget.targetCode(UPDATED_TARGET_CODE).kind(UPDATED_KIND).priority(UPDATED_PRIORITY).notes(UPDATED_NOTES);
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(updatedDroneMissionTarget);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, droneMissionTargetDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMissionTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDroneMissionTargetToMatchAllProperties(updatedDroneMissionTarget);
    }

    @Test
    void putNonExistingDroneMissionTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionTarget.setId(longCount.incrementAndGet());

        // Create the DroneMissionTarget
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, droneMissionTargetDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDroneMissionTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionTarget.setId(longCount.incrementAndGet());

        // Create the DroneMissionTarget
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDroneMissionTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionTarget.setId(longCount.incrementAndGet());

        // Create the DroneMissionTarget
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DroneMissionTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDroneMissionTargetWithPatch() throws Exception {
        // Initialize the database
        insertedDroneMissionTarget = droneMissionTargetRepository.save(droneMissionTarget).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMissionTarget using partial update
        DroneMissionTarget partialUpdatedDroneMissionTarget = new DroneMissionTarget();
        partialUpdatedDroneMissionTarget.setId(droneMissionTarget.getId());

        partialUpdatedDroneMissionTarget.targetCode(UPDATED_TARGET_CODE).kind(UPDATED_KIND).priority(UPDATED_PRIORITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDroneMissionTarget.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDroneMissionTarget))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMissionTarget in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDroneMissionTargetUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDroneMissionTarget, droneMissionTarget),
            getPersistedDroneMissionTarget(droneMissionTarget)
        );
    }

    @Test
    void fullUpdateDroneMissionTargetWithPatch() throws Exception {
        // Initialize the database
        insertedDroneMissionTarget = droneMissionTargetRepository.save(droneMissionTarget).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMissionTarget using partial update
        DroneMissionTarget partialUpdatedDroneMissionTarget = new DroneMissionTarget();
        partialUpdatedDroneMissionTarget.setId(droneMissionTarget.getId());

        partialUpdatedDroneMissionTarget.targetCode(UPDATED_TARGET_CODE).kind(UPDATED_KIND).priority(UPDATED_PRIORITY).notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDroneMissionTarget.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDroneMissionTarget))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMissionTarget in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDroneMissionTargetUpdatableFieldsEquals(
            partialUpdatedDroneMissionTarget,
            getPersistedDroneMissionTarget(partialUpdatedDroneMissionTarget)
        );
    }

    @Test
    void patchNonExistingDroneMissionTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionTarget.setId(longCount.incrementAndGet());

        // Create the DroneMissionTarget
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, droneMissionTargetDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDroneMissionTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionTarget.setId(longCount.incrementAndGet());

        // Create the DroneMissionTarget
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDroneMissionTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionTarget.setId(longCount.incrementAndGet());

        // Create the DroneMissionTarget
        DroneMissionTargetDTO droneMissionTargetDTO = droneMissionTargetMapper.toDto(droneMissionTarget);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionTargetDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DroneMissionTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDroneMissionTarget() {
        // Initialize the database
        insertedDroneMissionTarget = droneMissionTargetRepository.save(droneMissionTarget).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the droneMissionTarget
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, droneMissionTarget.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return droneMissionTargetRepository.count().block();
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

    protected DroneMissionTarget getPersistedDroneMissionTarget(DroneMissionTarget droneMissionTarget) {
        return droneMissionTargetRepository.findById(droneMissionTarget.getId()).block();
    }

    protected void assertPersistedDroneMissionTargetToMatchAllProperties(DroneMissionTarget expectedDroneMissionTarget) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDroneMissionTargetAllPropertiesEquals(expectedDroneMissionTarget, getPersistedDroneMissionTarget(expectedDroneMissionTarget));
        assertDroneMissionTargetUpdatableFieldsEquals(
            expectedDroneMissionTarget,
            getPersistedDroneMissionTarget(expectedDroneMissionTarget)
        );
    }

    protected void assertPersistedDroneMissionTargetToMatchUpdatableProperties(DroneMissionTarget expectedDroneMissionTarget) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDroneMissionTargetAllUpdatablePropertiesEquals(expectedDroneMissionTarget, getPersistedDroneMissionTarget(expectedDroneMissionTarget));
        assertDroneMissionTargetUpdatableFieldsEquals(
            expectedDroneMissionTarget,
            getPersistedDroneMissionTarget(expectedDroneMissionTarget)
        );
    }
}
