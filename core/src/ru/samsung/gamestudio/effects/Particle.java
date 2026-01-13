package ru.samsung.gamestudio.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Particle {
    public Vector2 position;
    public Vector2 velocity;
    public Color color;
    public float size;
    public float life;
    public float maxLife;
    public float rotation;
    public float rotationSpeed;
    
    public Particle(float x, float y, float vx, float vy, Color color, float size, float life) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(vx, vy);
        this.color = new Color(color);
        this.size = size;
        this.life = life;
        this.maxLife = life;
        this.rotation = MathUtils.random(0, 360);
        this.rotationSpeed = MathUtils.random(-300, 300);
    }
    
    public boolean update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        velocity.y -= 200 * delta; // Гравитация
        velocity.x *= 0.98f; // Сопротивление воздуха
        velocity.y *= 0.98f;
        rotation += rotationSpeed * delta;
        life -= delta;
        
        // Изменение цвета и размера со временем
        float lifePercent = life / maxLife;
        color.a = lifePercent;
        size *= 0.99f;
        
        return life > 0 && size > 0.1f;
    }
}

