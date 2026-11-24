package ar.com.heliguard.app.domain.enumeration;

/**
 * The DroneMissionStatus enumeration.
 */
public enum DroneMissionStatus {
    DRAFT,
    SAFETY_REVIEW,
    AWAITING_AUTHORIZATION,
    AUTHORIZED,
    DISPATCHED,
    IN_FLIGHT,
    COMPLETED,
    ABORTED,
}
