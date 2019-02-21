package com.opentracing;

import io.jaegertracing.Configuration;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

public class ScopeMainDemo {

    private Tracer tracer;

    public ScopeMainDemo(Tracer tracer) {
        this.tracer = tracer;
    }

    private void sayHello(String helloTo) {

        try (Scope scope = tracer.buildSpan("sayHello").startActive(true)) {
            scope.span().setTag("hello-to", helloTo);
            String str = formatHello(helloTo);
            printHello(str);
        }

    }

    private String formatHello(String helloTo) {
        try (Scope scope = tracer.buildSpan("formatHello").startActive(true)) {
            scope.span().setTag("hello-to", helloTo);
            return String.format("Hello, %s!", helloTo);
        }
    }

    private void printHello(String helloTo) {

        try (Scope scope = tracer.buildSpan("printHello").startActive(true)) {
            scope.span().setTag("hello-to", helloTo);
            System.out.println(helloTo);
        }
    }

    public static void main(String[] args) {
        new ScopeMainDemo(initTrace()).sayHello("xiaoshao");
    }

    public static Tracer initTrace() {
        Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
        Configuration config = new Configuration("ScopeMainDemo").withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }
}
