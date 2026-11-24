package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.EquipmentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.EquipmentModel;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.EquipmentStatus;
import ar.com.heliguard.app.repository.EquipmentRepository;
import ar.com.heliguard.app.service.EquipmentService;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.mapper.EquipmentMapper;
import java.time.LocalDate;
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
 * Integration tests for the {@link EquipmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EquipmentResourceIT {

    private static final String DEFAULT_TAG = "AAAAAAAAAA";
    private static final String UPDATED_TAG = "BBBBBBBBBB";

    private static final String DEFAULT_SERIAL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_NUMBER = "BBBBBBBBBB";

    private static final EquipmentStatus DEFAULT_STATUS = EquipmentStatus.ACTIVE;
    private static final EquipmentStatus UPDATED_STATUS = EquipmentStatus.FAILED;

    private static final LocalDate DEFAULT_INSTALLED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_INSTALLED_ON = LocalDate.parse("2026-11-22");

    private static final LocalDate DEFAULT_REMOVED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REMOVED_ON = LocalDate.parse("2026-11-22");

    private static final String ENTITY_API_URL = "/api/equipment";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentRepository equipmentRepositoryMock;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Mock
    private EquipmentService equipmentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Equipment equipment;

    private Equipment insertedEquipment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipment createEntity(R2dbcEntityTemplate template) {
        Equipment equipment = new Equipment()
            .tag(DEFAULT_TAG)
            .serialNumber(DEFAULT_SERIAL_NUMBER)
            .status(DEFAULT_STATUS)
            .installedOn(DEFAULT_INSTALLED_ON)
            .removedOn(DEFAULT_REMOVED_ON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        equipment.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        equipment.setInstallation(installation);
        // Add required entity
        EquipmentModel equipmentModel;
        equipmentModel = template.insert(EquipmentModelResourceIT.createEntity()).block();
        equipment.setEquipmentModel(equipmentModel);
        return equipment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipment createUpdatedEntity(R2dbcEntityTemplate template) {
        Equipment updatedEquipment = new Equipment()
            .tag(UPDATED_TAG)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .status(UPDATED_STATUS)
            .installedOn(UPDATED_INSTALLED_ON)
            .removedOn(UPDATED_REMOVED_ON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedEquipment.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedEquipment.setInstallation(installation);
        // Add required entity
        EquipmentModel equipmentModel;
        equipmentModel = template.insert(EquipmentModelResourceIT.createUpdatedEntity()).block();
        updatedEquipment.setEquipmentModel(equipmentModel);
        return updatedEquipment;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Equipment.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
        EquipmentModelResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        equipment = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedEquipment != null) {
            equipmentRepository.delete(insertedEquipment).block();
            insertedEquipment = null;
        }
        deleteEntities(template);
    }

    @Test
    void createEquipment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);
        var returnedEquipmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EquipmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Equipment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEquipment = equipmentMapper.toEntity(returnedEquipmentDTO);
        assertEquipmentUpdatableFieldsEquals(returnedEquipment, getPersistedEquipment(returnedEquipment));

        insertedEquipment = returnedEquipment;
    }

    @Test
    void createEquipmentWithExistingId() throws Exception {
        // Create the Equipment with an existing ID
        equipment.setId(1L);
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTagIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipment.setTag(null);

        // Create the Equipment, which fails.
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipment.setStatus(null);

        // Create the Equipment, which fails.
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEquipments() {
        // Initialize the database
        insertedEquipment = equipmentRepository.save(equipment).block();

        // Get all the equipmentList
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
            .value(hasItem(equipment.getId().intValue()))
            .jsonPath("$.[*].tag")
            .value(hasItem(DEFAULT_TAG))
            .jsonPath("$.[*].serialNumber")
            .value(hasItem(DEFAULT_SERIAL_NUMBER))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].installedOn")
            .value(hasItem(DEFAULT_INSTALLED_ON.toString()))
            .jsonPath("$.[*].removedOn")
            .value(hasItem(DEFAULT_REMOVED_ON.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipmentsWithEagerRelationshipsIsEnabled() {
        when(equipmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(equipmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipmentsWithEagerRelationshipsIsNotEnabled() {
        when(equipmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(equipmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEquipment() {
        // Initialize the database
        insertedEquipment = equipmentRepository.save(equipment).block();

        // Get the equipment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, equipment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(equipment.getId().intValue()))
            .jsonPath("$.tag")
            .value(is(DEFAULT_TAG))
            .jsonPath("$.serialNumber")
            .value(is(DEFAULT_SERIAL_NUMBER))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.installedOn")
            .value(is(DEFAULT_INSTALLED_ON.toString()))
            .jsonPath("$.removedOn")
            .value(is(DEFAULT_REMOVED_ON.toString()));
    }

    @Test
    void getNonExistingEquipment() {
        // Get the equipment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEquipment() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.save(equipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipment
        Equipment updatedEquipment = equipmentRepository.findById(equipment.getId()).block();
        updatedEquipment
            .tag(UPDATED_TAG)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .status(UPDATED_STATUS)
            .installedOn(UPDATED_INSTALLED_ON)
            .removedOn(UPDATED_REMOVED_ON);
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(updatedEquipment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEquipmentToMatchAllProperties(updatedEquipment);
    }

    @Test
    void putNonExistingEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEquipmentWithPatch() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.save(equipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipment using partial update
        Equipment partialUpdatedEquipment = new Equipment();
        partialUpdatedEquipment.setId(equipment.getId());

        partialUpdatedEquipment.tag(UPDATED_TAG).status(UPDATED_STATUS).installedOn(UPDATED_INSTALLED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEquipment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEquipment, equipment),
            getPersistedEquipment(equipment)
        );
    }

    @Test
    void fullUpdateEquipmentWithPatch() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.save(equipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipment using partial update
        Equipment partialUpdatedEquipment = new Equipment();
        partialUpdatedEquipment.setId(equipment.getId());

        partialUpdatedEquipment
            .tag(UPDATED_TAG)
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .status(UPDATED_STATUS)
            .installedOn(UPDATED_INSTALLED_ON)
            .removedOn(UPDATED_REMOVED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEquipment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentUpdatableFieldsEquals(partialUpdatedEquipment, getPersistedEquipment(partialUpdatedEquipment));
    }

    @Test
    void patchNonExistingEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, equipmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(equipmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEquipment() {
        // Initialize the database
        insertedEquipment = equipmentRepository.save(equipment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the equipment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, equipment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return equipmentRepository.count().block();
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

    protected Equipment getPersistedEquipment(Equipment equipment) {
        return equipmentRepository.findById(equipment.getId()).block();
    }

    protected void assertPersistedEquipmentToMatchAllProperties(Equipment expectedEquipment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEquipmentAllPropertiesEquals(expectedEquipment, getPersistedEquipment(expectedEquipment));
        assertEquipmentUpdatableFieldsEquals(expectedEquipment, getPersistedEquipment(expectedEquipment));
    }

    protected void assertPersistedEquipmentToMatchUpdatableProperties(Equipment expectedEquipment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEquipmentAllUpdatablePropertiesEquals(expectedEquipment, getPersistedEquipment(expectedEquipment));
        assertEquipmentUpdatableFieldsEquals(expectedEquipment, getPersistedEquipment(expectedEquipment));
    }
}
