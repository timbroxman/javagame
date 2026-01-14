package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.samsung.gamestudio.GameResources;

public class LiveView extends View {

    private final static int livePadding = 6;

    private Texture texture;

    private int leftLives;

    public LiveView(float x, float y) {
        super(x, y);
        texture = new Texture(GameResources.LIVE_IMG_PATH);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        leftLives = 0;
    }

    public void setLeftLives(int leftLives) {
        this.leftLives = leftLives;
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Рисуем сердца слева направо: максимум 3
        int heartsToDraw = Math.max(0, Math.min(leftLives, 3));
        for (int i = 0; i < heartsToDraw; i++) {
            float drawX = x + i * (texture.getWidth() + livePadding);
            batch.draw(texture, drawX, y, width, height);
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}