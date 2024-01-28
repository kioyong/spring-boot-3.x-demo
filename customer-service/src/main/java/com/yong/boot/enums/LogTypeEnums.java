package com.yong.boot.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LogTypeEnums {

    APPLICATION("Application"),
    INTEGRATION("Integration"),
    SECURITY_AUDIT("SecurityAudit");
    private String type;

    LogTypeEnums(String type) {
        this.type = type;
    }

    public static LogTypeEnums of(String type) {
        return Arrays.stream(LogTypeEnums.values())
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }

}
