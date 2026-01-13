package ru.samsung.gamestudio.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class FloatingDamageText {
    public float x, y;
    public String text;
    public Color color;
    public float life;
    public float maxLife;
    public float velocityY;
    public float scale;
    public float rotation;
    
    public FloatingDamageText(float x, float y, int damage, Color color) {
        this.x = x;
        this.y = y;
        this.text = String.valueOf(damage);
        this.color = new Color(color);
        this.life = 1.2f;
        this.maxLife = 1.2f;
        this.velocityY = 60f + MathUtils.random(0, 40);
        this.scale = 0.8f; // Начинаем с меньшего размера
        this.rotation = MathUtils.random(-15, 15);
    }
    
    public boolean update(float delta) {
        y += velocityY * delta;
        velocityY *= 0.97f; // Замедление
        life -= delta;
        
        // Анимация масштаба - сначала увеличивается, потом уменьшается
        float lifePercent = life / maxLife;
        if (lifePercent > 0.5f) {
            // Первая половина жизни - увеличиваемся
            scale = 0.8f + (1.0f - lifePercent) * 0.4f; // От 0.8 до 1.2
        } else {
            // Вторая половина - уменьшаемся
            scale = 1.2f * lifePercent * 2f; // От 1.2 до 0
        }
        
        // Изменение прозрачности с задержкой
        if (lifePercent > 0.3f) {
            color.a = 1.0f; // Полностью видимый
        } else {
            color.a = lifePercent / 0.3f; // Плавное затухание
        }
        
        // Плавное горизонтальное движение с затуханием (более плавное)
        float bounceAmount = MathUtils.sin(life * 6f) * 2f;
        x += bounceAmount * delta * 10f;
        
        return life > 0;
    }
    
    public void draw(SpriteBatch batch, BitmapFont font) {
        if (life <= 0 || scale <= 0) return;
        
        // Сохраняем оригинальный цвет
        Color originalColor = batch.getColor();
        
        // Устанавливаем цвет и масштаб
        float originalScale = font.getData().scaleX;
        font.getData().setScale(scale);
        
        // Добавляем тень для лучшей читаемости
        GlyphLayout layout = new GlyphLayout(font, text);
        float drawX = x - layout.width / 2f;
        float drawY = y;
        
        // Тень (темная)
        batch.setColor(0f, 0f, 0f, color.a * 0.5f);
        font.draw(batch, text, drawX + 2, drawY - 2);
        
        // Основной текст
        batch.setColor(color);
        font.draw(batch, text, drawX, drawY);
        
        // Восстанавливаем
        font.getData().setScale(originalScale);
        batch.setColor(originalColor);
    }
}

