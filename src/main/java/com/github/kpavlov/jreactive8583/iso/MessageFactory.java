package com.github.kpavlov.jreactive8583.iso;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public interface MessageFactory<T> {

    T newMessage(int type);

    T createResponse(T requestMessage);

    T parseMessage(byte[] buf, int isoHeaderLength) throws UnsupportedEncodingException, ParseException;
}
