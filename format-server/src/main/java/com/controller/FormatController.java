package com.controller;

import io.jaegertracing.Configuration;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.tag.Tags;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class FormatController {

    @GetMapping("/format/{helloTo}")
    @ResponseBody
    public String format(@PathVariable(name = "helloTo") String helloTo, @RequestHeader HttpHeaders httpHeaders) {
        Tracer tracer = initTrace();
        try (Scope scope = startServerSpan(tracer, httpHeaders, "format")) {
            scope.span().setTag("event", "publish");
            for (String key : httpHeaders.keySet()) {
                System.out.println("key=>" + key + " value=>" + httpHeaders.get(key));
            }
            return String.format("Hello, %s!", helloTo);
        }
    }

    public static Tracer initTrace() {
        Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
        Configuration config = new Configuration("ScopeMainDemo").withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }

    public static Scope startServerSpan(Tracer tracer, HttpHeaders httpHeaders, String operationName) {
        // format the headers for extraction
        final HashMap<String, String> headers = new HashMap<String, String>();
        for (String key : httpHeaders.keySet()) {
            headers.put(key, httpHeaders.get(key).get(0));
        }

        Tracer.SpanBuilder spanBuilder;
        try {
            SpanContext parentSpan = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(headers));
            if (parentSpan == null) {
                spanBuilder = tracer.buildSpan(operationName);
            } else {
                spanBuilder = tracer.buildSpan(operationName).asChildOf(parentSpan);
            }
        } catch (IllegalArgumentException e) {
            spanBuilder = tracer.buildSpan(operationName);
        }
        return spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER).startActive(true);
    }
}
