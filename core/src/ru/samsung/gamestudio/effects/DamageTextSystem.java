package ru.samsung.gamestudio.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class DamageTextSystem {
    private ArrayList<FloatingDamageText> damageTexts;
    
    public DamageTextSystem() {
        damageTexts = new ArrayList<>();
    }
    
    public void createDamageText(float x, float y, int damage, Color color) {
        damageTexts.add(new FloatingDamageText(x, y, damage, color));
    }
    
    public void createDamageText(float x, float y, int damage) {
        // По умолчанию красный цвет для урона
        createDamageText(x, y, damage, new Color(1f, 0.2f, 0.2f, 1f));
    }
    
    public void createComboText(float x, float y, int combo, int multiplier) {
        // Специальный текст для комбо с улучшенной анимацией
        Color comboColor = new Color(1f, 1f, 0f, 1f); // Желтый
        String comboMessage = "COMBO x" + multiplier + "!";
        
        if (multiplier >= 5) {
            comboColor = new Color(1f, 0f, 1f, 1f); // Фиолетовый для высокого комбо
            comboMessage = "LEGENDARY x" + multiplier + "!!!";
        } else if (multiplier >= 3) {
            comboColor = new Color(1f, 0.5f, 0f, 1f); // Оранжевый
            comboMessage = "AMAZING x" + multiplier + "!!";
        } else if (combo >= 10) {
            comboColor = new Color(0f, 1f, 1f, 1f); // Голубой
            comboMessage = "GREAT x" + multiplier + "!";
        }
        
        FloatingDamageText comboText = new FloatingDamageText(x, y, 0, comboColor);
        comboText.text = comboMessage;
        comboText.maxLife = 2.5f; // Комбо-текст живет дольше
        comboText.life = 2.5f;
        comboText.velocityY = 120f; // Быстрее поднимается
        comboText.scale = 1.3f; // Начинаем с большего размера для лучшей видимости
        damageTexts.add(comboText);
    }
    
    public void update(float delta) {
        for (int i = damageTexts.size() - 1; i >= 0; i--) {
            if (!damageTexts.get(i).update(delta)) {
                damageTexts.remove(i);
            }
        }
    }
    
    public void draw(SpriteBatch batch, BitmapFont font) {
        for (FloatingDamageText text : damageTexts) {
            text.draw(batch, font);
        }
    }
    
    public void clear() {
        damageTexts.clear();
    }
}

