package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.GatewayAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.GatewayStatus;
import ar.com.heliguard.app.repository.GatewayRepository;
import ar.com.heliguard.app.service.GatewayService;
import ar.com.heliguard.app.service.dto.GatewayDTO;
import ar.com.heliguard.app.service.mapper.GatewayMapper;
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
 * Integration tests for the {@link GatewayResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class GatewayResourceIT {

    private static final String DEFAULT_HARDWARE_ID = "AAAAAAAAAA";
    private static final String UPDATED_HARDWARE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_FIRMWARE_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_FIRMWARE_VERSION = "BBBBBBBBBB";

    private static final GatewayStatus DEFAULT_STATUS = GatewayStatus.PROVISIONING;
    private static final GatewayStatus UPDATED_STATUS = GatewayStatus.ONLINE;

    private static final Instant DEFAULT_LAST_SEEN_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_SEEN_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_PROVISIONED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PROVISIONED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/gateways";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GatewayRepository gatewayRepository;

    @Mock
    private GatewayRepository gatewayRepositoryMock;

    @Autowired
    private GatewayMapper gatewayMapper;

    @Mock
    private GatewayService gatewayServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Gateway gateway;

    private Gateway insertedGateway;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gateway createEntity(R2dbcEntityTemplate template) {
        Gateway gateway = new Gateway()
            .hardwareId(DEFAULT_HARDWARE_ID)
            .firmwareVersion(DEFAULT_FIRMWARE_VERSION)
            .status(DEFAULT_STATUS)
            .lastSeenAt(DEFAULT_LAST_SEEN_AT)
            .provisionedAt(DEFAULT_PROVISIONED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        gateway.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        gateway.setInstallation(installation);
        return gateway;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gateway createUpdatedEntity(R2dbcEntityTemplate template) {
        Gateway updatedGateway = new Gateway()
            .hardwareId(UPDATED_HARDWARE_ID)
            .firmwareVersion(UPDATED_FIRMWARE_VERSION)
            .status(UPDATED_STATUS)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .provisionedAt(UPDATED_PROVISIONED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedGateway.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedGateway.setInstallation(installation);
        return updatedGateway;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Gateway.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        gateway = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedGateway != null) {
            gatewayRepository.delete(insertedGateway).block();
            insertedGateway = null;
        }
        deleteEntities(template);
    }

    @Test
    void createGateway() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Gateway
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);
        var returnedGatewayDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(GatewayDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Gateway in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGateway = gatewayMapper.toEntity(returnedGatewayDTO);
        assertGatewayUpdatableFieldsEquals(returnedGateway, getPersistedGateway(returnedGateway));

        insertedGateway = returnedGateway;
    }

    @Test
    void createGatewayWithExistingId() throws Exception {
        // Create the Gateway with an existing ID
        gateway.setId(1L);
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gateway in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkHardwareIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gateway.setHardwareId(null);

        // Create the Gateway, which fails.
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gateway.setStatus(null);

        // Create the Gateway, which fails.
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllGateways() {
        // Initialize the database
        insertedGateway = gatewayRepository.save(gateway).block();

        // Get all the gatewayList
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
            .value(hasItem(gateway.getId().intValue()))
            .jsonPath("$.[*].hardwareId")
            .value(hasItem(DEFAULT_HARDWARE_ID))
            .jsonPath("$.[*].firmwareVersion")
            .value(hasItem(DEFAULT_FIRMWARE_VERSION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].lastSeenAt")
            .value(hasItem(DEFAULT_LAST_SEEN_AT.toString()))
            .jsonPath("$.[*].provisionedAt")
            .value(hasItem(DEFAULT_PROVISIONED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGatewaysWithEagerRelationshipsIsEnabled() {
        when(gatewayServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(gatewayServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGatewaysWithEagerRelationshipsIsNotEnabled() {
        when(gatewayServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(gatewayRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getGateway() {
        // Initialize the database
        insertedGateway = gatewayRepository.save(gateway).block();

        // Get the gateway
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, gateway.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(gateway.getId().intValue()))
            .jsonPath("$.hardwareId")
            .value(is(DEFAULT_HARDWARE_ID))
            .jsonPath("$.firmwareVersion")
            .value(is(DEFAULT_FIRMWARE_VERSION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.lastSeenAt")
            .value(is(DEFAULT_LAST_SEEN_AT.toString()))
            .jsonPath("$.provisionedAt")
            .value(is(DEFAULT_PROVISIONED_AT.toString()));
    }

    @Test
    void getNonExistingGateway() {
        // Get the gateway
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingGateway() throws Exception {
        // Initialize the database
        insertedGateway = gatewayRepository.save(gateway).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gateway
        Gateway updatedGateway = gatewayRepository.findById(gateway.getId()).block();
        updatedGateway
            .hardwareId(UPDATED_HARDWARE_ID)
            .firmwareVersion(UPDATED_FIRMWARE_VERSION)
            .status(UPDATED_STATUS)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .provisionedAt(UPDATED_PROVISIONED_AT);
        GatewayDTO gatewayDTO = gatewayMapper.toDto(updatedGateway);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, gatewayDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Gateway in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGatewayToMatchAllProperties(updatedGateway);
    }

    @Test
    void putNonExistingGateway() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gateway.setId(longCount.incrementAndGet());

        // Create the Gateway
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, gatewayDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gateway in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchGateway() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gateway.setId(longCount.incrementAndGet());

        // Create the Gateway
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gateway in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamGateway() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gateway.setId(longCount.incrementAndGet());

        // Create the Gateway
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Gateway in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateGatewayWithPatch() throws Exception {
        // Initialize the database
        insertedGateway = gatewayRepository.save(gateway).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gateway using partial update
        Gateway partialUpdatedGateway = new Gateway();
        partialUpdatedGateway.setId(gateway.getId());

        partialUpdatedGateway.firmwareVersion(UPDATED_FIRMWARE_VERSION).provisionedAt(UPDATED_PROVISIONED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGateway.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedGateway))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Gateway in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGatewayUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGateway, gateway), getPersistedGateway(gateway));
    }

    @Test
    void fullUpdateGatewayWithPatch() throws Exception {
        // Initialize the database
        insertedGateway = gatewayRepository.save(gateway).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gateway using partial update
        Gateway partialUpdatedGateway = new Gateway();
        partialUpdatedGateway.setId(gateway.getId());

        partialUpdatedGateway
            .hardwareId(UPDATED_HARDWARE_ID)
            .firmwareVersion(UPDATED_FIRMWARE_VERSION)
            .status(UPDATED_STATUS)
            .lastSeenAt(UPDATED_LAST_SEEN_AT)
            .provisionedAt(UPDATED_PROVISIONED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGateway.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedGateway))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Gateway in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGatewayUpdatableFieldsEquals(partialUpdatedGateway, getPersistedGateway(partialUpdatedGateway));
    }

    @Test
    void patchNonExistingGateway() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gateway.setId(longCount.incrementAndGet());

        // Create the Gateway
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, gatewayDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gateway in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchGateway() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gateway.setId(longCount.incrementAndGet());

        // Create the Gateway
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gateway in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamGateway() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gateway.setId(longCount.incrementAndGet());

        // Create the Gateway
        GatewayDTO gatewayDTO = gatewayMapper.toDto(gateway);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(gatewayDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Gateway in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteGateway() {
        // Initialize the database
        insertedGateway = gatewayRepository.save(gateway).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the gateway
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, gateway.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return gatewayRepository.count().block();
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

    protected Gateway getPersistedGateway(Gateway gateway) {
        return gatewayRepository.findById(gateway.getId()).block();
    }

    protected void assertPersistedGatewayToMatchAllProperties(Gateway expectedGateway) {
        // Test fails because reactive api returns an empty object instead of null
        // assertGatewayAllPropertiesEquals(expectedGateway, getPersistedGateway(expectedGateway));
        assertGatewayUpdatableFieldsEquals(expectedGateway, getPersistedGateway(expectedGateway));
    }

    protected void assertPersistedGatewayToMatchUpdatableProperties(Gateway expectedGateway) {
        // Test fails because reactive api returns an empty object instead of null
        // assertGatewayAllUpdatablePropertiesEquals(expectedGateway, getPersistedGateway(expectedGateway));
        assertGatewayUpdatableFieldsEquals(expectedGateway, getPersistedGateway(expectedGateway));
    }
}
