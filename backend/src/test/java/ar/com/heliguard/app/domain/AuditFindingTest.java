package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AuditFindingTestSamples.*;
import static ar.com.heliguard.app.domain.AuditReportTestSamples.*;
import static ar.com.heliguard.app.domain.EquipmentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditFindingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditFinding.class);
        AuditFinding auditFinding1 = getAuditFindingSample1();
        AuditFinding auditFinding2 = new AuditFinding();
        assertThat(auditFinding1).isNotEqualTo(auditFinding2);

        auditFinding2.setId(auditFinding1.getId());
        assertThat(auditFinding1).isEqualTo(auditFinding2);

        auditFinding2 = getAuditFindingSample2();
        assertThat(auditFinding1).isNotEqualTo(auditFinding2);
    }

    @Test
    void organizationTest() {
        AuditFinding auditFinding = getAuditFindingRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        auditFinding.setOrganization(organizationBack);
        assertThat(auditFinding.getOrganization()).isEqualTo(organizationBack);

        auditFinding.organization(null);
        assertThat(auditFinding.getOrganization()).isNull();
    }

    @Test
    void auditReportTest() {
        AuditFinding auditFinding = getAuditFindingRandomSampleGenerator();
        AuditReport auditReportBack = getAuditReportRandomSampleGenerator();

        auditFinding.setAuditReport(auditReportBack);
        assertThat(auditFinding.getAuditReport()).isEqualTo(auditReportBack);

        auditFinding.auditReport(null);
        assertThat(auditFinding.getAuditReport()).isNull();
    }

    @Test
    void equipmentTest() {
        AuditFinding auditFinding = getAuditFindingRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        auditFinding.setEquipment(equipmentBack);
        assertThat(auditFinding.getEquipment()).isEqualTo(equipmentBack);

        auditFinding.equipment(null);
        assertThat(auditFinding.getEquipment()).isNull();
    }
}
