package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.WorkOrderAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.WorkOrderStatus;
import ar.com.heliguard.app.repository.WorkOrderRepository;
import ar.com.heliguard.app.service.WorkOrderService;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import ar.com.heliguard.app.service.mapper.WorkOrderMapper;
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
 * Integration tests for the {@link WorkOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class WorkOrderResourceIT {

    private static final String DEFAULT_REFERENCE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final WorkOrderStatus DEFAULT_STATUS = WorkOrderStatus.CREATED;
    private static final WorkOrderStatus UPDATED_STATUS = WorkOrderStatus.ASSIGNED;

    private static final AlertSeverity DEFAULT_PRIORITY = AlertSeverity.INFO;
    private static final AlertSeverity UPDATED_PRIORITY = AlertSeverity.LOW;

    private static final Instant DEFAULT_OPENED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OPENED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_SCHEDULED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SCHEDULED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_CLOSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CLOSED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/work-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Mock
    private WorkOrderRepository workOrderRepositoryMock;

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Mock
    private WorkOrderService workOrderServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private WorkOrder workOrder;

    private WorkOrder insertedWorkOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkOrder createEntity(R2dbcEntityTemplate template) {
        WorkOrder workOrder = new WorkOrder()
            .referenceCode(DEFAULT_REFERENCE_CODE)
            .title(DEFAULT_TITLE)
            .status(DEFAULT_STATUS)
            .priority(DEFAULT_PRIORITY)
            .openedAt(DEFAULT_OPENED_AT)
            .scheduledAt(DEFAULT_SCHEDULED_AT)
            .closedAt(DEFAULT_CLOSED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        workOrder.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        workOrder.setInstallation(installation);
        return workOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkOrder createUpdatedEntity(R2dbcEntityTemplate template) {
        WorkOrder updatedWorkOrder = new WorkOrder()
            .referenceCode(UPDATED_REFERENCE_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .openedAt(UPDATED_OPENED_AT)
            .scheduledAt(UPDATED_SCHEDULED_AT)
            .closedAt(UPDATED_CLOSED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedWorkOrder.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedWorkOrder.setInstallation(installation);
        return updatedWorkOrder;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(WorkOrder.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        workOrder = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedWorkOrder != null) {
            workOrderRepository.delete(insertedWorkOrder).block();
            insertedWorkOrder = null;
        }
        deleteEntities(template);
    }

    @Test
    void createWorkOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WorkOrder
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);
        var returnedWorkOrderDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(WorkOrderDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the WorkOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWorkOrder = workOrderMapper.toEntity(returnedWorkOrderDTO);
        assertWorkOrderUpdatableFieldsEquals(returnedWorkOrder, getPersistedWorkOrder(returnedWorkOrder));

        insertedWorkOrder = returnedWorkOrder;
    }

    @Test
    void createWorkOrderWithExistingId() throws Exception {
        // Create the WorkOrder with an existing ID
        workOrder.setId(1L);
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrder.setReferenceCode(null);

        // Create the WorkOrder, which fails.
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrder.setTitle(null);

        // Create the WorkOrder, which fails.
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrder.setStatus(null);

        // Create the WorkOrder, which fails.
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPriorityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrder.setPriority(null);

        // Create the WorkOrder, which fails.
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkOpenedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workOrder.setOpenedAt(null);

        // Create the WorkOrder, which fails.
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllWorkOrders() {
        // Initialize the database
        insertedWorkOrder = workOrderRepository.save(workOrder).block();

        // Get all the workOrderList
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
            .value(hasItem(workOrder.getId().intValue()))
            .jsonPath("$.[*].referenceCode")
            .value(hasItem(DEFAULT_REFERENCE_CODE))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].priority")
            .value(hasItem(DEFAULT_PRIORITY.toString()))
            .jsonPath("$.[*].openedAt")
            .value(hasItem(DEFAULT_OPENED_AT.toString()))
            .jsonPath("$.[*].scheduledAt")
            .value(hasItem(DEFAULT_SCHEDULED_AT.toString()))
            .jsonPath("$.[*].closedAt")
            .value(hasItem(DEFAULT_CLOSED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkOrdersWithEagerRelationshipsIsEnabled() {
        when(workOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(workOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkOrdersWithEagerRelationshipsIsNotEnabled() {
        when(workOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(workOrderRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getWorkOrder() {
        // Initialize the database
        insertedWorkOrder = workOrderRepository.save(workOrder).block();

        // Get the workOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, workOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(workOrder.getId().intValue()))
            .jsonPath("$.referenceCode")
            .value(is(DEFAULT_REFERENCE_CODE))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.priority")
            .value(is(DEFAULT_PRIORITY.toString()))
            .jsonPath("$.openedAt")
            .value(is(DEFAULT_OPENED_AT.toString()))
            .jsonPath("$.scheduledAt")
            .value(is(DEFAULT_SCHEDULED_AT.toString()))
            .jsonPath("$.closedAt")
            .value(is(DEFAULT_CLOSED_AT.toString()));
    }

    @Test
    void getNonExistingWorkOrder() {
        // Get the workOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingWorkOrder() throws Exception {
        // Initialize the database
        insertedWorkOrder = workOrderRepository.save(workOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrder
        WorkOrder updatedWorkOrder = workOrderRepository.findById(workOrder.getId()).block();
        updatedWorkOrder
            .referenceCode(UPDATED_REFERENCE_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .openedAt(UPDATED_OPENED_AT)
            .scheduledAt(UPDATED_SCHEDULED_AT)
            .closedAt(UPDATED_CLOSED_AT);
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(updatedWorkOrder);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, workOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWorkOrderToMatchAllProperties(updatedWorkOrder);
    }

    @Test
    void putNonExistingWorkOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrder.setId(longCount.incrementAndGet());

        // Create the WorkOrder
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, workOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchWorkOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrder.setId(longCount.incrementAndGet());

        // Create the WorkOrder
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamWorkOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrder.setId(longCount.incrementAndGet());

        // Create the WorkOrder
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WorkOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateWorkOrderWithPatch() throws Exception {
        // Initialize the database
        insertedWorkOrder = workOrderRepository.save(workOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrder using partial update
        WorkOrder partialUpdatedWorkOrder = new WorkOrder();
        partialUpdatedWorkOrder.setId(workOrder.getId());

        partialUpdatedWorkOrder.referenceCode(UPDATED_REFERENCE_CODE).title(UPDATED_TITLE).priority(UPDATED_PRIORITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWorkOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWorkOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWorkOrder, workOrder),
            getPersistedWorkOrder(workOrder)
        );
    }

    @Test
    void fullUpdateWorkOrderWithPatch() throws Exception {
        // Initialize the database
        insertedWorkOrder = workOrderRepository.save(workOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workOrder using partial update
        WorkOrder partialUpdatedWorkOrder = new WorkOrder();
        partialUpdatedWorkOrder.setId(workOrder.getId());

        partialUpdatedWorkOrder
            .referenceCode(UPDATED_REFERENCE_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .openedAt(UPDATED_OPENED_AT)
            .scheduledAt(UPDATED_SCHEDULED_AT)
            .closedAt(UPDATED_CLOSED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWorkOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedWorkOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WorkOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkOrderUpdatableFieldsEquals(partialUpdatedWorkOrder, getPersistedWorkOrder(partialUpdatedWorkOrder));
    }

    @Test
    void patchNonExistingWorkOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrder.setId(longCount.incrementAndGet());

        // Create the WorkOrder
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, workOrderDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchWorkOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrder.setId(longCount.incrementAndGet());

        // Create the WorkOrder
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WorkOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamWorkOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workOrder.setId(longCount.incrementAndGet());

        // Create the WorkOrder
        WorkOrderDTO workOrderDTO = workOrderMapper.toDto(workOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(workOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WorkOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteWorkOrder() {
        // Initialize the database
        insertedWorkOrder = workOrderRepository.save(workOrder).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the workOrder
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, workOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return workOrderRepository.count().block();
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

    protected WorkOrder getPersistedWorkOrder(WorkOrder workOrder) {
        return workOrderRepository.findById(workOrder.getId()).block();
    }

    protected void assertPersistedWorkOrderToMatchAllProperties(WorkOrder expectedWorkOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderAllPropertiesEquals(expectedWorkOrder, getPersistedWorkOrder(expectedWorkOrder));
        assertWorkOrderUpdatableFieldsEquals(expectedWorkOrder, getPersistedWorkOrder(expectedWorkOrder));
    }

    protected void assertPersistedWorkOrderToMatchUpdatableProperties(WorkOrder expectedWorkOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertWorkOrderAllUpdatablePropertiesEquals(expectedWorkOrder, getPersistedWorkOrder(expectedWorkOrder));
        assertWorkOrderUpdatableFieldsEquals(expectedWorkOrder, getPersistedWorkOrder(expectedWorkOrder));
    }
}
