package com.yong.boot.config;

import brave.baggage.BaggageField;
import brave.baggage.CorrelationScopeConfig;
import brave.baggage.CorrelationScopeCustomizer;
import brave.internal.baggage.BaggageFields;
import brave.internal.baggage.ExtraBaggageContext;
import brave.internal.codec.HexCodec;
import brave.internal.propagation.StringPropagationAdapter;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yong.boot.properties.AIACustomBaggageProperties;
import com.yong.boot.util.B3TraceIdUtils;
import com.yong.boot.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static brave.internal.codec.HexCodec.toLowerHex;
import static com.yong.boot.constant.BusinessConstant.*;
import static com.yong.boot.util.LogUtils.application;


@Component
@Log4j2
@RequiredArgsConstructor
public class CustomPropagator extends Propagation.Factory implements Propagation<String> {


    private final AIACustomBaggageProperties properties;

    @Value("${spring.application.name}")
    private String applicationId;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Override
    public List<String> keys() {
        return List.of();
    }

    @Override
    public <R> TraceContext.Injector<R> injector(Setter<R, String> setter) {
        return (traceContext, request) -> properties.getRemoteFields().forEach(key -> {
            key = key.toLowerCase();
            if (BaggageField.getByName(key) != null) {
                String value = BaggageField.getByName(key).getValue();
                if (org.springframework.util.StringUtils.hasText(value)) {
                    setter.put(request, key, value);
                }
            }
        });
    }

    @Override
    public <R> TraceContext.Extractor<R> extractor(Getter<R, String> getter) {
        return request -> {

            // create TraceContext and set default Value
            TraceContext traceContext = TraceContext.newBuilder()
                    .addExtra(BaggageFields.newFactory(new ArrayList<>(), 10).create())
                    .traceId(HexCodec.lowerHexToUnsignedLong(B3TraceIdUtils.b3TraceIdString()))
                    .spanId(HexCodec.lowerHexToUnsignedLong(toLowerHex(B3TraceIdUtils.nextId())))
                    .build();

            // extractor from config
            properties.getRemoteFields().forEach(key -> {
                key = key.toLowerCase();
                String value = getter.get(request, key);
                if (org.springframework.util.StringUtils.hasText(value)) {
                    ExtraBaggageContext.get().updateValue(BaggageField.create(key), traceContext, value);
                }
            });

            //extractor and set to alias
            properties.getCorrelationFieldsAlias().forEach((key, alias) -> {
                key = key.toLowerCase();
                String value = getter.get(request, key);
                if (org.springframework.util.StringUtils.hasText(value)) {
                    ExtraBaggageContext.get().updateValue(BaggageField.create(alias), traceContext, value);
                    BaggageField.create("alias").updateValue(value);
                }
            });

            // special case
            String contextId = StringUtils.generateRandomString(5);

            // set MDC contextId
            ExtraBaggageContext.get().updateValue(BaggageField
                    .create(MDC_CONTEXT_ID.toLowerCase()), traceContext, contextId);
            // pass current contextId to downstream
            ExtraBaggageContext.get().updateValue(BaggageField
                    .create(X_AIAHK_CONTEXT_ID.toLowerCase()), traceContext, contextId);


            //set default TraceId if empty
            if (!org.springframework.util.StringUtils.hasText(getter.get(request, X_AIAHK_TRACE_ID.toLowerCase()))) {
                String traceId = StringUtils.generateRandomString(10);
                // set MDC contextId
                ExtraBaggageContext.get().updateValue(BaggageField
                        .create(MDC_TRACE_ID), traceContext, traceId);
                // pass current traceId to downstream
                ExtraBaggageContext.get().updateValue(BaggageField
                        .create(X_AIAHK_TRACE_ID.toLowerCase()), traceContext, traceId);
            }

            //set business keys
            setBusinessKeys(getter, request, traceContext);
            return TraceContextOrSamplingFlags.create(traceContext);
        };
    }

    private static <R> void setBusinessKeys(Getter<R, String> getter, R request, TraceContext traceContext) {
        Map<String, String> businessKeys = new HashMap<>();

        String userId = getter.get(request, "x-user-id");
        if (org.springframework.util.StringUtils.hasText(userId)) {
            businessKeys.put("userId", userId);
        }
        String clientIp = getter.get(request, "x-forwarded-for");
        if (org.springframework.util.StringUtils.hasText(clientIp)) {
            String[] clientIps = clientIp.split(",");
            clientIp = clientIps[0].trim();
        }
        if (org.springframework.util.StringUtils.hasText(clientIp)) {
            businessKeys.put("clientIPAddress", clientIp);
        }
        if (!businessKeys.isEmpty()) {
            try {
                String valueStr = new ObjectMapper().writeValueAsString(businessKeys);
                ExtraBaggageContext.get().updateValue(BaggageField
                        .create("biz_keys"), traceContext, valueStr);
            } catch (JsonProcessingException e) {
                log.error(application, "fail to set businessKeys {}", e.getMessage());
            }
        }
    }

    /**
     * This is deprecated: end users and instrumentation should never call this, and instead use
     * {@link #get()}.
     * <h3>Implementation advice</h3>
     * This is deprecated, but abstract. This means those implementing custom propagation formats
     * will have to implement this until it is removed in Brave 6. If you are able to use a tool
     * such as "maven-shade-plugin", consider using {@link StringPropagationAdapter}.
     *
     * @param <K> Deprecated except when a {@link String}.
     * @see KeyFactory#STRING
     * @since 4.0
     * @deprecated Since 5.12, use {@link #get()}
     */
    @Override
    @Deprecated(since = "4.0")
    public <K> Propagation<K> create(KeyFactory<K> keyFactory) {
        return StringPropagationAdapter.create(this, keyFactory);
    }


    // MDC constant field config
    @Bean
    public CorrelationScopeCustomizer createCorrelationScopeCustomizer() {
        return builder -> {
            builder.add(CorrelationScopeConfig.SingleCorrelationField
                    .create(brave.baggage.BaggageFields.constant(APPLICATION_VERSION, applicationVersion)));
            builder.add(CorrelationScopeConfig.SingleCorrelationField
                    .create(brave.baggage.BaggageFields.constant(APPLICATION_ID, applicationId)));
            builder.build();
        };
    }
}
