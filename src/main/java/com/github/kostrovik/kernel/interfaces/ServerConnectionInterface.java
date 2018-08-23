package com.github.kostrovik.kernel.interfaces;

import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    25/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public interface ServerConnectionInterface {
    String sendGet(String apiUrl, Map<String, String> headers);

    String sendGet(String apiUrl, Map<String, String> headers, Map<String, String> urlParams);

    String sendPost(String apiUrl, String json, Map<String, String> headers);

    String sendPost(String apiUrl, String json, Map<String, String> headers, Map<String, String> urlParams);
}
