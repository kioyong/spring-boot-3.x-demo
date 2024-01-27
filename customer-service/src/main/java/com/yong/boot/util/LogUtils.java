package com.yong.boot.util;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class LogUtils {
    public LogUtils() {
    }

    public static final Marker securityAudit = MarkerManager.getMarker("SecurityAudit");
    public static final Marker application = MarkerManager.getMarker("Application");
    public static final Marker integration = MarkerManager.getMarker("Integeration");
}
