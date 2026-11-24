package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.TelemetryChannelAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.MetricDefinition;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import ar.com.heliguard.app.repository.TelemetryChannelRepository;
import ar.com.heliguard.app.service.TelemetryChannelService;
import ar.com.heliguard.app.service.dto.TelemetryChannelDTO;
import ar.com.heliguard.app.service.mapper.TelemetryChannelMapper;
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
 * Integration tests for the {@link TelemetryChannelResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TelemetryChannelResourceIT {

    private static final String DEFAULT_EXTERNAL_CHANNEL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_CHANNEL_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final Integer DEFAULT_SAMPLE_PERIOD_SECONDS = 1;
    private static final Integer UPDATED_SAMPLE_PERIOD_SECONDS = 2;

    private static final String ENTITY_API_URL = "/api/telemetry-channels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TelemetryChannelRepository telemetryChannelRepository;

    @Mock
    private TelemetryChannelRepository telemetryChannelRepositoryMock;

    @Autowired
    private TelemetryChannelMapper telemetryChannelMapper;

    @Mock
    private TelemetryChannelService telemetryChannelServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private TelemetryChannel telemetryChannel;

    private TelemetryChannel insertedTelemetryChannel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelemetryChannel createEntity(R2dbcEntityTemplate template) {
        TelemetryChannel telemetryChannel = new TelemetryChannel()
            .externalChannelCode(DEFAULT_EXTERNAL_CHANNEL_CODE)
            .enabled(DEFAULT_ENABLED)
            .samplePeriodSeconds(DEFAULT_SAMPLE_PERIOD_SECONDS);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        telemetryChannel.setOrganization(organization);
        // Add required entity
        Equipment equipment;
        equipment = template.insert(EquipmentResourceIT.createEntity(template)).block();
        telemetryChannel.setEquipment(equipment);
        // Add required entity
        MetricDefinition metricDefinition;
        metricDefinition = template.insert(MetricDefinitionResourceIT.createEntity()).block();
        telemetryChannel.setMetricDefinition(metricDefinition);
        return telemetryChannel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelemetryChannel createUpdatedEntity(R2dbcEntityTemplate template) {
        TelemetryChannel updatedTelemetryChannel = new TelemetryChannel()
            .externalChannelCode(UPDATED_EXTERNAL_CHANNEL_CODE)
            .enabled(UPDATED_ENABLED)
            .samplePeriodSeconds(UPDATED_SAMPLE_PERIOD_SECONDS);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedTelemetryChannel.setOrganization(organization);
        // Add required entity
        Equipment equipment;
        equipment = template.insert(EquipmentResourceIT.createUpdatedEntity(template)).block();
        updatedTelemetryChannel.setEquipment(equipment);
        // Add required entity
        MetricDefinition metricDefinition;
        metricDefinition = template.insert(MetricDefinitionResourceIT.createUpdatedEntity()).block();
        updatedTelemetryChannel.setMetricDefinition(metricDefinition);
        return updatedTelemetryChannel;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(TelemetryChannel.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        EquipmentResourceIT.deleteEntities(template);
        MetricDefinitionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        telemetryChannel = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedTelemetryChannel != null) {
            telemetryChannelRepository.delete(insertedTelemetryChannel).block();
            insertedTelemetryChannel = null;
        }
        deleteEntities(template);
    }

    @Test
    void createTelemetryChannel() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TelemetryChannel
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);
        var returnedTelemetryChannelDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TelemetryChannelDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TelemetryChannel in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTelemetryChannel = telemetryChannelMapper.toEntity(returnedTelemetryChannelDTO);
        assertTelemetryChannelUpdatableFieldsEquals(returnedTelemetryChannel, getPersistedTelemetryChannel(returnedTelemetryChannel));

        insertedTelemetryChannel = returnedTelemetryChannel;
    }

    @Test
    void createTelemetryChannelWithExistingId() throws Exception {
        // Create the TelemetryChannel with an existing ID
        telemetryChannel.setId(1L);
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannel in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkExternalChannelCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        telemetryChannel.setExternalChannelCode(null);

        // Create the TelemetryChannel, which fails.
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        telemetryChannel.setEnabled(null);

        // Create the TelemetryChannel, which fails.
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTelemetryChannelsAsStream() {
        // Initialize the database
        telemetryChannelRepository.save(telemetryChannel).block();

        List<TelemetryChannel> telemetryChannelList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TelemetryChannelDTO.class)
            .getResponseBody()
            .map(telemetryChannelMapper::toEntity)
            .filter(telemetryChannel::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(telemetryChannelList).isNotNull();
        assertThat(telemetryChannelList).hasSize(1);
        TelemetryChannel testTelemetryChannel = telemetryChannelList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertTelemetryChannelAllPropertiesEquals(telemetryChannel, testTelemetryChannel);
        assertTelemetryChannelUpdatableFieldsEquals(telemetryChannel, testTelemetryChannel);
    }

    @Test
    void getAllTelemetryChannels() {
        // Initialize the database
        insertedTelemetryChannel = telemetryChannelRepository.save(telemetryChannel).block();

        // Get all the telemetryChannelList
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
            .value(hasItem(telemetryChannel.getId().intValue()))
            .jsonPath("$.[*].externalChannelCode")
            .value(hasItem(DEFAULT_EXTERNAL_CHANNEL_CODE))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED))
            .jsonPath("$.[*].samplePeriodSeconds")
            .value(hasItem(DEFAULT_SAMPLE_PERIOD_SECONDS));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTelemetryChannelsWithEagerRelationshipsIsEnabled() {
        when(telemetryChannelServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(telemetryChannelServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTelemetryChannelsWithEagerRelationshipsIsNotEnabled() {
        when(telemetryChannelServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(telemetryChannelRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTelemetryChannel() {
        // Initialize the database
        insertedTelemetryChannel = telemetryChannelRepository.save(telemetryChannel).block();

        // Get the telemetryChannel
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, telemetryChannel.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(telemetryChannel.getId().intValue()))
            .jsonPath("$.externalChannelCode")
            .value(is(DEFAULT_EXTERNAL_CHANNEL_CODE))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED))
            .jsonPath("$.samplePeriodSeconds")
            .value(is(DEFAULT_SAMPLE_PERIOD_SECONDS));
    }

    @Test
    void getNonExistingTelemetryChannel() {
        // Get the telemetryChannel
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTelemetryChannel() throws Exception {
        // Initialize the database
        insertedTelemetryChannel = telemetryChannelRepository.save(telemetryChannel).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryChannel
        TelemetryChannel updatedTelemetryChannel = telemetryChannelRepository.findById(telemetryChannel.getId()).block();
        updatedTelemetryChannel
            .externalChannelCode(UPDATED_EXTERNAL_CHANNEL_CODE)
            .enabled(UPDATED_ENABLED)
            .samplePeriodSeconds(UPDATED_SAMPLE_PERIOD_SECONDS);
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(updatedTelemetryChannel);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, telemetryChannelDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryChannel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTelemetryChannelToMatchAllProperties(updatedTelemetryChannel);
    }

    @Test
    void putNonExistingTelemetryChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannel.setId(longCount.incrementAndGet());

        // Create the TelemetryChannel
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, telemetryChannelDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTelemetryChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannel.setId(longCount.incrementAndGet());

        // Create the TelemetryChannel
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTelemetryChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannel.setId(longCount.incrementAndGet());

        // Create the TelemetryChannel
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TelemetryChannel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTelemetryChannelWithPatch() throws Exception {
        // Initialize the database
        insertedTelemetryChannel = telemetryChannelRepository.save(telemetryChannel).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryChannel using partial update
        TelemetryChannel partialUpdatedTelemetryChannel = new TelemetryChannel();
        partialUpdatedTelemetryChannel.setId(telemetryChannel.getId());

        partialUpdatedTelemetryChannel.enabled(UPDATED_ENABLED).samplePeriodSeconds(UPDATED_SAMPLE_PERIOD_SECONDS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTelemetryChannel.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTelemetryChannel))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryChannel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTelemetryChannelUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTelemetryChannel, telemetryChannel),
            getPersistedTelemetryChannel(telemetryChannel)
        );
    }

    @Test
    void fullUpdateTelemetryChannelWithPatch() throws Exception {
        // Initialize the database
        insertedTelemetryChannel = telemetryChannelRepository.save(telemetryChannel).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryChannel using partial update
        TelemetryChannel partialUpdatedTelemetryChannel = new TelemetryChannel();
        partialUpdatedTelemetryChannel.setId(telemetryChannel.getId());

        partialUpdatedTelemetryChannel
            .externalChannelCode(UPDATED_EXTERNAL_CHANNEL_CODE)
            .enabled(UPDATED_ENABLED)
            .samplePeriodSeconds(UPDATED_SAMPLE_PERIOD_SECONDS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTelemetryChannel.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTelemetryChannel))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryChannel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTelemetryChannelUpdatableFieldsEquals(
            partialUpdatedTelemetryChannel,
            getPersistedTelemetryChannel(partialUpdatedTelemetryChannel)
        );
    }

    @Test
    void patchNonExistingTelemetryChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannel.setId(longCount.incrementAndGet());

        // Create the TelemetryChannel
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, telemetryChannelDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTelemetryChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannel.setId(longCount.incrementAndGet());

        // Create the TelemetryChannel
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTelemetryChannel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannel.setId(longCount.incrementAndGet());

        // Create the TelemetryChannel
        TelemetryChannelDTO telemetryChannelDTO = telemetryChannelMapper.toDto(telemetryChannel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryChannelDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TelemetryChannel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTelemetryChannel() {
        // Initialize the database
        insertedTelemetryChannel = telemetryChannelRepository.save(telemetryChannel).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the telemetryChannel
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, telemetryChannel.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return telemetryChannelRepository.count().block();
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

    protected TelemetryChannel getPersistedTelemetryChannel(TelemetryChannel telemetryChannel) {
        return telemetryChannelRepository.findById(telemetryChannel.getId()).block();
    }

    protected void assertPersistedTelemetryChannelToMatchAllProperties(TelemetryChannel expectedTelemetryChannel) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTelemetryChannelAllPropertiesEquals(expectedTelemetryChannel, getPersistedTelemetryChannel(expectedTelemetryChannel));
        assertTelemetryChannelUpdatableFieldsEquals(expectedTelemetryChannel, getPersistedTelemetryChannel(expectedTelemetryChannel));
    }

    protected void assertPersistedTelemetryChannelToMatchUpdatableProperties(TelemetryChannel expectedTelemetryChannel) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTelemetryChannelAllUpdatablePropertiesEquals(expectedTelemetryChannel, getPersistedTelemetryChannel(expectedTelemetryChannel));
        assertTelemetryChannelUpdatableFieldsEquals(expectedTelemetryChannel, getPersistedTelemetryChannel(expectedTelemetryChannel));
    }
}
