package com.github.kpavlov.jreactive8583.iso;

import javax.annotation.Nonnull;

public class MTI {

    public static int mtiValue(@Nonnull ISO8583Version iso8583Version,
                               @Nonnull MessageClass messageClass,
                               @Nonnull MessageFunction messageFunction,
                               @Nonnull MessageOrigin messageOrigin
    ) {
        return iso8583Version.value() + messageClass.value() + messageFunction.value() + messageOrigin.value();
    }
}
