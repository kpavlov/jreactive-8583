package com.github.kpavlov.jreactive8583.example

import org.assertj.core.api.Fail

object TestUtil {

    @JvmStatic
    fun waitFor(message: String, block: () -> Boolean) {
        var counter = 100
        while (counter > 0 && !block.invoke()) {
            try {
                Thread.sleep(100)
                counter--
            } catch (e: InterruptedException) { //noop
            }
        }
        if (counter == 0) {
            Fail.fail<Any>("Timeout: $message")
        }
    }
}
