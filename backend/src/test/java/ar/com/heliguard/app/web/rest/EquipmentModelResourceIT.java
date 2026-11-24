package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.EquipmentModelAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.EquipmentModel;
import ar.com.heliguard.app.domain.enumeration.EquipmentKind;
import ar.com.heliguard.app.repository.EquipmentModelRepository;
import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
import ar.com.heliguard.app.service.mapper.EquipmentModelMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link EquipmentModelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EquipmentModelResourceIT {

    private static final String DEFAULT_MANUFACTURER = "AAAAAAAAAA";
    private static final String UPDATED_MANUFACTURER = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_REVISION = "AAAAAAAAAA";
    private static final String UPDATED_REVISION = "BBBBBBBBBB";

    private static final EquipmentKind DEFAULT_KIND = EquipmentKind.MODULE;
    private static final EquipmentKind UPDATED_KIND = EquipmentKind.INVERTER;

    private static final BigDecimal DEFAULT_NOMINAL_POWER_W = new BigDecimal(1);
    private static final BigDecimal UPDATED_NOMINAL_POWER_W = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/equipment-models";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EquipmentModelRepository equipmentModelRepository;

    @Autowired
    private EquipmentModelMapper equipmentModelMapper;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private EquipmentModel equipmentModel;

    private EquipmentModel insertedEquipmentModel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EquipmentModel createEntity() {
        return new EquipmentModel()
            .manufacturer(DEFAULT_MANUFACTURER)
            .modelCode(DEFAULT_MODEL_CODE)
            .revision(DEFAULT_REVISION)
            .kind(DEFAULT_KIND)
            .nominalPowerW(DEFAULT_NOMINAL_POWER_W);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EquipmentModel createUpdatedEntity() {
        return new EquipmentModel()
            .manufacturer(UPDATED_MANUFACTURER)
            .modelCode(UPDATED_MODEL_CODE)
            .revision(UPDATED_REVISION)
            .kind(UPDATED_KIND)
            .nominalPowerW(UPDATED_NOMINAL_POWER_W);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(EquipmentModel.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        equipmentModel = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEquipmentModel != null) {
            equipmentModelRepository.delete(insertedEquipmentModel).block();
            insertedEquipmentModel = null;
        }
        deleteEntities(template);
    }

    @Test
    void createEquipmentModel() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EquipmentModel
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);
        var returnedEquipmentModelDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EquipmentModelDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EquipmentModel in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEquipmentModel = equipmentModelMapper.toEntity(returnedEquipmentModelDTO);
        assertEquipmentModelUpdatableFieldsEquals(returnedEquipmentModel, getPersistedEquipmentModel(returnedEquipmentModel));

        insertedEquipmentModel = returnedEquipmentModel;
    }

    @Test
    void createEquipmentModelWithExistingId() throws Exception {
        // Create the EquipmentModel with an existing ID
        equipmentModel.setId(1L);
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentModel in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkManufacturerIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipmentModel.setManufacturer(null);

        // Create the EquipmentModel, which fails.
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkModelCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipmentModel.setModelCode(null);

        // Create the EquipmentModel, which fails.
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipmentModel.setKind(null);

        // Create the EquipmentModel, which fails.
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEquipmentModelsAsStream() {
        // Initialize the database
        equipmentModelRepository.save(equipmentModel).block();

        List<EquipmentModel> equipmentModelList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(EquipmentModelDTO.class)
            .getResponseBody()
            .map(equipmentModelMapper::toEntity)
            .filter(equipmentModel::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(equipmentModelList).isNotNull();
        assertThat(equipmentModelList).hasSize(1);
        EquipmentModel testEquipmentModel = equipmentModelList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertEquipmentModelAllPropertiesEquals(equipmentModel, testEquipmentModel);
        assertEquipmentModelUpdatableFieldsEquals(equipmentModel, testEquipmentModel);
    }

    @Test
    void getAllEquipmentModels() {
        // Initialize the database
        insertedEquipmentModel = equipmentModelRepository.save(equipmentModel).block();

        // Get all the equipmentModelList
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
            .value(hasItem(equipmentModel.getId().intValue()))
            .jsonPath("$.[*].manufacturer")
            .value(hasItem(DEFAULT_MANUFACTURER))
            .jsonPath("$.[*].modelCode")
            .value(hasItem(DEFAULT_MODEL_CODE))
            .jsonPath("$.[*].revision")
            .value(hasItem(DEFAULT_REVISION))
            .jsonPath("$.[*].kind")
            .value(hasItem(DEFAULT_KIND.toString()))
            .jsonPath("$.[*].nominalPowerW")
            .value(hasItem(sameNumber(DEFAULT_NOMINAL_POWER_W)));
    }

    @Test
    void getEquipmentModel() {
        // Initialize the database
        insertedEquipmentModel = equipmentModelRepository.save(equipmentModel).block();

        // Get the equipmentModel
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, equipmentModel.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(equipmentModel.getId().intValue()))
            .jsonPath("$.manufacturer")
            .value(is(DEFAULT_MANUFACTURER))
            .jsonPath("$.modelCode")
            .value(is(DEFAULT_MODEL_CODE))
            .jsonPath("$.revision")
            .value(is(DEFAULT_REVISION))
            .jsonPath("$.kind")
            .value(is(DEFAULT_KIND.toString()))
            .jsonPath("$.nominalPowerW")
            .value(is(sameNumber(DEFAULT_NOMINAL_POWER_W)));
    }

    @Test
    void getNonExistingEquipmentModel() {
        // Get the equipmentModel
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEquipmentModel() throws Exception {
        // Initialize the database
        insertedEquipmentModel = equipmentModelRepository.save(equipmentModel).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentModel
        EquipmentModel updatedEquipmentModel = equipmentModelRepository.findById(equipmentModel.getId()).block();
        updatedEquipmentModel
            .manufacturer(UPDATED_MANUFACTURER)
            .modelCode(UPDATED_MODEL_CODE)
            .revision(UPDATED_REVISION)
            .kind(UPDATED_KIND)
            .nominalPowerW(UPDATED_NOMINAL_POWER_W);
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(updatedEquipmentModel);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipmentModelDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EquipmentModel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEquipmentModelToMatchAllProperties(updatedEquipmentModel);
    }

    @Test
    void putNonExistingEquipmentModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentModel.setId(longCount.incrementAndGet());

        // Create the EquipmentModel
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipmentModelDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentModel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEquipmentModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentModel.setId(longCount.incrementAndGet());

        // Create the EquipmentModel
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentModel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEquipmentModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentModel.setId(longCount.incrementAndGet());

        // Create the EquipmentModel
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EquipmentModel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEquipmentModelWithPatch() throws Exception {
        // Initialize the database
        insertedEquipmentModel = equipmentModelRepository.save(equipmentModel).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentModel using partial update
        EquipmentModel partialUpdatedEquipmentModel = new EquipmentModel();
        partialUpdatedEquipmentModel.setId(equipmentModel.getId());

        partialUpdatedEquipmentModel.revision(UPDATED_REVISION).nominalPowerW(UPDATED_NOMINAL_POWER_W);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipmentModel.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEquipmentModel))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EquipmentModel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentModelUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEquipmentModel, equipmentModel),
            getPersistedEquipmentModel(equipmentModel)
        );
    }

    @Test
    void fullUpdateEquipmentModelWithPatch() throws Exception {
        // Initialize the database
        insertedEquipmentModel = equipmentModelRepository.save(equipmentModel).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentModel using partial update
        EquipmentModel partialUpdatedEquipmentModel = new EquipmentModel();
        partialUpdatedEquipmentModel.setId(equipmentModel.getId());

        partialUpdatedEquipmentModel
            .manufacturer(UPDATED_MANUFACTURER)
            .modelCode(UPDATED_MODEL_CODE)
            .revision(UPDATED_REVISION)
            .kind(UPDATED_KIND)
            .nominalPowerW(UPDATED_NOMINAL_POWER_W);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipmentModel.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEquipmentModel))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EquipmentModel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentModelUpdatableFieldsEquals(partialUpdatedEquipmentModel, getPersistedEquipmentModel(partialUpdatedEquipmentModel));
    }

    @Test
    void patchNonExistingEquipmentModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentModel.setId(longCount.incrementAndGet());

        // Create the EquipmentModel
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, equipmentModelDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentModel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEquipmentModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentModel.setId(longCount.incrementAndGet());

        // Create the EquipmentModel
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentModel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEquipmentModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentModel.setId(longCount.incrementAndGet());

        // Create the EquipmentModel
        EquipmentModelDTO equipmentModelDTO = equipmentModelMapper.toDto(equipmentModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentModelDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EquipmentModel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEquipmentModel() {
        // Initialize the database
        insertedEquipmentModel = equipmentModelRepository.save(equipmentModel).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the equipmentModel
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, equipmentModel.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return equipmentModelRepository.count().block();
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

    protected EquipmentModel getPersistedEquipmentModel(EquipmentModel equipmentModel) {
        return equipmentModelRepository.findById(equipmentModel.getId()).block();
    }

    protected void assertPersistedEquipmentModelToMatchAllProperties(EquipmentModel expectedEquipmentModel) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEquipmentModelAllPropertiesEquals(expectedEquipmentModel, getPersistedEquipmentModel(expectedEquipmentModel));
        assertEquipmentModelUpdatableFieldsEquals(expectedEquipmentModel, getPersistedEquipmentModel(expectedEquipmentModel));
    }

    protected void assertPersistedEquipmentModelToMatchUpdatableProperties(EquipmentModel expectedEquipmentModel) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEquipmentModelAllUpdatablePropertiesEquals(expectedEquipmentModel, getPersistedEquipmentModel(expectedEquipmentModel));
        assertEquipmentModelUpdatableFieldsEquals(expectedEquipmentModel, getPersistedEquipmentModel(expectedEquipmentModel));
    }
}
