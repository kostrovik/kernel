package com.github.kostrovik.kernel.interfaces;

import com.github.kostrovik.kernel.models.ServerAnswer;

import java.util.List;
import java.util.Map;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2019-01-11
 * github:  https://github.com/kostrovik/kernel
 */
public interface ServerConnectionServiceInterface {
    ServerAnswer sendGet(String apiUrl, Map<String, List<String>> urlParams);

    ServerAnswer sendPost(String apiUrl, String json);

    ServerAnswer sendPost(String apiUrl, String json, Map<String, List<String>> urlParams);
}
