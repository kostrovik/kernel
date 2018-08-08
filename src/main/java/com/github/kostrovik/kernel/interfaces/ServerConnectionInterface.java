package com.github.kostrovik.kernel.interfaces;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    25/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public interface ServerConnectionInterface {
    String sendGet(String apiUrl);

    String sendPost(String apiUrl, String json);
}
