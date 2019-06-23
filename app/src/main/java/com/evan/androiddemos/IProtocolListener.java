package com.evan.androiddemos;

/**
 *
 * Created by shidu on 18/1/23.
 */

public interface IProtocolListener {
    public void onConnected();

    public void onError(int code, String msg);

    public int onClose(boolean hasError);

    public int onResponse(int code, String errorMsg, long serviceId, int seq, String data);

    public int onMessage(long serviceId, String msg);
}
