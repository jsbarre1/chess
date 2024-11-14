package model;

public record UserData(String username, String password, String email) {
    public UserData(String... params) {
        this(params[0], params[1], params[2]);
    }
}
