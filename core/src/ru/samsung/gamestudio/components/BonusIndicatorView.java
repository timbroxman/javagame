package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.objects.ShipObject;

public class BonusIndicatorView extends View {

    private BitmapFont font;
    private ShipObject ship;
    private int x;
    private int y;
    private int spacing = 100; // Расстояние между индикаторами
    
    // Кэшированные текстуры
    private Texture rapidFireIcon;
    private Texture shieldIcon;
    private Texture multishotIcon;
    private boolean texturesLoaded = false;

    public BonusIndicatorView(BitmapFont font, ShipObject ship, int x, int y) {
        super(x, y);
        this.font = font;
        this.ship = ship;
        this.x = x;
        this.y = y;
        loadTextures();
    }
    
    private void loadTextures() {
        try {
            rapidFireIcon = new Texture(GameResources.BONUS_RAPIDFIRE_IMG_PATH);
            shieldIcon = new Texture(GameResources.BONUS_SHIELD_IMG_PATH);
            multishotIcon = new Texture(GameResources.BONUS_MULTISHOT_IMG_PATH);
            texturesLoaded = true;
        } catch (Exception e) {
            // Если текстуры не найдены, используем только текст
            texturesLoaded = false;
        }
    }

    public void draw(SpriteBatch batch) {
        int currentX = x;
        long currentTime = TimeUtils.millis();

        // Индикатор RapidFire
        if (ship.isRapidFireActive()) {
            long remaining = (ship.rapidFireEndTime - currentTime) / 1000;
            if (remaining > 0) {
                drawBonusIndicator(batch, currentX, y, rapidFireIcon, remaining);
                currentX += spacing;
            }
        }

        // Индикатор Shield
        if (ship.isShieldActive()) {
            long remaining = (ship.shieldEndTime - currentTime) / 1000;
            if (remaining > 0) {
                drawBonusIndicator(batch, currentX, y, shieldIcon, remaining);
                currentX += spacing;
            }
        }

        // Индикатор Multishot
        if (ship.isMultishotActive()) {
            long remaining = (ship.multishotEndTime - currentTime) / 1000;
            if (remaining > 0) {
                drawBonusIndicator(batch, currentX, y, multishotIcon, remaining);
                currentX += spacing;
            }
        }
    }

    private void drawBonusIndicator(SpriteBatch batch, int x, int y, Texture icon, long secondsRemaining) {
        int iconSize = 50;
        
        // Рисуем иконку, если она загружена
        if (texturesLoaded && icon != null) {
            batch.draw(icon, x, y, iconSize, iconSize);
        }
        
        // Рисуем таймер под иконкой
        String timeText = secondsRemaining + "s";
        float textWidth = font.getXHeight() * timeText.length() * 0.6f;
        font.draw(batch, timeText, x + iconSize / 2 - textWidth / 2, y - 5);
    }
    
    @Override
    public void dispose() {
        if (rapidFireIcon != null) rapidFireIcon.dispose();
        if (shieldIcon != null) shieldIcon.dispose();
        if (multishotIcon != null) multishotIcon.dispose();
    }
}

