package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.EquipmentConnectionAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.EquipmentConnection;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.TopologyConnectionKind;
import ar.com.heliguard.app.repository.EquipmentConnectionRepository;
import ar.com.heliguard.app.service.EquipmentConnectionService;
import ar.com.heliguard.app.service.dto.EquipmentConnectionDTO;
import ar.com.heliguard.app.service.mapper.EquipmentConnectionMapper;
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
 * Integration tests for the {@link EquipmentConnectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EquipmentConnectionResourceIT {

    private static final TopologyConnectionKind DEFAULT_KIND = TopologyConnectionKind.DC;
    private static final TopologyConnectionKind UPDATED_KIND = TopologyConnectionKind.AC;

    private static final String DEFAULT_CONNECTION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CONNECTION_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/equipment-connections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EquipmentConnectionRepository equipmentConnectionRepository;

    @Mock
    private EquipmentConnectionRepository equipmentConnectionRepositoryMock;

    @Autowired
    private EquipmentConnectionMapper equipmentConnectionMapper;

    @Mock
    private EquipmentConnectionService equipmentConnectionServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private EquipmentConnection equipmentConnection;

    private EquipmentConnection insertedEquipmentConnection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EquipmentConnection createEntity(R2dbcEntityTemplate template) {
        EquipmentConnection equipmentConnection = new EquipmentConnection()
            .kind(DEFAULT_KIND)
            .connectionCode(DEFAULT_CONNECTION_CODE)
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        equipmentConnection.setOrganization(organization);
        // Add required entity
        Equipment equipment;
        equipment = template.insert(EquipmentResourceIT.createEntity(template)).block();
        equipmentConnection.setFromEquipment(equipment);
        // Add required entity
        equipmentConnection.setToEquipment(equipment);
        return equipmentConnection;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EquipmentConnection createUpdatedEntity(R2dbcEntityTemplate template) {
        EquipmentConnection updatedEquipmentConnection = new EquipmentConnection()
            .kind(UPDATED_KIND)
            .connectionCode(UPDATED_CONNECTION_CODE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedEquipmentConnection.setOrganization(organization);
        // Add required entity
        Equipment equipment;
        equipment = template.insert(EquipmentResourceIT.createUpdatedEntity(template)).block();
        updatedEquipmentConnection.setFromEquipment(equipment);
        // Add required entity
        updatedEquipmentConnection.setToEquipment(equipment);
        return updatedEquipmentConnection;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(EquipmentConnection.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        EquipmentResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        equipmentConnection = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedEquipmentConnection != null) {
            equipmentConnectionRepository.delete(insertedEquipmentConnection).block();
            insertedEquipmentConnection = null;
        }
        deleteEntities(template);
    }

    @Test
    void createEquipmentConnection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EquipmentConnection
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);
        var returnedEquipmentConnectionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EquipmentConnectionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EquipmentConnection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEquipmentConnection = equipmentConnectionMapper.toEntity(returnedEquipmentConnectionDTO);
        assertEquipmentConnectionUpdatableFieldsEquals(
            returnedEquipmentConnection,
            getPersistedEquipmentConnection(returnedEquipmentConnection)
        );

        insertedEquipmentConnection = returnedEquipmentConnection;
    }

    @Test
    void createEquipmentConnectionWithExistingId() throws Exception {
        // Create the EquipmentConnection with an existing ID
        equipmentConnection.setId(1L);
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipmentConnection.setKind(null);

        // Create the EquipmentConnection, which fails.
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkConnectionCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipmentConnection.setConnectionCode(null);

        // Create the EquipmentConnection, which fails.
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipmentConnection.setValidFrom(null);

        // Create the EquipmentConnection, which fails.
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEquipmentConnectionsAsStream() {
        // Initialize the database
        equipmentConnectionRepository.save(equipmentConnection).block();

        List<EquipmentConnection> equipmentConnectionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(EquipmentConnectionDTO.class)
            .getResponseBody()
            .map(equipmentConnectionMapper::toEntity)
            .filter(equipmentConnection::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(equipmentConnectionList).isNotNull();
        assertThat(equipmentConnectionList).hasSize(1);
        EquipmentConnection testEquipmentConnection = equipmentConnectionList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertEquipmentConnectionAllPropertiesEquals(equipmentConnection, testEquipmentConnection);
        assertEquipmentConnectionUpdatableFieldsEquals(equipmentConnection, testEquipmentConnection);
    }

    @Test
    void getAllEquipmentConnections() {
        // Initialize the database
        insertedEquipmentConnection = equipmentConnectionRepository.save(equipmentConnection).block();

        // Get all the equipmentConnectionList
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
            .value(hasItem(equipmentConnection.getId().intValue()))
            .jsonPath("$.[*].kind")
            .value(hasItem(DEFAULT_KIND.toString()))
            .jsonPath("$.[*].connectionCode")
            .value(hasItem(DEFAULT_CONNECTION_CODE))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipmentConnectionsWithEagerRelationshipsIsEnabled() {
        when(equipmentConnectionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(equipmentConnectionServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipmentConnectionsWithEagerRelationshipsIsNotEnabled() {
        when(equipmentConnectionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(equipmentConnectionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEquipmentConnection() {
        // Initialize the database
        insertedEquipmentConnection = equipmentConnectionRepository.save(equipmentConnection).block();

        // Get the equipmentConnection
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, equipmentConnection.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(equipmentConnection.getId().intValue()))
            .jsonPath("$.kind")
            .value(is(DEFAULT_KIND.toString()))
            .jsonPath("$.connectionCode")
            .value(is(DEFAULT_CONNECTION_CODE))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()));
    }

    @Test
    void getNonExistingEquipmentConnection() {
        // Get the equipmentConnection
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEquipmentConnection() throws Exception {
        // Initialize the database
        insertedEquipmentConnection = equipmentConnectionRepository.save(equipmentConnection).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentConnection
        EquipmentConnection updatedEquipmentConnection = equipmentConnectionRepository.findById(equipmentConnection.getId()).block();
        updatedEquipmentConnection
            .kind(UPDATED_KIND)
            .connectionCode(UPDATED_CONNECTION_CODE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(updatedEquipmentConnection);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipmentConnectionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EquipmentConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEquipmentConnectionToMatchAllProperties(updatedEquipmentConnection);
    }

    @Test
    void putNonExistingEquipmentConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentConnection.setId(longCount.incrementAndGet());

        // Create the EquipmentConnection
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipmentConnectionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEquipmentConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentConnection.setId(longCount.incrementAndGet());

        // Create the EquipmentConnection
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEquipmentConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentConnection.setId(longCount.incrementAndGet());

        // Create the EquipmentConnection
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EquipmentConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEquipmentConnectionWithPatch() throws Exception {
        // Initialize the database
        insertedEquipmentConnection = equipmentConnectionRepository.save(equipmentConnection).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentConnection using partial update
        EquipmentConnection partialUpdatedEquipmentConnection = new EquipmentConnection();
        partialUpdatedEquipmentConnection.setId(equipmentConnection.getId());

        partialUpdatedEquipmentConnection
            .kind(UPDATED_KIND)
            .connectionCode(UPDATED_CONNECTION_CODE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipmentConnection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEquipmentConnection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EquipmentConnection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentConnectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEquipmentConnection, equipmentConnection),
            getPersistedEquipmentConnection(equipmentConnection)
        );
    }

    @Test
    void fullUpdateEquipmentConnectionWithPatch() throws Exception {
        // Initialize the database
        insertedEquipmentConnection = equipmentConnectionRepository.save(equipmentConnection).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentConnection using partial update
        EquipmentConnection partialUpdatedEquipmentConnection = new EquipmentConnection();
        partialUpdatedEquipmentConnection.setId(equipmentConnection.getId());

        partialUpdatedEquipmentConnection
            .kind(UPDATED_KIND)
            .connectionCode(UPDATED_CONNECTION_CODE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipmentConnection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEquipmentConnection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EquipmentConnection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentConnectionUpdatableFieldsEquals(
            partialUpdatedEquipmentConnection,
            getPersistedEquipmentConnection(partialUpdatedEquipmentConnection)
        );
    }

    @Test
    void patchNonExistingEquipmentConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentConnection.setId(longCount.incrementAndGet());

        // Create the EquipmentConnection
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, equipmentConnectionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEquipmentConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentConnection.setId(longCount.incrementAndGet());

        // Create the EquipmentConnection
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EquipmentConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEquipmentConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentConnection.setId(longCount.incrementAndGet());

        // Create the EquipmentConnection
        EquipmentConnectionDTO equipmentConnectionDTO = equipmentConnectionMapper.toDto(equipmentConnection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentConnectionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EquipmentConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEquipmentConnection() {
        // Initialize the database
        insertedEquipmentConnection = equipmentConnectionRepository.save(equipmentConnection).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the equipmentConnection
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, equipmentConnection.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return equipmentConnectionRepository.count().block();
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

    protected EquipmentConnection getPersistedEquipmentConnection(EquipmentConnection equipmentConnection) {
        return equipmentConnectionRepository.findById(equipmentConnection.getId()).block();
    }

    protected void assertPersistedEquipmentConnectionToMatchAllProperties(EquipmentConnection expectedEquipmentConnection) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEquipmentConnectionAllPropertiesEquals(expectedEquipmentConnection, getPersistedEquipmentConnection(expectedEquipmentConnection));
        assertEquipmentConnectionUpdatableFieldsEquals(
            expectedEquipmentConnection,
            getPersistedEquipmentConnection(expectedEquipmentConnection)
        );
    }

    protected void assertPersistedEquipmentConnectionToMatchUpdatableProperties(EquipmentConnection expectedEquipmentConnection) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEquipmentConnectionAllUpdatablePropertiesEquals(expectedEquipmentConnection, getPersistedEquipmentConnection(expectedEquipmentConnection));
        assertEquipmentConnectionUpdatableFieldsEquals(
            expectedEquipmentConnection,
            getPersistedEquipmentConnection(expectedEquipmentConnection)
        );
    }
}
