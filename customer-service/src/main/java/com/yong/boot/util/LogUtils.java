package com.yong.boot.util;

import brave.baggage.BaggageField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yong.boot.enums.LogTypeEnums;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.yong.boot.constant.BusinessConstant.BIZ_KEYS;
import static com.yong.boot.constant.BusinessConstant.SEQNO;

@Log4j2
public class LogUtils {
    public LogUtils() {
    }

    public static final Marker securityAudit = MarkerManager.getMarker(LogTypeEnums.SECURITY_AUDIT.getType());
    public static final Marker application = MarkerManager.getMarker(LogTypeEnums.APPLICATION.getType());
    public static final Marker integration = MarkerManager.getMarker(LogTypeEnums.INTEGRATION.getType());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ThreadLocal<LogSeqNo> logSeqNoThreadLocal = new InheritableThreadLocal<>();

    public static void info(org.apache.logging.log4j.Logger log, Marker marker, String message, Object p0) {
        int seqNo = getSeqNo(LogTypeEnums.of(marker.getName()));
        updateBusinessKey(SEQNO, seqNo);
        log.info(marker, message, p0);
    }

    public static void info(org.apache.logging.log4j.Logger log, String message, Object p0) {
        updateBusinessKey(SEQNO, getSeqNo(LogTypeEnums.APPLICATION));
        log.info(application, message, p0);
    }

    public static void info(org.apache.logging.log4j.Logger log, String message, Object p0, Object p1) {
        updateBusinessKey(SEQNO, getSeqNo(LogTypeEnums.APPLICATION));
        log.info(application, message, p0, p1);
    }

    public static void info(org.apache.logging.log4j.Logger log, String message, Object p0, Object p1, Object p2) {
        updateBusinessKey(SEQNO, getSeqNo(LogTypeEnums.APPLICATION));
        log.info(application, message, p0, p1, p2);
    }


    public static void info(Logger log, Marker marker, String message) {
        updateBusinessKey(SEQNO, getSeqNo(LogTypeEnums.of(marker.getName())));
        log.info(marker, message);
    }

    public static void info(Logger log, String message) {
        int seqNo = getSeqNo(LogTypeEnums.APPLICATION);
        updateBusinessKey(SEQNO, seqNo);
        log.info(application, message);
    }

    static int getSeqNo(LogTypeEnums logType) {
        LogSeqNo logSeqNo = logSeqNoThreadLocal.get();
        if (logSeqNo == null) {
            logSeqNo = new LogSeqNo();
            logSeqNoThreadLocal.set(logSeqNo);
        }
        return logSeqNo.incrementAndGet(logType);
    }

    public static void cleanSeqNo() {
        logSeqNoThreadLocal.set(null);
    }

    static class LogSeqNo {
        private final AtomicInteger appLogIndex = new AtomicInteger();
        private final AtomicInteger intLogIndex = new AtomicInteger();
        private final AtomicInteger secLogIndex = new AtomicInteger();

        public synchronized Integer incrementAndGet(LogTypeEnums logType) {
            return switch (logType) {
                case APPLICATION -> appLogIndex.incrementAndGet();
                case INTEGRATION -> intLogIndex.incrementAndGet();
                case SECURITY_AUDIT -> secLogIndex.incrementAndGet();
            };
        }
    }

    public static void updateBusinessKey(String key, Object value) {
        try {
            BaggageField field = BaggageField.getByName(BIZ_KEYS);
            if (field != null) {
                String businessKeyStr = field.getValue();
                Map<String, Object> map = new HashMap<>();
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


