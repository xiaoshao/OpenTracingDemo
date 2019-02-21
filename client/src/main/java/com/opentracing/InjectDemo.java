package com.opentracing;

import com.opentracing.request.RequestBuilderCarrier;
import io.jaegertracing.Configuration;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InjectDemo {

    private Tracer tracer;

    public InjectDemo(Tracer tracer) {
        this.tracer = tracer;
    }

    public void sayHello(String helloTo) {
        try (Scope scope = tracer.buildSpan("sayHello").startActive(true)) {
            scope.span().setTag("event", "sayHello");

            String helloTo1 = formatHello(helloTo);

            printHello(helloTo1);
        }
    }

    private void printHello(String helloTo) {
        try (Scope scope = tracer.buildSpan("printHello").startActive(true)) {
            scope.span().setTag("event", "printHello");

            String url = "http://localhost:8081/publish/" + helloTo;
            Request.Builder builder = new Request.Builder();
            scope.span().setBaggageItem("firstbb", "bbbbb");
            tracer.inject(tracer.activeSpan().context(), Format.Builtin.HTTP_HEADERS, new RequestBuilderCarrier(builder));


            Response response = buildHttpClient().newCall(builder.url(url).build()).execute();

            System.out.println("got response " + response.body().string());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String formatHello(String helloTo) {
        try (Scope scope = tracer.buildSpan("formatHello").startActive(true)) {
            scope.span().setTag("event", "formatHello");
            String url = "http://localhost:8080/format/" + helloTo;
            Request.Builder builder = new Request.Builder();
            tracer.inject(tracer.activeSpan().context(), Format.Builtin.HTTP_HEADERS, new RequestBuilderCarrier(builder));

            Response response = buildHttpClient().newCall(builder.url(url).build()).execute();

            return response.body().string();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient();

    }


    public static void main(String[] args) {
        new InjectDemo(initTrace()).sayHello("xiaoshao");
    }

    public static Tracer initTrace() {
        Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
        Configuration config = new Configuration("ScopeMainDemo").withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }
}
