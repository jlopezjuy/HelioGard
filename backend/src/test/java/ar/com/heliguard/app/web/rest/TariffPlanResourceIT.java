package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.TariffPlanAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffPlan;
import ar.com.heliguard.app.repository.TariffPlanRepository;
import ar.com.heliguard.app.service.TariffPlanService;
import ar.com.heliguard.app.service.dto.TariffPlanDTO;
import ar.com.heliguard.app.service.mapper.TariffPlanMapper;
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
 * Integration tests for the {@link TariffPlanResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TariffPlanResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY_CODE = "AAA";
    private static final String UPDATED_CURRENCY_CODE = "BBB";

    private static final String DEFAULT_DISTRIBUTOR = "AAAAAAAAAA";
    private static final String UPDATED_DISTRIBUTOR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tariff-plans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TariffPlanRepository tariffPlanRepository;

    @Mock
    private TariffPlanRepository tariffPlanRepositoryMock;

    @Autowired
    private TariffPlanMapper tariffPlanMapper;

    @Mock
    private TariffPlanService tariffPlanServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private TariffPlan tariffPlan;

    private TariffPlan insertedTariffPlan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TariffPlan createEntity(R2dbcEntityTemplate template) {
        TariffPlan tariffPlan = new TariffPlan().name(DEFAULT_NAME).currencyCode(DEFAULT_CURRENCY_CODE).distributor(DEFAULT_DISTRIBUTOR);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        tariffPlan.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        tariffPlan.setInstallation(installation);
        return tariffPlan;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TariffPlan createUpdatedEntity(R2dbcEntityTemplate template) {
        TariffPlan updatedTariffPlan = new TariffPlan()
            .name(UPDATED_NAME)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .distributor(UPDATED_DISTRIBUTOR);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedTariffPlan.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedTariffPlan.setInstallation(installation);
        return updatedTariffPlan;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(TariffPlan.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        tariffPlan = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedTariffPlan != null) {
            tariffPlanRepository.delete(insertedTariffPlan).block();
            insertedTariffPlan = null;
        }
        deleteEntities(template);
    }

    @Test
    void createTariffPlan() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TariffPlan
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);
        var returnedTariffPlanDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TariffPlanDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TariffPlan in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTariffPlan = tariffPlanMapper.toEntity(returnedTariffPlanDTO);
        assertTariffPlanUpdatableFieldsEquals(returnedTariffPlan, getPersistedTariffPlan(returnedTariffPlan));

        insertedTariffPlan = returnedTariffPlan;
    }

    @Test
    void createTariffPlanWithExistingId() throws Exception {
        // Create the TariffPlan with an existing ID
        tariffPlan.setId(1L);
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tariffPlan.setName(null);

        // Create the TariffPlan, which fails.
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tariffPlan.setCurrencyCode(null);

        // Create the TariffPlan, which fails.
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTariffPlansAsStream() {
        // Initialize the database
        tariffPlanRepository.save(tariffPlan).block();

        List<TariffPlan> tariffPlanList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TariffPlanDTO.class)
            .getResponseBody()
            .map(tariffPlanMapper::toEntity)
            .filter(tariffPlan::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tariffPlanList).isNotNull();
        assertThat(tariffPlanList).hasSize(1);
        TariffPlan testTariffPlan = tariffPlanList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertTariffPlanAllPropertiesEquals(tariffPlan, testTariffPlan);
        assertTariffPlanUpdatableFieldsEquals(tariffPlan, testTariffPlan);
    }

    @Test
    void getAllTariffPlans() {
        // Initialize the database
        insertedTariffPlan = tariffPlanRepository.save(tariffPlan).block();

        // Get all the tariffPlanList
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
            .value(hasItem(tariffPlan.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].currencyCode")
            .value(hasItem(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.[*].distributor")
            .value(hasItem(DEFAULT_DISTRIBUTOR));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTariffPlansWithEagerRelationshipsIsEnabled() {
        when(tariffPlanServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(tariffPlanServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTariffPlansWithEagerRelationshipsIsNotEnabled() {
        when(tariffPlanServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(tariffPlanRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTariffPlan() {
        // Initialize the database
        insertedTariffPlan = tariffPlanRepository.save(tariffPlan).block();

        // Get the tariffPlan
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tariffPlan.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tariffPlan.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.currencyCode")
            .value(is(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.distributor")
            .value(is(DEFAULT_DISTRIBUTOR));
    }

    @Test
    void getNonExistingTariffPlan() {
        // Get the tariffPlan
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTariffPlan() throws Exception {
        // Initialize the database
        insertedTariffPlan = tariffPlanRepository.save(tariffPlan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tariffPlan
        TariffPlan updatedTariffPlan = tariffPlanRepository.findById(tariffPlan.getId()).block();
        updatedTariffPlan.name(UPDATED_NAME).currencyCode(UPDATED_CURRENCY_CODE).distributor(UPDATED_DISTRIBUTOR);
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(updatedTariffPlan);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tariffPlanDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TariffPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTariffPlanToMatchAllProperties(updatedTariffPlan);
    }

    @Test
    void putNonExistingTariffPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffPlan.setId(longCount.incrementAndGet());

        // Create the TariffPlan
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tariffPlanDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTariffPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffPlan.setId(longCount.incrementAndGet());

        // Create the TariffPlan
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTariffPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffPlan.setId(longCount.incrementAndGet());

        // Create the TariffPlan
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TariffPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTariffPlanWithPatch() throws Exception {
        // Initialize the database
        insertedTariffPlan = tariffPlanRepository.save(tariffPlan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tariffPlan using partial update
        TariffPlan partialUpdatedTariffPlan = new TariffPlan();
        partialUpdatedTariffPlan.setId(tariffPlan.getId());

        partialUpdatedTariffPlan.currencyCode(UPDATED_CURRENCY_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTariffPlan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTariffPlan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TariffPlan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTariffPlanUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTariffPlan, tariffPlan),
            getPersistedTariffPlan(tariffPlan)
        );
    }

    @Test
    void fullUpdateTariffPlanWithPatch() throws Exception {
        // Initialize the database
        insertedTariffPlan = tariffPlanRepository.save(tariffPlan).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tariffPlan using partial update
        TariffPlan partialUpdatedTariffPlan = new TariffPlan();
        partialUpdatedTariffPlan.setId(tariffPlan.getId());

        partialUpdatedTariffPlan.name(UPDATED_NAME).currencyCode(UPDATED_CURRENCY_CODE).distributor(UPDATED_DISTRIBUTOR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTariffPlan.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTariffPlan))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TariffPlan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTariffPlanUpdatableFieldsEquals(partialUpdatedTariffPlan, getPersistedTariffPlan(partialUpdatedTariffPlan));
    }

    @Test
    void patchNonExistingTariffPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffPlan.setId(longCount.incrementAndGet());

        // Create the TariffPlan
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tariffPlanDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTariffPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffPlan.setId(longCount.incrementAndGet());

        // Create the TariffPlan
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTariffPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffPlan.setId(longCount.incrementAndGet());

        // Create the TariffPlan
        TariffPlanDTO tariffPlanDTO = tariffPlanMapper.toDto(tariffPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tariffPlanDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TariffPlan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTariffPlan() {
        // Initialize the database
        insertedTariffPlan = tariffPlanRepository.save(tariffPlan).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tariffPlan
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tariffPlan.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tariffPlanRepository.count().block();
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

    protected TariffPlan getPersistedTariffPlan(TariffPlan tariffPlan) {
        return tariffPlanRepository.findById(tariffPlan.getId()).block();
    }

    protected void assertPersistedTariffPlanToMatchAllProperties(TariffPlan expectedTariffPlan) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTariffPlanAllPropertiesEquals(expectedTariffPlan, getPersistedTariffPlan(expectedTariffPlan));
        assertTariffPlanUpdatableFieldsEquals(expectedTariffPlan, getPersistedTariffPlan(expectedTariffPlan));
    }

    protected void assertPersistedTariffPlanToMatchUpdatableProperties(TariffPlan expectedTariffPlan) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTariffPlanAllUpdatablePropertiesEquals(expectedTariffPlan, getPersistedTariffPlan(expectedTariffPlan));
        assertTariffPlanUpdatableFieldsEquals(expectedTariffPlan, getPersistedTariffPlan(expectedTariffPlan));
    }
}
