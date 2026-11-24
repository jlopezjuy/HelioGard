package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.TariffRateAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffPlan;
import ar.com.heliguard.app.domain.TariffRate;
import ar.com.heliguard.app.repository.TariffRateRepository;
import ar.com.heliguard.app.service.TariffRateService;
import ar.com.heliguard.app.service.dto.TariffRateDTO;
import ar.com.heliguard.app.service.mapper.TariffRateMapper;
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
 * Integration tests for the {@link TariffRateResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TariffRateResourceIT {

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_TO = Instant.ofEpochMilli(1795370143700L);

    private static final BigDecimal DEFAULT_PURCHASE_PRICE_PER_KWH = new BigDecimal(1);
    private static final BigDecimal UPDATED_PURCHASE_PRICE_PER_KWH = new BigDecimal(2);

    private static final BigDecimal DEFAULT_EXPORT_PRICE_PER_KWH = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPORT_PRICE_PER_KWH = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/tariff-rates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TariffRateRepository tariffRateRepository;

    @Mock
    private TariffRateRepository tariffRateRepositoryMock;

    @Autowired
    private TariffRateMapper tariffRateMapper;

    @Mock
    private TariffRateService tariffRateServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private TariffRate tariffRate;

    private TariffRate insertedTariffRate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TariffRate createEntity(R2dbcEntityTemplate template) {
        TariffRate tariffRate = new TariffRate()
            .validFrom(DEFAULT_VALID_FROM)
            .validTo(DEFAULT_VALID_TO)
            .purchasePricePerKwh(DEFAULT_PURCHASE_PRICE_PER_KWH)
            .exportPricePerKwh(DEFAULT_EXPORT_PRICE_PER_KWH);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        tariffRate.setOrganization(organization);
        // Add required entity
        TariffPlan tariffPlan;
        tariffPlan = template.insert(TariffPlanResourceIT.createEntity(template)).block();
        tariffRate.setTariffPlan(tariffPlan);
        return tariffRate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TariffRate createUpdatedEntity(R2dbcEntityTemplate template) {
        TariffRate updatedTariffRate = new TariffRate()
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .purchasePricePerKwh(UPDATED_PURCHASE_PRICE_PER_KWH)
            .exportPricePerKwh(UPDATED_EXPORT_PRICE_PER_KWH);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedTariffRate.setOrganization(organization);
        // Add required entity
        TariffPlan tariffPlan;
        tariffPlan = template.insert(TariffPlanResourceIT.createUpdatedEntity(template)).block();
        updatedTariffRate.setTariffPlan(tariffPlan);
        return updatedTariffRate;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(TariffRate.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        TariffPlanResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        tariffRate = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedTariffRate != null) {
            tariffRateRepository.delete(insertedTariffRate).block();
            insertedTariffRate = null;
        }
        deleteEntities(template);
    }

    @Test
    void createTariffRate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TariffRate
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);
        var returnedTariffRateDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TariffRateDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TariffRate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTariffRate = tariffRateMapper.toEntity(returnedTariffRateDTO);
        assertTariffRateUpdatableFieldsEquals(returnedTariffRate, getPersistedTariffRate(returnedTariffRate));

        insertedTariffRate = returnedTariffRate;
    }

    @Test
    void createTariffRateWithExistingId() throws Exception {
        // Create the TariffRate with an existing ID
        tariffRate.setId(1L);
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffRate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tariffRate.setValidFrom(null);

        // Create the TariffRate, which fails.
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPurchasePricePerKwhIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tariffRate.setPurchasePricePerKwh(null);

        // Create the TariffRate, which fails.
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTariffRatesAsStream() {
        // Initialize the database
        tariffRateRepository.save(tariffRate).block();

        List<TariffRate> tariffRateList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TariffRateDTO.class)
            .getResponseBody()
            .map(tariffRateMapper::toEntity)
            .filter(tariffRate::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tariffRateList).isNotNull();
        assertThat(tariffRateList).hasSize(1);
        TariffRate testTariffRate = tariffRateList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertTariffRateAllPropertiesEquals(tariffRate, testTariffRate);
        assertTariffRateUpdatableFieldsEquals(tariffRate, testTariffRate);
    }

    @Test
    void getAllTariffRates() {
        // Initialize the database
        insertedTariffRate = tariffRateRepository.save(tariffRate).block();

        // Get all the tariffRateList
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
            .value(hasItem(tariffRate.getId().intValue()))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validTo")
            .value(hasItem(DEFAULT_VALID_TO.toString()))
            .jsonPath("$.[*].purchasePricePerKwh")
            .value(hasItem(sameNumber(DEFAULT_PURCHASE_PRICE_PER_KWH)))
            .jsonPath("$.[*].exportPricePerKwh")
            .value(hasItem(sameNumber(DEFAULT_EXPORT_PRICE_PER_KWH)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTariffRatesWithEagerRelationshipsIsEnabled() {
        when(tariffRateServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(tariffRateServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTariffRatesWithEagerRelationshipsIsNotEnabled() {
        when(tariffRateServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(tariffRateRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTariffRate() {
        // Initialize the database
        insertedTariffRate = tariffRateRepository.save(tariffRate).block();

        // Get the tariffRate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tariffRate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tariffRate.getId().intValue()))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validTo")
            .value(is(DEFAULT_VALID_TO.toString()))
            .jsonPath("$.purchasePricePerKwh")
            .value(is(sameNumber(DEFAULT_PURCHASE_PRICE_PER_KWH)))
            .jsonPath("$.exportPricePerKwh")
            .value(is(sameNumber(DEFAULT_EXPORT_PRICE_PER_KWH)));
    }

    @Test
    void getNonExistingTariffRate() {
        // Get the tariffRate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTariffRate() throws Exception {
        // Initialize the database
        insertedTariffRate = tariffRateRepository.save(tariffRate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tariffRate
        TariffRate updatedTariffRate = tariffRateRepository.findById(tariffRate.getId()).block();
        updatedTariffRate
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .purchasePricePerKwh(UPDATED_PURCHASE_PRICE_PER_KWH)
            .exportPricePerKwh(UPDATED_EXPORT_PRICE_PER_KWH);
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(updatedTariffRate);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tariffRateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TariffRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTariffRateToMatchAllProperties(updatedTariffRate);
    }

    @Test
    void putNonExistingTariffRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffRate.setId(longCount.incrementAndGet());

        // Create the TariffRate
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tariffRateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTariffRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffRate.setId(longCount.incrementAndGet());

        // Create the TariffRate
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTariffRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffRate.setId(longCount.incrementAndGet());

        // Create the TariffRate
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TariffRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTariffRateWithPatch() throws Exception {
        // Initialize the database
        insertedTariffRate = tariffRateRepository.save(tariffRate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tariffRate using partial update
        TariffRate partialUpdatedTariffRate = new TariffRate();
        partialUpdatedTariffRate.setId(tariffRate.getId());

        partialUpdatedTariffRate.exportPricePerKwh(UPDATED_EXPORT_PRICE_PER_KWH);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTariffRate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTariffRate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TariffRate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTariffRateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTariffRate, tariffRate),
            getPersistedTariffRate(tariffRate)
        );
    }

    @Test
    void fullUpdateTariffRateWithPatch() throws Exception {
        // Initialize the database
        insertedTariffRate = tariffRateRepository.save(tariffRate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tariffRate using partial update
        TariffRate partialUpdatedTariffRate = new TariffRate();
        partialUpdatedTariffRate.setId(tariffRate.getId());

        partialUpdatedTariffRate
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .purchasePricePerKwh(UPDATED_PURCHASE_PRICE_PER_KWH)
            .exportPricePerKwh(UPDATED_EXPORT_PRICE_PER_KWH);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTariffRate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTariffRate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TariffRate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTariffRateUpdatableFieldsEquals(partialUpdatedTariffRate, getPersistedTariffRate(partialUpdatedTariffRate));
    }

    @Test
    void patchNonExistingTariffRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffRate.setId(longCount.incrementAndGet());

        // Create the TariffRate
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tariffRateDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTariffRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffRate.setId(longCount.incrementAndGet());

        // Create the TariffRate
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TariffRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTariffRate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tariffRate.setId(longCount.incrementAndGet());

        // Create the TariffRate
        TariffRateDTO tariffRateDTO = tariffRateMapper.toDto(tariffRate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tariffRateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TariffRate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTariffRate() {
        // Initialize the database
        insertedTariffRate = tariffRateRepository.save(tariffRate).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tariffRate
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tariffRate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tariffRateRepository.count().block();
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

    protected TariffRate getPersistedTariffRate(TariffRate tariffRate) {
        return tariffRateRepository.findById(tariffRate.getId()).block();
    }

    protected void assertPersistedTariffRateToMatchAllProperties(TariffRate expectedTariffRate) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTariffRateAllPropertiesEquals(expectedTariffRate, getPersistedTariffRate(expectedTariffRate));
        assertTariffRateUpdatableFieldsEquals(expectedTariffRate, getPersistedTariffRate(expectedTariffRate));
    }

    protected void assertPersistedTariffRateToMatchUpdatableProperties(TariffRate expectedTariffRate) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTariffRateAllUpdatablePropertiesEquals(expectedTariffRate, getPersistedTariffRate(expectedTariffRate));
        assertTariffRateUpdatableFieldsEquals(expectedTariffRate, getPersistedTariffRate(expectedTariffRate));
    }
}
