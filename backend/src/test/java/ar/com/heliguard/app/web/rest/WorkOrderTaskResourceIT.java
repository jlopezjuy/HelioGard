package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.WorkOrderTaskAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.WorkOrderTask;
import ar.com.heliguard.app.domain.enumeration.WorkTaskStatus;
import ar.com.heliguard.app.repository.WorkOrderTaskRepository;
import ar.com.heliguard.app.service.WorkOrderTaskService;
import ar.com.heliguard.app.service.dto.WorkOrderTaskDTO;
import ar.com.heliguard.app.service.mapper.WorkOrderTaskMapper;
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
 * Integration tests for the {@link WorkOrderTaskResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class WorkOrderTaskResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final WorkTaskStatus DEFAULT_STATUS = WorkTaskStatus.TODO;
    private static final WorkTaskStatus UPDATED_STATUS = WorkTaskStatus.IN_PROGRESS;

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/work-order-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkOrderTaskRepository workOrderTaskRepository;

    @Mock
    private WorkOrderTaskRepository workOrderTaskRepositoryMock;

    @Autowired
    private WorkOrderTaskMapper workOrderTaskMapper;

    @Mock
    private WorkOrderTaskService workOrderTaskServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private WorkOrderTask workOrderTask;

    private WorkOrderTask insertedWorkOrderTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkOrderTask createEntity(R2dbcEntityTemplate template) {
        WorkOrderTask workOrderTask = new WorkOrderTask()
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .sortOrder(DEFAULT_SORT_ORDER);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        workOrderTask.setOrganization(organization);
        // Add required entity
        WorkOrder workOrder;
        workOrder = template.insert(WorkOrderResourceIT.createEntity(template)).block();
        workOrderTask.setWorkOrder(workOrder);
        return workOrderTask;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkOrderTask createUpdatedEntity(R2dbcEntityTemplate template) {
        WorkOrderTask updatedWorkOrderTask = new WorkOrderTask()
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .sortOrder(UPDATED_SORT_ORDER);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedWorkOrderTask.setOrganization(organization);
        // Add required entity
        WorkOrder workOrder;
        workOrder = template.insert(WorkOrderResourceIT.createUpdatedEntity(template)).block();
        updatedWorkOrderTask.setWorkOrder(workOrder);
        return updatedWorkOrderTask;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(WorkOrderTask.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        WorkOrderResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        workOrderTask = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedWorkOrderTask != null) {
            workOrderTaskRepository.delete(insertedWorkOrderTask).block();
            insertedWorkOrderTask = null;
        }
        deleteEntities(template);
    }

    @Test
    void createWorkOrderTask() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WorkOrderTask
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);
        var returnedWorkOrderTaskDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(WorkOrderTaskDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the WorkOrderTask in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWorkOrderTask = workOrderTaskMapper.toEntity(returnedWorkOrderTaskDTO);
        assertWorkOrderTaskUpdatableFieldsEquals(returnedWorkOrderTask, getPersistedWorkOrderTask(returnedWorkOrderTask));

        insertedWorkOrderTask = returnedWorkOrderTask;
    }

    @Test
    void createWorkOrderTaskWithExistingId() throws Exception {
        // Create the WorkOrderTask with an existing ID
        workOrderTask.setId(1L);
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderTask in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrderTask.setDescription(null);

        // Create the WorkOrderTask, which fails.
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrderTask.setStatus(null);

        // Create the WorkOrderTask, which fails.
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSortOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrderTask.setSortOrder(null);

        // Create the WorkOrderTask, which fails.
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllWorkOrderTasksAsStream() {
        // Initialize the database
        workOrderTaskRepository.save(workOrderTask).block();

        List<WorkOrderTask> workOrderTaskList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(WorkOrderTaskDTO.class)
            .getResponseBody()
            .map(workOrderTaskMapper::toEntity)
            .filter(workOrderTask::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(workOrderTaskList).isNotNull();
        assertThat(workOrderTaskList).hasSize(1);
        WorkOrderTask testWorkOrderTask = workOrderTaskList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderTaskAllPropertiesEquals(workOrderTask, testWorkOrderTask);
        assertWorkOrderTaskUpdatableFieldsEquals(workOrderTask, testWorkOrderTask);
    }

    @Test
    void getAllWorkOrderTasks() {
        // Initialize the database
        insertedWorkOrderTask = workOrderTaskRepository.save(workOrderTask).block();

        // Get all the workOrderTaskList
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
            .value(hasItem(workOrderTask.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].sortOrder")
            .value(hasItem(DEFAULT_SORT_ORDER));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkOrderTasksWithEagerRelationshipsIsEnabled() {
        when(workOrderTaskServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(workOrderTaskServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkOrderTasksWithEagerRelationshipsIsNotEnabled() {
        when(workOrderTaskServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(workOrderTaskRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getWorkOrderTask() {
        // Initialize the database
        insertedWorkOrderTask = workOrderTaskRepository.save(workOrderTask).block();

        // Get the workOrderTask
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, workOrderTask.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(workOrderTask.getId().intValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.sortOrder")
            .value(is(DEFAULT_SORT_ORDER));
    }

    @Test
    void getNonExistingWorkOrderTask() {
        // Get the workOrderTask
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingWorkOrderTask() throws Exception {
        // Initialize the database
        insertedWorkOrderTask = workOrderTaskRepository.save(workOrderTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderTask
        WorkOrderTask updatedWorkOrderTask = workOrderTaskRepository.findById(workOrderTask.getId()).block();
        updatedWorkOrderTask.description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).sortOrder(UPDATED_SORT_ORDER);
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(updatedWorkOrderTask);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, workOrderTaskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWorkOrderTaskToMatchAllProperties(updatedWorkOrderTask);
    }

    @Test
    void putNonExistingWorkOrderTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderTask.setId(longCount.incrementAndGet());

        // Create the WorkOrderTask
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, workOrderTaskDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchWorkOrderTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderTask.setId(longCount.incrementAndGet());

        // Create the WorkOrderTask
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamWorkOrderTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderTask.setId(longCount.incrementAndGet());

        // Create the WorkOrderTask
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WorkOrderTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateWorkOrderTaskWithPatch() throws Exception {
        // Initialize the database
        insertedWorkOrderTask = workOrderTaskRepository.save(workOrderTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderTask using partial update
        WorkOrderTask partialUpdatedWorkOrderTask = new WorkOrderTask();
        partialUpdatedWorkOrderTask.setId(workOrderTask.getId());

        partialUpdatedWorkOrderTask.status(UPDATED_STATUS).sortOrder(UPDATED_SORT_ORDER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWorkOrderTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWorkOrderTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkOrderTaskUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWorkOrderTask, workOrderTask),
            getPersistedWorkOrderTask(workOrderTask)
        );
    }

    @Test
    void fullUpdateWorkOrderTaskWithPatch() throws Exception {
        // Initialize the database
        insertedWorkOrderTask = workOrderTaskRepository.save(workOrderTask).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrderTask using partial update
        WorkOrderTask partialUpdatedWorkOrderTask = new WorkOrderTask();
        partialUpdatedWorkOrderTask.setId(workOrderTask.getId());

        partialUpdatedWorkOrderTask.description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).sortOrder(UPDATED_SORT_ORDER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWorkOrderTask.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWorkOrderTask))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrderTask in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkOrderTaskUpdatableFieldsEquals(partialUpdatedWorkOrderTask, getPersistedWorkOrderTask(partialUpdatedWorkOrderTask));
    }

    @Test
    void patchNonExistingWorkOrderTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderTask.setId(longCount.incrementAndGet());

        // Create the WorkOrderTask
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, workOrderTaskDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchWorkOrderTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderTask.setId(longCount.incrementAndGet());

        // Create the WorkOrderTask
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrderTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamWorkOrderTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrderTask.setId(longCount.incrementAndGet());

        // Create the WorkOrderTask
        WorkOrderTaskDTO workOrderTaskDTO = workOrderTaskMapper.toDto(workOrderTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderTaskDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WorkOrderTask in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteWorkOrderTask() {
        // Initialize the database
        insertedWorkOrderTask = workOrderTaskRepository.save(workOrderTask).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the workOrderTask
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, workOrderTask.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return workOrderTaskRepository.count().block();
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

    protected WorkOrderTask getPersistedWorkOrderTask(WorkOrderTask workOrderTask) {
        return workOrderTaskRepository.findById(workOrderTask.getId()).block();
    }

    protected void assertPersistedWorkOrderTaskToMatchAllProperties(WorkOrderTask expectedWorkOrderTask) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderTaskAllPropertiesEquals(expectedWorkOrderTask, getPersistedWorkOrderTask(expectedWorkOrderTask));
        assertWorkOrderTaskUpdatableFieldsEquals(expectedWorkOrderTask, getPersistedWorkOrderTask(expectedWorkOrderTask));
    }

    protected void assertPersistedWorkOrderTaskToMatchUpdatableProperties(WorkOrderTask expectedWorkOrderTask) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderTaskAllUpdatablePropertiesEquals(expectedWorkOrderTask, getPersistedWorkOrderTask(expectedWorkOrderTask));
        assertWorkOrderTaskUpdatableFieldsEquals(expectedWorkOrderTask, getPersistedWorkOrderTask(expectedWorkOrderTask));
    }
}
