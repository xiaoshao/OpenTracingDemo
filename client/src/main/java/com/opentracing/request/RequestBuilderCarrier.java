package com.opentracing.request;

import io.opentracing.propagation.TextMap;
import okhttp3.Request;

import java.util.Iterator;
import java.util.Map;

public class RequestBuilderCarrier implements TextMap {

    private final Request.Builder builder;

    public RequestBuilderCarrier(Request.Builder builder) {
        this.builder = builder;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
    }

    @Override
    public void put(String key, String value) {
        builder.addHeader(key, value);
    }
}
