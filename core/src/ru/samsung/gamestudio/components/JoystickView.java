package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class JoystickView extends View {

    private Texture outerTexture;
    private Texture innerTexture;
    
    private float centerX;
    private float centerY;
    private float outerRadius;
    private float innerRadius;
    
    private float innerX;
    private float innerY;
    
    private boolean isActive;
    
    // Мертвая зона - минимальное расстояние для активации движения (в процентах от радиуса)
    private static final float DEAD_ZONE = 0.15f; // 15% от максимального радиуса
    
    public JoystickView(float x, float y, float size) {
        super(x, y, size, size);
        outerTexture = new Texture("textures/outer_circle.png");
        innerTexture = new Texture("textures/iner_circle.png");
        
        centerX = x + size / 2f;
        centerY = y + size / 2f;
        outerRadius = size / 2f;
        innerRadius = size / 4f; // Внутренний круг в 2 раза меньше
        
        innerX = centerX;
        innerY = centerY;
        isActive = false;
    }
    
    public void handleTouch(Vector3 touchPos) {
        if (!isActive) {
            // Проверяем, началось ли касание в области джойстика
            float dx = touchPos.x - centerX;
            float dy = touchPos.y - centerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance <= outerRadius) {
                isActive = true;
                updateInnerPosition(touchPos.x, touchPos.y);
            }
        } else {
            updateInnerPosition(touchPos.x, touchPos.y);
        }
    }
    
    public void handleTouchUp() {
        if (isActive) {
            isActive = false;
            // Возвращаем внутренний круг в центр
            innerX = centerX;
            innerY = centerY;
        }
    }
    
    private void updateInnerPosition(float touchX, float touchY) {
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Ограничиваем расстояние радиусом внешнего круга
        if (distance > outerRadius - innerRadius) {
            distance = outerRadius - innerRadius;
            float angle = (float) Math.atan2(dy, dx);
            innerX = centerX + (float) Math.cos(angle) * distance;
            innerY = centerY + (float) Math.sin(angle) * distance;
        } else {
            innerX = touchX;
            innerY = touchY;
        }
    }
    
    /**
     * Получить направление движения джойстика в виде нормализованного вектора
     * @return Vector2 с направлением (x, y), где значения от -1 до 1
     */
    public Vector2 getDirection() {
        if (!isActive) {
            return new Vector2(0, 0);
        }
        
        float dx = innerX - centerX;
        float dy = innerY - centerY;
        float maxDistance = outerRadius - innerRadius;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Применяем мертвую зону
        if (distance < maxDistance * DEAD_ZONE) {
            return new Vector2(0, 0);
        }
        
        // Нормализуем значения от -1 до 1
        float normalizedX = dx / maxDistance;
        float normalizedY = dy / maxDistance;
        
        // Применяем экспоненциальную кривую для более плавного управления
        // Это делает управление более чувствительным в центре и менее чувствительным на краях
        float magnitude = (float) Math.sqrt(normalizedX * normalizedX + normalizedY * normalizedY);
        if (magnitude > 0) {
            // Квадратичная кривая для более плавного управления
            float smoothMagnitude = magnitude * magnitude;
            normalizedX = (normalizedX / magnitude) * smoothMagnitude;
            normalizedY = (normalizedY / magnitude) * smoothMagnitude;
        }
        
        return new Vector2(normalizedX, normalizedY);
    }
    
    /**
     * Получить силу нажатия (0.0 - 1.0)
     */
    public float getStrength() {
        if (!isActive) {
            return 0f;
        }
        
        float dx = innerX - centerX;
        float dy = innerY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float maxDistance = outerRadius - innerRadius;
        
        return Math.min(1.0f, distance / maxDistance);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        // Рисуем внешний круг как квадрат, чтобы он оставался кругом
        float outerSize = width; // Используем одинаковый размер для width и height
        batch.draw(outerTexture, x, y, outerSize, outerSize);
        
        // Рисуем внутренний круг как квадрат
        float innerSize = innerRadius * 2;
        batch.draw(innerTexture, 
                   innerX - innerRadius, 
                   innerY - innerRadius, 
                   innerSize, 
                   innerSize);
    }
    
    @Override
    public void dispose() {
        outerTexture.dispose();
        innerTexture.dispose();
    }
}
