package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.DroneAssetAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DroneAsset;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.DroneAssetStatus;
import ar.com.heliguard.app.repository.DroneAssetRepository;
import ar.com.heliguard.app.service.DroneAssetService;
import ar.com.heliguard.app.service.dto.DroneAssetDTO;
import ar.com.heliguard.app.service.mapper.DroneAssetMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link DroneAssetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DroneAssetResourceIT {

    private static final String DEFAULT_ASSET_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ASSET_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_NAME = "BBBBBBBBBB";

    private static final DroneAssetStatus DEFAULT_STATUS = DroneAssetStatus.READY;
    private static final DroneAssetStatus UPDATED_STATUS = DroneAssetStatus.MAINTENANCE;

    private static final BigDecimal DEFAULT_MAXIMUM_WIND_MS = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAXIMUM_WIND_MS = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/drone-assets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DroneAssetRepository droneAssetRepository;

    @Mock
    private DroneAssetRepository droneAssetRepositoryMock;

    @Autowired
    private DroneAssetMapper droneAssetMapper;

    @Mock
    private DroneAssetService droneAssetServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private DroneAsset droneAsset;

    private DroneAsset insertedDroneAsset;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroneAsset createEntity(R2dbcEntityTemplate template) {
        DroneAsset droneAsset = new DroneAsset()
            .assetCode(DEFAULT_ASSET_CODE)
            .modelName(DEFAULT_MODEL_NAME)
            .status(DEFAULT_STATUS)
            .maximumWindMs(DEFAULT_MAXIMUM_WIND_MS);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        droneAsset.setOrganization(organization);
        return droneAsset;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroneAsset createUpdatedEntity(R2dbcEntityTemplate template) {
        DroneAsset updatedDroneAsset = new DroneAsset()
            .assetCode(UPDATED_ASSET_CODE)
            .modelName(UPDATED_MODEL_NAME)
            .status(UPDATED_STATUS)
            .maximumWindMs(UPDATED_MAXIMUM_WIND_MS);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedDroneAsset.setOrganization(organization);
        return updatedDroneAsset;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(DroneAsset.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        droneAsset = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedDroneAsset != null) {
            droneAssetRepository.delete(insertedDroneAsset).block();
            insertedDroneAsset = null;
        }
        deleteEntities(template);
    }

    @Test
    void createDroneAsset() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DroneAsset
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);
        var returnedDroneAssetDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DroneAssetDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the DroneAsset in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDroneAsset = droneAssetMapper.toEntity(returnedDroneAssetDTO);
        assertDroneAssetUpdatableFieldsEquals(returnedDroneAsset, getPersistedDroneAsset(returnedDroneAsset));

        insertedDroneAsset = returnedDroneAsset;
    }

    @Test
    void createDroneAssetWithExistingId() throws Exception {
        // Create the DroneAsset with an existing ID
        droneAsset.setId(1L);
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneAsset in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAssetCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneAsset.setAssetCode(null);

        // Create the DroneAsset, which fails.
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneAsset.setStatus(null);

        // Create the DroneAsset, which fails.
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDroneAssetsAsStream() {
        // Initialize the database
        droneAssetRepository.save(droneAsset).block();

        List<DroneAsset> droneAssetList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(DroneAssetDTO.class)
            .getResponseBody()
            .map(droneAssetMapper::toEntity)
            .filter(droneAsset::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(droneAssetList).isNotNull();
        assertThat(droneAssetList).hasSize(1);
        DroneAsset testDroneAsset = droneAssetList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertDroneAssetAllPropertiesEquals(droneAsset, testDroneAsset);
        assertDroneAssetUpdatableFieldsEquals(droneAsset, testDroneAsset);
    }

    @Test
    void getAllDroneAssets() {
        // Initialize the database
        insertedDroneAsset = droneAssetRepository.save(droneAsset).block();

        // Get all the droneAssetList
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
            .value(hasItem(droneAsset.getId().intValue()))
            .jsonPath("$.[*].assetCode")
            .value(hasItem(DEFAULT_ASSET_CODE))
            .jsonPath("$.[*].modelName")
            .value(hasItem(DEFAULT_MODEL_NAME))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].maximumWindMs")
            .value(hasItem(sameNumber(DEFAULT_MAXIMUM_WIND_MS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDroneAssetsWithEagerRelationshipsIsEnabled() {
        when(droneAssetServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(droneAssetServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDroneAssetsWithEagerRelationshipsIsNotEnabled() {
        when(droneAssetServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(droneAssetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDroneAsset() {
        // Initialize the database
        insertedDroneAsset = droneAssetRepository.save(droneAsset).block();

        // Get the droneAsset
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, droneAsset.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(droneAsset.getId().intValue()))
            .jsonPath("$.assetCode")
            .value(is(DEFAULT_ASSET_CODE))
            .jsonPath("$.modelName")
            .value(is(DEFAULT_MODEL_NAME))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.maximumWindMs")
            .value(is(sameNumber(DEFAULT_MAXIMUM_WIND_MS)));
    }

    @Test
    void getNonExistingDroneAsset() {
        // Get the droneAsset
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDroneAsset() throws Exception {
        // Initialize the database
        insertedDroneAsset = droneAssetRepository.save(droneAsset).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneAsset
        DroneAsset updatedDroneAsset = droneAssetRepository.findById(droneAsset.getId()).block();
        updatedDroneAsset
            .assetCode(UPDATED_ASSET_CODE)
            .modelName(UPDATED_MODEL_NAME)
            .status(UPDATED_STATUS)
            .maximumWindMs(UPDATED_MAXIMUM_WIND_MS);
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(updatedDroneAsset);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, droneAssetDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneAsset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDroneAssetToMatchAllProperties(updatedDroneAsset);
    }

    @Test
    void putNonExistingDroneAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneAsset.setId(longCount.incrementAndGet());

        // Create the DroneAsset
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, droneAssetDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneAsset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDroneAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneAsset.setId(longCount.incrementAndGet());

        // Create the DroneAsset
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneAsset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDroneAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneAsset.setId(longCount.incrementAndGet());

        // Create the DroneAsset
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DroneAsset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDroneAssetWithPatch() throws Exception {
        // Initialize the database
        insertedDroneAsset = droneAssetRepository.save(droneAsset).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneAsset using partial update
        DroneAsset partialUpdatedDroneAsset = new DroneAsset();
        partialUpdatedDroneAsset.setId(droneAsset.getId());

        partialUpdatedDroneAsset.assetCode(UPDATED_ASSET_CODE).modelName(UPDATED_MODEL_NAME).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDroneAsset.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDroneAsset))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneAsset in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDroneAssetUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDroneAsset, droneAsset),
            getPersistedDroneAsset(droneAsset)
        );
    }

    @Test
    void fullUpdateDroneAssetWithPatch() throws Exception {
        // Initialize the database
        insertedDroneAsset = droneAssetRepository.save(droneAsset).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneAsset using partial update
        DroneAsset partialUpdatedDroneAsset = new DroneAsset();
        partialUpdatedDroneAsset.setId(droneAsset.getId());

        partialUpdatedDroneAsset
            .assetCode(UPDATED_ASSET_CODE)
            .modelName(UPDATED_MODEL_NAME)
            .status(UPDATED_STATUS)
            .maximumWindMs(UPDATED_MAXIMUM_WIND_MS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDroneAsset.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDroneAsset))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneAsset in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDroneAssetUpdatableFieldsEquals(partialUpdatedDroneAsset, getPersistedDroneAsset(partialUpdatedDroneAsset));
    }

    @Test
    void patchNonExistingDroneAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneAsset.setId(longCount.incrementAndGet());

        // Create the DroneAsset
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, droneAssetDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneAsset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDroneAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneAsset.setId(longCount.incrementAndGet());

        // Create the DroneAsset
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneAsset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDroneAsset() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneAsset.setId(longCount.incrementAndGet());

        // Create the DroneAsset
        DroneAssetDTO droneAssetDTO = droneAssetMapper.toDto(droneAsset);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneAssetDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DroneAsset in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDroneAsset() {
        // Initialize the database
        insertedDroneAsset = droneAssetRepository.save(droneAsset).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the droneAsset
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, droneAsset.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return droneAssetRepository.count().block();
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

    protected DroneAsset getPersistedDroneAsset(DroneAsset droneAsset) {
        return droneAssetRepository.findById(droneAsset.getId()).block();
    }

    protected void assertPersistedDroneAssetToMatchAllProperties(DroneAsset expectedDroneAsset) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDroneAssetAllPropertiesEquals(expectedDroneAsset, getPersistedDroneAsset(expectedDroneAsset));
        assertDroneAssetUpdatableFieldsEquals(expectedDroneAsset, getPersistedDroneAsset(expectedDroneAsset));
    }

    protected void assertPersistedDroneAssetToMatchUpdatableProperties(DroneAsset expectedDroneAsset) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDroneAssetAllUpdatablePropertiesEquals(expectedDroneAsset, getPersistedDroneAsset(expectedDroneAsset));
        assertDroneAssetUpdatableFieldsEquals(expectedDroneAsset, getPersistedDroneAsset(expectedDroneAsset));
    }
}
