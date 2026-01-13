package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.GameSettings;

public class ShipObject extends GameObject {

    long lastShotTime;
    int livesLeft;
    long lastHitTime;
    private static final long INVINCIBILITY_DURATION = 2000; // 2 секунды неуязвимости после попадания
    
    // Бонусы (публичные для доступа из BonusIndicatorView)
    public long rapidFireEndTime = 0;
    public long shieldEndTime = 0;
    public boolean multishotActive = false;
    public long multishotEndTime = 0;

    public ShipObject(int x, int y, int width, int height, String texturePath, World world) {
        super(texturePath, x, y, width, height, GameSettings.SHIP_BIT, world);
        body.setLinearDamping(8); // Немного уменьшили демпфирование для более отзывчивого управления
        livesLeft = 3;
        lastHitTime = 0;
    }


    public int getLiveLeft() {
        return livesLeft;
    }

    @Override
    public void draw(SpriteBatch batch) {
        putInFrame();
        
        // Рисуем основной корабль
        if (isShieldActive()) {
            batch.setColor(0.5f, 0.7f, 1f, 0.8f);
            super.draw(batch);
            batch.setColor(Color.WHITE);
        } else {
            // Эффект мигания при получении урона
            long timeSinceHit = TimeUtils.millis() - lastHitTime;
            if (timeSinceHit < INVINCIBILITY_DURATION) {
                // Мигание: видимый каждые 100мс
                boolean visible = (timeSinceHit / 100) % 2 == 0;
                if (visible) {
                    // Рисуем с красноватым оттенком при получении урона
                    batch.setColor(1f, 0.5f, 0.5f, 1f);
                    super.draw(batch);
                    batch.setColor(Color.WHITE);
                }
            } else {
                super.draw(batch);
            }
        }
        
        // Анимация двигателей (огненные частицы сзади корабля)
        drawEngineEffects(batch);
    }
    
    private void drawEngineEffects(SpriteBatch batch) {
        // Рисуем эффект двигателей внизу корабля
        float engineY = getY() - height / 2f - 5;
        float engineX = getX();
        float time = TimeUtils.millis() / 1000f;
        
        // Огненные частицы двигателей
        batch.setColor(1f, 0.5f + (float)Math.sin(time * 10) * 0.3f, 0f, 0.8f);
        for (int i = -1; i <= 1; i++) {
            float offsetX = i * (width / 4f);
            float particleSize = 8 + (float)Math.sin(time * 15 + i) * 3;
            float particleY = engineY - (float)Math.sin(time * 20 + i) * 5;
            
            // Рисуем простой прямоугольник для эффекта огня
            batch.draw(texture,
                    engineX + offsetX - particleSize / 2,
                    particleY - particleSize / 2,
                    particleSize, particleSize);
        }
        batch.setColor(Color.WHITE);
    }

    public void move(Vector3 vector3) {
        body.applyForceToCenter(new Vector2(
                        (vector3.x - getX()) * GameSettings.SHIP_FORCE_RATIO,
                        (vector3.y - getY()) * GameSettings.SHIP_FORCE_RATIO),
                true
        );
    }
    
    /**
     * Движение корабля по направлению джойстика
     * @param direction нормализованный вектор направления (от -1 до 1)
     */
    public void moveByDirection(Vector2 direction) {
        // Применяем силу в направлении джойстика
        float forceX = direction.x * GameSettings.SHIP_FORCE_RATIO * 150f;
        float forceY = direction.y * GameSettings.SHIP_FORCE_RATIO * 150f;
        body.applyForceToCenter(new Vector2(forceX, forceY), true);
    }

    private void putInFrame() {
        if (getY() > (GameSettings.SCREEN_HEIGHT / 2f - height / 2f)) {
            setY((int) (GameSettings.SCREEN_HEIGHT / 2f - height / 2f));
        }
        if (getY() <= (height / 2f)) {
            setY(height / 2);
        }
        if (getX() < (-width / 2f)) {
            setX(GameSettings.SCREEN_WIDTH);
        }
        if (getX() > (GameSettings.SCREEN_WIDTH + width / 2f)) {
            setX(0);
        }
    }

    public boolean needToShoot() {
        long cooldown = GameSettings.SHOOTING_COOL_DOWN;
        // Ускорение стрельбы уменьшает кулдаун в 2 раза
        if (isRapidFireActive()) {
            cooldown = cooldown / 2;
        }
        
        if (TimeUtils.millis() - lastShotTime >= cooldown) {
            lastShotTime = TimeUtils.millis();
            return true;
        }
        return false;
    }
    
    public boolean isRapidFireActive() {
        return TimeUtils.millis() < rapidFireEndTime;
    }
    
    public boolean isShieldActive() {
        return TimeUtils.millis() < shieldEndTime;
    }
    
    public boolean isMultishotActive() {
        return TimeUtils.millis() < multishotEndTime;
    }
    
    public void applyBonus(ru.samsung.gamestudio.objects.BonusType type) {
        long currentTime = TimeUtils.millis();
        switch (type) {
            case HEALTH:
                if (livesLeft < 3) {
                    livesLeft++;
                }
                break;
            case RAPIDFIRE:
                rapidFireEndTime = currentTime + 10000; // 10 секунд
                break;
            case SHIELD:
                shieldEndTime = currentTime + 8000; // 8 секунд
                break;
            case MULTISHOT:
                multishotActive = true;
                multishotEndTime = currentTime + 12000; // 12 секунд
                break;
        }
    }

    @Override
    public void hit() {
        long currentTime = TimeUtils.millis();
        // Если активен щит, не получаем урон
        if (isShieldActive()) {
            return;
        }
        // Защита от множественных попаданий в короткий промежуток времени
        if (currentTime - lastHitTime >= INVINCIBILITY_DURATION) {
            livesLeft -= 1;
            lastHitTime = currentTime;
        }
    }
    
    public boolean isInvincible() {
        return (TimeUtils.millis() - lastHitTime) < INVINCIBILITY_DURATION || isShieldActive();
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }
}
