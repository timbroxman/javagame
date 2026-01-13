package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;

public class EnemyBulletObject extends GameObject {

    public boolean wasHit;

    public EnemyBulletObject(int x, int y, int width, int height, String texturePath, World world) {
        // Пули врагов сталкиваются только с кораблём игрока
        // Не сталкиваются с другими пулями (BULLET_BIT, ENEMY_BULLET_BIT)
        super(texturePath, x, y, width, height, GameSettings.ENEMY_BULLET_BIT,
              (short) (GameSettings.SHIP_BIT), world);
        body.setLinearVelocity(new Vector2(0, -GameSettings.ENEMY_BULLET_VELOCITY));
        body.setBullet(true);
        wasHit = false;
    }

    public boolean hasToBeDestroyed() {
        return wasHit || (getY() + height / 2 < 0);
    }

    @Override
    public void hit() {
        wasHit = true;
    }
}

