
package com.evan.androiddemos;

import android.content.Context;

import com.evan.androiddemos.http.rpc.RPCDispatcher;
import com.evan.androiddemos.http.rpc.RPCRequest;
import com.evan.androiddemos.http.rpc.RPCResponse;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultRPCDispatcher extends RPCDispatcher {
    private static final String TAG = DefaultRPCDispatcher.class.getSimpleName();

    private Context mContext;
    private ConcurrentMap<String, BigInteger> mNonceMap;

    public DefaultRPCDispatcher(Context context) {
        mContext = context;
        mNonceMap = new ConcurrentHashMap<>();
    }


    @Override
    protected void doRequest(RPCRequest request, RPCResponse response) {

    }


    private boolean verify(RPCResponse response, String id, String data, String nonce, String sign, String address) {

        return true;
    }

    private boolean verifyNonce(String nonce, String address) {
        boolean res = false;
        BigInteger n = null;

        return res;
    }

    private void responseResult(RPCResponse response, String id, String nonce, String address) {

    }
}
