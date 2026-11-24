package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.MaintenanceInterventionAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.MaintenanceIntervention;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.enumeration.InterventionType;
import ar.com.heliguard.app.repository.MaintenanceInterventionRepository;
import ar.com.heliguard.app.service.MaintenanceInterventionService;
import ar.com.heliguard.app.service.dto.MaintenanceInterventionDTO;
import ar.com.heliguard.app.service.mapper.MaintenanceInterventionMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link MaintenanceInterventionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MaintenanceInterventionResourceIT {

    private static final InterventionType DEFAULT_TYPE = InterventionType.INSPECTION;
    private static final InterventionType UPDATED_TYPE = InterventionType.CLEANING;

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_FINISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINISHED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final BigDecimal DEFAULT_WORK_COST = new BigDecimal(1);
    private static final BigDecimal UPDATED_WORK_COST = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY_CODE = "AAA";
    private static final String UPDATED_CURRENCY_CODE = "BBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/maintenance-interventions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MaintenanceInterventionRepository maintenanceInterventionRepository;

    @Mock
    private MaintenanceInterventionRepository maintenanceInterventionRepositoryMock;

    @Autowired
    private MaintenanceInterventionMapper maintenanceInterventionMapper;

    @Mock
    private MaintenanceInterventionService maintenanceInterventionServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private MaintenanceIntervention maintenanceIntervention;

    private MaintenanceIntervention insertedMaintenanceIntervention;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MaintenanceIntervention createEntity(R2dbcEntityTemplate template) {
        MaintenanceIntervention maintenanceIntervention = new MaintenanceIntervention()
            .type(DEFAULT_TYPE)
            .startedAt(DEFAULT_STARTED_AT)
            .finishedAt(DEFAULT_FINISHED_AT)
            .workCost(DEFAULT_WORK_COST)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .notes(DEFAULT_NOTES);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        maintenanceIntervention.setOrganization(organization);
        // Add required entity
        WorkOrder workOrder;
        workOrder = template.insert(WorkOrderResourceIT.createEntity(template)).block();
        maintenanceIntervention.setWorkOrder(workOrder);
        return maintenanceIntervention;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MaintenanceIntervention createUpdatedEntity(R2dbcEntityTemplate template) {
        MaintenanceIntervention updatedMaintenanceIntervention = new MaintenanceIntervention()
            .type(UPDATED_TYPE)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT)
            .workCost(UPDATED_WORK_COST)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .notes(UPDATED_NOTES);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedMaintenanceIntervention.setOrganization(organization);
        // Add required entity
        WorkOrder workOrder;
        workOrder = template.insert(WorkOrderResourceIT.createUpdatedEntity(template)).block();
        updatedMaintenanceIntervention.setWorkOrder(workOrder);
        return updatedMaintenanceIntervention;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(MaintenanceIntervention.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        WorkOrderResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        maintenanceIntervention = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedMaintenanceIntervention != null) {
            maintenanceInterventionRepository.delete(insertedMaintenanceIntervention).block();
            insertedMaintenanceIntervention = null;
        }
        deleteEntities(template);
    }

    @Test
    void createMaintenanceIntervention() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MaintenanceIntervention
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);
        var returnedMaintenanceInterventionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MaintenanceInterventionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MaintenanceIntervention in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMaintenanceIntervention = maintenanceInterventionMapper.toEntity(returnedMaintenanceInterventionDTO);
        assertMaintenanceInterventionUpdatableFieldsEquals(
            returnedMaintenanceIntervention,
            getPersistedMaintenanceIntervention(returnedMaintenanceIntervention)
        );

        insertedMaintenanceIntervention = returnedMaintenanceIntervention;
    }

    @Test
    void createMaintenanceInterventionWithExistingId() throws Exception {
        // Create the MaintenanceIntervention with an existing ID
        maintenanceIntervention.setId(1L);
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenanceIntervention in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        maintenanceIntervention.setType(null);

        // Create the MaintenanceIntervention, which fails.
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMaintenanceInterventionsAsStream() {
        // Initialize the database
        maintenanceInterventionRepository.save(maintenanceIntervention).block();

        List<MaintenanceIntervention> maintenanceInterventionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(MaintenanceInterventionDTO.class)
            .getResponseBody()
            .map(maintenanceInterventionMapper::toEntity)
            .filter(maintenanceIntervention::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(maintenanceInterventionList).isNotNull();
        assertThat(maintenanceInterventionList).hasSize(1);
        MaintenanceIntervention testMaintenanceIntervention = maintenanceInterventionList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenanceInterventionAllPropertiesEquals(maintenanceIntervention, testMaintenanceIntervention);
        assertMaintenanceInterventionUpdatableFieldsEquals(maintenanceIntervention, testMaintenanceIntervention);
    }

    @Test
    void getAllMaintenanceInterventions() {
        // Initialize the database
        insertedMaintenanceIntervention = maintenanceInterventionRepository.save(maintenanceIntervention).block();

        // Get all the maintenanceInterventionList
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
            .value(hasItem(maintenanceIntervention.getId().intValue()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].startedAt")
            .value(hasItem(DEFAULT_STARTED_AT.toString()))
            .jsonPath("$.[*].finishedAt")
            .value(hasItem(DEFAULT_FINISHED_AT.toString()))
            .jsonPath("$.[*].workCost")
            .value(hasItem(sameNumber(DEFAULT_WORK_COST)))
            .jsonPath("$.[*].currencyCode")
            .value(hasItem(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMaintenanceInterventionsWithEagerRelationshipsIsEnabled() {
        when(maintenanceInterventionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(maintenanceInterventionServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMaintenanceInterventionsWithEagerRelationshipsIsNotEnabled() {
        when(maintenanceInterventionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(maintenanceInterventionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMaintenanceIntervention() {
        // Initialize the database
        insertedMaintenanceIntervention = maintenanceInterventionRepository.save(maintenanceIntervention).block();

        // Get the maintenanceIntervention
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, maintenanceIntervention.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(maintenanceIntervention.getId().intValue()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.startedAt")
            .value(is(DEFAULT_STARTED_AT.toString()))
            .jsonPath("$.finishedAt")
            .value(is(DEFAULT_FINISHED_AT.toString()))
            .jsonPath("$.workCost")
            .value(is(sameNumber(DEFAULT_WORK_COST)))
            .jsonPath("$.currencyCode")
            .value(is(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES));
    }

    @Test
    void getNonExistingMaintenanceIntervention() {
        // Get the maintenanceIntervention
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMaintenanceIntervention() throws Exception {
        // Initialize the database
        insertedMaintenanceIntervention = maintenanceInterventionRepository.save(maintenanceIntervention).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenanceIntervention
        MaintenanceIntervention updatedMaintenanceIntervention = maintenanceInterventionRepository
            .findById(maintenanceIntervention.getId())
            .block();
        updatedMaintenanceIntervention
            .type(UPDATED_TYPE)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT)
            .workCost(UPDATED_WORK_COST)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .notes(UPDATED_NOTES);
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(updatedMaintenanceIntervention);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, maintenanceInterventionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenanceIntervention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMaintenanceInterventionToMatchAllProperties(updatedMaintenanceIntervention);
    }

    @Test
    void putNonExistingMaintenanceIntervention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenanceIntervention.setId(longCount.incrementAndGet());

        // Create the MaintenanceIntervention
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, maintenanceInterventionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenanceIntervention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMaintenanceIntervention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenanceIntervention.setId(longCount.incrementAndGet());

        // Create the MaintenanceIntervention
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenanceIntervention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMaintenanceIntervention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenanceIntervention.setId(longCount.incrementAndGet());

        // Create the MaintenanceIntervention
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MaintenanceIntervention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMaintenanceInterventionWithPatch() throws Exception {
        // Initialize the database
        insertedMaintenanceIntervention = maintenanceInterventionRepository.save(maintenanceIntervention).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenanceIntervention using partial update
        MaintenanceIntervention partialUpdatedMaintenanceIntervention = new MaintenanceIntervention();
        partialUpdatedMaintenanceIntervention.setId(maintenanceIntervention.getId());

        partialUpdatedMaintenanceIntervention
            .type(UPDATED_TYPE)
            .startedAt(UPDATED_STARTED_AT)
            .workCost(UPDATED_WORK_COST)
            .currencyCode(UPDATED_CURRENCY_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMaintenanceIntervention.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMaintenanceIntervention))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenanceIntervention in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaintenanceInterventionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMaintenanceIntervention, maintenanceIntervention),
            getPersistedMaintenanceIntervention(maintenanceIntervention)
        );
    }

    @Test
    void fullUpdateMaintenanceInterventionWithPatch() throws Exception {
        // Initialize the database
        insertedMaintenanceIntervention = maintenanceInterventionRepository.save(maintenanceIntervention).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenanceIntervention using partial update
        MaintenanceIntervention partialUpdatedMaintenanceIntervention = new MaintenanceIntervention();
        partialUpdatedMaintenanceIntervention.setId(maintenanceIntervention.getId());

        partialUpdatedMaintenanceIntervention
            .type(UPDATED_TYPE)
            .startedAt(UPDATED_STARTED_AT)
            .finishedAt(UPDATED_FINISHED_AT)
            .workCost(UPDATED_WORK_COST)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMaintenanceIntervention.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMaintenanceIntervention))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenanceIntervention in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaintenanceInterventionUpdatableFieldsEquals(
            partialUpdatedMaintenanceIntervention,
            getPersistedMaintenanceIntervention(partialUpdatedMaintenanceIntervention)
        );
    }

    @Test
    void patchNonExistingMaintenanceIntervention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenanceIntervention.setId(longCount.incrementAndGet());

        // Create the MaintenanceIntervention
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, maintenanceInterventionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenanceIntervention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMaintenanceIntervention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenanceIntervention.setId(longCount.incrementAndGet());

        // Create the MaintenanceIntervention
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenanceIntervention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMaintenanceIntervention() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenanceIntervention.setId(longCount.incrementAndGet());

        // Create the MaintenanceIntervention
        MaintenanceInterventionDTO maintenanceInterventionDTO = maintenanceInterventionMapper.toDto(maintenanceIntervention);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenanceInterventionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MaintenanceIntervention in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMaintenanceIntervention() {
        // Initialize the database
        insertedMaintenanceIntervention = maintenanceInterventionRepository.save(maintenanceIntervention).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the maintenanceIntervention
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, maintenanceIntervention.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return maintenanceInterventionRepository.count().block();
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

    protected MaintenanceIntervention getPersistedMaintenanceIntervention(MaintenanceIntervention maintenanceIntervention) {
        return maintenanceInterventionRepository.findById(maintenanceIntervention.getId()).block();
    }

    protected void assertPersistedMaintenanceInterventionToMatchAllProperties(MaintenanceIntervention expectedMaintenanceIntervention) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenanceInterventionAllPropertiesEquals(expectedMaintenanceIntervention, getPersistedMaintenanceIntervention(expectedMaintenanceIntervention));
        assertMaintenanceInterventionUpdatableFieldsEquals(
            expectedMaintenanceIntervention,
            getPersistedMaintenanceIntervention(expectedMaintenanceIntervention)
        );
    }

    protected void assertPersistedMaintenanceInterventionToMatchUpdatableProperties(
        MaintenanceIntervention expectedMaintenanceIntervention
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenanceInterventionAllUpdatablePropertiesEquals(expectedMaintenanceIntervention, getPersistedMaintenanceIntervention(expectedMaintenanceIntervention));
        assertMaintenanceInterventionUpdatableFieldsEquals(
            expectedMaintenanceIntervention,
            getPersistedMaintenanceIntervention(expectedMaintenanceIntervention)
        );
    }
}
