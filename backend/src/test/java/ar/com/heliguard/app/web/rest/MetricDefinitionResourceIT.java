package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.MetricDefinitionAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.MetricDefinition;
import ar.com.heliguard.app.repository.MetricDefinitionRepository;
import ar.com.heliguard.app.service.dto.MetricDefinitionDTO;
import ar.com.heliguard.app.service.mapper.MetricDefinitionMapper;
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
 * Integration tests for the {@link MetricDefinitionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MetricDefinitionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_UNIT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNIT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_COUNTER = false;
    private static final Boolean UPDATED_COUNTER = true;

    private static final String ENTITY_API_URL = "/api/metric-definitions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MetricDefinitionRepository metricDefinitionRepository;

    @Autowired
    private MetricDefinitionMapper metricDefinitionMapper;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private MetricDefinition metricDefinition;

    private MetricDefinition insertedMetricDefinition;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricDefinition createEntity() {
        return new MetricDefinition()
            .code(DEFAULT_CODE)
            .unitCode(DEFAULT_UNIT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .counter(DEFAULT_COUNTER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MetricDefinition createUpdatedEntity() {
        return new MetricDefinition()
            .code(UPDATED_CODE)
            .unitCode(UPDATED_UNIT_CODE)
            .description(UPDATED_DESCRIPTION)
            .counter(UPDATED_COUNTER);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(MetricDefinition.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        metricDefinition = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMetricDefinition != null) {
            metricDefinitionRepository.delete(insertedMetricDefinition).block();
            insertedMetricDefinition = null;
        }
        deleteEntities(template);
    }

    @Test
    void createMetricDefinition() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MetricDefinition
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);
        var returnedMetricDefinitionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MetricDefinitionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MetricDefinition in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMetricDefinition = metricDefinitionMapper.toEntity(returnedMetricDefinitionDTO);
        assertMetricDefinitionUpdatableFieldsEquals(returnedMetricDefinition, getPersistedMetricDefinition(returnedMetricDefinition));

        insertedMetricDefinition = returnedMetricDefinition;
    }

    @Test
    void createMetricDefinitionWithExistingId() throws Exception {
        // Create the MetricDefinition with an existing ID
        metricDefinition.setId(1L);
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MetricDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        metricDefinition.setCode(null);

        // Create the MetricDefinition, which fails.
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkUnitCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        metricDefinition.setUnitCode(null);

        // Create the MetricDefinition, which fails.
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCounterIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        metricDefinition.setCounter(null);

        // Create the MetricDefinition, which fails.
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMetricDefinitionsAsStream() {
        // Initialize the database
        metricDefinitionRepository.save(metricDefinition).block();

        List<MetricDefinition> metricDefinitionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(MetricDefinitionDTO.class)
            .getResponseBody()
            .map(metricDefinitionMapper::toEntity)
            .filter(metricDefinition::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(metricDefinitionList).isNotNull();
        assertThat(metricDefinitionList).hasSize(1);
        MetricDefinition testMetricDefinition = metricDefinitionList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertMetricDefinitionAllPropertiesEquals(metricDefinition, testMetricDefinition);
        assertMetricDefinitionUpdatableFieldsEquals(metricDefinition, testMetricDefinition);
    }

    @Test
    void getAllMetricDefinitions() {
        // Initialize the database
        insertedMetricDefinition = metricDefinitionRepository.save(metricDefinition).block();

        // Get all the metricDefinitionList
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
            .value(hasItem(metricDefinition.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].unitCode")
            .value(hasItem(DEFAULT_UNIT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].counter")
            .value(hasItem(DEFAULT_COUNTER));
    }

    @Test
    void getMetricDefinition() {
        // Initialize the database
        insertedMetricDefinition = metricDefinitionRepository.save(metricDefinition).block();

        // Get the metricDefinition
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, metricDefinition.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(metricDefinition.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.unitCode")
            .value(is(DEFAULT_UNIT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.counter")
            .value(is(DEFAULT_COUNTER));
    }

    @Test
    void getNonExistingMetricDefinition() {
        // Get the metricDefinition
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMetricDefinition() throws Exception {
        // Initialize the database
        insertedMetricDefinition = metricDefinitionRepository.save(metricDefinition).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricDefinition
        MetricDefinition updatedMetricDefinition = metricDefinitionRepository.findById(metricDefinition.getId()).block();
        updatedMetricDefinition.code(UPDATED_CODE).unitCode(UPDATED_UNIT_CODE).description(UPDATED_DESCRIPTION).counter(UPDATED_COUNTER);
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(updatedMetricDefinition);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, metricDefinitionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MetricDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMetricDefinitionToMatchAllProperties(updatedMetricDefinition);
    }

    @Test
    void putNonExistingMetricDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricDefinition.setId(longCount.incrementAndGet());

        // Create the MetricDefinition
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, metricDefinitionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MetricDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMetricDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricDefinition.setId(longCount.incrementAndGet());

        // Create the MetricDefinition
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MetricDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMetricDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricDefinition.setId(longCount.incrementAndGet());

        // Create the MetricDefinition
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MetricDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMetricDefinitionWithPatch() throws Exception {
        // Initialize the database
        insertedMetricDefinition = metricDefinitionRepository.save(metricDefinition).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricDefinition using partial update
        MetricDefinition partialUpdatedMetricDefinition = new MetricDefinition();
        partialUpdatedMetricDefinition.setId(metricDefinition.getId());

        partialUpdatedMetricDefinition.counter(UPDATED_COUNTER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMetricDefinition.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMetricDefinition))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MetricDefinition in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricDefinitionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMetricDefinition, metricDefinition),
            getPersistedMetricDefinition(metricDefinition)
        );
    }

    @Test
    void fullUpdateMetricDefinitionWithPatch() throws Exception {
        // Initialize the database
        insertedMetricDefinition = metricDefinitionRepository.save(metricDefinition).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the metricDefinition using partial update
        MetricDefinition partialUpdatedMetricDefinition = new MetricDefinition();
        partialUpdatedMetricDefinition.setId(metricDefinition.getId());

        partialUpdatedMetricDefinition
            .code(UPDATED_CODE)
            .unitCode(UPDATED_UNIT_CODE)
            .description(UPDATED_DESCRIPTION)
            .counter(UPDATED_COUNTER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMetricDefinition.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMetricDefinition))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MetricDefinition in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMetricDefinitionUpdatableFieldsEquals(
            partialUpdatedMetricDefinition,
            getPersistedMetricDefinition(partialUpdatedMetricDefinition)
        );
    }

    @Test
    void patchNonExistingMetricDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricDefinition.setId(longCount.incrementAndGet());

        // Create the MetricDefinition
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, metricDefinitionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MetricDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMetricDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricDefinition.setId(longCount.incrementAndGet());

        // Create the MetricDefinition
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MetricDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMetricDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        metricDefinition.setId(longCount.incrementAndGet());

        // Create the MetricDefinition
        MetricDefinitionDTO metricDefinitionDTO = metricDefinitionMapper.toDto(metricDefinition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(metricDefinitionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MetricDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMetricDefinition() {
        // Initialize the database
        insertedMetricDefinition = metricDefinitionRepository.save(metricDefinition).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the metricDefinition
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, metricDefinition.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return metricDefinitionRepository.count().block();
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

    protected MetricDefinition getPersistedMetricDefinition(MetricDefinition metricDefinition) {
        return metricDefinitionRepository.findById(metricDefinition.getId()).block();
    }

    protected void assertPersistedMetricDefinitionToMatchAllProperties(MetricDefinition expectedMetricDefinition) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMetricDefinitionAllPropertiesEquals(expectedMetricDefinition, getPersistedMetricDefinition(expectedMetricDefinition));
        assertMetricDefinitionUpdatableFieldsEquals(expectedMetricDefinition, getPersistedMetricDefinition(expectedMetricDefinition));
    }

    protected void assertPersistedMetricDefinitionToMatchUpdatableProperties(MetricDefinition expectedMetricDefinition) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMetricDefinitionAllUpdatablePropertiesEquals(expectedMetricDefinition, getPersistedMetricDefinition(expectedMetricDefinition));
        assertMetricDefinitionUpdatableFieldsEquals(expectedMetricDefinition, getPersistedMetricDefinition(expectedMetricDefinition));
    }
}
