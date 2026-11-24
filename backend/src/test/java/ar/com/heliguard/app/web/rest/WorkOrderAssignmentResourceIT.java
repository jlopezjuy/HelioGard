package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.WorkOrderAssignmentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.WorkOrderAssignment;
import ar.com.heliguard.app.domain.enumeration.AssignmentStatus;
import ar.com.heliguard.app.repository.WorkOrderAssignmentRepository;
import ar.com.heliguard.app.service.WorkOrderAssignmentService;
import ar.com.heliguard.app.service.dto.WorkOrderAssignmentDTO;
import ar.com.heliguard.app.service.mapper.WorkOrderAssignmentMapper;
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
 * Integration tests for the {@link WorkOrderAssignmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class WorkOrderAssignmentResourceIT {

    private static final AssignmentStatus DEFAULT_STATUS = AssignmentStatus.OFFERED;
    private static final AssignmentStatus UPDATED_STATUS = AssignmentStatus.ACCEPTED;

    private static final Instant DEFAULT_ASSIGNED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_ACCEPTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACCEPTED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_RELEASED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RELEASED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/work-order-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkOrderAssignmentRepository workOrderAssignmentRepository;

    @Mock
    private WorkOrderAssignmentRepository workOrderAssignmentRepositoryMock;

    @Autowired
    private WorkOrderAssignmentMapper workOrderAssignmentMapper;

    @Mock
    private WorkOrderAssignmentService workOrderAssignmentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private WorkOrderAssignment workOrderAssignment;

    private WorkOrderAssignment insertedWorkOrderAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkOrderAssignment createEntity(R2dbcEntityTemplate template) {
        WorkOrderAssignment workOrderAssignment = new WorkOrderAssignment()
            .status(DEFAULT_STATUS)
            .assignedAt(DEFAULT_ASSIGNED_AT)
            .acceptedAt(DEFAULT_ACCEPTED_AT)
            .releasedAt(DEFAULT_RELEASED_AT)
            .notes(DEFAULT_NOTES);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        workOrderAssignment.setOrganization(organization);
        // Add required entity
        WorkOrder workOrder;
        workOrder = template.insert(WorkOrderResourceIT.createEntity(template)).block();
        workOrderAssignment.setWorkOrder(workOrder);
        // Add required entity
        workOrderAssignment.setProviderOrganization(organization);
        // Add required entity
        OrganizationMembership organizationMembership;
        organizationMembership = template.insert(OrganizationMembershipResourceIT.createEntity(template)).block();
        workOrderAssignment.setAssigneeMembership(organizationMembership);
        return workOrderAssignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkOrderAssignment createUpdatedEntity(R2dbcEntityTemplate template) {
        WorkOrderAssignment updatedWorkOrderAssignment = new WorkOrderAssignment()
            .status(UPDATED_STATUS)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .acceptedAt(UPDATED_ACCEPTED_AT)
            .releasedAt(UPDATED_RELEASED_AT)
            .notes(UPDATED_NOTES);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedWorkOrderAssignment.setOrganization(organization);
        // Add required entity
        WorkOrder workOrder;
        workOrder = template.insert(WorkOrderResourceIT.createUpdatedEntity(template)).block();
        updatedWorkOrderAssignment.setWorkOrder(workOrder);
        // Add required entity
        updatedWorkOrderAssignment.setProviderOrganization(organization);
        // Add required entity
        OrganizationMembership organizationMembership;
        organizationMembership = template.insert(OrganizationMembershipResourceIT.createUpdatedEntity(template)).block();
        updatedWorkOrderAssignment.setAssigneeMembership(organizationMembership);
        return updatedWorkOrderAssignment;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(WorkOrderAssignment.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        WorkOrderResourceIT.deleteEntities(template);
        OrganizationMembershipResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        workOrderAssignment = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedWorkOrderAssignment != null) {
            workOrderAssignmentRepository.delete(insertedWorkOrderAssignment).block();
            insertedWorkOrderAssignment = null;
        }
        deleteEntities(template);
    }

    @Test
    void createWorkOrderAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WorkOrderAssignment
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);
        var returnedWorkOrderAssignmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(WorkOrderAssignmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the WorkOrderAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWorkOrderAssignment = workOrderAssignmentMapper.toEntity(returnedWorkOrderAssignmentDTO);
        assertWorkOrderAssignmentUpdatableFieldsEquals(
            returnedWorkOrderAssignment,
            getPersistedWorkOrderAssignment(returnedWorkOrderAssignment)
        );

        insertedWorkOrderAssignment = returnedWorkOrderAssignment;
    }

    @Test
    void createWorkOrderAssignmentWithExistingId() throws Exception {
        // Create the WorkOrderAssignment with an existing ID
        workOrderAssignment.setId(1L);
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrderAssignment.setStatus(null);

        // Create the WorkOrderAssignment, which fails.
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAssignedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrderAssignment.setAssignedAt(null);

        // Create the WorkOrderAssignment, which fails.
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllWorkOrderAssignmentsAsStream() {
        // Initialize the database
        workOrderAssignmentRepository.save(workOrderAssignment).block();

        List<WorkOrderAssignment> workOrderAssignmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(WorkOrderAssignmentDTO.class)
            .getResponseBody()
            .map(workOrderAssignmentMapper::toEntity)
            .filter(workOrderAssignment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(workOrderAssignmentList).isNotNull();
        assertThat(workOrderAssignmentList).hasSize(1);
        WorkOrderAssignment testWorkOrderAssignment = workOrderAssignmentList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderAssignmentAllPropertiesEquals(workOrderAssignment, testWorkOrderAssignment);
        assertWorkOrderAssignmentUpdatableFieldsEquals(workOrderAssignment, testWorkOrderAssignment);
    }

    @Test
    void getAllWorkOrderAssignments() {
        // Initialize the database
        insertedWorkOrderAssignment = workOrderAssignmentRepository.save(workOrderAssignment).block();

        // Get all the workOrderAssignmentList
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
            .value(hasItem(workOrderAssignment.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].assignedAt")
            .value(hasItem(DEFAULT_ASSIGNED_AT.toString()))
            .jsonPath("$.[*].acceptedAt")
            .value(hasItem(DEFAULT_ACCEPTED_AT.toString()))
            .jsonPath("$.[*].releasedAt")
            .value(hasItem(DEFAULT_RELEASED_AT.toString()))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkOrderAssignmentsWithEagerRelationshipsIsEnabled() {
        when(workOrderAssignmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(workOrderAssignmentServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkOrderAssignmentsWithEagerRelationshipsIsNotEnabled() {
        when(workOrderAssignmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(workOrderAssignmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getWorkOrderAssignment() {
        // Initialize the database
        insertedWorkOrderAssignment = workOrderAssignmentRepository.save(workOrderAssignment).block();

        // Get the workOrderAssignment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, workOrderAssignment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(workOrderAssignment.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.assignedAt")
            .value(is(DEFAULT_ASSIGNED_AT.toString()))
            .jsonPath("$.acceptedAt")
            .value(is(DEFAULT_ACCEPTED_AT.toString()))
            .jsonPath("$.releasedAt")
            .value(is(DEFAULT_RELEASED_AT.toString()))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES));
    }

    @Test
    void getNonExistingWorkOrderAssignment() {
        // Get the workOrderAssignment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingWorkOrderAssignment() throws Exception {
        // Initialize the database
        insertedWorkOrderAssignment = workOrderAssignmentRepository.save(workOrderAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderAssignment
        WorkOrderAssignment updatedWorkOrderAssignment = workOrderAssignmentRepository.findById(workOrderAssignment.getId()).block();
        updatedWorkOrderAssignment
            .status(UPDATED_STATUS)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .acceptedAt(UPDATED_ACCEPTED_AT)
            .releasedAt(UPDATED_RELEASED_AT)
            .notes(UPDATED_NOTES);
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(updatedWorkOrderAssignment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, workOrderAssignmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWorkOrderAssignmentToMatchAllProperties(updatedWorkOrderAssignment);
    }

    @Test
    void putNonExistingWorkOrderAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderAssignment.setId(longCount.incrementAndGet());

        // Create the WorkOrderAssignment
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, workOrderAssignmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchWorkOrderAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderAssignment.setId(longCount.incrementAndGet());

        // Create the WorkOrderAssignment
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamWorkOrderAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderAssignment.setId(longCount.incrementAndGet());

        // Create the WorkOrderAssignment
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WorkOrderAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateWorkOrderAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedWorkOrderAssignment = workOrderAssignmentRepository.save(workOrderAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderAssignment using partial update
        WorkOrderAssignment partialUpdatedWorkOrderAssignment = new WorkOrderAssignment();
        partialUpdatedWorkOrderAssignment.setId(workOrderAssignment.getId());

        partialUpdatedWorkOrderAssignment.status(UPDATED_STATUS).releasedAt(UPDATED_RELEASED_AT).notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWorkOrderAssignment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWorkOrderAssignment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkOrderAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWorkOrderAssignment, workOrderAssignment),
            getPersistedWorkOrderAssignment(workOrderAssignment)
        );
    }

    @Test
    void fullUpdateWorkOrderAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedWorkOrderAssignment = workOrderAssignmentRepository.save(workOrderAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderAssignment using partial update
        WorkOrderAssignment partialUpdatedWorkOrderAssignment = new WorkOrderAssignment();
        partialUpdatedWorkOrderAssignment.setId(workOrderAssignment.getId());

        partialUpdatedWorkOrderAssignment
            .status(UPDATED_STATUS)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .acceptedAt(UPDATED_ACCEPTED_AT)
            .releasedAt(UPDATED_RELEASED_AT)
            .notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWorkOrderAssignment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWorkOrderAssignment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkOrderAssignmentUpdatableFieldsEquals(
            partialUpdatedWorkOrderAssignment,
            getPersistedWorkOrderAssignment(partialUpdatedWorkOrderAssignment)
        );
    }

    @Test
    void patchNonExistingWorkOrderAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderAssignment.setId(longCount.incrementAndGet());

        // Create the WorkOrderAssignment
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, workOrderAssignmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchWorkOrderAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderAssignment.setId(longCount.incrementAndGet());

        // Create the WorkOrderAssignment
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamWorkOrderAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderAssignment.setId(longCount.incrementAndGet());

        // Create the WorkOrderAssignment
        WorkOrderAssignmentDTO workOrderAssignmentDTO = workOrderAssignmentMapper.toDto(workOrderAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderAssignmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WorkOrderAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteWorkOrderAssignment() {
        // Initialize the database
        insertedWorkOrderAssignment = workOrderAssignmentRepository.save(workOrderAssignment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the workOrderAssignment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, workOrderAssignment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return workOrderAssignmentRepository.count().block();
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

    protected WorkOrderAssignment getPersistedWorkOrderAssignment(WorkOrderAssignment workOrderAssignment) {
        return workOrderAssignmentRepository.findById(workOrderAssignment.getId()).block();
    }

    protected void assertPersistedWorkOrderAssignmentToMatchAllProperties(WorkOrderAssignment expectedWorkOrderAssignment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderAssignmentAllPropertiesEquals(expectedWorkOrderAssignment, getPersistedWorkOrderAssignment(expectedWorkOrderAssignment));
        assertWorkOrderAssignmentUpdatableFieldsEquals(
            expectedWorkOrderAssignment,
            getPersistedWorkOrderAssignment(expectedWorkOrderAssignment)
        );
    }

    protected void assertPersistedWorkOrderAssignmentToMatchUpdatableProperties(WorkOrderAssignment expectedWorkOrderAssignment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderAssignmentAllUpdatablePropertiesEquals(expectedWorkOrderAssignment, getPersistedWorkOrderAssignment(expectedWorkOrderAssignment));
        assertWorkOrderAssignmentUpdatableFieldsEquals(
            expectedWorkOrderAssignment,
            getPersistedWorkOrderAssignment(expectedWorkOrderAssignment)
        );
    }
}
