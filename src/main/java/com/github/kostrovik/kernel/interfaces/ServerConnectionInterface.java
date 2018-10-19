package com.github.kostrovik.kernel.interfaces;

import java.util.List;
import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    25/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ServerConnectionInterface {
    Map<String, Object> sendGet(String apiUrl, Map<String, String> headers);

    Map<String, Object> sendGet(String apiUrl, Map<String, String> headers, Map<String, List<String>> urlParams);

    Map<String, Object> sendPost(String apiUrl, String json, Map<String, String> headers);

    Map<String, Object> sendPost(String apiUrl, String json, Map<String, String> headers, Map<String, List<String>> urlParams);
}
