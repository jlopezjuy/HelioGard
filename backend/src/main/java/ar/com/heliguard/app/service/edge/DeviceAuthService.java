package ar.com.heliguard.app.service.edge;

import ar.com.heliguard.app.domain.DeviceIdentity;
import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.domain.enumeration.DeviceTrustStatus;
import ar.com.heliguard.app.domain.enumeration.GatewayStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import ar.com.heliguard.app.repository.DeviceIdentityRepository;
import ar.com.heliguard.app.repository.GatewayRepository;
import ar.com.heliguard.app.repository.TelemetryIntegrityEventRepository;
import ar.com.heliguard.app.service.error.DomainRuleException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Authenticates edge Gateways/ESP32 devices using registered {@link DeviceIdentity}
 * credentials (SHA-256 fingerprint) without human JWTs (RB-TEL-001, RB-SEC-003).
 */
@Service
public class DeviceAuthService {

    private final DeviceIdentityRepository deviceIdentityRepository;
    private final GatewayRepository gatewayRepository;
    private final TelemetryIntegrityEventRepository integrityEventRepository;

    public DeviceAuthService(
        DeviceIdentityRepository deviceIdentityRepository,
        GatewayRepository gatewayRepository,
        TelemetryIntegrityEventRepository integrityEventRepository
    ) {
        this.deviceIdentityRepository = deviceIdentityRepository;
        this.gatewayRepository = gatewayRepository;
        this.integrityEventRepository = integrityEventRepository;
    }

    public Mono<AuthenticatedDevice> authenticateDevice(String deviceUid, String rawSecret) {
        if (deviceUid == null || deviceUid.isBlank() || rawSecret == null || rawSecret.isBlank()) {
            return Mono.error(
                new DomainRuleException(HttpStatus.UNAUTHORIZED, "DEVICE_AUTH_FAILED", "Device credentials are required")
            );
        }
        String presentedHash = sha256Hex(rawSecret);
        Instant now = Instant.now();

        return deviceIdentityRepository
            .findOneByDeviceUid(deviceUid.trim())
            .switchIfEmpty(
                Mono.error(new DomainRuleException(HttpStatus.UNAUTHORIZED, "DEVICE_AUTH_FAILED", "Invalid device credentials"))
            )
            .flatMap(device -> {
                if (!isDeviceValid(device, presentedHash, now) || device.getGatewayId() == null) {
                    return recordAuthFailure(device, null, "Invalid, expired, or non-ACTIVE DeviceIdentity")
                        .then(
                            Mono.error(
                                new DomainRuleException(HttpStatus.UNAUTHORIZED, "DEVICE_AUTH_FAILED", "Invalid device credentials")
                            )
                        );
                }
                return gatewayRepository
                    .findById(device.getGatewayId())
                    .filter(gw ->
                        gw.getStatus() != GatewayStatus.REVOKED &&
                        device.getOrganizationId().equals(gw.getOrganizationId())
                    )
                    .switchIfEmpty(
                        recordAuthFailure(device, null, "Gateway missing, revoked, or tenant-mismatched")
                            .then(
                                Mono.error(
                                    new DomainRuleException(HttpStatus.UNAUTHORIZED, "DEVICE_AUTH_FAILED", "Gateway not authorized")
                                )
                            )
                    )
                    .flatMap(gw -> {
                        gw.setLastSeenAt(now);
                        if (gw.getStatus() == GatewayStatus.PROVISIONING || gw.getStatus() == GatewayStatus.OFFLINE) {
                            gw.setStatus(GatewayStatus.ONLINE);
                        }
                        return gatewayRepository
                            .save(gw)
                            .thenReturn(
                                new AuthenticatedDevice(
                                    gw.getOrganizationId(),
                                    gw.getInstallationId(),
                                    gw.getId(),
                                    device.getId()
                                )
                            );
                    });
            });
    }

    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private boolean isDeviceValid(DeviceIdentity device, String presentedHash, Instant now) {
        if (device.getTrustStatus() != DeviceTrustStatus.ACTIVE) {
            return false;
        }
        if (device.getValidUntil() != null && !device.getValidUntil().isAfter(now)) {
            return false;
        }
        if (device.getKeyFingerprint() == null || device.getKeyFingerprint().isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
            device.getKeyFingerprint().toLowerCase().getBytes(StandardCharsets.UTF_8),
            presentedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private Mono<Void> recordAuthFailure(DeviceIdentity device, Gateway gw, String detail) {
        if (device.getOrganizationId() == null) {
            return Mono.empty();
        }
        Long installationId = gw != null ? gw.getInstallationId() : null;
        if (installationId == null && device.getGatewayId() != null) {
            return gatewayRepository
                .findById(device.getGatewayId())
                .flatMap(foundGw -> saveAuthFailureEvent(device, foundGw.getInstallationId(), detail))
                .onErrorResume(err -> Mono.empty());
        }
        if (installationId == null) {
            return Mono.empty();
        }
        return saveAuthFailureEvent(device, installationId, detail).onErrorResume(err -> Mono.empty());
    }

    private Mono<Void> saveAuthFailureEvent(DeviceIdentity device, Long installationId, String detail) {
        if (installationId == null) {
            return Mono.empty();
        }
        TelemetryIntegrityEvent ev = new TelemetryIntegrityEvent();
        ev.setEventUid("auth-fail-" + UUID.randomUUID());
        ev.setEventType(IntegrityEventType.AUTH_FAILURE);
        ev.setStatus(IntegrityEventStatus.OBSERVED);
        ev.setObservedAt(Instant.now());
        ev.setOrganizationId(device.getOrganizationId());
        ev.setInstallationId(installationId);
        ev.setDeviceIdentityId(device.getId());
        ev.setDetail(detail);
        return integrityEventRepository.save(ev).then();
    }

    public record AuthenticatedDevice(Long organizationId, Long installationId, Long gatewayId, Long deviceIdentityId) {}
}
