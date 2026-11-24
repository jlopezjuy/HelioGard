package ar.com.heliguard.app.service.topology;

import ar.com.heliguard.app.domain.enumeration.LocalizationScope;
import ar.com.heliguard.app.repository.SolarArrayRepository;
import ar.com.heliguard.app.repository.SolarStringRepository;
import ar.com.heliguard.app.repository.StringInputAssignmentRepository;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Resolves fault/anomaly localization precision strictly from available topology
 * and telemetry evidence without fabricating module/string precision (RB-LOC-001, RB-ASSET-002).
 */
@Service
public class TopologyLocalizationService {

    private final TenantContextService tenantContextService;
    private final SolarStringRepository solarStringRepository;
    private final SolarArrayRepository solarArrayRepository;
    private final StringInputAssignmentRepository assignmentRepository;

    public TopologyLocalizationService(
        TenantContextService tenantContextService,
        SolarStringRepository solarStringRepository,
        SolarArrayRepository solarArrayRepository,
        StringInputAssignmentRepository assignmentRepository
    ) {
        this.tenantContextService = tenantContextService;
        this.solarStringRepository = solarStringRepository;
        this.solarArrayRepository = solarArrayRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public LocalizationScope resolvePrecision(
        boolean hasModuleTelemetry,
        boolean hasStringTelemetry,
        boolean hasInverterTelemetry,
        boolean hasSectorMapping,
        boolean hasInstallation
    ) {
        if (hasModuleTelemetry && hasStringTelemetry) {
            return LocalizationScope.MODULE;
        }
        if (hasStringTelemetry) {
            return LocalizationScope.STRING;
        }
        if (hasInverterTelemetry) {
            return LocalizationScope.INVERTER;
        }
        if (hasSectorMapping && hasInstallation) {
            return LocalizationScope.SECTOR;
        }
        if (hasInstallation) {
            return LocalizationScope.INSTALLATION;
        }
        return LocalizationScope.UNKNOWN;
    }

    @Transactional(readOnly = true)
    public Mono<LocalizationScope> resolvePrecisionForString(
        Long solarStringId,
        Instant at,
        boolean hasModuleTelemetry,
        boolean hasStringTelemetry,
        boolean hasInverterTelemetry
    ) {
        return tenantContextService
            .requireCurrentTenant()
            .flatMap(tenant ->
                solarStringRepository
                    .findById(solarStringId)
                    .filter(s -> tenant.organizationId().equals(s.getOrganizationId()))
                    .flatMap(s ->
                        solarArrayRepository
                            .findById(s.getSolarArrayId())
                            .filter(arr -> tenant.organizationId().equals(arr.getOrganizationId()))
                            .flatMap(arr ->
                                assignmentRepository
                                    .findBySolarString(solarStringId)
                                    .filter(a ->
                                        tenant.organizationId().equals(a.getOrganizationId()) &&
                                        !a.getValidFrom().isAfter(at) &&
                                        (a.getValidUntil() == null || a.getValidUntil().isAfter(at))
                                    )
                                    .hasElements()
                                    .map(hasActiveMppt ->
                                        resolvePrecision(
                                            hasModuleTelemetry,
                                            hasStringTelemetry && hasActiveMppt,
                                            hasInverterTelemetry && hasActiveMppt,
                                            arr.getSolarSectorId() != null,
                                            arr.getInstallationId() != null
                                        )
                                    )
                            )
                    )
                    .defaultIfEmpty(LocalizationScope.UNKNOWN)
            );
    }
}
