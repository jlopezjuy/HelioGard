package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AlertRuleAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AlertRule;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.repository.AlertRuleRepository;
import ar.com.heliguard.app.service.AlertRuleService;
import ar.com.heliguard.app.service.dto.AlertRuleDTO;
import ar.com.heliguard.app.service.mapper.AlertRuleMapper;
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
 * Integration tests for the {@link AlertRuleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AlertRuleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RULE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_RULE_CODE = "BBBBBBBBBB";

    private static final AlertSeverity DEFAULT_SEVERITY = AlertSeverity.INFO;
    private static final AlertSeverity UPDATED_SEVERITY = AlertSeverity.LOW;

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/alert-rules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlertRuleRepository alertRuleRepository;

    @Mock
    private AlertRuleRepository alertRuleRepositoryMock;

    @Autowired
    private AlertRuleMapper alertRuleMapper;

    @Mock
    private AlertRuleService alertRuleServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AlertRule alertRule;

    private AlertRule insertedAlertRule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlertRule createEntity(R2dbcEntityTemplate template) {
        AlertRule alertRule = new AlertRule()
            .name(DEFAULT_NAME)
            .ruleCode(DEFAULT_RULE_CODE)
            .severity(DEFAULT_SEVERITY)
            .enabled(DEFAULT_ENABLED);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        alertRule.setOrganization(organization);
        return alertRule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlertRule createUpdatedEntity(R2dbcEntityTemplate template) {
        AlertRule updatedAlertRule = new AlertRule()
            .name(UPDATED_NAME)
            .ruleCode(UPDATED_RULE_CODE)
            .severity(UPDATED_SEVERITY)
            .enabled(UPDATED_ENABLED);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAlertRule.setOrganization(organization);
        return updatedAlertRule;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AlertRule.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        alertRule = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAlertRule != null) {
            alertRuleRepository.delete(insertedAlertRule).block();
            insertedAlertRule = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAlertRule() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AlertRule
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);
        var returnedAlertRuleDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AlertRuleDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AlertRule in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAlertRule = alertRuleMapper.toEntity(returnedAlertRuleDTO);
        assertAlertRuleUpdatableFieldsEquals(returnedAlertRule, getPersistedAlertRule(returnedAlertRule));

        insertedAlertRule = returnedAlertRule;
    }

    @Test
    void createAlertRuleWithExistingId() throws Exception {
        // Create the AlertRule with an existing ID
        alertRule.setId(1L);
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRule in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alertRule.setName(null);

        // Create the AlertRule, which fails.
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRuleCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alertRule.setRuleCode(null);

        // Create the AlertRule, which fails.
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSeverityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alertRule.setSeverity(null);

        // Create the AlertRule, which fails.
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alertRule.setEnabled(null);

        // Create the AlertRule, which fails.
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAlertRulesAsStream() {
        // Initialize the database
        alertRuleRepository.save(alertRule).block();

        List<AlertRule> alertRuleList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AlertRuleDTO.class)
            .getResponseBody()
            .map(alertRuleMapper::toEntity)
            .filter(alertRule::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(alertRuleList).isNotNull();
        assertThat(alertRuleList).hasSize(1);
        AlertRule testAlertRule = alertRuleList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertAlertRuleAllPropertiesEquals(alertRule, testAlertRule);
        assertAlertRuleUpdatableFieldsEquals(alertRule, testAlertRule);
    }

    @Test
    void getAllAlertRules() {
        // Initialize the database
        insertedAlertRule = alertRuleRepository.save(alertRule).block();

        // Get all the alertRuleList
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
            .value(hasItem(alertRule.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].ruleCode")
            .value(hasItem(DEFAULT_RULE_CODE))
            .jsonPath("$.[*].severity")
            .value(hasItem(DEFAULT_SEVERITY.toString()))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlertRulesWithEagerRelationshipsIsEnabled() {
        when(alertRuleServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(alertRuleServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlertRulesWithEagerRelationshipsIsNotEnabled() {
        when(alertRuleServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(alertRuleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAlertRule() {
        // Initialize the database
        insertedAlertRule = alertRuleRepository.save(alertRule).block();

        // Get the alertRule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, alertRule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(alertRule.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.ruleCode")
            .value(is(DEFAULT_RULE_CODE))
            .jsonPath("$.severity")
            .value(is(DEFAULT_SEVERITY.toString()))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED));
    }

    @Test
    void getNonExistingAlertRule() {
        // Get the alertRule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAlertRule() throws Exception {
        // Initialize the database
        insertedAlertRule = alertRuleRepository.save(alertRule).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertRule
        AlertRule updatedAlertRule = alertRuleRepository.findById(alertRule.getId()).block();
        updatedAlertRule.name(UPDATED_NAME).ruleCode(UPDATED_RULE_CODE).severity(UPDATED_SEVERITY).enabled(UPDATED_ENABLED);
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(updatedAlertRule);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, alertRuleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AlertRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlertRuleToMatchAllProperties(updatedAlertRule);
    }

    @Test
    void putNonExistingAlertRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRule.setId(longCount.incrementAndGet());

        // Create the AlertRule
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, alertRuleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAlertRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRule.setId(longCount.incrementAndGet());

        // Create the AlertRule
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAlertRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRule.setId(longCount.incrementAndGet());

        // Create the AlertRule
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AlertRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAlertRuleWithPatch() throws Exception {
        // Initialize the database
        insertedAlertRule = alertRuleRepository.save(alertRule).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertRule using partial update
        AlertRule partialUpdatedAlertRule = new AlertRule();
        partialUpdatedAlertRule.setId(alertRule.getId());

        partialUpdatedAlertRule.name(UPDATED_NAME).ruleCode(UPDATED_RULE_CODE).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAlertRule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAlertRule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AlertRule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertRuleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAlertRule, alertRule),
            getPersistedAlertRule(alertRule)
        );
    }

    @Test
    void fullUpdateAlertRuleWithPatch() throws Exception {
        // Initialize the database
        insertedAlertRule = alertRuleRepository.save(alertRule).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertRule using partial update
        AlertRule partialUpdatedAlertRule = new AlertRule();
        partialUpdatedAlertRule.setId(alertRule.getId());

        partialUpdatedAlertRule.name(UPDATED_NAME).ruleCode(UPDATED_RULE_CODE).severity(UPDATED_SEVERITY).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAlertRule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAlertRule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AlertRule in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertRuleUpdatableFieldsEquals(partialUpdatedAlertRule, getPersistedAlertRule(partialUpdatedAlertRule));
    }

    @Test
    void patchNonExistingAlertRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRule.setId(longCount.incrementAndGet());

        // Create the AlertRule
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, alertRuleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAlertRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRule.setId(longCount.incrementAndGet());

        // Create the AlertRule
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAlertRule() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRule.setId(longCount.incrementAndGet());

        // Create the AlertRule
        AlertRuleDTO alertRuleDTO = alertRuleMapper.toDto(alertRule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(alertRuleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AlertRule in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAlertRule() {
        // Initialize the database
        insertedAlertRule = alertRuleRepository.save(alertRule).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the alertRule
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, alertRule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alertRuleRepository.count().block();
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

    protected AlertRule getPersistedAlertRule(AlertRule alertRule) {
        return alertRuleRepository.findById(alertRule.getId()).block();
    }

    protected void assertPersistedAlertRuleToMatchAllProperties(AlertRule expectedAlertRule) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAlertRuleAllPropertiesEquals(expectedAlertRule, getPersistedAlertRule(expectedAlertRule));
        assertAlertRuleUpdatableFieldsEquals(expectedAlertRule, getPersistedAlertRule(expectedAlertRule));
    }

    protected void assertPersistedAlertRuleToMatchUpdatableProperties(AlertRule expectedAlertRule) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAlertRuleAllUpdatablePropertiesEquals(expectedAlertRule, getPersistedAlertRule(expectedAlertRule));
        assertAlertRuleUpdatableFieldsEquals(expectedAlertRule, getPersistedAlertRule(expectedAlertRule));
    }
}
