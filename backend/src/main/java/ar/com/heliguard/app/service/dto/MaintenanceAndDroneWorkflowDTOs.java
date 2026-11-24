package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.DroneMissionStatus;
import ar.com.heliguard.app.domain.enumeration.VerificationStatus;
import ar.com.heliguard.app.domain.enumeration.WorkOrderStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTOs for Sprint S8 work order acceptance, simulated drone dispatch, and recovery verification.
 */
public final class MaintenanceAndDroneWorkflowDTOs {

    private MaintenanceAndDroneWorkflowDTOs() {}

    public record WorkOrderAcceptResponse(Long workOrderId, Long assignmentId, WorkOrderStatus status) implements Serializable {}

    public record DroneDispatchRequest(
        BigDecimal windSpeedMs,
        boolean icePresent,
        boolean energizedDamagedEquipment
    ) implements Serializable {}

    public record DroneDispatchResponse(
        Long missionId,
        DroneMissionStatus status,
        boolean simulatedOnly,
        boolean physicalHardwareInvoked,
        String note
    ) implements Serializable {}

    public record RecoveryVerifyRequest(
        @NotNull Long interventionId,
        Long droneMissionId,
        Long modelRunId,
        int postSamplesCount,
        boolean irradianceNormalizedAvailable,
        BigDecimal verifiedRecoveredWh
    ) implements Serializable {}

    public record RecoveryVerifyResponse(
        Long recoveryAssessmentId,
        Long verificationResultId,
        VerificationStatus status,
        BigDecimal recoveredWh,
        String summary
    ) implements Serializable {}
}
