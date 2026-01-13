package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.GameSettings;

import java.util.Random;

public class EnemyShipObject extends GameObject {

    private static final int paddingHorizontal = 30;
    private static final int TOP_AREA_HEIGHT = 200; // Высота верхней области для полёта
    
    // Типы врагов
    public enum EnemyType {
        FAST,      // Быстрый враг (1 жизнь, высокая скорость)
        NORMAL,    // Обычный враг (1 жизнь, средняя скорость)
        SLOW,      // Медленный враг (2 жизни, низкая скорость)
        TANK,      // Танк (3 жизни, очень низкая скорость)
        BOSS       // Босс (5+ жизней, средняя скорость, большой размер)
    }
    
    // Типы движения вражеских кораблей
    private enum MovementPattern {
        STRAIGHT,      // Прямолинейное движение
        SINE_WAVE,     // Синусоидальное движение (волна)
        ZIGZAG,        // Зигзагообразное движение
        CIRCLE,        // Движение по кругу
        ACCELERATE     // Движение с ускорением/замедлением
    }
    
    private EnemyType enemyType;
    private int maxLives;
    long lastShotTime;
    private int livesLeft;
    private boolean justHit; // Флаг для отслеживания попадания
    private float direction; // Направление движения: 1 - вправо, -1 - влево
    private MovementPattern movementPattern;
    private float startTime;
    private float baseY;
    private float amplitude; // Амплитуда для волнового движения
    private float frequency; // Частота для волнового движения
    private float zigzagTimer; // Таймер для зигзага
    private float circleAngle; // Угол для кругового движения
    private float speedMultiplier; // Множитель скорости для ускорения

    public EnemyShipObject(int width, int height, String texturePath, World world) {
        this(width, height, texturePath, world, EnemyType.NORMAL);
    }
    
    public EnemyShipObject(int width, int height, String texturePath, World world, EnemyType type) {
        super(
                texturePath,
                width / 2 + paddingHorizontal + (new Random(System.nanoTime())).nextInt((GameSettings.SCREEN_WIDTH - 2 * paddingHorizontal - width)),
                GameSettings.SCREEN_HEIGHT - TOP_AREA_HEIGHT, // Летает в верхней части экрана
                width, height,
                GameSettings.ENEMY_SHIP_BIT,
                world
        );
        
        this.enemyType = type;
        
        // Устанавливаем характеристики в зависимости от типа
        switch (type) {
            case FAST:
                maxLives = 1;
                livesLeft = 1;
                break;
            case NORMAL:
                maxLives = 1;
                livesLeft = 1;
                break;
            case SLOW:
                maxLives = 2;
                livesLeft = 2;
                break;
            case TANK:
                maxLives = 3;
                livesLeft = 3;
                break;
            case BOSS:
                maxLives = 5;
                livesLeft = 5;
                break;
        }
        
        // Используем nanoTime для лучшей случайности
        Random random = new Random(System.nanoTime() + TimeUtils.nanoTime());
        
        // Начальное направление движения (случайное)
        direction = random.nextBoolean() ? 1 : -1;
        
        // Случайно выбираем паттерн движения (исключаем STRAIGHT для более интересного движения)
        float patternChance = random.nextFloat();
        if (patternChance < 0.3f) {
            movementPattern = MovementPattern.SINE_WAVE;
        } else if (patternChance < 0.55f) {
            movementPattern = MovementPattern.ZIGZAG;
        } else if (patternChance < 0.8f) {
            movementPattern = MovementPattern.CIRCLE;
        } else {
            movementPattern = MovementPattern.ACCELERATE;
        }
        
        // Инициализация параметров движения с большим разбросом
        startTime = TimeUtils.millis() / 1000f + random.nextFloat() * 2f; // Случайный сдвиг времени
        baseY = GameSettings.SCREEN_HEIGHT - TOP_AREA_HEIGHT + (random.nextFloat() - 0.5f) * 50; // Разные стартовые Y
        amplitude = 20 + random.nextFloat() * 80; // Амплитуда 20-100 (больше разброс)
        frequency = 0.3f + random.nextFloat() * 2.0f; // Частота 0.3-2.3 (больше разброс)
        zigzagTimer = random.nextFloat() * 1.5f; // Случайный старт для зигзага
        circleAngle = random.nextFloat() * MathUtils.PI2; // Случайный начальный угол
        speedMultiplier = 0.3f + random.nextFloat() * 1.2f; // 0.3-1.5 (больше разброс)
        
        // Разные начальные скорости в зависимости от типа
        float speedMultiplierByType = 1.0f;
        switch (type) {
            case FAST:
                speedMultiplierByType = 1.8f; // Быстрее
                break;
            case NORMAL:
                speedMultiplierByType = 1.0f; // Обычная скорость
                break;
            case SLOW:
                speedMultiplierByType = 0.6f; // Медленнее
                break;
            case TANK:
                speedMultiplierByType = 0.4f; // Очень медленно
                break;
            case BOSS:
                speedMultiplierByType = 0.8f; // Немного медленнее обычного
                break;
        }
        
        float initialSpeed = GameSettings.ENEMY_SHIP_VELOCITY * speedMultiplierByType * (0.7f + random.nextFloat() * 0.6f);
        body.setLinearVelocity(new Vector2(initialSpeed * direction, 0));
        body.setLinearDamping(3 + random.nextFloat() * 4); // Разное затухание 3-7
        lastShotTime = TimeUtils.millis() + random.nextInt(2000); // Random initial delay 0-2 сек
    }
    
    public EnemyType getEnemyType() {
        return enemyType;
    }
    
    public int getMaxLives() {
        return maxLives;
    }
    
    public float getHealthPercent() {
        return (float) livesLeft / maxLives;
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        float currentTime = TimeUtils.millis() / 1000f - startTime;
        
        float velocityX = 0;
        float velocityY = 0;
        float targetY = baseY;
        
        // Применяем выбранный паттерн движения с вариациями
        switch (movementPattern) {
            case STRAIGHT:
                // Движение с заметными волнами (если всё же выбран)
                float straightSpeed = GameSettings.ENEMY_SHIP_VELOCITY * (0.8f + MathUtils.sin(currentTime * 0.5f) * 0.2f);
                velocityX = straightSpeed * direction;
                velocityY = MathUtils.sin(currentTime * 0.8f) * 15f; // Заметное вертикальное покачивание
                targetY = baseY + MathUtils.sin(currentTime * 0.6f) * 20f;
                break;
                
            case SINE_WAVE:
                // Синусоидальное движение (волна вверх-вниз) с комбинацией частот
                velocityX = GameSettings.ENEMY_SHIP_VELOCITY * direction * (0.85f + MathUtils.sin(currentTime * 0.7f) * 0.15f);
                // Комбинация двух синусоид для более сложной траектории
                targetY = baseY + 
                    MathUtils.sin(currentTime * frequency * MathUtils.PI2) * amplitude +
                    MathUtils.sin(currentTime * frequency * 1.7f * MathUtils.PI2) * (amplitude * 0.4f);
                velocityY = (targetY - getY()) * (5f + MathUtils.sin(currentTime) * 2f); // Переменная скорость следования
                // Добавляем горизонтальное покачивание для более интересной траектории
                velocityX += MathUtils.sin(currentTime * frequency * 0.5f) * 5f;
                break;
                
            case ZIGZAG:
                // Зигзагообразное движение с переменной частотой и более резкими поворотами
                zigzagTimer += deltaTime;
                float zigzagInterval = 0.8f + MathUtils.sin(currentTime * 0.3f) * 0.4f; // Переменный интервал (0.8-1.2 сек)
                if (zigzagTimer > zigzagInterval) {
                    direction *= -1;
                    zigzagTimer = 0;
                }
                float zigzagSpeed = GameSettings.ENEMY_SHIP_VELOCITY * (0.75f + MathUtils.sin(currentTime * 2f) * 0.25f);
                velocityX = zigzagSpeed * direction;
                // Более выраженное вертикальное движение с переменной амплитудой
                targetY = baseY + MathUtils.sin(currentTime * 2.5f * MathUtils.PI2) * (25 + MathUtils.sin(currentTime * 0.5f) * 20);
                velocityY = (targetY - getY()) * (4f + MathUtils.sin(currentTime * 1.5f) * 2f);
                // Добавляем диагональное движение для более интересного зигзага
                velocityX += MathUtils.cos(currentTime * 3f) * 8f;
                break;
                
            case CIRCLE:
                // Движение по кругу/эллипсу с переменной скоростью
                float rotationSpeed = 0.5f + MathUtils.sin(currentTime * 0.4f) * 0.3f; // Переменная скорость вращения
                circleAngle += deltaTime * rotationSpeed;
                float radiusX = GameSettings.SCREEN_WIDTH * (0.25f + MathUtils.sin(currentTime * 0.2f) * 0.1f); // Пульсирующий радиус
                float radiusY = amplitude * (0.8f + MathUtils.sin(currentTime * 0.3f) * 0.2f);
                float centerX = GameSettings.SCREEN_WIDTH / 2f + MathUtils.sin(currentTime * 0.15f) * 50; // Движущийся центр
                float targetX = centerX + MathUtils.cos(circleAngle) * radiusX;
                targetY = baseY + MathUtils.sin(circleAngle) * radiusY;
                
                velocityX = (targetX - getX()) * (1.5f + MathUtils.sin(currentTime) * 0.5f);
                velocityY = (targetY - getY()) * (1.5f + MathUtils.sin(currentTime) * 0.5f);
                break;
                
            case ACCELERATE:
                // Движение с переменной скоростью и сложной траекторией
                speedMultiplier += MathUtils.sin(currentTime * 2f) * 0.2f + MathUtils.cos(currentTime * 1.3f) * 0.15f;
                speedMultiplier = MathUtils.clamp(speedMultiplier, 0.2f, 1.8f);
                velocityX = GameSettings.ENEMY_SHIP_VELOCITY * direction * speedMultiplier;
                // Сложное вертикальное движение
                targetY = baseY + 
                    MathUtils.sin(currentTime * 1.5f * MathUtils.PI2) * 30 +
                    MathUtils.cos(currentTime * 2.3f * MathUtils.PI2) * 15;
                velocityY = (targetY - getY()) * (3f + MathUtils.sin(currentTime * 1.2f) * 2f);
                break;
        }
        
        // Ограничиваем движение в верхней части экрана
        float topY = GameSettings.SCREEN_HEIGHT - TOP_AREA_HEIGHT + 50;
        float bottomY = GameSettings.SCREEN_HEIGHT - TOP_AREA_HEIGHT - 100;
        
        if (getY() > topY) {
            setY((int) topY);
            velocityY = 0;
        }
        if (getY() < bottomY) {
            setY((int) bottomY);
            velocityY = 0;
        }
        
        // Циклическое движение по горизонтали
        if (getX() < -width / 2) {
            setX(GameSettings.SCREEN_WIDTH + width / 2);
        }
        if (getX() > GameSettings.SCREEN_WIDTH + width / 2) {
            setX(-width / 2);
        }
        
        // Применяем вычисленную скорость
        body.setLinearVelocity(new Vector2(velocityX, velocityY));
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }

    public boolean isInFrame() {
        // Корабль в кадре, если он в пределах экрана по горизонтали
        return getX() > -width && getX() < GameSettings.SCREEN_WIDTH + width;
    }
    
    private static com.badlogic.gdx.graphics.Texture healthBarTexture;
    
    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        // Визуальные эффекты для разных типов врагов
        com.badlogic.gdx.graphics.Color originalColor = batch.getColor();
        
        // Эффект для боссов - пульсация
        if (enemyType == EnemyType.BOSS) {
            float time = TimeUtils.millis() / 1000f;
            float pulse = 0.9f + (float)Math.sin(time * 3f) * 0.1f;
            batch.setColor(pulse, pulse, pulse, 1f);
        }
        // Эффект для танков - легкое свечение
        else if (enemyType == EnemyType.TANK) {
            batch.setColor(1f, 0.95f, 0.9f, 1f);
        }
        
        super.draw(batch);
        batch.setColor(originalColor);
        
        // Рисуем улучшенную полоску здоровья для врагов с несколькими жизнями
        if (maxLives > 1) {
            drawHealthBar(batch);
        }
    }
    
    private void drawHealthBar(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        // Создаем текстуру для полоски здоровья, если её еще нет
        if (healthBarTexture == null) {
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            pixmap.fill();
            healthBarTexture = new com.badlogic.gdx.graphics.Texture(pixmap);
            pixmap.dispose();
        }
        
        float barWidth = width + 15;
        float barHeight = 7;
        float barX = getX() - barWidth / 2f;
        float barY = getY() + height / 2f + 15;
        
        // Внешняя рамка (черная)
        batch.setColor(0f, 0f, 0f, 0.8f);
        batch.draw(healthBarTexture, barX - 2, barY - 2, barWidth + 4, barHeight + 4);
        
        // Внутренняя рамка (темно-серая)
        batch.setColor(0.2f, 0.2f, 0.2f, 0.9f);
        batch.draw(healthBarTexture, barX - 1, barY - 1, barWidth + 2, barHeight + 2);
        
        // Фон полоски (темно-красный)
        batch.setColor(0.5f, 0f, 0f, 0.9f);
        batch.draw(healthBarTexture, barX, barY, barWidth, barHeight);
        
        // Здоровье с плавным градиентом
        float healthPercent = (float) livesLeft / maxLives;
        com.badlogic.gdx.graphics.Color healthColor;
        
        // Плавный переход цветов
        if (healthPercent > 0.6f) {
            // От зеленого к желто-зеленому
            float t = (healthPercent - 0.6f) / 0.4f;
            healthColor = new com.badlogic.gdx.graphics.Color(
                0f + t * 0.5f, 
                1f, 
                0f, 
                1f
            );
        } else if (healthPercent > 0.3f) {
            // От желтого к оранжевому
            float t = (healthPercent - 0.3f) / 0.3f;
            healthColor = new com.badlogic.gdx.graphics.Color(
                1f, 
                1f - t * 0.3f, 
                0f, 
                1f
            );
        } else {
            // От оранжевого к красному
            float t = healthPercent / 0.3f;
            healthColor = new com.badlogic.gdx.graphics.Color(
                1f, 
                0.7f * t, 
                0f, 
                1f
            );
        }
        
        // Основная полоска здоровья
        batch.setColor(healthColor);
        batch.draw(healthBarTexture, barX, barY, barWidth * healthPercent, barHeight);
        
        // Светлая полоска сверху для объема (опционально)
        if (healthPercent > 0.1f) {
            batch.setColor(1f, 1f, 1f, 0.3f);
            batch.draw(healthBarTexture, barX, barY + barHeight * 0.7f, barWidth * healthPercent, barHeight * 0.3f);
        }
        
        // Восстанавливаем цвет
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public boolean needToShoot() {
        if (TimeUtils.millis() - lastShotTime >= GameSettings.ENEMY_SHOOTING_COOL_DOWN) {
            lastShotTime = TimeUtils.millis();
            return true;
        }
        return false;
    }

    @Override
    public void hit() {
        if (livesLeft > 0) {
            livesLeft -= 1;
            justHit = true; // Устанавливаем флаг попадания
        }
    }
    
    public boolean wasJustHit() {
        if (justHit) {
            justHit = false; // Сбрасываем флаг после проверки
            return true;
        }
        return false;
    }
    
    public int getLivesLeft() {
        return livesLeft;
    }
    
    public String getTypeName() {
        switch (enemyType) {
            case FAST:
                return "FAST";
            case NORMAL:
                return "NORMAL";
            case SLOW:
                return "SLOW";
            case TANK:
                return "TANK";
            case BOSS:
                return "BOSS";
            default:
                return "UNKNOWN";
        }
    }
}

