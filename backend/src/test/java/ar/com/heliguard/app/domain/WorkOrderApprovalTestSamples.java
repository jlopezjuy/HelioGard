package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WorkOrderApprovalTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static WorkOrderApproval getWorkOrderApprovalSample1() {
        return new WorkOrderApproval().id(1L).consentReference("consentReference1").decisionNote("decisionNote1");
    }

    public static WorkOrderApproval getWorkOrderApprovalSample2() {
        return new WorkOrderApproval().id(2L).consentReference("consentReference2").decisionNote("decisionNote2");
    }

    public static WorkOrderApproval getWorkOrderApprovalRandomSampleGenerator() {
        return new WorkOrderApproval()
            .id(longCount.incrementAndGet())
            .consentReference(UUID.randomUUID().toString())
            .decisionNote(UUID.randomUUID().toString());
    }
}
