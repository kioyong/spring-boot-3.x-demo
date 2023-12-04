package com.yong.boot.configuration;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.PropagatingReceiverTracingObservationHandler;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.observation.ServerRequestObservationContext;

import static org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration.RECEIVER_TRACING_OBSERVATION_HANDLER_ORDER;

@Configuration
public class TracingConfiguration {



}
