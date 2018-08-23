package com.github.kostrovik.kernel.common;

import com.github.kostrovik.kernel.interfaces.ServerConnectionInterface;
import com.github.kostrovik.kernel.models.ServerConnectionAddress;
import com.github.kostrovik.kernel.settings.Configurator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * project: kernel
 * author:  kostrovik
 * date:    25/07/2018
 * github:  https://github.com/kostrovik/kernel
 */
public class ServerConnector implements ServerConnectionInterface {
    private static Logger logger = Configurator.getConfig().getLogger(ServerConnector.class.getName());
    private ServerConnectionAddress serverAddress;
    private final Charset charset = Charset.forName("UTF-8");

    public ServerConnector() {
        this.serverAddress = ApplicationSettings.getInstance().getDefaultHost();
    }

    @Override
    public String sendGet(String apiUrl, Map<String, String> headers) {
        return sendRequest("GET", apiUrl, headers, "", new HashMap<>());
    }

    @Override
    public String sendGet(String apiUrl, Map<String, String> headers, Map<String, String> urlParams) {
        return sendRequest("GET", apiUrl, headers, "", urlParams);
    }

    @Override
    public String sendPost(String apiUrl, String data, Map<String, String> headers) {
        return sendRequest("POST", apiUrl, headers, data, new HashMap<>());
    }

    @Override
    public String sendPost(String apiUrl, String data, Map<String, String> headers, Map<String, String> urlParams) {
        return sendRequest("POST", apiUrl, headers, data, urlParams);
    }

    private String sendRequest(String method, String apiUrl, Map<String, String> headers, String data, Map<String, String> urlParams) {
        StringBuilder response = new StringBuilder();
        StringBuilder responseResult = new StringBuilder();

        try {
            String requestParams = urlParams.keySet().stream().map(key -> String.format("%s=%s", key, encodeValue(urlParams.get(key)))).collect(Collectors.joining("&"));

            URL serverApiUrl = new URL(serverAddress.getUrl() + apiUrl + "?" + requestParams);

            HttpURLConnection connection = createConnection(method, headers, serverApiUrl);

            if (!data.trim().isEmpty()) {
                try (OutputStream output = connection.getOutputStream()) {
                    if (output != null) {
                        output.write(data.getBytes(charset));
                    }
                }
            }

            prepareResponseResult(responseResult, serverApiUrl, connection);
            parseResponse(response, connection);

            ApplicationSettings.getInstance().updateHostLastUsage();
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("Ошибка выполнения запроса%n%s", responseResult), e);
        }

        return response.toString();
    }

    private void parseResponse(StringBuilder answer, HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            try (InputStreamReader input = new InputStreamReader(connection.getErrorStream(), charset)) {
                int character;
                while (((character = input.read()) != -1)) {
                    answer.append((char) character);
                }
            }
        } else {
            try (InputStreamReader input = new InputStreamReader(connection.getInputStream(), charset)) {
                int character;
                while (((character = input.read()) != -1)) {
                    answer.append((char) character);
                }
            }
        }
    }

    private HttpURLConnection createConnection(String method, Map<String, String> headers, URL connectionUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) connectionUrl.openConnection();
        if (!method.equals("GET")) {
            connection.setDoOutput(true);
            connection.setDoInput(true);
        }
        connection.setRequestMethod(method);

        headers.keySet().forEach(key -> connection.setRequestProperty(key, headers.get(key)));

        return connection;
    }

    private void prepareResponseResult(StringBuilder builder, URL url, HttpURLConnection connection) throws IOException {
        builder.append(String.format("Запрос: %s%n", url.toExternalForm()));
        builder.append(String.format("Meтoд: %s%n", connection.getRequestMethod()));
        builder.append(String.format("Koд ответа: %s%n", connection.getResponseCode()));
        builder.append(String.format("Oтвeт: %s%n", connection.getResponseMessage()));
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, charset);
    }
}
