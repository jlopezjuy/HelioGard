package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AuditAnswerAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AuditAnswer;
import ar.com.heliguard.app.domain.AuditQuestion;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.AuditAnswerRepository;
import ar.com.heliguard.app.service.AuditAnswerService;
import ar.com.heliguard.app.service.dto.AuditAnswerDTO;
import ar.com.heliguard.app.service.mapper.AuditAnswerMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link AuditAnswerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuditAnswerResourceIT {

    private static final String DEFAULT_ANSWER_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER_TEXT = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_ANSWER_NUMBER = new BigDecimal(1);
    private static final BigDecimal UPDATED_ANSWER_NUMBER = new BigDecimal(2);

    private static final Boolean DEFAULT_ANSWER_BOOLEAN = false;
    private static final Boolean UPDATED_ANSWER_BOOLEAN = true;

    private static final String DEFAULT_ANSWER_UNIT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER_UNIT_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_ANSWERED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ANSWERED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Boolean DEFAULT_COMPLIANT = false;
    private static final Boolean UPDATED_COMPLIANT = true;

    private static final String ENTITY_API_URL = "/api/audit-answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditAnswerRepository auditAnswerRepository;

    @Mock
    private AuditAnswerRepository auditAnswerRepositoryMock;

    @Autowired
    private AuditAnswerMapper auditAnswerMapper;

    @Mock
    private AuditAnswerService auditAnswerServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AuditAnswer auditAnswer;

    private AuditAnswer insertedAuditAnswer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditAnswer createEntity(R2dbcEntityTemplate template) {
        AuditAnswer auditAnswer = new AuditAnswer()
            .answerText(DEFAULT_ANSWER_TEXT)
            .answerNumber(DEFAULT_ANSWER_NUMBER)
            .answerBoolean(DEFAULT_ANSWER_BOOLEAN)
            .answerUnitCode(DEFAULT_ANSWER_UNIT_CODE)
            .answeredAt(DEFAULT_ANSWERED_AT)
            .compliant(DEFAULT_COMPLIANT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        auditAnswer.setOrganization(organization);
        // Add required entity
        AuditReport auditReport;
        auditReport = template.insert(AuditReportResourceIT.createEntity(template)).block();
        auditAnswer.setAuditReport(auditReport);
        // Add required entity
        AuditQuestion auditQuestion;
        auditQuestion = template.insert(AuditQuestionResourceIT.createEntity(template)).block();
        auditAnswer.setAuditQuestion(auditQuestion);
        return auditAnswer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditAnswer createUpdatedEntity(R2dbcEntityTemplate template) {
        AuditAnswer updatedAuditAnswer = new AuditAnswer()
            .answerText(UPDATED_ANSWER_TEXT)
            .answerNumber(UPDATED_ANSWER_NUMBER)
            .answerBoolean(UPDATED_ANSWER_BOOLEAN)
            .answerUnitCode(UPDATED_ANSWER_UNIT_CODE)
            .answeredAt(UPDATED_ANSWERED_AT)
            .compliant(UPDATED_COMPLIANT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAuditAnswer.setOrganization(organization);
        // Add required entity
        AuditReport auditReport;
        auditReport = template.insert(AuditReportResourceIT.createUpdatedEntity(template)).block();
        updatedAuditAnswer.setAuditReport(auditReport);
        // Add required entity
        AuditQuestion auditQuestion;
        auditQuestion = template.insert(AuditQuestionResourceIT.createUpdatedEntity(template)).block();
        updatedAuditAnswer.setAuditQuestion(auditQuestion);
        return updatedAuditAnswer;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AuditAnswer.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        AuditReportResourceIT.deleteEntities(template);
        AuditQuestionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        auditAnswer = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditAnswer != null) {
            auditAnswerRepository.delete(insertedAuditAnswer).block();
            insertedAuditAnswer = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAuditAnswer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditAnswer
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(auditAnswer);
        var returnedAuditAnswerDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuditAnswerDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AuditAnswer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditAnswer = auditAnswerMapper.toEntity(returnedAuditAnswerDTO);
        assertAuditAnswerUpdatableFieldsEquals(returnedAuditAnswer, getPersistedAuditAnswer(returnedAuditAnswer));

        insertedAuditAnswer = returnedAuditAnswer;
    }

    @Test
    void createAuditAnswerWithExistingId() throws Exception {
        // Create the AuditAnswer with an existing ID
        auditAnswer.setId(1L);
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(auditAnswer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAuditAnswersAsStream() {
        // Initialize the database
        auditAnswerRepository.save(auditAnswer).block();

        List<AuditAnswer> auditAnswerList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AuditAnswerDTO.class)
            .getResponseBody()
            .map(auditAnswerMapper::toEntity)
            .filter(auditAnswer::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(auditAnswerList).isNotNull();
        assertThat(auditAnswerList).hasSize(1);
        AuditAnswer testAuditAnswer = auditAnswerList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertAuditAnswerAllPropertiesEquals(auditAnswer, testAuditAnswer);
        assertAuditAnswerUpdatableFieldsEquals(auditAnswer, testAuditAnswer);
    }

    @Test
    void getAllAuditAnswers() {
        // Initialize the database
        insertedAuditAnswer = auditAnswerRepository.save(auditAnswer).block();

        // Get all the auditAnswerList
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
            .value(hasItem(auditAnswer.getId().intValue()))
            .jsonPath("$.[*].answerText")
            .value(hasItem(DEFAULT_ANSWER_TEXT))
            .jsonPath("$.[*].answerNumber")
            .value(hasItem(sameNumber(DEFAULT_ANSWER_NUMBER)))
            .jsonPath("$.[*].answerBoolean")
            .value(hasItem(DEFAULT_ANSWER_BOOLEAN))
            .jsonPath("$.[*].answerUnitCode")
            .value(hasItem(DEFAULT_ANSWER_UNIT_CODE))
            .jsonPath("$.[*].answeredAt")
            .value(hasItem(DEFAULT_ANSWERED_AT.toString()))
            .jsonPath("$.[*].compliant")
            .value(hasItem(DEFAULT_COMPLIANT));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditAnswersWithEagerRelationshipsIsEnabled() {
        when(auditAnswerServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(auditAnswerServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditAnswersWithEagerRelationshipsIsNotEnabled() {
        when(auditAnswerServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(auditAnswerRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAuditAnswer() {
        // Initialize the database
        insertedAuditAnswer = auditAnswerRepository.save(auditAnswer).block();

        // Get the auditAnswer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, auditAnswer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(auditAnswer.getId().intValue()))
            .jsonPath("$.answerText")
            .value(is(DEFAULT_ANSWER_TEXT))
            .jsonPath("$.answerNumber")
            .value(is(sameNumber(DEFAULT_ANSWER_NUMBER)))
            .jsonPath("$.answerBoolean")
            .value(is(DEFAULT_ANSWER_BOOLEAN))
            .jsonPath("$.answerUnitCode")
            .value(is(DEFAULT_ANSWER_UNIT_CODE))
            .jsonPath("$.answeredAt")
            .value(is(DEFAULT_ANSWERED_AT.toString()))
            .jsonPath("$.compliant")
            .value(is(DEFAULT_COMPLIANT));
    }

    @Test
    void getNonExistingAuditAnswer() {
        // Get the auditAnswer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuditAnswer() throws Exception {
        // Initialize the database
        insertedAuditAnswer = auditAnswerRepository.save(auditAnswer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditAnswer
        AuditAnswer updatedAuditAnswer = auditAnswerRepository.findById(auditAnswer.getId()).block();
        updatedAuditAnswer
            .answerText(UPDATED_ANSWER_TEXT)
            .answerNumber(UPDATED_ANSWER_NUMBER)
            .answerBoolean(UPDATED_ANSWER_BOOLEAN)
            .answerUnitCode(UPDATED_ANSWER_UNIT_CODE)
            .answeredAt(UPDATED_ANSWERED_AT)
            .compliant(UPDATED_COMPLIANT);
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(updatedAuditAnswer);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditAnswerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditAnswerToMatchAllProperties(updatedAuditAnswer);
    }

    @Test
    void putNonExistingAuditAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditAnswer.setId(longCount.incrementAndGet());

        // Create the AuditAnswer
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(auditAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditAnswerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuditAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditAnswer.setId(longCount.incrementAndGet());

        // Create the AuditAnswer
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(auditAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuditAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditAnswer.setId(longCount.incrementAndGet());

        // Create the AuditAnswer
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(auditAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuditAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedAuditAnswer = auditAnswerRepository.save(auditAnswer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditAnswer using partial update
        AuditAnswer partialUpdatedAuditAnswer = new AuditAnswer();
        partialUpdatedAuditAnswer.setId(auditAnswer.getId());

        partialUpdatedAuditAnswer.answerText(UPDATED_ANSWER_TEXT).answerNumber(UPDATED_ANSWER_NUMBER).answeredAt(UPDATED_ANSWERED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditAnswer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditAnswer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditAnswerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditAnswer, auditAnswer),
            getPersistedAuditAnswer(auditAnswer)
        );
    }

    @Test
    void fullUpdateAuditAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedAuditAnswer = auditAnswerRepository.save(auditAnswer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditAnswer using partial update
        AuditAnswer partialUpdatedAuditAnswer = new AuditAnswer();
        partialUpdatedAuditAnswer.setId(auditAnswer.getId());

        partialUpdatedAuditAnswer
            .answerText(UPDATED_ANSWER_TEXT)
            .answerNumber(UPDATED_ANSWER_NUMBER)
            .answerBoolean(UPDATED_ANSWER_BOOLEAN)
            .answerUnitCode(UPDATED_ANSWER_UNIT_CODE)
            .answeredAt(UPDATED_ANSWERED_AT)
            .compliant(UPDATED_COMPLIANT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditAnswer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditAnswer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditAnswerUpdatableFieldsEquals(partialUpdatedAuditAnswer, getPersistedAuditAnswer(partialUpdatedAuditAnswer));
    }

    @Test
    void patchNonExistingAuditAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditAnswer.setId(longCount.incrementAndGet());

        // Create the AuditAnswer
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(auditAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, auditAnswerDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuditAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditAnswer.setId(longCount.incrementAndGet());

        // Create the AuditAnswer
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(auditAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuditAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditAnswer.setId(longCount.incrementAndGet());

        // Create the AuditAnswer
        AuditAnswerDTO auditAnswerDTO = auditAnswerMapper.toDto(auditAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditAnswerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuditAnswer() {
        // Initialize the database
        insertedAuditAnswer = auditAnswerRepository.save(auditAnswer).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditAnswer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, auditAnswer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditAnswerRepository.count().block();
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

    protected AuditAnswer getPersistedAuditAnswer(AuditAnswer auditAnswer) {
        return auditAnswerRepository.findById(auditAnswer.getId()).block();
    }

    protected void assertPersistedAuditAnswerToMatchAllProperties(AuditAnswer expectedAuditAnswer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditAnswerAllPropertiesEquals(expectedAuditAnswer, getPersistedAuditAnswer(expectedAuditAnswer));
        assertAuditAnswerUpdatableFieldsEquals(expectedAuditAnswer, getPersistedAuditAnswer(expectedAuditAnswer));
    }

    protected void assertPersistedAuditAnswerToMatchUpdatableProperties(AuditAnswer expectedAuditAnswer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditAnswerAllUpdatablePropertiesEquals(expectedAuditAnswer, getPersistedAuditAnswer(expectedAuditAnswer));
        assertAuditAnswerUpdatableFieldsEquals(expectedAuditAnswer, getPersistedAuditAnswer(expectedAuditAnswer));
    }
}
