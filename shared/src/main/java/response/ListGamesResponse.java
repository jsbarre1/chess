package response;

import model.GameData;

import java.util.ArrayList;

public record ListGamesResponse(ArrayList<GameData> games) {
    @Override
    public ArrayList<GameData> games() {
        return games;
    }
}
