
package com.evan.androiddemos.http.rpc;

import org.json.JSONException;
import org.json.JSONObject;

public class RPCResponse {
    private static final String VERSION = "2.0";

    private String id;
    private String jsonrpc;
    private JSONObject result;
    private JSONObject error;

    public RPCResponse() {
        this.jsonrpc = VERSION;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    public void setError(JSONObject error) {
        this.error = error;
    }

    public void setError(String id, int code, String message) {
        try {
            JSONObject error = new JSONObject();
            error.put("code", code);
            error.put("message", message);

            setId(id);
            setError(error);
        } catch (JSONException e) {
        }
    }

    public JSONObject getResult() {
        return result;
    }

    public String getJSONString() {
        JSONObject response = new JSONObject();
        try {
            response.put("jsonrpc", jsonrpc);
            response.put("id", id);
            response.put("result", result);
            response.put("error", error);
        } catch (JSONException e) {
        }
        return response.toString();
    }
}
