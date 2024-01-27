package com.yong.boot.constant;

public class BusinessConstant {
    private BusinessConstant() {
    }

    public static final String X_AIAHK_TRACE_ID = "x-AIAHK-Trace-ID";
    public static final String X_AIAHK_CONTEXT_ID = "x-AIAHK-context-ID";

    public static final String MDC_CONTEXT_ID = "context_id";

    public static final String MDC_TRACE_ID = "trace_id";

    public static final String MESSAGE_TYPE_SECURITY_AUDIT = "SecurityAudit";
    public static final String MESSAGE_TYPE_APPLICATION = "Application";
    public static final String MESSAGE_TYPE_INTEGRATION = "Integration";

    public static final String APPLICATION_VERSION = "app_version";
    public static final String APPLICATION_ID = "app_id";

}
