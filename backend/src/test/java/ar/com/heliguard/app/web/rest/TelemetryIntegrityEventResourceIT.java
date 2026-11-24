package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.TelemetryIntegrityEventAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import ar.com.heliguard.app.repository.TelemetryIntegrityEventRepository;
import ar.com.heliguard.app.service.TelemetryIntegrityEventService;
import ar.com.heliguard.app.service.dto.TelemetryIntegrityEventDTO;
import ar.com.heliguard.app.service.mapper.TelemetryIntegrityEventMapper;
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
 * Integration tests for the {@link TelemetryIntegrityEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TelemetryIntegrityEventResourceIT {

    private static final String DEFAULT_EVENT_UID = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_UID = "BBBBBBBBBB";

    private static final IntegrityEventType DEFAULT_EVENT_TYPE = IntegrityEventType.REPLAY;
    private static final IntegrityEventType UPDATED_EVENT_TYPE = IntegrityEventType.DUPLICATE_EVENT;

    private static final IntegrityEventStatus DEFAULT_STATUS = IntegrityEventStatus.OBSERVED;
    private static final IntegrityEventStatus UPDATED_STATUS = IntegrityEventStatus.QUARANTINED;

    private static final Instant DEFAULT_OBSERVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OBSERVED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_SOURCE_OBSERVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SOURCE_OBSERVED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_BOOT_ID = "AAAAAAAAAA";
    private static final String UPDATED_BOOT_ID = "BBBBBBBBBB";

    private static final Long DEFAULT_SEQUENCE_NUMBER = 1L;
    private static final Long UPDATED_SEQUENCE_NUMBER = 2L;

    private static final String DEFAULT_PAYLOAD_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD_SHA_256 = "BBBBBBBBBB";

    private static final String DEFAULT_DETAIL = "AAAAAAAAAA";
    private static final String UPDATED_DETAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/telemetry-integrity-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TelemetryIntegrityEventRepository telemetryIntegrityEventRepository;

    @Mock
    private TelemetryIntegrityEventRepository telemetryIntegrityEventRepositoryMock;

    @Autowired
    private TelemetryIntegrityEventMapper telemetryIntegrityEventMapper;

    @Mock
    private TelemetryIntegrityEventService telemetryIntegrityEventServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private TelemetryIntegrityEvent telemetryIntegrityEvent;

    private TelemetryIntegrityEvent insertedTelemetryIntegrityEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelemetryIntegrityEvent createEntity(R2dbcEntityTemplate template) {
        TelemetryIntegrityEvent telemetryIntegrityEvent = new TelemetryIntegrityEvent()
            .eventUid(DEFAULT_EVENT_UID)
            .eventType(DEFAULT_EVENT_TYPE)
            .status(DEFAULT_STATUS)
            .observedAt(DEFAULT_OBSERVED_AT)
            .sourceObservedAt(DEFAULT_SOURCE_OBSERVED_AT)
            .bootId(DEFAULT_BOOT_ID)
            .sequenceNumber(DEFAULT_SEQUENCE_NUMBER)
            .payloadSha256(DEFAULT_PAYLOAD_SHA_256)
            .detail(DEFAULT_DETAIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        telemetryIntegrityEvent.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        telemetryIntegrityEvent.setInstallation(installation);
        return telemetryIntegrityEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelemetryIntegrityEvent createUpdatedEntity(R2dbcEntityTemplate template) {
        TelemetryIntegrityEvent updatedTelemetryIntegrityEvent = new TelemetryIntegrityEvent()
            .eventUid(UPDATED_EVENT_UID)
            .eventType(UPDATED_EVENT_TYPE)
            .status(UPDATED_STATUS)
            .observedAt(UPDATED_OBSERVED_AT)
            .sourceObservedAt(UPDATED_SOURCE_OBSERVED_AT)
            .bootId(UPDATED_BOOT_ID)
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER)
            .payloadSha256(UPDATED_PAYLOAD_SHA_256)
            .detail(UPDATED_DETAIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedTelemetryIntegrityEvent.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedTelemetryIntegrityEvent.setInstallation(installation);
        return updatedTelemetryIntegrityEvent;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(TelemetryIntegrityEvent.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        telemetryIntegrityEvent = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedTelemetryIntegrityEvent != null) {
            telemetryIntegrityEventRepository.delete(insertedTelemetryIntegrityEvent).block();
            insertedTelemetryIntegrityEvent = null;
        }
        deleteEntities(template);
    }

    @Test
    void createTelemetryIntegrityEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TelemetryIntegrityEvent
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);
        var returnedTelemetryIntegrityEventDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TelemetryIntegrityEventDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TelemetryIntegrityEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTelemetryIntegrityEvent = telemetryIntegrityEventMapper.toEntity(returnedTelemetryIntegrityEventDTO);
        assertTelemetryIntegrityEventUpdatableFieldsEquals(
            returnedTelemetryIntegrityEvent,
            getPersistedTelemetryIntegrityEvent(returnedTelemetryIntegrityEvent)
        );

        insertedTelemetryIntegrityEvent = returnedTelemetryIntegrityEvent;
    }

    @Test
    void createTelemetryIntegrityEventWithExistingId() throws Exception {
        // Create the TelemetryIntegrityEvent with an existing ID
        telemetryIntegrityEvent.setId(1L);
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryIntegrityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEventUidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        telemetryIntegrityEvent.setEventUid(null);

        // Create the TelemetryIntegrityEvent, which fails.
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEventTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        telemetryIntegrityEvent.setEventType(null);

        // Create the TelemetryIntegrityEvent, which fails.
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        telemetryIntegrityEvent.setStatus(null);

        // Create the TelemetryIntegrityEvent, which fails.
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkObservedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        telemetryIntegrityEvent.setObservedAt(null);

        // Create the TelemetryIntegrityEvent, which fails.
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTelemetryIntegrityEvents() {
        // Initialize the database
        insertedTelemetryIntegrityEvent = telemetryIntegrityEventRepository.save(telemetryIntegrityEvent).block();

        // Get all the telemetryIntegrityEventList
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
            .value(hasItem(telemetryIntegrityEvent.getId().intValue()))
            .jsonPath("$.[*].eventUid")
            .value(hasItem(DEFAULT_EVENT_UID))
            .jsonPath("$.[*].eventType")
            .value(hasItem(DEFAULT_EVENT_TYPE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].observedAt")
            .value(hasItem(DEFAULT_OBSERVED_AT.toString()))
            .jsonPath("$.[*].sourceObservedAt")
            .value(hasItem(DEFAULT_SOURCE_OBSERVED_AT.toString()))
            .jsonPath("$.[*].bootId")
            .value(hasItem(DEFAULT_BOOT_ID))
            .jsonPath("$.[*].sequenceNumber")
            .value(hasItem(DEFAULT_SEQUENCE_NUMBER.intValue()))
            .jsonPath("$.[*].payloadSha256")
            .value(hasItem(DEFAULT_PAYLOAD_SHA_256))
            .jsonPath("$.[*].detail")
            .value(hasItem(DEFAULT_DETAIL));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTelemetryIntegrityEventsWithEagerRelationshipsIsEnabled() {
        when(telemetryIntegrityEventServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(telemetryIntegrityEventServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTelemetryIntegrityEventsWithEagerRelationshipsIsNotEnabled() {
        when(telemetryIntegrityEventServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(telemetryIntegrityEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTelemetryIntegrityEvent() {
        // Initialize the database
        insertedTelemetryIntegrityEvent = telemetryIntegrityEventRepository.save(telemetryIntegrityEvent).block();

        // Get the telemetryIntegrityEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, telemetryIntegrityEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(telemetryIntegrityEvent.getId().intValue()))
            .jsonPath("$.eventUid")
            .value(is(DEFAULT_EVENT_UID))
            .jsonPath("$.eventType")
            .value(is(DEFAULT_EVENT_TYPE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.observedAt")
            .value(is(DEFAULT_OBSERVED_AT.toString()))
            .jsonPath("$.sourceObservedAt")
            .value(is(DEFAULT_SOURCE_OBSERVED_AT.toString()))
            .jsonPath("$.bootId")
            .value(is(DEFAULT_BOOT_ID))
            .jsonPath("$.sequenceNumber")
            .value(is(DEFAULT_SEQUENCE_NUMBER.intValue()))
            .jsonPath("$.payloadSha256")
            .value(is(DEFAULT_PAYLOAD_SHA_256))
            .jsonPath("$.detail")
            .value(is(DEFAULT_DETAIL));
    }

    @Test
    void getNonExistingTelemetryIntegrityEvent() {
        // Get the telemetryIntegrityEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTelemetryIntegrityEvent() throws Exception {
        // Initialize the database
        insertedTelemetryIntegrityEvent = telemetryIntegrityEventRepository.save(telemetryIntegrityEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryIntegrityEvent
        TelemetryIntegrityEvent updatedTelemetryIntegrityEvent = telemetryIntegrityEventRepository
            .findById(telemetryIntegrityEvent.getId())
            .block();
        updatedTelemetryIntegrityEvent
            .eventUid(UPDATED_EVENT_UID)
            .eventType(UPDATED_EVENT_TYPE)
            .status(UPDATED_STATUS)
            .observedAt(UPDATED_OBSERVED_AT)
            .sourceObservedAt(UPDATED_SOURCE_OBSERVED_AT)
            .bootId(UPDATED_BOOT_ID)
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER)
            .payloadSha256(UPDATED_PAYLOAD_SHA_256)
            .detail(UPDATED_DETAIL);
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(updatedTelemetryIntegrityEvent);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, telemetryIntegrityEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryIntegrityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTelemetryIntegrityEventToMatchAllProperties(updatedTelemetryIntegrityEvent);
    }

    @Test
    void putNonExistingTelemetryIntegrityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryIntegrityEvent.setId(longCount.incrementAndGet());

        // Create the TelemetryIntegrityEvent
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, telemetryIntegrityEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryIntegrityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTelemetryIntegrityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryIntegrityEvent.setId(longCount.incrementAndGet());

        // Create the TelemetryIntegrityEvent
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryIntegrityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTelemetryIntegrityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryIntegrityEvent.setId(longCount.incrementAndGet());

        // Create the TelemetryIntegrityEvent
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TelemetryIntegrityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTelemetryIntegrityEventWithPatch() throws Exception {
        // Initialize the database
        insertedTelemetryIntegrityEvent = telemetryIntegrityEventRepository.save(telemetryIntegrityEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryIntegrityEvent using partial update
        TelemetryIntegrityEvent partialUpdatedTelemetryIntegrityEvent = new TelemetryIntegrityEvent();
        partialUpdatedTelemetryIntegrityEvent.setId(telemetryIntegrityEvent.getId());

        partialUpdatedTelemetryIntegrityEvent
            .eventType(UPDATED_EVENT_TYPE)
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER)
            .payloadSha256(UPDATED_PAYLOAD_SHA_256);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTelemetryIntegrityEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTelemetryIntegrityEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryIntegrityEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTelemetryIntegrityEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTelemetryIntegrityEvent, telemetryIntegrityEvent),
            getPersistedTelemetryIntegrityEvent(telemetryIntegrityEvent)
        );
    }

    @Test
    void fullUpdateTelemetryIntegrityEventWithPatch() throws Exception {
        // Initialize the database
        insertedTelemetryIntegrityEvent = telemetryIntegrityEventRepository.save(telemetryIntegrityEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryIntegrityEvent using partial update
        TelemetryIntegrityEvent partialUpdatedTelemetryIntegrityEvent = new TelemetryIntegrityEvent();
        partialUpdatedTelemetryIntegrityEvent.setId(telemetryIntegrityEvent.getId());

        partialUpdatedTelemetryIntegrityEvent
            .eventUid(UPDATED_EVENT_UID)
            .eventType(UPDATED_EVENT_TYPE)
            .status(UPDATED_STATUS)
            .observedAt(UPDATED_OBSERVED_AT)
            .sourceObservedAt(UPDATED_SOURCE_OBSERVED_AT)
            .bootId(UPDATED_BOOT_ID)
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER)
            .payloadSha256(UPDATED_PAYLOAD_SHA_256)
            .detail(UPDATED_DETAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTelemetryIntegrityEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTelemetryIntegrityEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryIntegrityEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTelemetryIntegrityEventUpdatableFieldsEquals(
            partialUpdatedTelemetryIntegrityEvent,
            getPersistedTelemetryIntegrityEvent(partialUpdatedTelemetryIntegrityEvent)
        );
    }

    @Test
    void patchNonExistingTelemetryIntegrityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryIntegrityEvent.setId(longCount.incrementAndGet());

        // Create the TelemetryIntegrityEvent
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, telemetryIntegrityEventDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryIntegrityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTelemetryIntegrityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryIntegrityEvent.setId(longCount.incrementAndGet());

        // Create the TelemetryIntegrityEvent
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryIntegrityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTelemetryIntegrityEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryIntegrityEvent.setId(longCount.incrementAndGet());

        // Create the TelemetryIntegrityEvent
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = telemetryIntegrityEventMapper.toDto(telemetryIntegrityEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryIntegrityEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TelemetryIntegrityEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTelemetryIntegrityEvent() {
        // Initialize the database
        insertedTelemetryIntegrityEvent = telemetryIntegrityEventRepository.save(telemetryIntegrityEvent).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the telemetryIntegrityEvent
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, telemetryIntegrityEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return telemetryIntegrityEventRepository.count().block();
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

    protected TelemetryIntegrityEvent getPersistedTelemetryIntegrityEvent(TelemetryIntegrityEvent telemetryIntegrityEvent) {
        return telemetryIntegrityEventRepository.findById(telemetryIntegrityEvent.getId()).block();
    }

    protected void assertPersistedTelemetryIntegrityEventToMatchAllProperties(TelemetryIntegrityEvent expectedTelemetryIntegrityEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTelemetryIntegrityEventAllPropertiesEquals(expectedTelemetryIntegrityEvent, getPersistedTelemetryIntegrityEvent(expectedTelemetryIntegrityEvent));
        assertTelemetryIntegrityEventUpdatableFieldsEquals(
            expectedTelemetryIntegrityEvent,
            getPersistedTelemetryIntegrityEvent(expectedTelemetryIntegrityEvent)
        );
    }

    protected void assertPersistedTelemetryIntegrityEventToMatchUpdatableProperties(
        TelemetryIntegrityEvent expectedTelemetryIntegrityEvent
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTelemetryIntegrityEventAllUpdatablePropertiesEquals(expectedTelemetryIntegrityEvent, getPersistedTelemetryIntegrityEvent(expectedTelemetryIntegrityEvent));
        assertTelemetryIntegrityEventUpdatableFieldsEquals(
            expectedTelemetryIntegrityEvent,
            getPersistedTelemetryIntegrityEvent(expectedTelemetryIntegrityEvent)
        );
    }
}
