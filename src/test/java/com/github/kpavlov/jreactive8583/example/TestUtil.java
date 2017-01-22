package com.github.kpavlov.jreactive8583.example;


import org.junit.Assert;

import java.util.function.BooleanSupplier;

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
            Assert.fail("Timeout: " + message);
        }
    }
}
