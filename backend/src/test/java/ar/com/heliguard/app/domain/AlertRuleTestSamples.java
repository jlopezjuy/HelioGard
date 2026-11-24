package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AlertRuleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AlertRule getAlertRuleSample1() {
        return new AlertRule().id(1L).name("name1").ruleCode("ruleCode1");
    }

    public static AlertRule getAlertRuleSample2() {
        return new AlertRule().id(2L).name("name2").ruleCode("ruleCode2");
    }

    public static AlertRule getAlertRuleRandomSampleGenerator() {
        return new AlertRule().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).ruleCode(UUID.randomUUID().toString());
    }
}
