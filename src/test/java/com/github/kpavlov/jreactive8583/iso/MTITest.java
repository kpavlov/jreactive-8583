package com.github.kpavlov.jreactive8583.iso;

import org.junit.jupiter.api.Test;

import static com.github.kpavlov.jreactive8583.iso.ISO8583Version.V2003;
import static com.github.kpavlov.jreactive8583.iso.MessageClass.ADMINISTRATIVE;
import static com.github.kpavlov.jreactive8583.iso.MessageFunction.NOTIFICATION;
import static com.github.kpavlov.jreactive8583.iso.MessageOrigin.OTHER;
import static org.assertj.core.api.Assertions.assertThat;

class MTITest {

    @Test
    void shouldGenerateEcho() {
        final var result = MTI.mtiValue(V2003, ADMINISTRATIVE, NOTIFICATION, OTHER);
        assertThat(result).isEqualTo(0x2644);
    }
}
