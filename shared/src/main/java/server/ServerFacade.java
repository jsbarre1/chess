package server;

import com.google.gson.Gson;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import response.ListGamesResponse;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void logoutUser(AuthData authToken) throws ResponseException{
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null);
        authToken = null;
    }

    public AuthData loginUser(UserData userData) throws ResponseException{
        var path = "/session";
        AuthData response = this.makeRequest("POST", path, userData, AuthData.class);
        authToken = response.authToken();
        return response;
    }


    public AuthData registerUser(UserData userData) throws ResponseException{
        var path = "/user";
        AuthData response = this.makeRequest("POST", path, userData, AuthData.class);
        authToken = response.authToken();
        return response;
    }

    public ArrayList<GameData> listGames() throws ResponseException{
        var path = "/game";
        ListGamesResponse response = this.makeRequest("GET", path, null, ListGamesResponse.class);
        return response.games();
    }

//
//    public Object joinGame(Request req, Response res) throws ResponseException{
//        var path = "/game";
//        return this.makeRequest("PUT", path, pet, Pet.class);
//    }
//

    public CreateGameRequest createChessGame(CreateGameRequest gameName) throws ResponseException{
        var path = "/game";
        return this.makeRequest("POST", path, gameName, CreateGameRequest.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
