package com.yong.boot.util;

import brave.internal.Platform;
import brave.internal.RecyclableBuffers;

import static brave.internal.codec.HexCodec.writeHexLong;

public class B3TraceIdUtils {

    private B3TraceIdUtils() {
    }

    public static long nextId() {
        return Platform.get().randomLong();
    }

    public static String b3TraceIdString() {
        long traceId = nextId();
        return toB3TraceIdString(Platform.get().nextTraceIdHigh(), traceId);
    }

    public static String toB3TraceIdString(long traceIdHigh, long traceId) {
        char[] result = RecyclableBuffers.parseBuffer();
        writeHexLong(result, 0, traceIdHigh);
        writeHexLong(result, 16, traceId);
        return new String(result, 0, 32);
    }
}
