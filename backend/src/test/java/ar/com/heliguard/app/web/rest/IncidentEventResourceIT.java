package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.IncidentEventAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.IncidentEvent;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.IncidentEventRepository;
import ar.com.heliguard.app.service.IncidentEventService;
import ar.com.heliguard.app.service.dto.IncidentEventDTO;
import ar.com.heliguard.app.service.mapper.IncidentEventMapper;
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
 * Integration tests for the {@link IncidentEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class IncidentEventResourceIT {

    private static final Instant DEFAULT_OCCURRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCCURRED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_EVENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DETAIL = "AAAAAAAAAA";
    private static final String UPDATED_DETAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/incident-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IncidentEventRepository incidentEventRepository;

    @Mock
    private IncidentEventRepository incidentEventRepositoryMock;

    @Autowired
    private IncidentEventMapper incidentEventMapper;

    @Mock
    private IncidentEventService incidentEventServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private IncidentEvent incidentEvent;

    private IncidentEvent insertedIncidentEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IncidentEvent createEntity(R2dbcEntityTemplate template) {
        IncidentEvent incidentEvent = new IncidentEvent()
            .occurredAt(DEFAULT_OCCURRED_AT)
            .eventCode(DEFAULT_EVENT_CODE)
            .detail(DEFAULT_DETAIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        incidentEvent.setOrganization(organization);
        // Add required entity
        Incident incident;
        incident = template.insert(IncidentResourceIT.createEntity(template)).block();
        incidentEvent.setIncident(incident);
        return incidentEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IncidentEvent createUpdatedEntity(R2dbcEntityTemplate template) {
        IncidentEvent updatedIncidentEvent = new IncidentEvent()
            .occurredAt(UPDATED_OCCURRED_AT)
            .eventCode(UPDATED_EVENT_CODE)
            .detail(UPDATED_DETAIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedIncidentEvent.setOrganization(organization);
        // Add required entity
        Incident incident;
        incident = template.insert(IncidentResourceIT.createUpdatedEntity(template)).block();
        updatedIncidentEvent.setIncident(incident);
        return updatedIncidentEvent;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(IncidentEvent.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        IncidentResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        incidentEvent = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedIncidentEvent != null) {
            incidentEventRepository.delete(insertedIncidentEvent).block();
            insertedIncidentEvent = null;
        }
        deleteEntities(template);
    }

    @Test
    void createIncidentEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the IncidentEvent
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);
        var returnedIncidentEventDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(IncidentEventDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the IncidentEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIncidentEvent = incidentEventMapper.toEntity(returnedIncidentEventDTO);
        assertIncidentEventUpdatableFieldsEquals(returnedIncidentEvent, getPersistedIncidentEvent(returnedIncidentEvent));

        insertedIncidentEvent = returnedIncidentEvent;
    }

    @Test
    void createIncidentEventWithExistingId() throws Exception {
        // Create the IncidentEvent with an existing ID
        incidentEvent.setId(1L);
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkOccurredAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incidentEvent.setOccurredAt(null);

        // Create the IncidentEvent, which fails.
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEventCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incidentEvent.setEventCode(null);

        // Create the IncidentEvent, which fails.
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllIncidentEventsAsStream() {
        // Initialize the database
        incidentEventRepository.save(incidentEvent).block();

        List<IncidentEvent> incidentEventList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(IncidentEventDTO.class)
            .getResponseBody()
            .map(incidentEventMapper::toEntity)
            .filter(incidentEvent::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(incidentEventList).isNotNull();
        assertThat(incidentEventList).hasSize(1);
        IncidentEvent testIncidentEvent = incidentEventList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertIncidentEventAllPropertiesEquals(incidentEvent, testIncidentEvent);
        assertIncidentEventUpdatableFieldsEquals(incidentEvent, testIncidentEvent);
    }

    @Test
    void getAllIncidentEvents() {
        // Initialize the database
        insertedIncidentEvent = incidentEventRepository.save(incidentEvent).block();

        // Get all the incidentEventList
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
            .value(hasItem(incidentEvent.getId().intValue()))
            .jsonPath("$.[*].occurredAt")
            .value(hasItem(DEFAULT_OCCURRED_AT.toString()))
            .jsonPath("$.[*].eventCode")
            .value(hasItem(DEFAULT_EVENT_CODE))
            .jsonPath("$.[*].detail")
            .value(hasItem(DEFAULT_DETAIL));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIncidentEventsWithEagerRelationshipsIsEnabled() {
        when(incidentEventServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(incidentEventServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIncidentEventsWithEagerRelationshipsIsNotEnabled() {
        when(incidentEventServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(incidentEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getIncidentEvent() {
        // Initialize the database
        insertedIncidentEvent = incidentEventRepository.save(incidentEvent).block();

        // Get the incidentEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, incidentEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(incidentEvent.getId().intValue()))
            .jsonPath("$.occurredAt")
            .value(is(DEFAULT_OCCURRED_AT.toString()))
            .jsonPath("$.eventCode")
            .value(is(DEFAULT_EVENT_CODE))
            .jsonPath("$.detail")
            .value(is(DEFAULT_DETAIL));
    }

    @Test
    void getNonExistingIncidentEvent() {
        // Get the incidentEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingIncidentEvent() throws Exception {
        // Initialize the database
        insertedIncidentEvent = incidentEventRepository.save(incidentEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidentEvent
        IncidentEvent updatedIncidentEvent = incidentEventRepository.findById(incidentEvent.getId()).block();
        updatedIncidentEvent.occurredAt(UPDATED_OCCURRED_AT).eventCode(UPDATED_EVENT_CODE).detail(UPDATED_DETAIL);
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(updatedIncidentEvent);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, incidentEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IncidentEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIncidentEventToMatchAllProperties(updatedIncidentEvent);
    }

    @Test
    void putNonExistingIncidentEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentEvent.setId(longCount.incrementAndGet());

        // Create the IncidentEvent
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, incidentEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchIncidentEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentEvent.setId(longCount.incrementAndGet());

        // Create the IncidentEvent
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamIncidentEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentEvent.setId(longCount.incrementAndGet());

        // Create the IncidentEvent
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the IncidentEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateIncidentEventWithPatch() throws Exception {
        // Initialize the database
        insertedIncidentEvent = incidentEventRepository.save(incidentEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidentEvent using partial update
        IncidentEvent partialUpdatedIncidentEvent = new IncidentEvent();
        partialUpdatedIncidentEvent.setId(incidentEvent.getId());

        partialUpdatedIncidentEvent.detail(UPDATED_DETAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIncidentEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedIncidentEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IncidentEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidentEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIncidentEvent, incidentEvent),
            getPersistedIncidentEvent(incidentEvent)
        );
    }

    @Test
    void fullUpdateIncidentEventWithPatch() throws Exception {
        // Initialize the database
        insertedIncidentEvent = incidentEventRepository.save(incidentEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidentEvent using partial update
        IncidentEvent partialUpdatedIncidentEvent = new IncidentEvent();
        partialUpdatedIncidentEvent.setId(incidentEvent.getId());

        partialUpdatedIncidentEvent.occurredAt(UPDATED_OCCURRED_AT).eventCode(UPDATED_EVENT_CODE).detail(UPDATED_DETAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIncidentEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedIncidentEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IncidentEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidentEventUpdatableFieldsEquals(partialUpdatedIncidentEvent, getPersistedIncidentEvent(partialUpdatedIncidentEvent));
    }

    @Test
    void patchNonExistingIncidentEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentEvent.setId(longCount.incrementAndGet());

        // Create the IncidentEvent
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, incidentEventDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchIncidentEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentEvent.setId(longCount.incrementAndGet());

        // Create the IncidentEvent
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamIncidentEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentEvent.setId(longCount.incrementAndGet());

        // Create the IncidentEvent
        IncidentEventDTO incidentEventDTO = incidentEventMapper.toDto(incidentEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the IncidentEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteIncidentEvent() {
        // Initialize the database
        insertedIncidentEvent = incidentEventRepository.save(incidentEvent).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the incidentEvent
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, incidentEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return incidentEventRepository.count().block();
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

    protected IncidentEvent getPersistedIncidentEvent(IncidentEvent incidentEvent) {
        return incidentEventRepository.findById(incidentEvent.getId()).block();
    }

    protected void assertPersistedIncidentEventToMatchAllProperties(IncidentEvent expectedIncidentEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertIncidentEventAllPropertiesEquals(expectedIncidentEvent, getPersistedIncidentEvent(expectedIncidentEvent));
        assertIncidentEventUpdatableFieldsEquals(expectedIncidentEvent, getPersistedIncidentEvent(expectedIncidentEvent));
    }

    protected void assertPersistedIncidentEventToMatchUpdatableProperties(IncidentEvent expectedIncidentEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertIncidentEventAllUpdatablePropertiesEquals(expectedIncidentEvent, getPersistedIncidentEvent(expectedIncidentEvent));
        assertIncidentEventUpdatableFieldsEquals(expectedIncidentEvent, getPersistedIncidentEvent(expectedIncidentEvent));
    }
}
