package com.yong.boot.util;

import brave.baggage.BaggageField;
import brave.internal.baggage.ExtraBaggageContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.yong.boot.constant.BusinessConstant.*;

@Log4j2
public class LogUtils {
    public LogUtils() {
    }

    public static final Marker securityAudit = MarkerManager.getMarker(MESSAGE_TYPE_SECURITY_AUDIT);
    public static final Marker application = MarkerManager.getMarker(MESSAGE_TYPE_APPLICATION);
    public static final Marker integration = MarkerManager.getMarker(MESSAGE_TYPE_INTEGRATION);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void updateBusinessKey(String key, String value) {
        try {
            BaggageField field = BaggageField.getByName(BIZ_KEYS);
            if (field != null) {
                String businessKeyStr = field.getValue();
                Map<String, String> map = new HashMap<>();
                if (StringUtils.hasText(businessKeyStr)) {
                    map = objectMapper.readValue(businessKeyStr, Map.class);
                }
                map.put(key, value);
                String valueStr = objectMapper.writeValueAsString(map);
                BaggageField.getByName(BIZ_KEYS).updateValue(valueStr);
            }
        } catch (Exception e) {
            log.error("update MDC scope error: {}", e.getMessage());
        }
    }

}
