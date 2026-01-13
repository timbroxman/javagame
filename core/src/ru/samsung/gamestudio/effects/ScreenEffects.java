package ru.samsung.gamestudio.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import ru.samsung.gamestudio.GameSettings;

public class ScreenEffects {
    private float healthDarkness; // Затемнение при низком здоровье (0-1)
    private float bonusFlashAlpha; // Альфа для вспышки бонуса (0-1)
    private float bonusFlashTime; // Время вспышки
    private Color bonusFlashColor;
    private Texture whitePixel;
    
    public ScreenEffects() {
        healthDarkness = 0f;
        bonusFlashAlpha = 0f;
        bonusFlashTime = 0f;
        bonusFlashColor = new Color(1f, 1f, 1f, 0f);
        
        // Создаем простую белую текстуру для рисования эффектов
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }
    
    public void updateHealthDarkness(int currentLives, int maxLives) {
        if (currentLives <= 1 && maxLives > 1) {
            // Затемнение увеличивается при низком здоровье
            float healthPercent = (float) currentLives / maxLives;
            healthDarkness = (1f - healthPercent) * 0.3f; // Максимум 30% затемнения (было 60%)
        } else {
            healthDarkness = 0f;
        }
    }
    
    public void triggerBonusFlash(Color color) {
        bonusFlashColor.set(color);
        bonusFlashAlpha = 1f;
        bonusFlashTime = 0.3f; // Вспышка длится 0.3 секунды
    }
    
    public void update(float delta) {
        if (bonusFlashTime > 0) {
            bonusFlashTime -= delta;
            // Плавное затухание вспышки
            bonusFlashAlpha = MathUtils.clamp(bonusFlashTime / 0.3f, 0f, 1f);
        } else {
            bonusFlashAlpha = 0f;
        }
    }
    
    public void draw(SpriteBatch batch) {
        // Затемнение при низком здоровье
        if (healthDarkness > 0) {
            batch.setColor(0f, 0f, 0f, healthDarkness);
            batch.draw(whitePixel, 0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            batch.setColor(1f, 1f, 1f, 1f);
        }
        
        // Вспышка при получении бонуса
        if (bonusFlashAlpha > 0) {
            Color flashColor = new Color(bonusFlashColor);
            flashColor.a = bonusFlashAlpha * 0.5f; // Полупрозрачная вспышка
            batch.setColor(flashColor);
            batch.draw(whitePixel, 0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }
    
    public void dispose() {
        if (whitePixel != null) {
            whitePixel.dispose();
        }
    }
}

