package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.SecurityCaseEventAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityCaseEvent;
import ar.com.heliguard.app.domain.SecurityIncident;
import ar.com.heliguard.app.repository.SecurityCaseEventRepository;
import ar.com.heliguard.app.service.SecurityCaseEventService;
import ar.com.heliguard.app.service.dto.SecurityCaseEventDTO;
import ar.com.heliguard.app.service.mapper.SecurityCaseEventMapper;
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
 * Integration tests for the {@link SecurityCaseEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SecurityCaseEventResourceIT {

    private static final String DEFAULT_EVENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_OCCURRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OCCURRED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_DETAIL = "AAAAAAAAAA";
    private static final String UPDATED_DETAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/security-case-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SecurityCaseEventRepository securityCaseEventRepository;

    @Mock
    private SecurityCaseEventRepository securityCaseEventRepositoryMock;

    @Autowired
    private SecurityCaseEventMapper securityCaseEventMapper;

    @Mock
    private SecurityCaseEventService securityCaseEventServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private SecurityCaseEvent securityCaseEvent;

    private SecurityCaseEvent insertedSecurityCaseEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityCaseEvent createEntity(R2dbcEntityTemplate template) {
        SecurityCaseEvent securityCaseEvent = new SecurityCaseEvent()
            .eventCode(DEFAULT_EVENT_CODE)
            .occurredAt(DEFAULT_OCCURRED_AT)
            .detail(DEFAULT_DETAIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        securityCaseEvent.setOrganization(organization);
        // Add required entity
        SecurityIncident securityIncident;
        securityIncident = template.insert(SecurityIncidentResourceIT.createEntity(template)).block();
        securityCaseEvent.setSecurityIncident(securityIncident);
        return securityCaseEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityCaseEvent createUpdatedEntity(R2dbcEntityTemplate template) {
        SecurityCaseEvent updatedSecurityCaseEvent = new SecurityCaseEvent()
            .eventCode(UPDATED_EVENT_CODE)
            .occurredAt(UPDATED_OCCURRED_AT)
            .detail(UPDATED_DETAIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedSecurityCaseEvent.setOrganization(organization);
        // Add required entity
        SecurityIncident securityIncident;
        securityIncident = template.insert(SecurityIncidentResourceIT.createUpdatedEntity(template)).block();
        updatedSecurityCaseEvent.setSecurityIncident(securityIncident);
        return updatedSecurityCaseEvent;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(SecurityCaseEvent.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        SecurityIncidentResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        securityCaseEvent = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedSecurityCaseEvent != null) {
            securityCaseEventRepository.delete(insertedSecurityCaseEvent).block();
            insertedSecurityCaseEvent = null;
        }
        deleteEntities(template);
    }

    @Test
    void createSecurityCaseEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SecurityCaseEvent
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);
        var returnedSecurityCaseEventDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SecurityCaseEventDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the SecurityCaseEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSecurityCaseEvent = securityCaseEventMapper.toEntity(returnedSecurityCaseEventDTO);
        assertSecurityCaseEventUpdatableFieldsEquals(returnedSecurityCaseEvent, getPersistedSecurityCaseEvent(returnedSecurityCaseEvent));

        insertedSecurityCaseEvent = returnedSecurityCaseEvent;
    }

    @Test
    void createSecurityCaseEventWithExistingId() throws Exception {
        // Create the SecurityCaseEvent with an existing ID
        securityCaseEvent.setId(1L);
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEventCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        securityCaseEvent.setEventCode(null);

        // Create the SecurityCaseEvent, which fails.
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkOccurredAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        securityCaseEvent.setOccurredAt(null);

        // Create the SecurityCaseEvent, which fails.
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSecurityCaseEventsAsStream() {
        // Initialize the database
        securityCaseEventRepository.save(securityCaseEvent).block();

        List<SecurityCaseEvent> securityCaseEventList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SecurityCaseEventDTO.class)
            .getResponseBody()
            .map(securityCaseEventMapper::toEntity)
            .filter(securityCaseEvent::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(securityCaseEventList).isNotNull();
        assertThat(securityCaseEventList).hasSize(1);
        SecurityCaseEvent testSecurityCaseEvent = securityCaseEventList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertSecurityCaseEventAllPropertiesEquals(securityCaseEvent, testSecurityCaseEvent);
        assertSecurityCaseEventUpdatableFieldsEquals(securityCaseEvent, testSecurityCaseEvent);
    }

    @Test
    void getAllSecurityCaseEvents() {
        // Initialize the database
        insertedSecurityCaseEvent = securityCaseEventRepository.save(securityCaseEvent).block();

        // Get all the securityCaseEventList
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
            .value(hasItem(securityCaseEvent.getId().intValue()))
            .jsonPath("$.[*].eventCode")
            .value(hasItem(DEFAULT_EVENT_CODE))
            .jsonPath("$.[*].occurredAt")
            .value(hasItem(DEFAULT_OCCURRED_AT.toString()))
            .jsonPath("$.[*].detail")
            .value(hasItem(DEFAULT_DETAIL));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSecurityCaseEventsWithEagerRelationshipsIsEnabled() {
        when(securityCaseEventServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(securityCaseEventServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSecurityCaseEventsWithEagerRelationshipsIsNotEnabled() {
        when(securityCaseEventServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(securityCaseEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSecurityCaseEvent() {
        // Initialize the database
        insertedSecurityCaseEvent = securityCaseEventRepository.save(securityCaseEvent).block();

        // Get the securityCaseEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, securityCaseEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(securityCaseEvent.getId().intValue()))
            .jsonPath("$.eventCode")
            .value(is(DEFAULT_EVENT_CODE))
            .jsonPath("$.occurredAt")
            .value(is(DEFAULT_OCCURRED_AT.toString()))
            .jsonPath("$.detail")
            .value(is(DEFAULT_DETAIL));
    }

    @Test
    void getNonExistingSecurityCaseEvent() {
        // Get the securityCaseEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSecurityCaseEvent() throws Exception {
        // Initialize the database
        insertedSecurityCaseEvent = securityCaseEventRepository.save(securityCaseEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityCaseEvent
        SecurityCaseEvent updatedSecurityCaseEvent = securityCaseEventRepository.findById(securityCaseEvent.getId()).block();
        updatedSecurityCaseEvent.eventCode(UPDATED_EVENT_CODE).occurredAt(UPDATED_OCCURRED_AT).detail(UPDATED_DETAIL);
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(updatedSecurityCaseEvent);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, securityCaseEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityCaseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSecurityCaseEventToMatchAllProperties(updatedSecurityCaseEvent);
    }

    @Test
    void putNonExistingSecurityCaseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvent.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvent
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, securityCaseEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSecurityCaseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvent.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvent
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSecurityCaseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvent.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvent
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SecurityCaseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSecurityCaseEventWithPatch() throws Exception {
        // Initialize the database
        insertedSecurityCaseEvent = securityCaseEventRepository.save(securityCaseEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityCaseEvent using partial update
        SecurityCaseEvent partialUpdatedSecurityCaseEvent = new SecurityCaseEvent();
        partialUpdatedSecurityCaseEvent.setId(securityCaseEvent.getId());

        partialUpdatedSecurityCaseEvent.occurredAt(UPDATED_OCCURRED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSecurityCaseEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSecurityCaseEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityCaseEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSecurityCaseEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSecurityCaseEvent, securityCaseEvent),
            getPersistedSecurityCaseEvent(securityCaseEvent)
        );
    }

    @Test
    void fullUpdateSecurityCaseEventWithPatch() throws Exception {
        // Initialize the database
        insertedSecurityCaseEvent = securityCaseEventRepository.save(securityCaseEvent).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the securityCaseEvent using partial update
        SecurityCaseEvent partialUpdatedSecurityCaseEvent = new SecurityCaseEvent();
        partialUpdatedSecurityCaseEvent.setId(securityCaseEvent.getId());

        partialUpdatedSecurityCaseEvent.eventCode(UPDATED_EVENT_CODE).occurredAt(UPDATED_OCCURRED_AT).detail(UPDATED_DETAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSecurityCaseEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSecurityCaseEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SecurityCaseEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSecurityCaseEventUpdatableFieldsEquals(
            partialUpdatedSecurityCaseEvent,
            getPersistedSecurityCaseEvent(partialUpdatedSecurityCaseEvent)
        );
    }

    @Test
    void patchNonExistingSecurityCaseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvent.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvent
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, securityCaseEventDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSecurityCaseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvent.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvent
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SecurityCaseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSecurityCaseEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        securityCaseEvent.setId(longCount.incrementAndGet());

        // Create the SecurityCaseEvent
        SecurityCaseEventDTO securityCaseEventDTO = securityCaseEventMapper.toDto(securityCaseEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(securityCaseEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SecurityCaseEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSecurityCaseEvent() {
        // Initialize the database
        insertedSecurityCaseEvent = securityCaseEventRepository.save(securityCaseEvent).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the securityCaseEvent
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, securityCaseEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return securityCaseEventRepository.count().block();
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

    protected SecurityCaseEvent getPersistedSecurityCaseEvent(SecurityCaseEvent securityCaseEvent) {
        return securityCaseEventRepository.findById(securityCaseEvent.getId()).block();
    }

    protected void assertPersistedSecurityCaseEventToMatchAllProperties(SecurityCaseEvent expectedSecurityCaseEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSecurityCaseEventAllPropertiesEquals(expectedSecurityCaseEvent, getPersistedSecurityCaseEvent(expectedSecurityCaseEvent));
        assertSecurityCaseEventUpdatableFieldsEquals(expectedSecurityCaseEvent, getPersistedSecurityCaseEvent(expectedSecurityCaseEvent));
    }

    protected void assertPersistedSecurityCaseEventToMatchUpdatableProperties(SecurityCaseEvent expectedSecurityCaseEvent) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSecurityCaseEventAllUpdatablePropertiesEquals(expectedSecurityCaseEvent, getPersistedSecurityCaseEvent(expectedSecurityCaseEvent));
        assertSecurityCaseEventUpdatableFieldsEquals(expectedSecurityCaseEvent, getPersistedSecurityCaseEvent(expectedSecurityCaseEvent));
    }
}
