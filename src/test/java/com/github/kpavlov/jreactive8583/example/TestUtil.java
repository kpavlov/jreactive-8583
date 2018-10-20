package com.github.kpavlov.jreactive8583.example;

import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Fail.fail;

public class TestUtil {

    private TestUtil() {
    }

    public static void waitFor(String message, BooleanSupplier condition) {
        int counter = 100;
        while (counter > 0 && !condition.getAsBoolean()) {
            try {
                Thread.sleep(100);
                counter--;
            } catch (InterruptedException e) {
                //noop
            }
        }
        if (counter == 0) {
            fail("Timeout: " + message);
        }
    }
}
