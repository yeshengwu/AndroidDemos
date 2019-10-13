
package com.evan.androiddemos.http.rpc;

import com.evan.androiddemos.http.Dispatcher;
import com.evan.androiddemos.http.Request;
import com.evan.androiddemos.http.Response;

public abstract class RPCDispatcher extends Dispatcher {

    @Override
    protected void doRequest(Request request, Response response) {
        RPCResponse rpcResponse = new RPCResponse();
       /* try {
            Log.e("evan","RPCDispatcher. request.getContent() = "+request.getContent());
            RPCRequest rpcRequest = new RPCRequest(request.getContent());
            rpcRequest.setRemoteAddress(request.getRemoteAddress());
            doRequest(rpcRequest, rpcResponse);
        } catch (JSONException e) {
            rpcResponse.setError(null, RPCErrorCode.INVALID_JSON, "Invalid json");
        }*/
        response.setContent("hello world");
    }

    protected abstract void doRequest(RPCRequest request, RPCResponse response);
}
