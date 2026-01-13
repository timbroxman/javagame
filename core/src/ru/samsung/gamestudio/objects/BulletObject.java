package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.samsung.gamestudio.GameSettings;

public class BulletObject extends GameObject {

    public boolean wasHit;

    public BulletObject(int x, int y, int width, int height, String texturePath, World world) {
        // Пули игрока сталкиваются только с мусором и вражескими кораблями
        // Не сталкиваются с другими пулями (BULLET_BIT, ENEMY_BULLET_BIT)
        super(texturePath, x, y, width, height, GameSettings.BULLET_BIT, 
              (short) (GameSettings.TRASH_BIT | GameSettings.ENEMY_SHIP_BIT), world);
        body.setLinearVelocity(new Vector2(0, GameSettings.BULLET_VELOCITY));
        body.setBullet(true);
        wasHit = false;
    }

    public boolean hasToBeDestroyed() {
        return wasHit || (getY() - height / 2 > GameSettings.SCREEN_HEIGHT);
    }

    @Override
    public void hit() {
        wasHit = true;
    }
}
