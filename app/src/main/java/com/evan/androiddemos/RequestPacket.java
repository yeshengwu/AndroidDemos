package com.evan.androiddemos;

import android.util.Log;

public class RequestPacket {
    public long serviceId;
    public String data;
    public int seq;
    public int timeoutMs;

    public RequestPacket(long serviceId, int seq, String data, int timeoutMs) {
        this.serviceId = serviceId;
        this.data = data;
        this.seq = seq;
        this.timeoutMs = timeoutMs;
    }

    public int getLength() {
        int len = 8 + 2 + 1 + data.length();
        Log.e("RequestPacket","RequestPacket len = "+len);
        return len;
    }
}
