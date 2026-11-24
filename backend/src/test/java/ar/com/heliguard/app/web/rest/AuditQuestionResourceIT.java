package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AuditQuestionAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AuditQuestion;
import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.AuditQuestionRepository;
import ar.com.heliguard.app.service.AuditQuestionService;
import ar.com.heliguard.app.service.dto.AuditQuestionDTO;
import ar.com.heliguard.app.service.mapper.AuditQuestionMapper;
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
 * Integration tests for the {@link AuditQuestionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuditQuestionResourceIT {

    private static final String DEFAULT_QUESTION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_ANSWER_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final Boolean DEFAULT_MANDATORY = false;
    private static final Boolean UPDATED_MANDATORY = true;

    private static final String ENTITY_API_URL = "/api/audit-questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditQuestionRepository auditQuestionRepository;

    @Mock
    private AuditQuestionRepository auditQuestionRepositoryMock;

    @Autowired
    private AuditQuestionMapper auditQuestionMapper;

    @Mock
    private AuditQuestionService auditQuestionServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AuditQuestion auditQuestion;

    private AuditQuestion insertedAuditQuestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditQuestion createEntity(R2dbcEntityTemplate template) {
        AuditQuestion auditQuestion = new AuditQuestion()
            .questionCode(DEFAULT_QUESTION_CODE)
            .label(DEFAULT_LABEL)
            .answerType(DEFAULT_ANSWER_TYPE)
            .sortOrder(DEFAULT_SORT_ORDER)
            .mandatory(DEFAULT_MANDATORY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        auditQuestion.setOrganization(organization);
        // Add required entity
        AuditTemplate auditTemplate;
        auditTemplate = template.insert(AuditTemplateResourceIT.createEntity(template)).block();
        auditQuestion.setAuditTemplate(auditTemplate);
        return auditQuestion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditQuestion createUpdatedEntity(R2dbcEntityTemplate template) {
        AuditQuestion updatedAuditQuestion = new AuditQuestion()
            .questionCode(UPDATED_QUESTION_CODE)
            .label(UPDATED_LABEL)
            .answerType(UPDATED_ANSWER_TYPE)
            .sortOrder(UPDATED_SORT_ORDER)
            .mandatory(UPDATED_MANDATORY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAuditQuestion.setOrganization(organization);
        // Add required entity
        AuditTemplate auditTemplate;
        auditTemplate = template.insert(AuditTemplateResourceIT.createUpdatedEntity(template)).block();
        updatedAuditQuestion.setAuditTemplate(auditTemplate);
        return updatedAuditQuestion;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AuditQuestion.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        AuditTemplateResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        auditQuestion = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditQuestion != null) {
            auditQuestionRepository.delete(insertedAuditQuestion).block();
            insertedAuditQuestion = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAuditQuestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditQuestion
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);
        var returnedAuditQuestionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuditQuestionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AuditQuestion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditQuestion = auditQuestionMapper.toEntity(returnedAuditQuestionDTO);
        assertAuditQuestionUpdatableFieldsEquals(returnedAuditQuestion, getPersistedAuditQuestion(returnedAuditQuestion));

        insertedAuditQuestion = returnedAuditQuestion;
    }

    @Test
    void createAuditQuestionWithExistingId() throws Exception {
        // Create the AuditQuestion with an existing ID
        auditQuestion.setId(1L);
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuestionCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditQuestion.setQuestionCode(null);

        // Create the AuditQuestion, which fails.
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditQuestion.setLabel(null);

        // Create the AuditQuestion, which fails.
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAnswerTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditQuestion.setAnswerType(null);

        // Create the AuditQuestion, which fails.
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSortOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditQuestion.setSortOrder(null);

        // Create the AuditQuestion, which fails.
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMandatoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditQuestion.setMandatory(null);

        // Create the AuditQuestion, which fails.
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAuditQuestionsAsStream() {
        // Initialize the database
        auditQuestionRepository.save(auditQuestion).block();

        List<AuditQuestion> auditQuestionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AuditQuestionDTO.class)
            .getResponseBody()
            .map(auditQuestionMapper::toEntity)
            .filter(auditQuestion::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(auditQuestionList).isNotNull();
        assertThat(auditQuestionList).hasSize(1);
        AuditQuestion testAuditQuestion = auditQuestionList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertAuditQuestionAllPropertiesEquals(auditQuestion, testAuditQuestion);
        assertAuditQuestionUpdatableFieldsEquals(auditQuestion, testAuditQuestion);
    }

    @Test
    void getAllAuditQuestions() {
        // Initialize the database
        insertedAuditQuestion = auditQuestionRepository.save(auditQuestion).block();

        // Get all the auditQuestionList
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
            .value(hasItem(auditQuestion.getId().intValue()))
            .jsonPath("$.[*].questionCode")
            .value(hasItem(DEFAULT_QUESTION_CODE))
            .jsonPath("$.[*].label")
            .value(hasItem(DEFAULT_LABEL))
            .jsonPath("$.[*].answerType")
            .value(hasItem(DEFAULT_ANSWER_TYPE))
            .jsonPath("$.[*].sortOrder")
            .value(hasItem(DEFAULT_SORT_ORDER))
            .jsonPath("$.[*].mandatory")
            .value(hasItem(DEFAULT_MANDATORY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditQuestionsWithEagerRelationshipsIsEnabled() {
        when(auditQuestionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(auditQuestionServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditQuestionsWithEagerRelationshipsIsNotEnabled() {
        when(auditQuestionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(auditQuestionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAuditQuestion() {
        // Initialize the database
        insertedAuditQuestion = auditQuestionRepository.save(auditQuestion).block();

        // Get the auditQuestion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, auditQuestion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(auditQuestion.getId().intValue()))
            .jsonPath("$.questionCode")
            .value(is(DEFAULT_QUESTION_CODE))
            .jsonPath("$.label")
            .value(is(DEFAULT_LABEL))
            .jsonPath("$.answerType")
            .value(is(DEFAULT_ANSWER_TYPE))
            .jsonPath("$.sortOrder")
            .value(is(DEFAULT_SORT_ORDER))
            .jsonPath("$.mandatory")
            .value(is(DEFAULT_MANDATORY));
    }

    @Test
    void getNonExistingAuditQuestion() {
        // Get the auditQuestion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuditQuestion() throws Exception {
        // Initialize the database
        insertedAuditQuestion = auditQuestionRepository.save(auditQuestion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditQuestion
        AuditQuestion updatedAuditQuestion = auditQuestionRepository.findById(auditQuestion.getId()).block();
        updatedAuditQuestion
            .questionCode(UPDATED_QUESTION_CODE)
            .label(UPDATED_LABEL)
            .answerType(UPDATED_ANSWER_TYPE)
            .sortOrder(UPDATED_SORT_ORDER)
            .mandatory(UPDATED_MANDATORY);
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(updatedAuditQuestion);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditQuestionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditQuestionToMatchAllProperties(updatedAuditQuestion);
    }

    @Test
    void putNonExistingAuditQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditQuestion.setId(longCount.incrementAndGet());

        // Create the AuditQuestion
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditQuestionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuditQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditQuestion.setId(longCount.incrementAndGet());

        // Create the AuditQuestion
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuditQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditQuestion.setId(longCount.incrementAndGet());

        // Create the AuditQuestion
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuditQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedAuditQuestion = auditQuestionRepository.save(auditQuestion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditQuestion using partial update
        AuditQuestion partialUpdatedAuditQuestion = new AuditQuestion();
        partialUpdatedAuditQuestion.setId(auditQuestion.getId());

        partialUpdatedAuditQuestion.label(UPDATED_LABEL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditQuestion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditQuestion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditQuestionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditQuestion, auditQuestion),
            getPersistedAuditQuestion(auditQuestion)
        );
    }

    @Test
    void fullUpdateAuditQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedAuditQuestion = auditQuestionRepository.save(auditQuestion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditQuestion using partial update
        AuditQuestion partialUpdatedAuditQuestion = new AuditQuestion();
        partialUpdatedAuditQuestion.setId(auditQuestion.getId());

        partialUpdatedAuditQuestion
            .questionCode(UPDATED_QUESTION_CODE)
            .label(UPDATED_LABEL)
            .answerType(UPDATED_ANSWER_TYPE)
            .sortOrder(UPDATED_SORT_ORDER)
            .mandatory(UPDATED_MANDATORY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditQuestion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditQuestion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditQuestionUpdatableFieldsEquals(partialUpdatedAuditQuestion, getPersistedAuditQuestion(partialUpdatedAuditQuestion));
    }

    @Test
    void patchNonExistingAuditQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditQuestion.setId(longCount.incrementAndGet());

        // Create the AuditQuestion
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, auditQuestionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuditQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditQuestion.setId(longCount.incrementAndGet());

        // Create the AuditQuestion
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuditQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditQuestion.setId(longCount.incrementAndGet());

        // Create the AuditQuestion
        AuditQuestionDTO auditQuestionDTO = auditQuestionMapper.toDto(auditQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditQuestionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuditQuestion() {
        // Initialize the database
        insertedAuditQuestion = auditQuestionRepository.save(auditQuestion).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditQuestion
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, auditQuestion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditQuestionRepository.count().block();
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

    protected AuditQuestion getPersistedAuditQuestion(AuditQuestion auditQuestion) {
        return auditQuestionRepository.findById(auditQuestion.getId()).block();
    }

    protected void assertPersistedAuditQuestionToMatchAllProperties(AuditQuestion expectedAuditQuestion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditQuestionAllPropertiesEquals(expectedAuditQuestion, getPersistedAuditQuestion(expectedAuditQuestion));
        assertAuditQuestionUpdatableFieldsEquals(expectedAuditQuestion, getPersistedAuditQuestion(expectedAuditQuestion));
    }

    protected void assertPersistedAuditQuestionToMatchUpdatableProperties(AuditQuestion expectedAuditQuestion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditQuestionAllUpdatablePropertiesEquals(expectedAuditQuestion, getPersistedAuditQuestion(expectedAuditQuestion));
        assertAuditQuestionUpdatableFieldsEquals(expectedAuditQuestion, getPersistedAuditQuestion(expectedAuditQuestion));
    }
}
