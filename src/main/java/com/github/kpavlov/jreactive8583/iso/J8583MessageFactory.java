package com.github.kpavlov.jreactive8583.iso;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class J8583MessageFactory<T extends IsoMessage>
        implements com.github.kpavlov.jreactive8583.iso.MessageFactory<T> {

    @Nonnull
    private final MessageFactory<T> messageFactory;
    @Nonnull
    private final ISO8583Version isoVersion;

    public J8583MessageFactory(@Nonnull final MessageFactory<T> messageFactory,
                               @Nonnull final ISO8583Version isoVersion) {
        this.messageFactory = messageFactory;
        this.isoVersion = isoVersion;
    }

    @SuppressWarnings("unchecked")
    public J8583MessageFactory() throws IOException {
        this((MessageFactory<T>) ConfigParser.createDefault(), ISO8583Version.V1987);
    }

    @SuppressWarnings("unchecked")
    public J8583MessageFactory(@Nonnull final ISO8583Version iso8583Version) throws IOException {
        this((MessageFactory<T>) ConfigParser.createDefault(), iso8583Version);
    }

    @Override
    public T newMessage(final int type) {
        return messageFactory.newMessage(type);
    }

    @Override
    public T newMessage(@Nonnull final MessageClass messageClass,
                        @Nonnull final MessageFunction messageFunction,
                        @Nonnull final MessageOrigin messageOrigin) {
        return newMessage(MTI.mtiValue(isoVersion, messageClass, messageFunction, messageOrigin));
    }

    @Override
    public T createResponse(final T requestMessage) {
        return messageFactory.createResponse(requestMessage);
    }

    @Override
    public T createResponse(final T request, final boolean copyAllFields) {
        return messageFactory.createResponse(request, copyAllFields);
    }

    @Override
    public T parseMessage(byte[] buf, int isoHeaderLength, boolean binaryIsoHeader) throws ParseException, UnsupportedEncodingException {
        return messageFactory.parseMessage(buf, isoHeaderLength, binaryIsoHeader);
    }

    @Override
    public T parseMessage(final byte[] buf, final int isoHeaderLength) throws UnsupportedEncodingException, ParseException {
        return messageFactory.parseMessage(buf, isoHeaderLength);
    }
}
