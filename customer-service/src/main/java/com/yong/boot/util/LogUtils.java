package com.yong.boot.util;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import static com.yong.boot.constant.BusinessConstant.*;

public class LogUtils {
    public LogUtils() {
    }

    public static final Marker securityAudit = MarkerManager.getMarker(MESSAGE_TYPE_SECURITY_AUDIT);
    public static final Marker application = MarkerManager.getMarker(MESSAGE_TYPE_APPLICATION);
    public static final Marker integration = MarkerManager.getMarker(MESSAGE_TYPE_INTEGRATION);
}
