package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.DeviceIdentityAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DeviceIdentity;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.DeviceTrustStatus;
import ar.com.heliguard.app.repository.DeviceIdentityRepository;
import ar.com.heliguard.app.service.DeviceIdentityService;
import ar.com.heliguard.app.service.dto.DeviceIdentityDTO;
import ar.com.heliguard.app.service.mapper.DeviceIdentityMapper;
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
 * Integration tests for the {@link DeviceIdentityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DeviceIdentityResourceIT {

    private static final String DEFAULT_DEVICE_UID = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_UID = "BBBBBBBBBB";

    private static final String DEFAULT_KEY_FINGERPRINT = "AAAAAAAAAA";
    private static final String UPDATED_KEY_FINGERPRINT = "BBBBBBBBBB";

    private static final String DEFAULT_PROTOCOL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PROTOCOL_NAME = "BBBBBBBBBB";

    private static final DeviceTrustStatus DEFAULT_TRUST_STATUS = DeviceTrustStatus.PROVISIONING;
    private static final DeviceTrustStatus UPDATED_TRUST_STATUS = DeviceTrustStatus.ACTIVE;

    private static final Instant DEFAULT_KEY_ROTATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_KEY_ROTATED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/device-identities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeviceIdentityRepository deviceIdentityRepository;

    @Mock
    private DeviceIdentityRepository deviceIdentityRepositoryMock;

    @Autowired
    private DeviceIdentityMapper deviceIdentityMapper;

    @Mock
    private DeviceIdentityService deviceIdentityServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private DeviceIdentity deviceIdentity;

    private DeviceIdentity insertedDeviceIdentity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeviceIdentity createEntity(R2dbcEntityTemplate template) {
        DeviceIdentity deviceIdentity = new DeviceIdentity()
            .deviceUid(DEFAULT_DEVICE_UID)
            .keyFingerprint(DEFAULT_KEY_FINGERPRINT)
            .protocolName(DEFAULT_PROTOCOL_NAME)
            .trustStatus(DEFAULT_TRUST_STATUS)
            .keyRotatedAt(DEFAULT_KEY_ROTATED_AT)
            .validUntil(DEFAULT_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        deviceIdentity.setOrganization(organization);
        return deviceIdentity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeviceIdentity createUpdatedEntity(R2dbcEntityTemplate template) {
        DeviceIdentity updatedDeviceIdentity = new DeviceIdentity()
            .deviceUid(UPDATED_DEVICE_UID)
            .keyFingerprint(UPDATED_KEY_FINGERPRINT)
            .protocolName(UPDATED_PROTOCOL_NAME)
            .trustStatus(UPDATED_TRUST_STATUS)
            .keyRotatedAt(UPDATED_KEY_ROTATED_AT)
            .validUntil(UPDATED_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedDeviceIdentity.setOrganization(organization);
        return updatedDeviceIdentity;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(DeviceIdentity.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        deviceIdentity = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedDeviceIdentity != null) {
            deviceIdentityRepository.delete(insertedDeviceIdentity).block();
            insertedDeviceIdentity = null;
        }
        deleteEntities(template);
    }

    @Test
    void createDeviceIdentity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DeviceIdentity
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);
        var returnedDeviceIdentityDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DeviceIdentityDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the DeviceIdentity in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDeviceIdentity = deviceIdentityMapper.toEntity(returnedDeviceIdentityDTO);
        assertDeviceIdentityUpdatableFieldsEquals(returnedDeviceIdentity, getPersistedDeviceIdentity(returnedDeviceIdentity));

        insertedDeviceIdentity = returnedDeviceIdentity;
    }

    @Test
    void createDeviceIdentityWithExistingId() throws Exception {
        // Create the DeviceIdentity with an existing ID
        deviceIdentity.setId(1L);
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DeviceIdentity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDeviceUidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        deviceIdentity.setDeviceUid(null);

        // Create the DeviceIdentity, which fails.
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkProtocolNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        deviceIdentity.setProtocolName(null);

        // Create the DeviceIdentity, which fails.
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTrustStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        deviceIdentity.setTrustStatus(null);

        // Create the DeviceIdentity, which fails.
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDeviceIdentitiesAsStream() {
        // Initialize the database
        deviceIdentityRepository.save(deviceIdentity).block();

        List<DeviceIdentity> deviceIdentityList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(DeviceIdentityDTO.class)
            .getResponseBody()
            .map(deviceIdentityMapper::toEntity)
            .filter(deviceIdentity::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(deviceIdentityList).isNotNull();
        assertThat(deviceIdentityList).hasSize(1);
        DeviceIdentity testDeviceIdentity = deviceIdentityList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertDeviceIdentityAllPropertiesEquals(deviceIdentity, testDeviceIdentity);
        assertDeviceIdentityUpdatableFieldsEquals(deviceIdentity, testDeviceIdentity);
    }

    @Test
    void getAllDeviceIdentities() {
        // Initialize the database
        insertedDeviceIdentity = deviceIdentityRepository.save(deviceIdentity).block();

        // Get all the deviceIdentityList
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
            .value(hasItem(deviceIdentity.getId().intValue()))
            .jsonPath("$.[*].deviceUid")
            .value(hasItem(DEFAULT_DEVICE_UID))
            .jsonPath("$.[*].keyFingerprint")
            .value(hasItem(DEFAULT_KEY_FINGERPRINT))
            .jsonPath("$.[*].protocolName")
            .value(hasItem(DEFAULT_PROTOCOL_NAME))
            .jsonPath("$.[*].trustStatus")
            .value(hasItem(DEFAULT_TRUST_STATUS.toString()))
            .jsonPath("$.[*].keyRotatedAt")
            .value(hasItem(DEFAULT_KEY_ROTATED_AT.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeviceIdentitiesWithEagerRelationshipsIsEnabled() {
        when(deviceIdentityServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(deviceIdentityServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeviceIdentitiesWithEagerRelationshipsIsNotEnabled() {
        when(deviceIdentityServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(deviceIdentityRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDeviceIdentity() {
        // Initialize the database
        insertedDeviceIdentity = deviceIdentityRepository.save(deviceIdentity).block();

        // Get the deviceIdentity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, deviceIdentity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(deviceIdentity.getId().intValue()))
            .jsonPath("$.deviceUid")
            .value(is(DEFAULT_DEVICE_UID))
            .jsonPath("$.keyFingerprint")
            .value(is(DEFAULT_KEY_FINGERPRINT))
            .jsonPath("$.protocolName")
            .value(is(DEFAULT_PROTOCOL_NAME))
            .jsonPath("$.trustStatus")
            .value(is(DEFAULT_TRUST_STATUS.toString()))
            .jsonPath("$.keyRotatedAt")
            .value(is(DEFAULT_KEY_ROTATED_AT.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()));
    }

    @Test
    void getNonExistingDeviceIdentity() {
        // Get the deviceIdentity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDeviceIdentity() throws Exception {
        // Initialize the database
        insertedDeviceIdentity = deviceIdentityRepository.save(deviceIdentity).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deviceIdentity
        DeviceIdentity updatedDeviceIdentity = deviceIdentityRepository.findById(deviceIdentity.getId()).block();
        updatedDeviceIdentity
            .deviceUid(UPDATED_DEVICE_UID)
            .keyFingerprint(UPDATED_KEY_FINGERPRINT)
            .protocolName(UPDATED_PROTOCOL_NAME)
            .trustStatus(UPDATED_TRUST_STATUS)
            .keyRotatedAt(UPDATED_KEY_ROTATED_AT)
            .validUntil(UPDATED_VALID_UNTIL);
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(updatedDeviceIdentity);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, deviceIdentityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DeviceIdentity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeviceIdentityToMatchAllProperties(updatedDeviceIdentity);
    }

    @Test
    void putNonExistingDeviceIdentity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deviceIdentity.setId(longCount.incrementAndGet());

        // Create the DeviceIdentity
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, deviceIdentityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DeviceIdentity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDeviceIdentity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deviceIdentity.setId(longCount.incrementAndGet());

        // Create the DeviceIdentity
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DeviceIdentity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDeviceIdentity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deviceIdentity.setId(longCount.incrementAndGet());

        // Create the DeviceIdentity
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DeviceIdentity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDeviceIdentityWithPatch() throws Exception {
        // Initialize the database
        insertedDeviceIdentity = deviceIdentityRepository.save(deviceIdentity).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deviceIdentity using partial update
        DeviceIdentity partialUpdatedDeviceIdentity = new DeviceIdentity();
        partialUpdatedDeviceIdentity.setId(deviceIdentity.getId());

        partialUpdatedDeviceIdentity.keyFingerprint(UPDATED_KEY_FINGERPRINT).trustStatus(UPDATED_TRUST_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDeviceIdentity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDeviceIdentity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DeviceIdentity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeviceIdentityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDeviceIdentity, deviceIdentity),
            getPersistedDeviceIdentity(deviceIdentity)
        );
    }

    @Test
    void fullUpdateDeviceIdentityWithPatch() throws Exception {
        // Initialize the database
        insertedDeviceIdentity = deviceIdentityRepository.save(deviceIdentity).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deviceIdentity using partial update
        DeviceIdentity partialUpdatedDeviceIdentity = new DeviceIdentity();
        partialUpdatedDeviceIdentity.setId(deviceIdentity.getId());

        partialUpdatedDeviceIdentity
            .deviceUid(UPDATED_DEVICE_UID)
            .keyFingerprint(UPDATED_KEY_FINGERPRINT)
            .protocolName(UPDATED_PROTOCOL_NAME)
            .trustStatus(UPDATED_TRUST_STATUS)
            .keyRotatedAt(UPDATED_KEY_ROTATED_AT)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDeviceIdentity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDeviceIdentity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DeviceIdentity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeviceIdentityUpdatableFieldsEquals(partialUpdatedDeviceIdentity, getPersistedDeviceIdentity(partialUpdatedDeviceIdentity));
    }

    @Test
    void patchNonExistingDeviceIdentity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deviceIdentity.setId(longCount.incrementAndGet());

        // Create the DeviceIdentity
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, deviceIdentityDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DeviceIdentity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDeviceIdentity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deviceIdentity.setId(longCount.incrementAndGet());

        // Create the DeviceIdentity
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DeviceIdentity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDeviceIdentity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deviceIdentity.setId(longCount.incrementAndGet());

        // Create the DeviceIdentity
        DeviceIdentityDTO deviceIdentityDTO = deviceIdentityMapper.toDto(deviceIdentity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(deviceIdentityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DeviceIdentity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDeviceIdentity() {
        // Initialize the database
        insertedDeviceIdentity = deviceIdentityRepository.save(deviceIdentity).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the deviceIdentity
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, deviceIdentity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deviceIdentityRepository.count().block();
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

    protected DeviceIdentity getPersistedDeviceIdentity(DeviceIdentity deviceIdentity) {
        return deviceIdentityRepository.findById(deviceIdentity.getId()).block();
    }

    protected void assertPersistedDeviceIdentityToMatchAllProperties(DeviceIdentity expectedDeviceIdentity) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDeviceIdentityAllPropertiesEquals(expectedDeviceIdentity, getPersistedDeviceIdentity(expectedDeviceIdentity));
        assertDeviceIdentityUpdatableFieldsEquals(expectedDeviceIdentity, getPersistedDeviceIdentity(expectedDeviceIdentity));
    }

    protected void assertPersistedDeviceIdentityToMatchUpdatableProperties(DeviceIdentity expectedDeviceIdentity) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDeviceIdentityAllUpdatablePropertiesEquals(expectedDeviceIdentity, getPersistedDeviceIdentity(expectedDeviceIdentity));
        assertDeviceIdentityUpdatableFieldsEquals(expectedDeviceIdentity, getPersistedDeviceIdentity(expectedDeviceIdentity));
    }
}
