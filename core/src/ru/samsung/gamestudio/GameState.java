package ru.samsung.gamestudio;

public final class GameState {
    public static final GameState PLAYING = new GameState();
    public static final GameState PAUSED = new GameState();
    public static final GameState ENDED = new GameState();
    
    private GameState() {
        // Private constructor to prevent instantiation
    }
    
    // Helper method for compatibility
    public static GameState[] values() {
        return new GameState[]{PLAYING, PAUSED, ENDED};
    }
    
    public static GameState valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        switch (name) {
            case "PLAYING":
                return PLAYING;
            case "PAUSED":
                return PAUSED;
            case "ENDED":
                return ENDED;
            default:
                throw new IllegalArgumentException("No enum constant " + GameState.class.getName() + "." + name);
        }
    }
}

