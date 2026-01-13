package ru.samsung.gamestudio.effects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ru.samsung.gamestudio.GameSettings;

public class CameraShake {
    private float shakeIntensity;
    private float shakeDuration;
    private float currentShakeTime;
    private Vector2 originalPosition;
    private OrthographicCamera camera;
    
    public CameraShake(OrthographicCamera camera) {
        this.camera = camera;
        this.originalPosition = new Vector2();
        this.shakeIntensity = 0;
        this.shakeDuration = 0;
        this.currentShakeTime = Float.MAX_VALUE; // Изначально не трясём
        // Инициализируем исходную позицию камеры (центр экрана)
        this.originalPosition.set(GameSettings.SCREEN_WIDTH / 2f, GameSettings.SCREEN_HEIGHT / 2f);
    }
    
    public void shake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.currentShakeTime = 0;
        // Сохраняем исходную позицию камеры при начале тряски
        this.originalPosition.set(camera.position.x, camera.position.y);
    }
    
    public void update(float delta) {
        if (currentShakeTime < shakeDuration) {
            currentShakeTime += delta;
            float progress = currentShakeTime / shakeDuration;
            float currentIntensity = shakeIntensity * (1f - progress); // Уменьшаем интенсивность со временем
            
            // Случайное смещение камеры
            float offsetX = (MathUtils.random() - 0.5f) * 2f * currentIntensity;
            float offsetY = (MathUtils.random() - 0.5f) * 2f * currentIntensity;
            
            camera.position.set(
                originalPosition.x + offsetX,
                originalPosition.y + offsetY,
                0
            );
        } else if (currentShakeTime != Float.MAX_VALUE && currentShakeTime >= shakeDuration) {
            // Возвращаем камеру в исходное положение только если была тряска
            camera.position.set(originalPosition.x, originalPosition.y, 0);
            currentShakeTime = Float.MAX_VALUE; // Сбрасываем, чтобы не выполнять это каждый кадр
        }
    }
    
    public boolean isShaking() {
        return currentShakeTime < shakeDuration;
    }
}

