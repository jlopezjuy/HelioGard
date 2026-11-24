package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.DroneMissionEventAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.DroneMissionEvent;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.DroneMissionEventRepository;
import ar.com.heliguard.app.service.DroneMissionEventService;
import ar.com.heliguard.app.service.dto.DroneMissionEventDTO;
import ar.com.heliguard.app.service.mapper.DroneMissionEventMapper;
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
 * Integration tests for the {@link DroneMissionEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DroneMissionEventResourceIT {

    private static final String DEFAULT_EVENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_OCCURRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCCURRED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/drone-mission-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DroneMissionEventRepository droneMissionEventRepository;

    @Mock
    private DroneMissionEventRepository droneMissionEventRepositoryMock;

    @Autowired
    private DroneMissionEventMapper droneMissionEventMapper;

    @Mock
    private DroneMissionEventService droneMissionEventServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private DroneMissionEvent droneMissionEvent;

    private DroneMissionEvent insertedDroneMissionEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroneMissionEvent createEntity(R2dbcEntityTemplate template) {
        DroneMissionEvent droneMissionEvent = new DroneMissionEvent()
            .eventCode(DEFAULT_EVENT_CODE)
            .occurredAt(DEFAULT_OCCURRED_AT)
            .description(DEFAULT_DESCRIPTION);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        droneMissionEvent.setOrganization(organization);
        // Add required entity
        DroneMission droneMission;
        droneMission = template.insert(DroneMissionResourceIT.createEntity(template)).block();
        droneMissionEvent.setMission(droneMission);
        return droneMissionEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroneMissionEvent createUpdatedEntity(R2dbcEntityTemplate template) {
        DroneMissionEvent updatedDroneMissionEvent = new DroneMissionEvent()
            .eventCode(UPDATED_EVENT_CODE)
            .occurredAt(UPDATED_OCCURRED_AT)
            .description(UPDATED_DESCRIPTION);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedDroneMissionEvent.setOrganization(organization);
        // Add required entity
        DroneMission droneMission;
        droneMission = template.insert(DroneMissionResourceIT.createUpdatedEntity(template)).block();
        updatedDroneMissionEvent.setMission(droneMission);
        return updatedDroneMissionEvent;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(DroneMissionEvent.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        DroneMissionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        droneMissionEvent = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedDroneMissionEvent != null) {
            droneMissionEventRepository.delete(insertedDroneMissionEvent).block();
            insertedDroneMissionEvent = null;
        }
        deleteEntities(template);
    }

    @Test
    void createDroneMissionEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DroneMissionEvent
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);
        var returnedDroneMissionEventDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DroneMissionEventDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the DroneMissionEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDroneMissionEvent = droneMissionEventMapper.toEntity(returnedDroneMissionEventDTO);
        assertDroneMissionEventUpdatableFieldsEquals(returnedDroneMissionEvent, getPersistedDroneMissionEvent(returnedDroneMissionEvent));

        insertedDroneMissionEvent = returnedDroneMissionEvent;
    }

    @Test
    void createDroneMissionEventWithExistingId() throws Exception {
        // Create the DroneMissionEvent with an existing ID
        droneMissionEvent.setId(1L);
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEventCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneMissionEvent.setEventCode(null);

        // Create the DroneMissionEvent, which fails.
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkOccurredAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        droneMissionEvent.setOccurredAt(null);

        // Create the DroneMissionEvent, which fails.
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDroneMissionEventsAsStream() {
        // Initialize the database
        droneMissionEventRepository.save(droneMissionEvent).block();

        List<DroneMissionEvent> droneMissionEventList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(DroneMissionEventDTO.class)
            .getResponseBody()
            .map(droneMissionEventMapper::toEntity)
            .filter(droneMissionEvent::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(droneMissionEventList).isNotNull();
        assertThat(droneMissionEventList).hasSize(1);
        DroneMissionEvent testDroneMissionEvent = droneMissionEventList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertDroneMissionEventAllPropertiesEquals(droneMissionEvent, testDroneMissionEvent);
        assertDroneMissionEventUpdatableFieldsEquals(droneMissionEvent, testDroneMissionEvent);
    }

    @Test
    void getAllDroneMissionEvents() {
        // Initialize the database
        insertedDroneMissionEvent = droneMissionEventRepository.save(droneMissionEvent).block();

        // Get all the droneMissionEventList
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
            .value(hasItem(droneMissionEvent.getId().intValue()))
            .jsonPath("$.[*].eventCode")
            .value(hasItem(DEFAULT_EVENT_CODE))
            .jsonPath("$.[*].occurredAt")
            .value(hasItem(DEFAULT_OCCURRED_AT.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDroneMissionEventsWithEagerRelationshipsIsEnabled() {
        when(droneMissionEventServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(droneMissionEventServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDroneMissionEventsWithEagerRelationshipsIsNotEnabled() {
        when(droneMissionEventServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(droneMissionEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDroneMissionEvent() {
        // Initialize the database
        insertedDroneMissionEvent = droneMissionEventRepository.save(droneMissionEvent).block();

        // Get the droneMissionEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, droneMissionEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(droneMissionEvent.getId().intValue()))
            .jsonPath("$.eventCode")
            .value(is(DEFAULT_EVENT_CODE))
            .jsonPath("$.occurredAt")
            .value(is(DEFAULT_OCCURRED_AT.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingDroneMissionEvent() {
        // Get the droneMissionEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDroneMissionEvent() throws Exception {
        // Initialize the database
        insertedDroneMissionEvent = droneMissionEventRepository.save(droneMissionEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMissionEvent
        DroneMissionEvent updatedDroneMissionEvent = droneMissionEventRepository.findById(droneMissionEvent.getId()).block();
        updatedDroneMissionEvent.eventCode(UPDATED_EVENT_CODE).occurredAt(UPDATED_OCCURRED_AT).description(UPDATED_DESCRIPTION);
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(updatedDroneMissionEvent);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, droneMissionEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMissionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDroneMissionEventToMatchAllProperties(updatedDroneMissionEvent);
    }

    @Test
    void putNonExistingDroneMissionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionEvent.setId(longCount.incrementAndGet());

        // Create the DroneMissionEvent
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, droneMissionEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDroneMissionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionEvent.setId(longCount.incrementAndGet());

        // Create the DroneMissionEvent
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDroneMissionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionEvent.setId(longCount.incrementAndGet());

        // Create the DroneMissionEvent
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DroneMissionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDroneMissionEventWithPatch() throws Exception {
        // Initialize the database
        insertedDroneMissionEvent = droneMissionEventRepository.save(droneMissionEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMissionEvent using partial update
        DroneMissionEvent partialUpdatedDroneMissionEvent = new DroneMissionEvent();
        partialUpdatedDroneMissionEvent.setId(droneMissionEvent.getId());

        partialUpdatedDroneMissionEvent.eventCode(UPDATED_EVENT_CODE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDroneMissionEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDroneMissionEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMissionEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDroneMissionEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDroneMissionEvent, droneMissionEvent),
            getPersistedDroneMissionEvent(droneMissionEvent)
        );
    }

    @Test
    void fullUpdateDroneMissionEventWithPatch() throws Exception {
        // Initialize the database
        insertedDroneMissionEvent = droneMissionEventRepository.save(droneMissionEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the droneMissionEvent using partial update
        DroneMissionEvent partialUpdatedDroneMissionEvent = new DroneMissionEvent();
        partialUpdatedDroneMissionEvent.setId(droneMissionEvent.getId());

        partialUpdatedDroneMissionEvent.eventCode(UPDATED_EVENT_CODE).occurredAt(UPDATED_OCCURRED_AT).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDroneMissionEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDroneMissionEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DroneMissionEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDroneMissionEventUpdatableFieldsEquals(
            partialUpdatedDroneMissionEvent,
            getPersistedDroneMissionEvent(partialUpdatedDroneMissionEvent)
        );
    }

    @Test
    void patchNonExistingDroneMissionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionEvent.setId(longCount.incrementAndGet());

        // Create the DroneMissionEvent
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, droneMissionEventDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDroneMissionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionEvent.setId(longCount.incrementAndGet());

        // Create the DroneMissionEvent
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DroneMissionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDroneMissionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        droneMissionEvent.setId(longCount.incrementAndGet());

        // Create the DroneMissionEvent
        DroneMissionEventDTO droneMissionEventDTO = droneMissionEventMapper.toDto(droneMissionEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(droneMissionEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DroneMissionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDroneMissionEvent() {
        // Initialize the database
        insertedDroneMissionEvent = droneMissionEventRepository.save(droneMissionEvent).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the droneMissionEvent
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, droneMissionEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return droneMissionEventRepository.count().block();
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

    protected DroneMissionEvent getPersistedDroneMissionEvent(DroneMissionEvent droneMissionEvent) {
        return droneMissionEventRepository.findById(droneMissionEvent.getId()).block();
    }

    protected void assertPersistedDroneMissionEventToMatchAllProperties(DroneMissionEvent expectedDroneMissionEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDroneMissionEventAllPropertiesEquals(expectedDroneMissionEvent, getPersistedDroneMissionEvent(expectedDroneMissionEvent));
        assertDroneMissionEventUpdatableFieldsEquals(expectedDroneMissionEvent, getPersistedDroneMissionEvent(expectedDroneMissionEvent));
    }

    protected void assertPersistedDroneMissionEventToMatchUpdatableProperties(DroneMissionEvent expectedDroneMissionEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDroneMissionEventAllUpdatablePropertiesEquals(expectedDroneMissionEvent, getPersistedDroneMissionEvent(expectedDroneMissionEvent));
        assertDroneMissionEventUpdatableFieldsEquals(expectedDroneMissionEvent, getPersistedDroneMissionEvent(expectedDroneMissionEvent));
    }
}
