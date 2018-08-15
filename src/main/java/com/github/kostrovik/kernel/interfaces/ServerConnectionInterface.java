package com.github.kostrovik.kernel.interfaces;

import java.util.Map;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    25/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public interface ServerConnectionInterface {
    String sendGet(String apiUrl);

    String sendGet(String apiUrl, Map<String, String> headers);

    String sendPost(String apiUrl, String json);

    String sendPost(String apiUrl, String json, Map<String, String> headers);
}
