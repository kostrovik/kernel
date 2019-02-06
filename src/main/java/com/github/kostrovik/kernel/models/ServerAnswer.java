package com.github.kostrovik.kernel.models;

import java.util.List;
import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public class ServerAnswer {
    private Object body;
    private Map<String, List<String>> headers;
    private int status;

    public ServerAnswer(Object body, Map<String, List<String>> headers, int status) {
        this.body = body;
        this.headers = headers;
        this.status = status;
    }

    public Object getBody() {
        return body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public int getStatus() {
        return status;
    }
}
