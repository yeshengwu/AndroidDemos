
package com.evan.androiddemos.http;

public abstract class Dispatcher {

    public Dispatcher() {
    }

    public final void service(Request request, Response response) {
        response.setStatus(200);
        response.setContentType("application/json;charset=utf-8");

        doRequest(request, response);
    }

    protected abstract void doRequest(Request request, Response response);
}
