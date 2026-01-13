package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import ru.samsung.gamestudio.GameSettings;

import java.util.Random;

public class BonusObject extends GameObject {

    private static final int paddingHorizontal = 30;
    
    private BonusType bonusType;
    private boolean collected;
    private long spawnTime;

    public BonusObject(int width, int height, String texturePath, World world, BonusType type) {
        // Бонусы сталкиваются только с кораблём игрока
        super(
                texturePath,
                width / 2 + paddingHorizontal + (new Random()).nextInt((GameSettings.SCREEN_WIDTH - 2 * paddingHorizontal - width)),
                GameSettings.SCREEN_HEIGHT + height / 2,
                width, height,
                GameSettings.BONUS_BIT,
                (short) (GameSettings.SHIP_BIT), // Сталкивается только с кораблём
                world
        );

        body.setLinearVelocity(new Vector2(0, -GameSettings.BONUS_VELOCITY));
        this.bonusType = type;
        this.collected = false;
        this.spawnTime = TimeUtils.millis();
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        // Пульсация бонуса (синусоидальное изменение размера)
        float time = (TimeUtils.millis() - spawnTime) / 1000f;
        float pulseScale = 1f + MathUtils.sin(time * 3f) * 0.15f; // Пульсация от 0.85 до 1.15
        
        float originX = width / 2f;
        float originY = height / 2f;
        float scaledWidth = width * pulseScale;
        float scaledHeight = height * pulseScale;
        
        batch.draw(texture,
                getX() - originX * pulseScale,
                getY() - originY * pulseScale,
                originX * pulseScale, originY * pulseScale,
                scaledWidth, scaledHeight,
                1f, 1f,
                0f,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    public boolean isInFrame() {
        return getY() + height / 2 > 0;
    }

    @Override
    public void hit() {
        // Бонусы не получают урон, только собираются
    }
}

