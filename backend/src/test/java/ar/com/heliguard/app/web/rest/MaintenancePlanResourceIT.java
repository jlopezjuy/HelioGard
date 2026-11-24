package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.MaintenancePlanAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.MaintenancePlan;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.MaintenancePlanStatus;
import ar.com.heliguard.app.repository.MaintenancePlanRepository;
import ar.com.heliguard.app.service.MaintenancePlanService;
import ar.com.heliguard.app.service.dto.MaintenancePlanDTO;
import ar.com.heliguard.app.service.mapper.MaintenancePlanMapper;
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
 * Integration tests for the {@link MaintenancePlanResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MaintenancePlanResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final MaintenancePlanStatus DEFAULT_STATUS = MaintenancePlanStatus.DRAFT;
    private static final MaintenancePlanStatus UPDATED_STATUS = MaintenancePlanStatus.ACTIVE;

    private static final String ENTITY_API_URL = "/api/maintenance-plans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Mock
    private MaintenancePlanRepository maintenancePlanRepositoryMock;

    @Autowired
    private MaintenancePlanMapper maintenancePlanMapper;

    @Mock
    private MaintenancePlanService maintenancePlanServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private MaintenancePlan maintenancePlan;

    private MaintenancePlan insertedMaintenancePlan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MaintenancePlan createEntity(R2dbcEntityTemplate template) {
        MaintenancePlan maintenancePlan = new MaintenancePlan().name(DEFAULT_NAME).status(DEFAULT_STATUS);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        maintenancePlan.setOrganization(organization);
        return maintenancePlan;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MaintenancePlan createUpdatedEntity(R2dbcEntityTemplate template) {
        MaintenancePlan updatedMaintenancePlan = new MaintenancePlan().name(UPDATED_NAME).status(UPDATED_STATUS);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedMaintenancePlan.setOrganization(organization);
        return updatedMaintenancePlan;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(MaintenancePlan.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        maintenancePlan = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedMaintenancePlan != null) {
            maintenancePlanRepository.delete(insertedMaintenancePlan).block();
            insertedMaintenancePlan = null;
        }
        deleteEntities(template);
    }

    @Test
    void createMaintenancePlan() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MaintenancePlan
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);
        var returnedMaintenancePlanDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MaintenancePlanDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MaintenancePlan in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMaintenancePlan = maintenancePlanMapper.toEntity(returnedMaintenancePlanDTO);
        assertMaintenancePlanUpdatableFieldsEquals(returnedMaintenancePlan, getPersistedMaintenancePlan(returnedMaintenancePlan));

        insertedMaintenancePlan = returnedMaintenancePlan;
    }

    @Test
    void createMaintenancePlanWithExistingId() throws Exception {
        // Create the MaintenancePlan with an existing ID
        maintenancePlan.setId(1L);
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlan in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        maintenancePlan.setName(null);

        // Create the MaintenancePlan, which fails.
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        maintenancePlan.setStatus(null);

        // Create the MaintenancePlan, which fails.
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMaintenancePlansAsStream() {
        // Initialize the database
        maintenancePlanRepository.save(maintenancePlan).block();

        List<MaintenancePlan> maintenancePlanList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(MaintenancePlanDTO.class)
            .getResponseBody()
            .map(maintenancePlanMapper::toEntity)
            .filter(maintenancePlan::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(maintenancePlanList).isNotNull();
        assertThat(maintenancePlanList).hasSize(1);
        MaintenancePlan testMaintenancePlan = maintenancePlanList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenancePlanAllPropertiesEquals(maintenancePlan, testMaintenancePlan);
        assertMaintenancePlanUpdatableFieldsEquals(maintenancePlan, testMaintenancePlan);
    }

    @Test
    void getAllMaintenancePlans() {
        // Initialize the database
        insertedMaintenancePlan = maintenancePlanRepository.save(maintenancePlan).block();

        // Get all the maintenancePlanList
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
            .value(hasItem(maintenancePlan.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMaintenancePlansWithEagerRelationshipsIsEnabled() {
        when(maintenancePlanServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(maintenancePlanServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMaintenancePlansWithEagerRelationshipsIsNotEnabled() {
        when(maintenancePlanServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(maintenancePlanRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMaintenancePlan() {
        // Initialize the database
        insertedMaintenancePlan = maintenancePlanRepository.save(maintenancePlan).block();

        // Get the maintenancePlan
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, maintenancePlan.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(maintenancePlan.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingMaintenancePlan() {
        // Get the maintenancePlan
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMaintenancePlan() throws Exception {
        // Initialize the database
        insertedMaintenancePlan = maintenancePlanRepository.save(maintenancePlan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenancePlan
        MaintenancePlan updatedMaintenancePlan = maintenancePlanRepository.findById(maintenancePlan.getId()).block();
        updatedMaintenancePlan.name(UPDATED_NAME).status(UPDATED_STATUS);
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(updatedMaintenancePlan);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, maintenancePlanDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenancePlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMaintenancePlanToMatchAllProperties(updatedMaintenancePlan);
    }

    @Test
    void putNonExistingMaintenancePlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlan.setId(longCount.incrementAndGet());

        // Create the MaintenancePlan
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, maintenancePlanDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMaintenancePlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlan.setId(longCount.incrementAndGet());

        // Create the MaintenancePlan
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMaintenancePlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlan.setId(longCount.incrementAndGet());

        // Create the MaintenancePlan
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MaintenancePlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMaintenancePlanWithPatch() throws Exception {
        // Initialize the database
        insertedMaintenancePlan = maintenancePlanRepository.save(maintenancePlan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenancePlan using partial update
        MaintenancePlan partialUpdatedMaintenancePlan = new MaintenancePlan();
        partialUpdatedMaintenancePlan.setId(maintenancePlan.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMaintenancePlan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMaintenancePlan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenancePlan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaintenancePlanUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMaintenancePlan, maintenancePlan),
            getPersistedMaintenancePlan(maintenancePlan)
        );
    }

    @Test
    void fullUpdateMaintenancePlanWithPatch() throws Exception {
        // Initialize the database
        insertedMaintenancePlan = maintenancePlanRepository.save(maintenancePlan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenancePlan using partial update
        MaintenancePlan partialUpdatedMaintenancePlan = new MaintenancePlan();
        partialUpdatedMaintenancePlan.setId(maintenancePlan.getId());

        partialUpdatedMaintenancePlan.name(UPDATED_NAME).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMaintenancePlan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMaintenancePlan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenancePlan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaintenancePlanUpdatableFieldsEquals(
            partialUpdatedMaintenancePlan,
            getPersistedMaintenancePlan(partialUpdatedMaintenancePlan)
        );
    }

    @Test
    void patchNonExistingMaintenancePlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlan.setId(longCount.incrementAndGet());

        // Create the MaintenancePlan
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, maintenancePlanDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMaintenancePlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlan.setId(longCount.incrementAndGet());

        // Create the MaintenancePlan
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMaintenancePlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlan.setId(longCount.incrementAndGet());

        // Create the MaintenancePlan
        MaintenancePlanDTO maintenancePlanDTO = maintenancePlanMapper.toDto(maintenancePlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenancePlanDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MaintenancePlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMaintenancePlan() {
        // Initialize the database
        insertedMaintenancePlan = maintenancePlanRepository.save(maintenancePlan).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the maintenancePlan
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, maintenancePlan.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return maintenancePlanRepository.count().block();
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

    protected MaintenancePlan getPersistedMaintenancePlan(MaintenancePlan maintenancePlan) {
        return maintenancePlanRepository.findById(maintenancePlan.getId()).block();
    }

    protected void assertPersistedMaintenancePlanToMatchAllProperties(MaintenancePlan expectedMaintenancePlan) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenancePlanAllPropertiesEquals(expectedMaintenancePlan, getPersistedMaintenancePlan(expectedMaintenancePlan));
        assertMaintenancePlanUpdatableFieldsEquals(expectedMaintenancePlan, getPersistedMaintenancePlan(expectedMaintenancePlan));
    }

    protected void assertPersistedMaintenancePlanToMatchUpdatableProperties(MaintenancePlan expectedMaintenancePlan) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenancePlanAllUpdatablePropertiesEquals(expectedMaintenancePlan, getPersistedMaintenancePlan(expectedMaintenancePlan));
        assertMaintenancePlanUpdatableFieldsEquals(expectedMaintenancePlan, getPersistedMaintenancePlan(expectedMaintenancePlan));
    }
}
