
package com.evan.androiddemos.http.rpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RPCRequest {
    private static final String VERSION = "2.0";
    private String id;
    private String method;
    private JSONArray params;
    private String remoteAddress;

    public RPCRequest() {
        this.params = new JSONArray();
    }

    public RPCRequest(String json) throws JSONException {
        parseJSON(json);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void putString(String str) {
        this.params.put(str);
    }

    public String getString(int index) {
        String value = null;
        try {
            if (params != null) {
                value = params.getString(index);
            }
        } catch (JSONException e) {
        }
        return value;
    }

    public void putInt(int value) {
        this.params.put(value);
    }

    public int getInt(int index) {
        int value = 0;
        try {
            if (params != null) {
                value = params.getInt(index);
            }
        } catch (JSONException e) {
        }
        return value;
    }

    public String toJSON() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("jsonrpc", VERSION);
            jsonObject.put("id", id);
            jsonObject.put("method", method);
            jsonObject.put("params", params);
            return jsonObject.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    private void parseJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        if (jsonObject.has("id")) {
            id = jsonObject.getString("id");
        }

        if (jsonObject.has("method")) {
            method = jsonObject.getString("method");
        }

        if (jsonObject.has("params")) {
            params = jsonObject.getJSONArray("params");
        }
    }
}
