package ar.com.heliguard.app.domain.enumeration;

/**
 * The IntegrityEventType enumeration.
 */
public enum IntegrityEventType {
    REPLAY,
    DUPLICATE_EVENT,
    SEQUENCE_GAP,
    SIGNATURE_INVALID,
    AUTH_FAILURE,
    CLOCK_SKEW,
    PHYSICAL_INCONSISTENCY,
    DEVICE_UNAVAILABLE,
}
