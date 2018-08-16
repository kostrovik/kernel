package com.github.kostrovik.kernel.common;

import com.github.kostrovik.kernel.interfaces.ServerConnectionInterface;
import com.github.kostrovik.kernel.models.ServerConnectionAddress;
import com.github.kostrovik.kernel.settings.Configurator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: glcmtx
 * author:  kostrovik
 * date:    25/07/2018
 * github:  https://github.com/kostrovik/glcmtx
 */
public class ServerConnector implements ServerConnectionInterface {
    private static Logger logger = Configurator.getConfig().getLogger(ServerConnector.class.getName());
    private ServerConnectionAddress serverAddress;

    public ServerConnector() {
        this.serverAddress = ApplicationSettings.getInstance().getDefaultHost();
    }

    @Override
    public String sendGet(String apiUrl) {
        return sendGet(apiUrl, new HashMap<>());
    }

    @Override
    public String sendGet(String apiUrl, Map<String, String> headers) {
        StringBuilder response = new StringBuilder();
        StringBuilder responseHeaders = new StringBuilder();

        try {
            URL serverApiUrl = new URL(serverAddress.getUrl() + apiUrl);
            HttpURLConnection connection = createConnection("GET", headers, serverApiUrl);
            connection.setRequestProperty("Accept", "application/json");

            prepareAnswer(responseHeaders, serverApiUrl, connection);
            parseResponse(response, connection);

            ApplicationSettings.getInstance().updateHostLastUsage();
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("Ошибка выполнения запроса\n%s", responseHeaders), e);
        }

        return response.toString();
    }

    @Override
    public String sendPost(String apiUrl, String json) {
        return sendPost(apiUrl, json, new HashMap<>());
    }

    @Override
    public String sendPost(String apiUrl, String json, Map<String, String> headers) {
        StringBuilder response = new StringBuilder();
        StringBuilder responseHeaders = new StringBuilder();

        try {
            URL serverApiUrl = new URL(serverAddress.getUrl() + apiUrl);
            HttpURLConnection connection = createConnection("POST", headers, serverApiUrl);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            try (OutputStream output = connection.getOutputStream()) {
                if (output != null) {
                    output.write(json.getBytes(Charset.forName("UTF-8")));
                }
            }

            prepareAnswer(responseHeaders, serverApiUrl, connection);
            parseResponse(response, connection);

            ApplicationSettings.getInstance().updateHostLastUsage();
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("Ошибка выполнения запроса\n%s", responseHeaders), e);
        }

        return response.toString();
    }

    private void parseResponse(StringBuilder answer, HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            try (InputStreamReader input = new InputStreamReader(connection.getErrorStream(), Charset.forName("UTF-8"))) {
                int character;
                while (((character = input.read()) != -1)) {
                    answer.append((char) character);
                }
            }
        } else {
            try (InputStreamReader input = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"))) {
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

    private void prepareAnswer(StringBuilder builder, URL url, HttpURLConnection connection) throws IOException {
        builder.append(String.format("Запрос: %s\n", url.toExternalForm()));
        builder.append(String.format("Meтoд: %s\n", connection.getRequestMethod()));
        builder.append(String.format("Koд ответа: %s\n", connection.getResponseCode()));
        builder.append(String.format("Oтвeт: %s\n", connection.getResponseMessage()));
    }
}
