package com.opentracing;

import com.google.common.collect.ImmutableMap;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Span;
import io.opentracing.Tracer;

public class OpenTraceDemo {

    private final Tracer tracer;

    private OpenTraceDemo(Tracer tracer) {
        this.tracer = tracer;
    }

    private void sayHello(Span rootSpan, String helloTo) {
        Span span = tracer.buildSpan("say-hello").asChildOf(rootSpan).start();
        try {
            span.log(ImmutableMap.of("event", "sayhello", "value", helloTo));
            formatString(span, helloTo);
        } finally {
            span.finish();
        }
    }

    private String formatString(Span rootSpan, String helloTo) {
        Span span = tracer.buildSpan("format-string").asChildOf(rootSpan).start();
        try {
            span.log(ImmutableMap.of("event", "formatString", "value", helloTo));
            return String.format("Hello, %s!", helloTo);
        } finally {
            span.finish();
        }
    }

    public static void main(String[] args) {

        OpenTraceDemo open = new OpenTraceDemo(initTracer("first"));
        Span span = open.tracer.buildSpan("main").start();
        open.sayHello(span, "xiaoshao");
        span.finish();
    }

    public static JaegerTracer initTracer(String service) {
        Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
        Configuration config = new Configuration(service).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }
}
