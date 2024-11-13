package model;

public record AuthData(String username, String authToken) {

    public AuthData setAuthToken(String authToken) {
        return new AuthData(this.username, authToken);
    }
}
