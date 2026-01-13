package ru.samsung.gamestudio.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import ru.samsung.gamestudio.GameSettings;

public class EnemyWarningSystem {
    private float warningAlpha;
    private float warningTime;
    private boolean isWarningActive;
    private float warningX;
    private String warningText;
    
    public EnemyWarningSystem() {
        warningAlpha = 0f;
        warningTime = 0f;
        isWarningActive = false;
        warningX = 0f;
        warningText = "";
    }
    
    public void triggerWarning(float enemyX, String enemyType) {
        isWarningActive = true;
        warningTime = 2.5f; // Предупреждение длится 2.5 секунды
        warningX = enemyX;
        
        // Разные тексты для разных типов врагов
        if (enemyType.equals("BOSS")) {
            warningText = "!!! BOSS INCOMING !!!";
        } else if (enemyType.equals("TANK")) {
            warningText = "WARNING: TANK APPROACHING!";
        } else {
            warningText = "WARNING: " + enemyType + "!";
        }
    }
    
    public void update(float delta) {
        if (isWarningActive && warningTime > 0) {
            warningTime -= delta;
            // Пульсация альфа-канала
            warningAlpha = 0.5f + MathUtils.sin(warningTime * 10f) * 0.5f;
            
            if (warningTime <= 0) {
                isWarningActive = false;
                warningAlpha = 0f;
            }
        }
    }
    
    public void draw(SpriteBatch batch, BitmapFont font) {
        if (!isWarningActive || warningAlpha <= 0) return;
        
        // Рисуем улучшенное предупреждение в верхней части экрана
        Color originalColor = batch.getColor();
        
        // Пульсирующий масштаб
        float pulseScale = 1.2f + (float)Math.sin(warningTime * 15f) * 0.2f;
        font.getData().setScale(pulseScale);
        
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, warningText);
        float x = warningX - layout.width / 2f;
        float y = GameSettings.SCREEN_HEIGHT - 100;
        
        // Тень
        batch.setColor(0f, 0f, 0f, warningAlpha * 0.7f);
        font.draw(batch, warningText, x + 3, y - 3);
        
        // Основной текст с градиентом (красный -> оранжевый)
        float red = 1f;
        float green = (float)Math.sin(warningTime * 10f) * 0.3f;
        batch.setColor(red, green, 0f, warningAlpha);
        font.draw(batch, warningText, x, y);
        
        // Восстанавливаем
        font.getData().setScale(1.0f);
        batch.setColor(originalColor);
    }
    
    public boolean isActive() {
        return isWarningActive;
    }
}

