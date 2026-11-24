package ar.com.heliguard.app.web.rest;

import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryIngestRequest;
import ar.com.heliguard.app.service.dto.TelemetryIngestDTOs.TelemetryIngestResponse;
import ar.com.heliguard.app.service.edge.EdgeIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * M2M telemetry ingestion endpoint for registered Gateways / ESP32 devices.
 */
@RestController
@RequestMapping("/api/ingest")
public class EdgeTelemetryResource {

    private final EdgeIngestionService edgeIngestionService;

    public EdgeTelemetryResource(EdgeIngestionService edgeIngestionService) {
        this.edgeIngestionService = edgeIngestionService;
    }

    @PostMapping("/telemetry")
    public Mono<ResponseEntity<TelemetryIngestResponse>> ingestTelemetry(
        @RequestHeader(value = "X-Device-Uid", required = false) String deviceUid,
        @RequestHeader(value = "X-Device-Secret", required = false) String deviceSecret,
        @Valid @RequestBody TelemetryIngestRequest request
    ) {
        return edgeIngestionService.ingest(deviceUid, deviceSecret, request).map(ResponseEntity::ok);
    }
}
