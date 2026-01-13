package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;

import java.util.Random;

public class TrashObject extends GameObject {

    private static final int paddingHorizontal = 30;

    private int livesLeft;
    private float rotationSpeed;
    private float currentRotation;

    public TrashObject(int width, int height, String texturePath, World world) {
        super(
                texturePath,
                width / 2 + paddingHorizontal + (new Random()).nextInt((GameSettings.SCREEN_WIDTH - 2 * paddingHorizontal - width)),
                GameSettings.SCREEN_HEIGHT + height / 2,
                width, height,
                GameSettings.TRASH_BIT,
                world
        );

        body.setLinearVelocity(new Vector2(0, -GameSettings.TRASH_VELOCITY));
        livesLeft = 1;
        // Случайная скорость вращения для каждого мусора
        rotationSpeed = MathUtils.random(-180, 180); // градусов в секунду
        currentRotation = MathUtils.random(0, 360);
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        // Обновляем вращение
        float delta = Gdx.graphics.getDeltaTime();
        currentRotation += rotationSpeed * delta;
        if (currentRotation > 360) currentRotation -= 360;
        if (currentRotation < 0) currentRotation += 360;
        
        // Рисуем с вращением
        super.draw(batch, currentRotation);
    }

    public boolean isAlive() {
        return livesLeft > 0;
    }

    public boolean isInFrame() {
        return getY() + height / 2 > 0;
    }

    @Override
    public void hit() {
        livesLeft -= 1;
    }
}
