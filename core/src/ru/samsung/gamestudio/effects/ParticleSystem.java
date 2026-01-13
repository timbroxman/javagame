package ru.samsung.gamestudio.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class ParticleSystem {
    private ArrayList<Particle> particles;
    private Color[] explosionColors;
    private Texture whitePixel;
    
    public ParticleSystem() {
        particles = new ArrayList<>();
        // Цвета для взрыва: оранжевый, красный, желтый
        explosionColors = new Color[]{
            new Color(1f, 0.5f, 0f, 1f), // Оранжевый
            new Color(1f, 0f, 0f, 1f),   // Красный
            new Color(1f, 1f, 0f, 1f),   // Желтый
            new Color(0.8f, 0.8f, 0.8f, 1f) // Серый (дым)
        };
        
        // Создаем простую белую текстуру для частиц
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }
    
    public void createExplosion(float x, float y, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
            float speed = MathUtils.random(50, 300);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;
            
            Color color = explosionColors[MathUtils.random(explosionColors.length - 1)];
            float size = MathUtils.random(3, 8);
            float life = MathUtils.random(0.3f, 0.8f);
            
            particles.add(new Particle(x, y, vx, vy, color, size, life));
        }
    }
    
    public void createSparkExplosion(float x, float y, int sparkCount) {
        for (int i = 0; i < sparkCount; i++) {
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
            float speed = MathUtils.random(100, 400);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;
            
            Color color = new Color(1f, MathUtils.random(0.7f, 1f), 0f, 1f); // Желто-оранжевые искры
            float size = MathUtils.random(2, 5);
            float life = MathUtils.random(0.2f, 0.5f);
            
            particles.add(new Particle(x, y, vx, vy, color, size, life));
        }
    }
    
    public void createSmoke(float x, float y, int smokeCount) {
        for (int i = 0; i < smokeCount; i++) {
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
            float speed = MathUtils.random(20, 80);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed + MathUtils.random(50, 150); // Дым поднимается вверх
            
            Color color = new Color(0.3f, 0.3f, 0.3f, 0.6f); // Серый дым
            float size = MathUtils.random(5, 15);
            float life = MathUtils.random(0.5f, 1.2f);
            
            particles.add(new Particle(x, y, vx, vy, color, size, life));
        }
    }
    
    public void createFlash(float x, float y, Color flashColor) {
        // Создаем яркую вспышку
        for (int i = 0; i < 30; i++) {
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
            float speed = MathUtils.random(200, 500);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;
            
            float size = MathUtils.random(4, 10);
            float life = MathUtils.random(0.1f, 0.3f);
            
            particles.add(new Particle(x, y, vx, vy, flashColor, size, life));
        }
    }
    
    public void update(float delta) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            if (!particles.get(i).update(delta)) {
                particles.remove(i);
            }
        }
    }
    
    public void draw(SpriteBatch batch) {
        for (Particle particle : particles) {
            batch.setColor(particle.color);
            // Рисуем частицу как квадрат с вращением
            float halfSize = particle.size / 2f;
            batch.draw(
                whitePixel,
                particle.position.x - halfSize,
                particle.position.y - halfSize,
                halfSize, halfSize,
                particle.size, particle.size,
                1f, 1f,
                particle.rotation,
                0, 0,
                whitePixel.getWidth(), whitePixel.getHeight(),
                false, false
            );
        }
        batch.setColor(1f, 1f, 1f, 1f); // Сбрасываем цвет
    }
    
    public void clear() {
        particles.clear();
    }
    
    public void dispose() {
        if (whitePixel != null) {
            whitePixel.dispose();
        }
    }
}

