package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.WorkOrderApprovalAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.WorkOrderApproval;
import ar.com.heliguard.app.domain.enumeration.WorkApprovalStatus;
import ar.com.heliguard.app.repository.WorkOrderApprovalRepository;
import ar.com.heliguard.app.service.WorkOrderApprovalService;
import ar.com.heliguard.app.service.dto.WorkOrderApprovalDTO;
import ar.com.heliguard.app.service.mapper.WorkOrderApprovalMapper;
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
 * Integration tests for the {@link WorkOrderApprovalResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class WorkOrderApprovalResourceIT {

    private static final WorkApprovalStatus DEFAULT_STATUS = WorkApprovalStatus.REQUESTED;
    private static final WorkApprovalStatus UPDATED_STATUS = WorkApprovalStatus.APPROVED;

    private static final Instant DEFAULT_REQUESTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUESTED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_DECIDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DECIDED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_CONSENT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_CONSENT_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_DECISION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_DECISION_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/work-order-approvals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkOrderApprovalRepository workOrderApprovalRepository;

    @Mock
    private WorkOrderApprovalRepository workOrderApprovalRepositoryMock;

    @Autowired
    private WorkOrderApprovalMapper workOrderApprovalMapper;

    @Mock
    private WorkOrderApprovalService workOrderApprovalServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private WorkOrderApproval workOrderApproval;

    private WorkOrderApproval insertedWorkOrderApproval;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkOrderApproval createEntity(R2dbcEntityTemplate template) {
        WorkOrderApproval workOrderApproval = new WorkOrderApproval()
            .status(DEFAULT_STATUS)
            .requestedAt(DEFAULT_REQUESTED_AT)
            .decidedAt(DEFAULT_DECIDED_AT)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .consentReference(DEFAULT_CONSENT_REFERENCE)
            .decisionNote(DEFAULT_DECISION_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        workOrderApproval.setOrganization(organization);
        // Add required entity
        WorkOrder workOrder;
        workOrder = template.insert(WorkOrderResourceIT.createEntity(template)).block();
        workOrderApproval.setWorkOrder(workOrder);
        return workOrderApproval;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkOrderApproval createUpdatedEntity(R2dbcEntityTemplate template) {
        WorkOrderApproval updatedWorkOrderApproval = new WorkOrderApproval()
            .status(UPDATED_STATUS)
            .requestedAt(UPDATED_REQUESTED_AT)
            .decidedAt(UPDATED_DECIDED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .consentReference(UPDATED_CONSENT_REFERENCE)
            .decisionNote(UPDATED_DECISION_NOTE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedWorkOrderApproval.setOrganization(organization);
        // Add required entity
        WorkOrder workOrder;
        workOrder = template.insert(WorkOrderResourceIT.createUpdatedEntity(template)).block();
        updatedWorkOrderApproval.setWorkOrder(workOrder);
        return updatedWorkOrderApproval;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(WorkOrderApproval.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        WorkOrderResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        workOrderApproval = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedWorkOrderApproval != null) {
            workOrderApprovalRepository.delete(insertedWorkOrderApproval).block();
            insertedWorkOrderApproval = null;
        }
        deleteEntities(template);
    }

    @Test
    void createWorkOrderApproval() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WorkOrderApproval
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);
        var returnedWorkOrderApprovalDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(WorkOrderApprovalDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the WorkOrderApproval in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWorkOrderApproval = workOrderApprovalMapper.toEntity(returnedWorkOrderApprovalDTO);
        assertWorkOrderApprovalUpdatableFieldsEquals(returnedWorkOrderApproval, getPersistedWorkOrderApproval(returnedWorkOrderApproval));

        insertedWorkOrderApproval = returnedWorkOrderApproval;
    }

    @Test
    void createWorkOrderApprovalWithExistingId() throws Exception {
        // Create the WorkOrderApproval with an existing ID
        workOrderApproval.setId(1L);
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderApproval in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrderApproval.setStatus(null);

        // Create the WorkOrderApproval, which fails.
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRequestedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrderApproval.setRequestedAt(null);

        // Create the WorkOrderApproval, which fails.
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllWorkOrderApprovalsAsStream() {
        // Initialize the database
        workOrderApprovalRepository.save(workOrderApproval).block();

        List<WorkOrderApproval> workOrderApprovalList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(WorkOrderApprovalDTO.class)
            .getResponseBody()
            .map(workOrderApprovalMapper::toEntity)
            .filter(workOrderApproval::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(workOrderApprovalList).isNotNull();
        assertThat(workOrderApprovalList).hasSize(1);
        WorkOrderApproval testWorkOrderApproval = workOrderApprovalList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderApprovalAllPropertiesEquals(workOrderApproval, testWorkOrderApproval);
        assertWorkOrderApprovalUpdatableFieldsEquals(workOrderApproval, testWorkOrderApproval);
    }

    @Test
    void getAllWorkOrderApprovals() {
        // Initialize the database
        insertedWorkOrderApproval = workOrderApprovalRepository.save(workOrderApproval).block();

        // Get all the workOrderApprovalList
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
            .value(hasItem(workOrderApproval.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].requestedAt")
            .value(hasItem(DEFAULT_REQUESTED_AT.toString()))
            .jsonPath("$.[*].decidedAt")
            .value(hasItem(DEFAULT_DECIDED_AT.toString()))
            .jsonPath("$.[*].expiresAt")
            .value(hasItem(DEFAULT_EXPIRES_AT.toString()))
            .jsonPath("$.[*].consentReference")
            .value(hasItem(DEFAULT_CONSENT_REFERENCE))
            .jsonPath("$.[*].decisionNote")
            .value(hasItem(DEFAULT_DECISION_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkOrderApprovalsWithEagerRelationshipsIsEnabled() {
        when(workOrderApprovalServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(workOrderApprovalServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkOrderApprovalsWithEagerRelationshipsIsNotEnabled() {
        when(workOrderApprovalServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(workOrderApprovalRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getWorkOrderApproval() {
        // Initialize the database
        insertedWorkOrderApproval = workOrderApprovalRepository.save(workOrderApproval).block();

        // Get the workOrderApproval
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, workOrderApproval.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(workOrderApproval.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.requestedAt")
            .value(is(DEFAULT_REQUESTED_AT.toString()))
            .jsonPath("$.decidedAt")
            .value(is(DEFAULT_DECIDED_AT.toString()))
            .jsonPath("$.expiresAt")
            .value(is(DEFAULT_EXPIRES_AT.toString()))
            .jsonPath("$.consentReference")
            .value(is(DEFAULT_CONSENT_REFERENCE))
            .jsonPath("$.decisionNote")
            .value(is(DEFAULT_DECISION_NOTE));
    }

    @Test
    void getNonExistingWorkOrderApproval() {
        // Get the workOrderApproval
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingWorkOrderApproval() throws Exception {
        // Initialize the database
        insertedWorkOrderApproval = workOrderApprovalRepository.save(workOrderApproval).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderApproval
        WorkOrderApproval updatedWorkOrderApproval = workOrderApprovalRepository.findById(workOrderApproval.getId()).block();
        updatedWorkOrderApproval
            .status(UPDATED_STATUS)
            .requestedAt(UPDATED_REQUESTED_AT)
            .decidedAt(UPDATED_DECIDED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .consentReference(UPDATED_CONSENT_REFERENCE)
            .decisionNote(UPDATED_DECISION_NOTE);
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(updatedWorkOrderApproval);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, workOrderApprovalDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderApproval in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWorkOrderApprovalToMatchAllProperties(updatedWorkOrderApproval);
    }

    @Test
    void putNonExistingWorkOrderApproval() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderApproval.setId(longCount.incrementAndGet());

        // Create the WorkOrderApproval
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, workOrderApprovalDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderApproval in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchWorkOrderApproval() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderApproval.setId(longCount.incrementAndGet());

        // Create the WorkOrderApproval
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderApproval in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamWorkOrderApproval() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderApproval.setId(longCount.incrementAndGet());

        // Create the WorkOrderApproval
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WorkOrderApproval in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateWorkOrderApprovalWithPatch() throws Exception {
        // Initialize the database
        insertedWorkOrderApproval = workOrderApprovalRepository.save(workOrderApproval).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderApproval using partial update
        WorkOrderApproval partialUpdatedWorkOrderApproval = new WorkOrderApproval();
        partialUpdatedWorkOrderApproval.setId(workOrderApproval.getId());

        partialUpdatedWorkOrderApproval.expiresAt(UPDATED_EXPIRES_AT).consentReference(UPDATED_CONSENT_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWorkOrderApproval.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWorkOrderApproval))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderApproval in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkOrderApprovalUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWorkOrderApproval, workOrderApproval),
            getPersistedWorkOrderApproval(workOrderApproval)
        );
    }

    @Test
    void fullUpdateWorkOrderApprovalWithPatch() throws Exception {
        // Initialize the database
        insertedWorkOrderApproval = workOrderApprovalRepository.save(workOrderApproval).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderApproval using partial update
        WorkOrderApproval partialUpdatedWorkOrderApproval = new WorkOrderApproval();
        partialUpdatedWorkOrderApproval.setId(workOrderApproval.getId());

        partialUpdatedWorkOrderApproval
            .status(UPDATED_STATUS)
            .requestedAt(UPDATED_REQUESTED_AT)
            .decidedAt(UPDATED_DECIDED_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .consentReference(UPDATED_CONSENT_REFERENCE)
            .decisionNote(UPDATED_DECISION_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWorkOrderApproval.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWorkOrderApproval))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderApproval in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkOrderApprovalUpdatableFieldsEquals(
            partialUpdatedWorkOrderApproval,
            getPersistedWorkOrderApproval(partialUpdatedWorkOrderApproval)
        );
    }

    @Test
    void patchNonExistingWorkOrderApproval() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderApproval.setId(longCount.incrementAndGet());

        // Create the WorkOrderApproval
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, workOrderApprovalDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderApproval in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchWorkOrderApproval() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderApproval.setId(longCount.incrementAndGet());

        // Create the WorkOrderApproval
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderApproval in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamWorkOrderApproval() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderApproval.setId(longCount.incrementAndGet());

        // Create the WorkOrderApproval
        WorkOrderApprovalDTO workOrderApprovalDTO = workOrderApprovalMapper.toDto(workOrderApproval);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderApprovalDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WorkOrderApproval in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteWorkOrderApproval() {
        // Initialize the database
        insertedWorkOrderApproval = workOrderApprovalRepository.save(workOrderApproval).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the workOrderApproval
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, workOrderApproval.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return workOrderApprovalRepository.count().block();
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

    protected WorkOrderApproval getPersistedWorkOrderApproval(WorkOrderApproval workOrderApproval) {
        return workOrderApprovalRepository.findById(workOrderApproval.getId()).block();
    }

    protected void assertPersistedWorkOrderApprovalToMatchAllProperties(WorkOrderApproval expectedWorkOrderApproval) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderApprovalAllPropertiesEquals(expectedWorkOrderApproval, getPersistedWorkOrderApproval(expectedWorkOrderApproval));
        assertWorkOrderApprovalUpdatableFieldsEquals(expectedWorkOrderApproval, getPersistedWorkOrderApproval(expectedWorkOrderApproval));
    }

    protected void assertPersistedWorkOrderApprovalToMatchUpdatableProperties(WorkOrderApproval expectedWorkOrderApproval) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderApprovalAllUpdatablePropertiesEquals(expectedWorkOrderApproval, getPersistedWorkOrderApproval(expectedWorkOrderApproval));
        assertWorkOrderApprovalUpdatableFieldsEquals(expectedWorkOrderApproval, getPersistedWorkOrderApproval(expectedWorkOrderApproval));
    }
}
